/*
 * Copyright 2003-2012 Yusuke Yamamoto
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
package samurai.swing;

import samurai.util.GUIResourceBundle;
import samurai.util.OSDetector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainFrame extends JFrame implements KeyListener, FileHistoryListener, CloseListener {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private JMenuItem menuEditPreferences = new JMenuItem("menu.edit.preferences");
    public ConfigDialog configDialog;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menuFile = new JMenu("menu.file");
    private JMenuItem menuFileNewTab = new JMenuItem("menu.file.newTab");
    private JMenuItem menuFileOpen;
    private JMenu menuFileRecent;
    private JMenuItem menuFileClose = new JMenuItem("menu.file.close");

    private JMenuItem menuFileExit = new JMenuItem("menu.file.exit");
    private JMenu menuEdit = new JMenu("menu.edit");
    private JMenuItem menuEditCopy = new JMenuItem("menu.edit.copy");
    private JMenuItem menuEditFind = new JMenuItem("menu.edit.find");
    private JMenuItem menuEditFindPrevious = new JMenuItem("menu.edit.findPrevious");
    private JMenuItem menuEditFindNext = new JMenuItem("menu.edit.findNext");

    private JMenu menuView = new JMenu("menu.view");
    private JMenuItem menuViewReload = new JMenuItem("menu.view.reload");
    private JMenuItem menuViewNext = new JMenuItem("menu.view.next");
    private JMenuItem menuViewPrevious = new JMenuItem("menu.view.previous");
    private JMenuItem menuViewStatusBar = new JCheckBoxMenuItem("menu.view.statusBar");
    private JMenu menuViewEncoding = new JMenu("menu.view.encoding");
    private JMenuItem menuViewClearBuffer = new JMenuItem("menu.view.clearBuffer");

    private JMenu menuHelp = new JMenu("menu.help");
    private JMenuItem menuHelpAbout = new JMenuItem("menu.help.about");
    public AboutSamuraiDialog dialog = new AboutSamuraiDialog(this);
    private TileTabPanel<SamuraiPanel> tab = new TileTabPanel<SamuraiPanel>(true){
        protected void selectedIndexChanged(int index){
            setSelectedEncoding(getSelectedComponent().getEncoding());
        }
    };
    private Context context;

    private EncodingMenuItem selectedEncoding;

    JPanel contentPane;
    JPanel southPane = new JPanel();
    JLabel statusBar = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    SearchPanel searcher;
    private boolean searchPanelAdded = false;

    //Construct the frame
    public MainFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        context = new Context(statusBar, this.tab);
        searcher = context.getSearchPanel();
        configDialog = new ConfigDialog(context);
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(400, 450));
        this.setTitle("*MainFrame.title*");
        statusBar.setPreferredSize(new Dimension(3, 14));
        menuFileNewTab
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openNewTab();
                    }
                });

        menuFileOpen = context.getFileHistory().getOpenMenu(this);
        menuFileOpen.setText("menu.file.open");
        context.getFileHistory().setFileHistoryListener(this);
        menuFileRecent = context.getFileHistory().getOpenRecentMenu();
        menuFileRecent.setText("menu.file.openRecent");

        menuFileClose
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeSamuraiPanel(tab.getSelectedIndex());
                    }
                });
        if (!OSDetector.isMac()) {
            menuFileExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleQuit();
                }
            });
        }
        if (!OSDetector.isMac()) {
            menuEditPreferences.setActionCommand("menu.edit.preferences");
            menuEditPreferences.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handlePreferences();
                }
            });
        }

        menuEditCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component component = tab.getSelectedComponent().getSelectedComponent();
                if (component instanceof ClipBoardOperationListener) {
                    ((ClipBoardOperationListener) component).copy();
                }
            }
        });
        menuEditFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSearchPanel();
            }
        });
        menuEditFindNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchNext();
            }
        });
        menuEditFindPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPrevious();
            }
        });
        searcher.btnHide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSearchPanel();
                validate();
            }
        });
        searcher.btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchNext();
            }
        });
        searcher.btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPrevious();
            }
        });
        searcher.config_searchText.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        removeSearchPanel();
                        e.consume();
                        break;
                    default:

                }
            }
        });

        menuViewReload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tab.getSelectedComponent().reload();
            }
        });
        menuViewPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previousTab();
            }
        });
        menuViewNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextTab();
            }
        });
        menuViewClearBuffer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearBuffer();
            }
        });

        menuViewStatusBar.setSelected(true);

        menuViewStatusBar.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (menuViewStatusBar.isSelected()) {
                    southPane.add(statusBar, BorderLayout.SOUTH);
                } else {
                    southPane.remove(statusBar);
                }
                validate();
            }
        });

        menuHelpAbout
                .addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        handleAbout();
                    }
                });

        menuFile.add(menuFileNewTab);
        menuFile.add(menuFileOpen);
        menuFile.add(menuFileRecent);
        menuFile.addSeparator();
        menuFile.add(menuFileClose);
        if (!OSDetector.isMac()) {
            menuFile.addSeparator();
            menuFile.add(menuFileExit);

            menuEdit.add(menuEditPreferences);
        }
        menuEdit.add(menuEditCopy);
        menuEdit.addSeparator();
        menuEdit.add(menuEditFind);
        menuEdit.add(menuEditFindNext);
        menuEdit.add(menuEditFindPrevious);

        menuView.add(menuViewReload);
        menuView.add(menuViewPrevious);
        menuView.add(menuViewNext);
        menuView.addSeparator();
        menuView.add(tab.jMenuViewTab);
        menuView.add(tab.jMenuViewSplitHorizontal);
        menuView.add(tab.jMenuViewSplitVertical);
        menuView.addSeparator();
        menuView.add(menuViewStatusBar);
        menuView.addSeparator();
        menuView.add(menuViewEncoding);
        String[] encodings = context.getConfig().getString("encodings").split(",");
        for(String encoding : encodings){
            if("-".equals(encoding)){
                menuViewEncoding.addSeparator();
            }else{
                EncodingMenuItem item = new EncodingMenuItem(encoding);
                menuViewEncoding.add(item);
                item.addActionListener(new EncodingMenuItemActionListener());
            }
        }

        menuView.addSeparator();
        menuView.add(menuViewClearBuffer);

        if (!OSDetector.isMac()) {
            menuHelp.add(menuHelpAbout);
        }

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuView);
        if (!OSDetector.isMac()) {
            menuBar.add(menuHelp);
        }
        setJMenuBar(menuBar);
        contentPane.add(tab, BorderLayout.CENTER);
        openNewTab();
        contentPane.add(southPane, BorderLayout.SOUTH);
        southPane.setLayout(borderLayout2);
        southPane.add(statusBar, BorderLayout.SOUTH);
        setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("images/samurai64.gif")));

        context.getConfig().applyRectangle("MainFrame.bounds", this);
        context.getConfig().applyLocation("ConfigDialog.location", configDialog);
        context.getKeyStroke().apply(this);
        context.getKeyStroke().apply(tab.popupMenu);
        resources.inject(this);
        if (OSDetector.isMac()) {
            macApp = new MacApplicationWrapper(this);
        }
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

    MacApplicationWrapper macApp = null;

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
                if (searcher.config_searchText.getText().toLowerCase().equals("kill bill")) {
                    activeComponent.setBackground(Color.YELLOW);
                    activeComponent.setForeground(Color.BLACK);
                }
                if (searcher.config_searchText.getText().toLowerCase().equals("kill bill2")) {
                    activeComponent.setBackground(Color.BLACK);
                    activeComponent.setForeground(Color.PINK);
                }
                if (searcher.config_searchText.getText().toLowerCase().equals("what is the matrix")) {
                    activeComponent.setBackground(Color.BLACK);
                    activeComponent.setForeground(new Color(40, 250, 120));
                }
                if (searcher.config_searchText.getText().toLowerCase().equals("killed bill")) {
                    activeComponent.setBackground(Color.WHITE);
                    activeComponent.setForeground(Color.BLACK);
                }
                if (searcher.config_searchText.getText().toLowerCase().equals("there is no spoon")) {
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
        tab.getComponentAt(index).close();
        if (tab.getComponentSize() > 1) {
            tab.removeComponentAt(index);
        }
        setAvailability();
    }

    private void setAvailability() {
        if (tab.getComponentSize() == 1 && tab.getComponentAt(0).isEmpty()) {
            tab.disableClosable();
            menuFileClose.setEnabled(false);
        } else {
            tab.enableClosable(this);
            menuFileClose.setEnabled(true);
        }
        menuViewReload.setEnabled(!tab.getSelectedComponent().isEmpty());
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
        Dimension dlgSize = dialog.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dialog.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                (frmSize.height - dlgSize.height) / 2 + loc.y);
        dialog.setModal(true);
        dialog.pack();
        dialog.setVisible(true);

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
        if(!selected.isEmpty()){
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
        if (context.getKeyStroke().isPressed("logPanel.reloadAlt", e)) {
            e.consume();
            SamuraiPanel samuraiPanel = tab.getSelectedComponent();
            if (!samuraiPanel.isEmpty()) {
                samuraiPanel.reload();
            }
            return;
        }
        if (context.getKeyStroke().isPressed("menu.edit.searchNextAlt", e)) {
            //search next
            searchNext();
            e.consume();
            return;
        }
        if (context.getKeyStroke().isPressed("menu.edit.searchPreviousAlt", e)) {
            //search previous
            searchPrevious();
            e.consume();
            return;
        }
        if (context.getKeyStroke().isPressed("menu.view.previous", e)) {
            previousTab();
            e.consume();
            return;
        }
        if (context.getKeyStroke().isPressed("menu.view.next", e)) {
            nextTab();
            e.consume();
            return;
        }

        if (searcher.isDisplayable() && e.getKeyChar() != e.CHAR_UNDEFINED && ((e.getModifiers() & e.SHIFT_DOWN_MASK) == e.getModifiers())) {
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
                    String afterCaret = searcher.config_searchText.getText().substring(searcher.config_searchText.getSelectionEnd(), searcher.config_searchText.getText().length());
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
        if (searcher.isDisplayable() && e.getKeyChar() != e.CHAR_UNDEFINED) {
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
            if (e.getKeyChar() != e.CHAR_UNDEFINED && ((e.getModifiers() & e.SHIFT_MASK) == e.getModifiers())) {
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

    JFrame THIS = this;
    Border border = new LineBorder(SystemColor.textHighlight, 2, true);
    Border emptyBorder = new EmptyBorder(2, 2, 2, 2);

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
                List filelist = (List) transfer.getTransferData(DataFlavor.
                        javaFileListFlavor);
                File[] files = (File[]) filelist.toArray(new File[0]);
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        //
                        return null;
                    }
                }
                return files;
            }
            return null;
        } catch (IOException ex) {
            return null;
        } catch (UnsupportedFlavorException ex) {
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

    private DropTargetListener tabDropTargetListener = new DropTargetListener() {
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
            System.out.println("changed");
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
                            context.getFileHistory().addHistory(files);
                            tab.setSelectedIndex(index);
                            tab.getSelectedComponent().openFiles(files);
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

    private DropTargetListener mainFrameDropTargetListener = new DropTargetListener() {
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
        SamuraiPanel samuraiPanel;

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

//    static GUIResourceBundle resource = GUIResourceBundle.getInstance("encoding-display-names");
    class EncodingMenuItem extends JCheckBoxMenuItem {
        String encoding;

        EncodingMenuItem(String encoding) {
            super(encoding);
            this.encoding = encoding;
        }
        public String getEncoding(){
            return encoding;
        }
        public String toString(){
           return encoding;
        }
    }
    class EncodingMenuItemActionListener implements ActionListener{
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
