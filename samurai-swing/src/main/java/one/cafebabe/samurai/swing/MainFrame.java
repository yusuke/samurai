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

public class MainFrame extends JFrame implements KeyListener, FileHistoryListener, CloseListener {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private static final CustomizableKeyStroke keyStroke = new CustomizableKeyStroke(resources);

    public final AboutSamuraiDialog aboutSamuraiDialog = new AboutSamuraiDialog(this);
    final JLabel statusBar = new JLabel();
    private final Context context = new Context(statusBar);
    public final ConfigDialog configDialog = new ConfigDialog(context);

    private EncodingMenuItem selectedEncoding;

    final JPanel contentPane;
    final JPanel southPane = new JPanel();
    final BorderLayout borderLayout1 = new BorderLayout();
    final BorderLayout borderLayout2 = new BorderLayout();
    final SearchPanel searcher;
    private boolean searchPanelAdded = false;
    final MenuBar menuBar  = MenuBar.newBuilder()
            .addMenu("menu.file",
                    fileMenu -> fileMenu.addMenuItem("menu.file.newTab", e -> openNewTab())
                            .addMenuItem("menu.file.open", context.getFileHistory()::menuOpen)
                            .addMenu("menu.file.openRecent", e -> {
                                context.getFileHistory().openRecentMenu = e;
                                context.getFileHistory().updateChildMenuItems();
                            })
                            .addMenu("menu.file.takeThreadDumpFrom", e -> new TakeThreadDump(context.getConfig(), context.getFileHistory(), e))
                            .addMenu("menu.file.viewGcLogFrom", e -> new ViewGcLog(context.getConfig(), context.getFileHistory(), e))
                            .addSeparator()
                            .addMenuItem("menu.file.close", e -> closeSamuraiPanel(context.tab.getSelectedIndex()))
                            .addMenuItemIfWin("menu.file.exit", e -> handleQuit()))
            .addMenu("menu.edit",
                    editMenu -> editMenu.addMenuItemIfWin("menu.edit.preferences", e -> handlePreferences())
                            .addMenuItem("menu.edit.copy", e -> {
                                Component component = context.tab.getSelectedComponent().getSelectedComponent();
                                if (component instanceof ClipBoardOperationListener) {
                                    ((ClipBoardOperationListener) component).copy();
                                }
                            })
                            .addSeparator()
                            .addMenuItem("menu.edit.find", e -> addSearchPanel())
                            .addMenuItem("menu.edit.findNext", e -> searchNext())
                            .addMenuItem("menu.edit.findPrevious", e -> searchPrevious()))
            .addMenu("menu.view",
                    viewMenu -> viewMenu.addMenuItem("menu.view.reload", e -> context.tab.getSelectedComponent().reload())
                            .addMenuItem("menu.view.previous", e -> previousTab())
                            .addMenuItem("menu.view.next", e -> nextTab())
                            .addSeparator()
                            .addMenuItem("TileTabPanel.tab", e -> {
                            })
                            .addMenuItem("TileTabPanel.splitHorizontal", e -> {
                            })
                            .addMenuItem("TileTabPanel.splitVertical", e -> {
                            })
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
                                String[] encodings = context.getConfig().getString("encodings").split(",");
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

    //Construct the frame
    public MainFrame() {
        super(resources.getMessage("MainFrame.title"));
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        searcher = context.getSearchPanel();
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(400, 450));
        statusBar.setPreferredSize(new Dimension(3, 14));

        context.getFileHistory().setFileHistoryListener(this);

        menuBar.getCheckBoxMenuItem("menu.view.statusBar").setSelected(true);

        setJMenuBar(this.menuBar.menuBar());

        context.tab = new TileTabPanel<>(true, MainFrame.this.menuBar.getMenuItem("TileTabPanel.tab"),
                MainFrame.this.menuBar.getMenuItem("TileTabPanel.splitHorizontal"),
                MainFrame.this.menuBar.getMenuItem("TileTabPanel.splitVertical")) {
            protected void selectedIndexChanged(int index) {
                setSelectedEncoding(getSelectedComponent().getEncoding());
            }
        };

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



        contentPane.add(context.tab, BorderLayout.CENTER);
        openNewTab();
        contentPane.add(southPane, BorderLayout.SOUTH);
        southPane.setLayout(borderLayout2);
        southPane.add(statusBar, BorderLayout.SOUTH);
        if (OSDetector.isWindows()) {
            setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("images/samurai.png")));
        }

        context.getConfig().applyRectangle("MainFrame.bounds", this);
        context.getConfig().applyLocation("ConfigDialog.location", configDialog);
        keyStroke.apply(this);
        keyStroke.apply(context.tab.popupMenu);
        if (OSDetector.isMac()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler(e -> this.handleAbout());
            desktop.setPreferencesHandler(e -> this.handlePreferences());
            desktop.setQuitHandler((e1, e2) -> this.handleQuit());
        }
        DropTarget target = new DropTarget(this,
                DnDConstants.ACTION_REFERENCE,
                mainFrameDropTargetListener
        );
        DropTarget target2 = new DropTarget(context.tab,
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
        Component activeComponent = context.tab.getSelectedComponent().tab.getSelectedComponent();
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
            context.getConfig().store(searcher);
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
            context.getConfig().store(searcher);
        }
    }

    private void closeSamuraiPanel(int index) {
        context.tab.getComponentAt(index).close();
        if (context.tab.getComponentSize() > 1) {
            context.tab.removeComponentAt(index);
        }
        setAvailability();
    }

    private void setAvailability() {
        if (context.tab.getComponentSize() == 1 && context.tab.getComponentAt(0).isEmpty()) {
            context.tab.disableClosable();
            menuBar.getMenuItem("menu.file.close").setEnabled(false);
        } else {
            context.tab.enableClosable(this);
            menuBar.getMenuItem("menu.file.close").setEnabled(true);
        }
        menuBar.getMenuItem("menu.view.reload").setEnabled(!context.tab.getSelectedComponent().isEmpty());
    }

    /*package*/ void handlePreferences() {
        context.getConfig().apply(configDialog);
        configDialog.setVisible(true);
    }

    /*package*/ void handleQuit() {
        context.getConfig().storeRectangle("MainFrame.bounds", this);
        context.getConfig().storeLocation("ConfigDialog.location", configDialog);
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
        int selected = context.tab.getSelectedIndex();
        selected++;
        if (selected >= context.tab.getComponentSize()) {
            selected = 0;
        }
        context.tab.setSelectedIndex(selected);
    }

    private void clearBuffer() {
        SamuraiPanel selected = context.tab.getSelectedComponent();
        if (!selected.isEmpty()) {
            selected.clearBuffer();
        }

    }

    private void previousTab() {
        int selected = context.tab.getSelectedIndex();
        selected--;
        if (selected < 0) {
            selected = context.tab.getComponentSize() - 1;
        }
        context.tab.setSelectedIndex(selected);
    }

    private long lastPressed = 0;

    public void keyPressed(KeyEvent e) {
        //@todo reloadAlt / searchNextalt / searchPreviousAlt actions have to be handled globally
        //handle reload request
        if (keyStroke.isPressed("logPanel.reloadAlt", e)) {
            e.consume();
            SamuraiPanel samuraiPanel = context.tab.getSelectedComponent();
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
        context.tab.getSelectedComponent().setEncoding(encoding);

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
        String defaultEncoding = context.getConfig().getString("defaultEncoding");

        SamuraiPanel samuraiPanel = new SamuraiPanel(context, this, defaultEncoding);
        samuraiPanel.setDroptargetListener(new SamuraiDropTargetListener(samuraiPanel));
        context.tab.addComponent(resources.getMessage("MainFrame.untitled"), samuraiPanel,
                SamuraiPanel.stoppedIcon);
        context.tab.setSelectedIndex(context.tab.getComponentSize() - 1);
        setSelectedEncoding(defaultEncoding);
        setAvailability();
    }

    public void filesOpened(File[] files) {
        findEmptyTabOrCreateNew();
        this.context.tab.getSelectedComponent().openFiles(files);
        setAvailability();
    }

    public void fileOpened(File file) {
        if (!checkOpened(file)) {
            findEmptyTabOrCreateNew();
            this.context.tab.getSelectedComponent().openFiles(new File[]{file});
        }
        setAvailability();
    }

    final Border border = new LineBorder(SystemColor.textHighlight, 2, true);
    final Border emptyBorder = new EmptyBorder(2, 2, 2, 2);

    private void setDragAccepting() {
        contentPane.setBorder(border);

    }

    private void setDragNotAccepting() {
        contentPane.setBorder(emptyBorder);
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
            context.tab.getComponentAt(index).setDragAccepting();
            defaultBackgroundColor = context.tab.getForegroundAt(index);
            context.tab.setForegroundAt(index, SystemColor.textHighlight);
            lastDragging = index;
        }
    }

    private void setSamuraiPanelNotDragAccepting() {
        if (-1 != lastDragging) {
            context.tab.getComponentAt(lastDragging).setDragNotAccepting();
            context.tab.setForegroundAt(lastDragging, defaultBackgroundColor);
            lastDragging = -1;
        }
    }

    private final DropTargetListener tabDropTargetListener = new DropTargetListener() {
        public void dragEnter(DropTargetDragEvent event) {
            int index;
            if (-1 != (index = context.tab.indexAtLocation(event.getLocation().x, event.getLocation().y))) {
                context.tab.setSelectedIndex(index);
                setDragNotAccepting();
                setSamuraiPanelDragAccepting(index);
            } else {
                setSamuraiPanelNotDragAccepting();
                setDragAccepting();
            }
        }

        public void dragOver(DropTargetDragEvent event) {
            int index;
            if (-1 != (index = context.tab.indexAtLocation(event.getLocation().x, event.getLocation().y))) {
                context.tab.setSelectedIndex(index);
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
                    if (-1 != (index = context.tab.indexAtLocation(drop.getLocation().x, drop.getLocation().y))) {
                        if (!(1 == files.length && checkOpened(files[0]))) {
                            context.getFileHistory().addHistory(files);
                            context.tab.setSelectedIndex(index);
                            context.tab.getSelectedComponent().openFiles(files);
                        }
                    } else {
                        context.getFileHistory().open(files);
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
                    context.getFileHistory().open(files);
                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                context.setTemporaryStatus(ex.toString());
            }
        }
    };

    private void findEmptyTabOrCreateNew() {
        if (!this.context.tab.getSelectedComponent().isEmpty()) {
            //find empty tab
            boolean foundEmptySamuraiPanel = false;
            for (int i = 0; i < context.tab.getComponentSize(); i++) {
                if (this.context.tab.getComponentAt(i).isEmpty()) {
                    //found one empty tab, select it
                    this.context.tab.setSelectedIndex(i);
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
        for (int i = 0; i < context.tab.getComponentSize(); i++) {

            List<File> currentFile = context.tab.getComponentAt(i).getCurrentFile();
            if (null != currentFile && file.equals(currentFile.get(0))) {
                this.context.tab.setSelectedIndex(i);
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
            for (int i = (context.tab.getComponentSize() - 1); i >= 0; i--) {
                if (samuraiPanel == context.tab.getComponentAt(i)) {
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
                        context.getFileHistory().addHistory(files);
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
}
