/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

public class ConfigDialog extends javax.swing.JDialog {
//    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    public javax.swing.JButton okButton = new javax.swing.JButton();
    public javax.swing.JButton cancelButton = new javax.swing.JButton();
    public javax.swing.JTabbedPane jTabbedPane1 = new javax.swing.JTabbedPane();

    java.awt.GridBagLayout gridBagLayout1 = new java.awt.GridBagLayout();

    javax.swing.JPanel logConfigPanel = new javax.swing.JPanel();
    java.awt.GridBagLayout logConfigLayout = new java.awt.GridBagLayout();
    public javax.swing.JLabel labelFontSize = new javax.swing.JLabel();
    public javax.swing.JLabel dumpFontSize = new javax.swing.JLabel();
    public javax.swing.JLabel logFontSample = new javax.swing.JLabel();
    public javax.swing.JLabel encoding = new javax.swing.JLabel();
    public javax.swing.JCheckBox config_wrapLog = new javax.swing.JCheckBox();
    public javax.swing.JComboBox config_logFontFamily = new javax.swing.JComboBox();
    public javax.swing.JComboBox config_logFontSize = new javax.swing.JComboBox();
    public javax.swing.JComboBox config_encoding = new javax.swing.JComboBox();

    javax.swing.JPanel dumpConfigPanel = new javax.swing.JPanel();
    public javax.swing.JLabel dumpFontSample = new javax.swing.JLabel();
    java.awt.GridBagLayout dumpConfigLayout = new java.awt.GridBagLayout();
    public javax.swing.JCheckBox config_wrapDump = new javax.swing.JCheckBox();
    public javax.swing.JComboBox config_dumpFontFamily = new javax.swing.JComboBox();
    public javax.swing.JComboBox config_dumpFontSize = new javax.swing.JComboBox();

//  public JPanel searchConfigPanel = new JPanel();
//  public JCheckBox config_useRegexp = new JCheckBox();
    //  public JCheckBox config_ignoreCase = new JCheckBox();
    public javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    java.awt.GridBagLayout gridBagLayout2 = new java.awt.GridBagLayout();
    //  JPanel jPanel1 = new JPanel();
    java.awt.GridBagLayout gridBagLayout3 = new java.awt.GridBagLayout();
    public javax.swing.JCheckBox config_shrinkIdleThreads = new javax.swing.JCheckBox();
    public javax.swing.JLabel labelFontFamily = new javax.swing.JLabel();
    javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();

    public ConfigDialog(java.awt.Frame frame, String title, boolean modal, Context context) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.context = context;
    }

    private final Context context;

    public ConfigDialog(Context context) {
        this(null, "*ConfigDialog.title*", false, context);
    }

    ConfigDialog THIS = this;

    private void jbInit() throws Exception {
        //panel root
        this.getContentPane().setLayout(gridBagLayout1);
        config_wrapLog.setText("*ConfigDialog.wrapLine*");
        jLabel1.setText("*ConfigDialog.fontFamily*");
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.TOP);
//    jPanel1.setLayout(gridBagLayout3);
        config_shrinkIdleThreads.setText("*ConfigDialog.shrinkIdleThreads*");
//        labelFontFamily.setText("*ConfigDialog.fontFamily*");
        labelFontFamily.setRequestFocusEnabled(true);
        labelFontFamily.setText("*ConfigDialog.fontFamily*");
        this.getContentPane().add(okButton, new java.awt.GridBagConstraints(1, 1, 1, 1, 1.0,
                0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 0, 5, 5),
                0, 0));
        this.getContentPane().add(cancelButton, new java.awt.GridBagConstraints(0, 1, 1, 1,
                20.0, 0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(5, 5, 5, 0),
                0, 0));
        this.getContentPane().add(jTabbedPane1, new java.awt.GridBagConstraints(0, 0, 2, 1,
                0.0, 0.0
                , java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(5, 5, 5,
                5), 0, 0));
//    config_useRegexp.setText("*ConfigDialog.useRegexp*");
        this.setModal(true);
        this.setResizable(false);
        this.addWindowListener(new ConfigDialog_this_windowAdapter(this));
        okButton.setDefaultCapable(false);
        okButton.setActionCommand("okButton");
        okButton.setText("*ConfigDialog.ok*");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                context.getConfig().store(THIS);
                context.getConfig().notifyChange();
                setVisible(false);
            }
        });

        cancelButton.setActionCommand("cancelButton");
//    cancelButton.setSelected(true);
        cancelButton.setText("*ConfigDialog.cancel*");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                setVisible(false);
            }
        });
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(400, 200));
        jTabbedPane1.setOpaque(false);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(400, 200));
//    config_ignoreCase.setText("*ConfigDialog.ignoreCase*");

        //dump panel
        config_wrapDump.setActionCommand("*ConfigDialog.wrapLine*");
        config_wrapDump.setSelected(false);
        config_wrapDump.setText("*ConfigDialog.wrapLine*");
        dumpConfigPanel.setLayout(dumpConfigLayout);
        dumpFontSize.setText("*ConfigDialog.fontSize*");
        dumpFontSample.setText("*ConfigDialog.fontSample*");

        config_dumpFontSize.setSelectedIndex(-1);
        config_dumpFontSize.addItemListener(new
                ConfigDialog_config_dumpFontSize_itemAdapter(this));
        dumpConfigPanel.setMinimumSize(new java.awt.Dimension(400, 100));
        dumpConfigPanel.setOpaque(true);
        dumpConfigPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        dumpConfigPanel.setRequestFocusEnabled(true);
        config_dumpFontFamily.addItemListener(new
                ConfigDialog_config_dumpFontFamily_itemAdapter(this));
        dumpConfigPanel.add(config_dumpFontFamily, new java.awt.GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(jLabel1, new java.awt.GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
        dumpConfigPanel.add(dumpFontSize, new java.awt.GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(config_dumpFontSize, new java.awt.GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(dumpFontSample, new java.awt.GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0), 0, 0));
        String[] fonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String font1 : fonts) {
            config_dumpFontFamily.addItem(font1);
        }
        String[] fontSizes = {"6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
        for (String fontSize : fontSizes) {
            config_dumpFontSize.addItem(fontSize);
        }

        logConfigPanel.setLayout(logConfigLayout);

        labelFontSize.setText("*ConfigDialog.fontSize*");

        logFontSample.setText("*ConfigDialog.fontSample*");
        config_logFontSize.setSelectedIndex(-1);
        config_logFontSize.addItemListener(new
                ConfigDialog_config_logFontSize_itemAdapter(this));

        encoding.setText("*ConfigDialog.encoding*");
        config_encoding.setSelectedIndex(-1);

        logConfigPanel.setMinimumSize(new java.awt.Dimension(400, 100));
        logConfigPanel.setOpaque(true);
        logConfigPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        logConfigPanel.setRequestFocusEnabled(true);
        config_logFontFamily.addItemListener(new
                ConfigDialog_config_logFontSize_itemAdapter(this));
        logConfigPanel.add(config_wrapLog, new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0,
                0.0
                , java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0),
                0, 0));

        logConfigPanel.add(config_logFontFamily, new java.awt.GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0
                , java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(labelFontSize, new java.awt.GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(config_logFontSize, new java.awt.GridBagConstraints(1, 2, 1, 1,
                0.0, 0.0
                , java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(logFontSample, new java.awt.GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 5,
                0), 0, 0));
        logConfigPanel.add(labelFontFamily, new java.awt.GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , java.awt.GridBagConstraints.EAST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
//        String[] fonts = getToolkit().getFontList();
        for (String font1 : fonts) {
            config_logFontFamily.addItem(font1);
        }
        for (String fontSize : fontSizes) {
            config_logFontSize.addItem(fontSize);
        }

//    searchConfigPanel.add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
//      , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//    jPanel1.add(config_useRegexp, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//      , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
//    jPanel1.add(config_ignoreCase, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
//      , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//    searchConfigPanel.setLayout(gridBagLayout2);
        jTabbedPane1.add("*ConfigDialog.log*", logConfigPanel);
//    jTabbedPane1.add("*ConfigDialog.search*", searchConfigPanel);
        jTabbedPane1.add("*ConfigDialog.threadDump*", dumpConfigPanel);
        dumpConfigPanel.add(config_shrinkIdleThreads,
                new java.awt.GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
                        , java.awt.GridBagConstraints.WEST,
                        java.awt.GridBagConstraints.NONE,
                        new java.awt.Insets(0, 0, 0, 0), 0, 0));
        getRootPane().setDefaultButton(this.okButton);
        getRootPane().getActionMap().put(
                ESC_ACTION_KEY,
                new javax.swing.AbstractAction() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        setVisible(false);
                    }
                });

        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                ESC_ACTION_KEY
        );
    }

    static final String ESC_ACTION_KEY = "ESC_ACTION_KEY";


    void this_windowActivated(java.awt.event.WindowEvent e) {
//    this.cancelButton.grabFocus();

    }

    void config_logFontSize_itemStateChanged(java.awt.event.ItemEvent e) {
        setLogSampleFont();
    }

    void config_dumpFontSize_itemStateChanged(java.awt.event.ItemEvent e) {
        setDumpSampleFont();
    }

    private void setLogSampleFont() {
        if (-1 != config_logFontSize.getSelectedIndex() &&
                -1 != config_logFontFamily.getSelectedIndex()) {
            logFontSample.setFont(new java.awt.Font((String) config_logFontFamily.
                    getSelectedItem(), 0,
                    Integer.parseInt((String) config_logFontSize.getSelectedItem())));
        }
    }

    private void setDumpSampleFont() {
        if (-1 != config_dumpFontSize.getSelectedIndex() &&
                -1 != config_dumpFontFamily.getSelectedIndex()) {
            dumpFontSample.setFont(new java.awt.Font((String) config_dumpFontFamily.
                    getSelectedItem(), 0,
                    Integer.parseInt((String) config_dumpFontSize.getSelectedItem())));
        }
    }

    void config_logFontFamily_itemStateChanged(java.awt.event.ItemEvent e) {
        setLogSampleFont();

    }

}

class ConfigDialog_this_windowAdapter extends java.awt.event.WindowAdapter {
    ConfigDialog adaptee;

    ConfigDialog_this_windowAdapter(ConfigDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void windowActivated(java.awt.event.WindowEvent e) {
        adaptee.this_windowActivated(e);
    }
}