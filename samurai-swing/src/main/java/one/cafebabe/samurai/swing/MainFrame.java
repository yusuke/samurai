/*
 * Copyright 2003-2021 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package one.cafebabe.samurai.swing;

import one.cafebabe.samurai.util.Configuration;
import one.cafebabe.samurai.util.CustomizableKeyStroke;
import one.cafebabe.samurai.util.GUIResourceBundle;
import one.cafebabe.samurai.util.OSDetector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static one.cafebabe.samurai.swing.TileTabPanel.*;

public class MainFrame extends JFrame implements KeyListener, FileHistoryListener, CloseListener {
    private final Configuration config = new Configuration("samurai");

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private static final CustomizableKeyStroke keyStroke = new CustomizableKeyStroke(resources);

    private final AboutSamuraiDialog aboutSamuraiDialog = new AboutSamuraiDialog(this);
    private final JLabel statusBar = new JLabel();
    private final Context context = new Context(statusBar);
    private final ConfigDialog configDialog = new ConfigDialog(config);

    private final JPanel southPane = new JPanel();
    private final SearchPanel searcher = new SearchPanel(context, config);
    private final FileHistory fileHistory = new FileHistory(config, this);
    TileTabPanel<SamuraiPanel> tab = new TileTabPanel<>(true) {
        protected void selectedIndexChanged(int index) {
            setSelectedEncoding(getSelectedComponent().getEncoding());
        }
    };
    
    private boolean searchPanelAdded = false;
    private EncodingMenuItem selectedEncoding;

    private final MenuBar menuBar = MenuBar.newBuilder()
            .addMenu("menu.file",
                    fileMenu -> fileMenu.addMenuItem("menu.file.newTab", e -> openNewTab())
                            .addMenuItem("menu.file.open", fileHistory::menuOpen)
                            .addMenu("menu.file.openRecent", e -> {
                                fileHistory.openRecentMenu = e;
                                fileHistory.updateChildMenuItems();
                            })
                            .addMenu("menu.file.takeThreadDumpFrom", e -> new TakeThreadDump(config, fileHistory, e))
                            .addMenu("menu.file.viewGcLogFrom", e -> new ViewGcLog(config, fileHistory, e))
                            .addSeparator()
                            .addMenuItem("menu.file.close", e -> closeSamuraiPanel(tab.getSelectedIndex()))
                            .addMenuItemIfWin("menu.file.exit", e -> handleQuit()))
            .addMenu("menu.edit",
                    editMenu -> editMenu.addMenuItemIfWin("menu.edit.preferences", e -> handlePreferences())
                            .addMenuItem("menu.edit.copy", e -> {
                                Component component = tab.getSelectedComponent().getSelectedComponent();
                                if (component instanceof ClipBoardOperationListener) {
                                    ((ClipBoardOperationListener) component).copy();
                                }
                            })
                            .addSeparator()
                            .addMenuItem("menu.edit.find", e -> addSearchPanel())
                            .addMenuItem("menu.edit.findNext", e -> searchNext())
                            .addMenuItem("menu.edit.findPrevious", e -> searchPrevious()))
            .addMenu("menu.view",
                    viewMenu -> viewMenu.addMenuItem("menu.view.reload", e -> tab.getSelectedComponent().reload())
                            .addMenuItem("menu.view.previous", e -> previousTab())
                            .addMenuItem("menu.view.next", e -> nextTab())
                            .addSeparator()
                            .addMenuItem("TileTabPanel.tab", e -> setOrientation(TAB))
                            .addMenuItem("TileTabPanel.splitHorizontal", e -> setOrientation(TileTabPanel.TILE_HORIZONTAL))
                            .addMenuItem("TileTabPanel.splitVertical", e -> setOrientation(TileTabPanel.TILE_VERTICAL))
                            .addSeparator()
                            .addCheckBoxMenuItem("menu.view.statusBar", e -> {
                                if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
                                    southPane.add(statusBar, BorderLayout.SOUTH);
                                } else {
                                    southPane.remove(statusBar);
                                }
                                validate();
                            })
                            .addSeparator()
                            .addMenu("menu.view.encoding", encodingMenu ->
                            {
                                String[] encodings = config.getString("encodings").split(",");
                                for (String encoding : encodings) {
                                    if ("-".equals(encoding)) {
                                        encodingMenu.addSeparator();
                                    } else {
                                        EncodingMenuItem item = new EncodingMenuItem(encoding);
                                        encodingMenu.add(item);
                                        item.addActionListener(new EncodingMenuItemActionListener());
                                    }
                                }
                            })
                            .addMenuItem("menu.view.clearBuffer", e -> clearBuffer()))
            .addMenuIfWin("menu.help",
                    helpMenu -> helpMenu.addMenuItem("menu.help.about", e -> handleAbout()));


    private void setOrientation(int orientation) {
        tab.setOrientation(orientation);
        menuBar.getMenuItem("TileTabPanel.tab").setEnabled(orientation != TAB);
        menuBar.getMenuItem("TileTabPanel.splitHorizontal").setEnabled(orientation != TILE_HORIZONTAL);
        menuBar.getMenuItem("TileTabPanel.splitVertical").setEnabled(orientation != TILE_VERTICAL);
    }

    public MainFrame() {
        super(resources.getMessage("MainFrame.title"));
        this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.getContentPane().setLayout(new BorderLayout());
        this.setSize(new Dimension(400, 450));

        setJMenuBar(this.menuBar.menuBar());
        menuBar.getCheckBoxMenuItem("menu.view.statusBar").setSelected(true);
        if (OSDetector.isMac()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler(e -> this.handleAbout());
            desktop.setPreferencesHandler(e -> this.handlePreferences());
            desktop.setQuitHandler((e1, e2) -> this.handleQuit());
        }

        statusBar.setPreferredSize(new Dimension(3, 14));


        searcher.btnHide.addActionListener(e -> {
            removeSearchPanel();
            validate();
        });
        searcher.btnNext.addActionListener(e -> searchNext());
        searcher.btnPrevious.addActionListener(e -> searchPrevious());
        searcher.config_searchText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    removeSearchPanel();
                    e.consume();
                }
            }
        });

        getContentPane().add(tab, BorderLayout.CENTER);
        openNewTab();
        getContentPane().add(southPane, BorderLayout.SOUTH);
        southPane.setLayout(new BorderLayout());
        southPane.add(statusBar, BorderLayout.SOUTH);
        if (OSDetector.isWindows()) {
            setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("images/samurai.png")));
        }

        config.applyRectangle("MainFrame.bounds", this);
        config.applyLocation("ConfigDialog.location", configDialog);
        keyStroke.apply(this);
        keyStroke.apply(tab.popupMenu);
        DropTarget target = new DropTarget(this,
                DnDConstants.ACTION_REFERENCE,
                mainFrameDropTargetListener
        );
        DropTarget target2 = new DropTarget(tab,
                DnDConstants.ACTION_REFERENCE,
                tabDropTargetListener
        );
        setDragNotAccepting();
    }

    private void removeSearchPanel() {
        southPane.remove(searcher);
        validate();
        searchPanelAdded = false;
    }

    private void addSearchPanel() {
        if (!searchPanelAdded) {
            southPane.add(searcher, BorderLayout.CENTER);
            int length = searcher.config_searchText.getText().length();
            searcher.config_searchText.setSelectionStart(0);
            searcher.config_searchText.setSelectionEnd(length);
            searchPanelAdded = true;
            validate();
        }
        searcher.config_searchText.grabFocus();
    }

    public void closePushed(int index) {
        closeSamuraiPanel(index);
    }

    private JTextComponent getActiveComponent() {
        Component activeComponent = tab.getSelectedComponent().tab.getSelectedComponent();
        JTextComponent com = null;
        if (activeComponent instanceof ThreadDumpPanel) {
            com = ((ThreadDumpPanel) activeComponent).threadDumpPanel;
        } else if (activeComponent instanceof LogPanel) {
            com = ((LogPanel) activeComponent).textArea;
        }
        return com;
    }

    private void searchNext() {
        JTextComponent activeComponent = getActiveComponent();
        if (null != activeComponent) {
            if (searcher.config_searchText.getText().length() != 0) {
                if (searcher.searchNext(activeComponent)) {
                    activeComponent.grabFocus();
                } else {
                    if (searcher.isDisplayable()) {
                        searcher.config_searchText.grabFocus();
                    }
                }
                //easter eggs
                if (searcher.config_searchText.getText().equalsIgnoreCase("kill bill")) {
                    activeComponent.setBackground(Color.YELLOW);
                    activeComponent.setForeground(Color.BLACK);
                }
                if (searcher.config_searchText.getText().equalsIgnoreCase("kill bill2")) {
                    activeComponent.setBackground(Color.BLACK);
                    activeComponent.setForeground(Color.PINK);
                }
                if (searcher.config_searchText.getText().equalsIgnoreCase("what is the matrix")) {
                    activeComponent.setBackground(Color.BLACK);
                    activeComponent.setForeground(new Color(40, 250, 120));
                }
                if (searcher.config_searchText.getText().equalsIgnoreCase("killed bill")) {
                    activeComponent.setBackground(Color.WHITE);
                    activeComponent.setForeground(Color.BLACK);
                }
                if (searcher.config_searchText.getText().equalsIgnoreCase("there is no spoon")) {
                    activeComponent.setBackground(Color.WHITE);
                    activeComponent.setForeground(Color.BLACK);
                }
            } else {
                activeComponent.setSelectionEnd(activeComponent.getSelectionStart());
            }
            config.store(searcher);
        }
    }

    private void searchPrevious() {
        JTextComponent activeComponent = getActiveComponent();
        if (null != activeComponent) {
            if (searcher.searchPrevious(activeComponent)) {
                activeComponent.grabFocus();
            } else {
                searcher.config_searchText.grabFocus();
            }
            config.store(searcher);
        }
    }

    private void closeSamuraiPanel(int index) {
        tab.getComponentAt(index).close();
        if (tab.getComponentSize() > 1) {
            tab.removeComponentAt(index);
        }
        setAvailability();
    }

    private void setAvailability() {
        if (tab.getComponentSize() == 1 && tab.getComponentAt(0).isEmpty()) {
            tab.disableClosable();
            menuBar.getMenuItem("menu.file.close").setEnabled(false);
        } else {
            tab.enableClosable(this);
            menuBar.getMenuItem("menu.file.close").setEnabled(true);
        }
        menuBar.getMenuItem("menu.view.reload").setEnabled(!tab.getSelectedComponent().isEmpty());
    }

    /*package*/ void handlePreferences() {
        config.apply(configDialog);
        configDialog.setVisible(true);
    }

    /*package*/ void handleQuit() {
        config.storeRectangle("MainFrame.bounds", this);
        config.storeLocation("ConfigDialog.location", configDialog);
        System.exit(0);
    }

    private void addDropTargetListenerToComponents(DropTargetListener listener, Component component) {
        DropTarget target = new DropTarget(component, DnDConstants.ACTION_REFERENCE, listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addDropTargetListenerToComponents(listener, container.getComponent(i));
            }
        }
    }

    /*package*/ void handleAbout() {
        Dimension dlgSize = aboutSamuraiDialog.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        aboutSamuraiDialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                (frmSize.height - dlgSize.height) / 2 + loc.y);
        aboutSamuraiDialog.setModal(true);
        aboutSamuraiDialog.pack();
        aboutSamuraiDialog.setVisible(true);
    }


    public void keyTyped(KeyEvent e) {
    }

    private void nextTab() {
        int selected = tab.getSelectedIndex();
        selected++;
        if (selected >= tab.getComponentSize()) {
            selected = 0;
        }
        tab.setSelectedIndex(selected);
    }

    private void clearBuffer() {
        SamuraiPanel selected = tab.getSelectedComponent();
        if (!selected.isEmpty()) {
            selected.clearBuffer();
        }

    }

    private void previousTab() {
        int selected = tab.getSelectedIndex();
        selected--;
        if (selected < 0) {
            selected = tab.getComponentSize() - 1;
        }
        tab.setSelectedIndex(selected);
    }

    private long lastPressed = 0;

    public void keyPressed(KeyEvent e) {
        //@todo reloadAlt / searchNextalt / searchPreviousAlt actions have to be handled globally
        //handle reload request
        if (keyStroke.isPressed("logPanel.reloadAlt", e)) {
            e.consume();
            SamuraiPanel samuraiPanel = tab.getSelectedComponent();
            if (!samuraiPanel.isEmpty()) {
                samuraiPanel.reload();
            }
            return;
        }
        if (keyStroke.isPressed("menu.edit.searchNextAlt", e)) {
            //search next
            searchNext();
            e.consume();
            return;
        }
        if (keyStroke.isPressed("menu.edit.searchPreviousAlt", e)) {
            //search previous
            searchPrevious();
            e.consume();
            return;
        }
        if (keyStroke.isPressed("menu.view.previous", e)) {
            previousTab();
            e.consume();
            return;
        }
        if (keyStroke.isPressed("menu.view.next", e)) {
            nextTab();
            e.consume();
            return;
        }

        if (searcher.isDisplayable() && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == e.getModifiersEx())) {
            switch (e.getKeyChar()) {
                case KeyEvent.VK_ENTER:
                    searchNext();
                    e.consume();
                    break;
                case KeyEvent.VK_ESCAPE:
                    removeSearchPanel();
                    e.consume();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    String beforeCaret = searcher.config_searchText.getText().substring(0, searcher.config_searchText.getSelectionStart());
                    String afterCaret = searcher.config_searchText.getText().substring(searcher.config_searchText.getSelectionEnd());
                    if (null != searcher.config_searchText.getSelectedText()) {
                        searcher.config_searchText.setText(beforeCaret + afterCaret);
                    } else {
                        if (0 < beforeCaret.length()) {
                            searcher.config_searchText.setText(beforeCaret.substring(0, beforeCaret.length() - 1) + afterCaret);
                        }
                    }
                    e.consume();
//          break;
                default:
                    addSearchPanel();
            }
            return;
        }
        Component com = e.getComponent();

        JTextComponent textCom;
        if (com instanceof JTextComponent) {
            textCom = (JTextComponent) com;
        } else {
            textCom = getActiveComponent();
        }
        if (searcher.isDisplayable() && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            switch (e.getKeyChar()) {
                case KeyEvent.VK_ENTER:
                    searchNext();
                    e.consume();
                    break;
                case KeyEvent.VK_ESCAPE:
                    removeSearchPanel();
                    e.consume();
                    break;
                default:
                    searcher.config_searchText.grabFocus();
            }
        } else if (null != textCom) {
            if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
                    && ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == e.getModifiersEx())) {
                switch (e.getKeyChar()) {
                    case KeyEvent.VK_BACK_SPACE:
                        //delete one character from the search text
                        e.consume();
                        if (0 < searcher.config_searchText.getText().length()) {
                            searcher.config_searchText.setText(searcher.config_searchText.getText().substring(0,
                                    searcher.config_searchText.getText().length() - 1));
                        }
                        if (searcher.config_searchText.getText().length() == 0) {
                            context.setStatusBar();
                            textCom.setSelectionEnd(textCom.getSelectionStart());
                        } else {
                            searchNext();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        //clear search text
                        searcher.config_searchText.setText("");
                        textCom.setSelectionEnd(textCom.getSelectionStart());
                        context.setStatusBar();
                        e.consume();
                        break;
                    default:
                        //search text input
                        if (!e.isActionKey() && (char) 32 <= e.getKeyCode()) {
                            e.consume();
                            if ((System.currentTimeMillis() - lastPressed) > 4000) {
                                searcher.config_searchText.setText("");
                            }
                            lastPressed = System.currentTimeMillis();
                            searcher.config_searchText.setText(searcher.config_searchText.getText() + e.getKeyChar());
                            searchNext();
                        }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    private void setSelectedEncoding(String encoding) {
        if (null != selectedEncoding) {
            selectedEncoding.setSelected(false);
        }
        tab.getSelectedComponent().setEncoding(encoding);

        JMenu menuViewEncoding = menuBar.getMenu("menu.view.encoding");
        for (int i = menuViewEncoding.getItemCount() - 1; i >= 0; i--) {
            Component component = menuViewEncoding.getItem(i);
            if (component instanceof EncodingMenuItem) {
                EncodingMenuItem encodingMenuItem = (EncodingMenuItem) component;
                if (encoding.equals(encodingMenuItem.getEncoding())) {
                    encodingMenuItem.setSelected(true);
                    selectedEncoding = encodingMenuItem;
                    break;
                }
            }
        }
    }

    public void openNewTab() {
        String defaultEncoding = config.getString("defaultEncoding");

        SamuraiPanel samuraiPanel = new SamuraiPanel(this::setIcon, this::setText, context, config, this, defaultEncoding);
        samuraiPanel.setDroptargetListener(new SamuraiDropTargetListener(samuraiPanel));
        tab.addComponent(resources.getMessage("MainFrame.untitled"), samuraiPanel,
                SamuraiPanel.stoppedIcon);
        tab.setSelectedIndex(tab.getComponentSize() - 1);
        setSelectedEncoding(defaultEncoding);
        setAvailability();
    }

    public void filesOpened(File[] files) {
        findEmptyTabOrCreateNew();
        this.tab.getSelectedComponent().openFiles(files);
        setAvailability();
    }

    public void fileOpened(File file) {
        if (!checkOpened(file)) {
            findEmptyTabOrCreateNew();
            this.tab.getSelectedComponent().openFiles(new File[]{file});
        }
        setAvailability();
    }

    final Border border = new LineBorder(SystemColor.textHighlight, 2, true);
    final Border emptyBorder = new EmptyBorder(2, 2, 2, 2);

    private void setDragAccepting() {
        ((JPanel) getContentPane()).setBorder(border);

    }

    private void setDragNotAccepting() {
        ((JPanel) getContentPane()).setBorder(emptyBorder);
    }

    private File[] checkAcceptable(Transferable transfer) {
        try {
            if (transfer
                    .isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
//dropped file(s)
                var filelist = (List<File>) transfer.getTransferData(DataFlavor.
                        javaFileListFlavor);
                File[] files = filelist.toArray(new File[0]);
                for (File file : files) {
                    if (file.isDirectory()) {
                        //
                        return null;
                    }
                }
                return files;
            }
            return null;
        } catch (IOException | UnsupportedFlavorException ex) {
            return null;
        }
    }

    int lastDragging = -1;
    Color defaultBackgroundColor = null;

    private void setSamuraiPanelDragAccepting(int index) {
        if (lastDragging != index) {
            setSamuraiPanelNotDragAccepting();
            tab.getComponentAt(index).setDragAccepting();
            defaultBackgroundColor = tab.getForegroundAt(index);
            tab.setForegroundAt(index, SystemColor.textHighlight);
            lastDragging = index;
        }
    }

    private void setSamuraiPanelNotDragAccepting() {
        if (-1 != lastDragging) {
            tab.getComponentAt(lastDragging).setDragNotAccepting();
            tab.setForegroundAt(lastDragging, defaultBackgroundColor);
            lastDragging = -1;
        }
    }

    private final DropTargetListener tabDropTargetListener = new DropTargetListener() {
        public void dragEnter(DropTargetDragEvent event) {
            int index;
            if (-1 != (index = tab.indexAtLocation(event.getLocation().x, event.getLocation().y))) {
                tab.setSelectedIndex(index);
                setDragNotAccepting();
                setSamuraiPanelDragAccepting(index);
            } else {
                setSamuraiPanelNotDragAccepting();
                setDragAccepting();
            }
        }

        public void dragOver(DropTargetDragEvent event) {
            int index;
            if (-1 != (index = tab.indexAtLocation(event.getLocation().x, event.getLocation().y))) {
                tab.setSelectedIndex(index);
                setDragNotAccepting();
                setSamuraiPanelDragAccepting(index);
            } else {
                setSamuraiPanelNotDragAccepting();
                setDragAccepting();
            }
        }

        public void dropActionChanged(DropTargetDragEvent event) {
        }

        public void dragExit(DropTargetEvent event) {
            setSamuraiPanelNotDragAccepting();
            setDragNotAccepting();
        }

        public void drop(DropTargetDropEvent drop) {
            setDragNotAccepting();
            setSamuraiPanelNotDragAccepting();
            try {
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {
                    int index;
                    if (-1 != (index = tab.indexAtLocation(drop.getLocation().x, drop.getLocation().y))) {
                        if (!(1 == files.length && checkOpened(files[0]))) {
                            fileHistory.addHistory(files);
                            tab.setSelectedIndex(index);
                            tab.getSelectedComponent().openFiles(files);
                        }
                    } else {
                        fileHistory.open(files);
                    }
                    setAvailability();
                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                context.setTemporaryStatus(ex.toString());
            }
        }
    };

    private final DropTargetListener mainFrameDropTargetListener = new DropTargetListener() {
        public void dragEnter(DropTargetDragEvent event) {
            setDragAccepting();
        }

        public void dragOver(DropTargetDragEvent event) {
            setDragAccepting();
        }

        public void dropActionChanged(DropTargetDragEvent event) {
        }

        public void dragExit(DropTargetEvent event) {
            setDragNotAccepting();
        }

        public void drop(DropTargetDropEvent drop) {
            setDragNotAccepting();
            try {
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files;
                if (null != (files = checkAcceptable(transfer))) {
                    fileHistory.open(files);
                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                context.setTemporaryStatus(ex.toString());
            }
        }
    };

    private void findEmptyTabOrCreateNew() {
        if (!this.tab.getSelectedComponent().isEmpty()) {
            //find empty tab
            boolean foundEmptySamuraiPanel = false;
            for (int i = 0; i < tab.getComponentSize(); i++) {
                if (this.tab.getComponentAt(i).isEmpty()) {
                    //found one empty tab, select it
                    this.tab.setSelectedIndex(i);
                    foundEmptySamuraiPanel = true;
                    break;
                }
            }
            //if there isn't create one
            if (!foundEmptySamuraiPanel) {
                openNewTab();
            }
        }
    }

    private boolean checkOpened(File file) {
        boolean foundOpened = false;
        for (int i = 0; i < tab.getComponentSize(); i++) {

            List<File> currentFile = tab.getComponentAt(i).getCurrentFile();
            if (null != currentFile && file.equals(currentFile.get(0))) {
                this.tab.setSelectedIndex(i);
                context.setTemporaryStatus(resources.getMessage("MainFrame.fileAlreadyOpened", file.getAbsolutePath()));
                foundOpened = true;
                break;
            }
        }
        return foundOpened;
    }

    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            handleQuit();
        }
    }

    class SamuraiDropTargetListener implements DropTargetListener {
        final SamuraiPanel samuraiPanel;

        SamuraiDropTargetListener(SamuraiPanel samuraiPanel) {
            this.samuraiPanel = samuraiPanel;
        }

        private int getSamuraiPanelIndex() {
            for (int i = (tab.getComponentSize() - 1); i >= 0; i--) {
                if (samuraiPanel == tab.getComponentAt(i)) {
                    return i;
                }
            }
            return -1;
        }

        public void dragEnter(DropTargetDragEvent event) {
            setSamuraiPanelDragAccepting(getSamuraiPanelIndex());
        }

        public void dragOver(DropTargetDragEvent event) {
            setSamuraiPanelDragAccepting(getSamuraiPanelIndex());
        }

        public void dropActionChanged(DropTargetDragEvent event) {
        }

        public void dragExit(DropTargetEvent event) {
            setSamuraiPanelNotDragAccepting();
        }

        public void drop(DropTargetDropEvent drop) {
            setSamuraiPanelNotDragAccepting();
            try {
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {
                    if (!(1 == files.length && checkOpened(files[0]))) {
                        fileHistory.addHistory(files);
                        samuraiPanel.openFiles(files);
                        setAvailability();
                    }
                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                context.setTemporaryStatus(ex.toString());
            }
        }
    }

    class EncodingMenuItem extends JCheckBoxMenuItem {
        final String encoding;

        EncodingMenuItem(String encoding) {
            super(encoding);
            this.encoding = encoding;
        }

        public String getEncoding() {
            return encoding;
        }

        public String toString() {
            return encoding;
        }
    }

    class EncodingMenuItemActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            EncodingMenuItem item = (EncodingMenuItem) e.getSource();
            item.setSelected(true);
            if (selectedEncoding != item) {
                // selected encoding has been changed
                setSelectedEncoding(item.getEncoding());
            }
        }
    }

    public void setIcon(ImageIcon icon, JComponent component) {
        for (int i = 0; i < tab.getComponentSize(); i++) {
            Component theComponent = tab.getComponentAt(i);
            if (component == theComponent) {
                tab.setIconAt(i, icon);
                break;
            }
        }
    }

    public void setText(String text, JComponent component) {
        for (int i = 0; i < tab.getComponentSize(); i++) {
            Component theComponent = tab.getComponentAt(i);
            if (component == theComponent) {
                tab.setTitleAt(i, text);
                break;
            }
        }
    }

}
