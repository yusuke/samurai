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
package one.cafebabe.samurai.swing;

import one.cafebabe.samurai.util.GUIResourceBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigDialog extends JDialog {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    public final JButton okButton = new JButton(resources.getMessage("ConfigDialog.ok"));
    public final JButton cancelButton = new JButton(resources.getMessage("ConfigDialog.cancel"));
    public final JTabbedPane jTabbedPane1 = new JTabbedPane();

    final GridBagLayout gridBagLayout1 = new GridBagLayout();

    final JPanel logConfigPanel = new JPanel();
    final GridBagLayout logConfigLayout = new GridBagLayout();
    public final JLabel labelFontSize = new JLabel(resources.getMessage("ConfigDialog.fontSize"));
    public final JLabel dumpFontSize = new JLabel(resources.getMessage("ConfigDialog.fontSize"));
    public final JLabel logFontSample = new JLabel(resources.getMessage("ConfigDialog.fontSample"));
    public final JLabel encoding = new JLabel(resources.getMessage("ConfigDialog.encoding"));
    public final JCheckBox config_wrapLog = new JCheckBox(resources.getMessage("ConfigDialog.wrapLine"));
    public final JComboBox<String> config_logFontFamily = new JComboBox<>();
    public final JComboBox<String> config_logFontSize = new JComboBox<>();
    public final JComboBox<String> config_encoding = new JComboBox<>();

    final JPanel dumpConfigPanel = new JPanel();
    public final JLabel dumpFontSample = new JLabel(resources.getMessage("ConfigDialog.fontSample"));
    final GridBagLayout dumpConfigLayout = new GridBagLayout();
    public final JCheckBox config_wrapDump = new JCheckBox(resources.getMessage("ConfigDialog.wrapLine"));
    public final JComboBox<String> config_dumpFontFamily = new JComboBox<>();
    public final JComboBox<String> config_dumpFontSize = new JComboBox<>();

    public final JLabel labelDumpPanelFontFamily = new JLabel(resources.getMessage("ConfigDialog.fontFamily"));
    public final JCheckBox config_shrinkIdleThreads = new JCheckBox(resources.getMessage("ConfigDialog.shrinkIdleThreads"));
    public final JLabel labelFontFamily = new JLabel(resources.getMessage("ConfigDialog.fontFamily"));
    
    public ConfigDialog(Frame frame, String title, boolean modal, Context context) {
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
        this(null, resources.getMessage("ConfigDialog.title"), false, context);
    }

    final ConfigDialog THIS = this;

    private void jbInit() {
        //panel root
        this.getContentPane().setLayout(gridBagLayout1);
        jTabbedPane1.setForeground(Color.black);
        jTabbedPane1.setTabPlacement(JTabbedPane.TOP);
        labelFontFamily.setRequestFocusEnabled(true);
        this.getContentPane().add(okButton, new GridBagConstraints(1, 1, 1, 1, 1.0,
                0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5),
                0, 0));
        this.getContentPane().add(cancelButton, new GridBagConstraints(0, 1, 1, 1,
                20.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0),
                0, 0));
        this.getContentPane().add(jTabbedPane1, new GridBagConstraints(0, 0, 2, 1,
                0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5,
                5), 0, 0));
        this.setModal(true);
        this.setResizable(false);
        this.addWindowListener(new ConfigDialog_this_windowAdapter(this));
        okButton.setDefaultCapable(false);
        okButton.setActionCommand("okButton");
        okButton.addActionListener(e -> {
            context.getConfig().store(THIS);
            context.getConfig().notifyChange();
            setVisible(false);
        });

        cancelButton.setActionCommand("cancelButton");
        cancelButton.addActionListener(e -> setVisible(false));
        jTabbedPane1.setMinimumSize(new Dimension(400, 200));
        jTabbedPane1.setOpaque(false);
        jTabbedPane1.setPreferredSize(new Dimension(400, 200));

        //dump panel
        config_wrapDump.setSelected(false);
        dumpConfigPanel.setLayout(dumpConfigLayout);

        config_dumpFontSize.setSelectedIndex(-1);
        config_dumpFontSize.addItemListener(new
                ConfigDialog_config_dumpFontSize_itemAdapter(this));
        dumpConfigPanel.setMinimumSize(new Dimension(400, 100));
        dumpConfigPanel.setOpaque(true);
        dumpConfigPanel.setPreferredSize(new Dimension(400, 100));
        dumpConfigPanel.setRequestFocusEnabled(true);
        config_dumpFontFamily.addItemListener(new
                ConfigDialog_config_dumpFontFamily_itemAdapter(this));
        dumpConfigPanel.add(config_dumpFontFamily, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(labelDumpPanelFontFamily, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        dumpConfigPanel.add(dumpFontSize, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(config_dumpFontSize, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
        dumpConfigPanel.add(dumpFontSample, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String font1 : fonts) {
            config_dumpFontFamily.addItem(font1);
        }
        String[] fontSizes = {"6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
        for (String fontSize : fontSizes) {
            config_dumpFontSize.addItem(fontSize);
        }

        logConfigPanel.setLayout(logConfigLayout);


        config_logFontSize.setSelectedIndex(-1);
        config_logFontSize.addItemListener(new
                ConfigDialog_config_logFontSize_itemAdapter(this));

        config_encoding.setSelectedIndex(-1);

        logConfigPanel.setMinimumSize(new Dimension(400, 100));
        logConfigPanel.setOpaque(true);
        logConfigPanel.setPreferredSize(new Dimension(400, 100));
        logConfigPanel.setRequestFocusEnabled(true);
        config_logFontFamily.addItemListener(new
                ConfigDialog_config_logFontSize_itemAdapter(this));
        logConfigPanel.add(config_wrapLog, new GridBagConstraints(1, 0, 1, 1, 0.0,
                0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
                0, 0));

        logConfigPanel.add(config_logFontFamily, new GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(labelFontSize, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(config_logFontSize, new GridBagConstraints(1, 2, 1, 1,
                0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 0),
                0, 0));
        logConfigPanel.add(logFontSample, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5,
                0), 0, 0));
        logConfigPanel.add(labelFontFamily, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        for (String font1 : fonts) {
            config_logFontFamily.addItem(font1);
        }
        for (String fontSize : fontSizes) {
            config_logFontSize.addItem(fontSize);
        }

        jTabbedPane1.add(resources.getMessage("ConfigDialog.log"), logConfigPanel);
        jTabbedPane1.add(resources.getMessage("ConfigDialog.threadDump"), dumpConfigPanel);
        dumpConfigPanel.add(config_shrinkIdleThreads,
                new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
                        , GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0));
        getRootPane().setDefaultButton(this.okButton);
        getRootPane().getActionMap().put(
                ESC_ACTION_KEY,
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });

        getRootPane().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                ESC_ACTION_KEY
        );
    }

    static final String ESC_ACTION_KEY = "ESC_ACTION_KEY";


    void this_windowActivated(WindowEvent e) {
//    this.cancelButton.grabFocus();

    }

    void config_logFontSize_itemStateChanged(ItemEvent e) {
        setLogSampleFont();
    }

    void config_dumpFontSize_itemStateChanged(ItemEvent e) {
        setDumpSampleFont();
    }

    private void setLogSampleFont() {
        if (-1 != config_logFontSize.getSelectedIndex() &&
                -1 != config_logFontFamily.getSelectedIndex()) {
            logFontSample.setFont(new Font((String) config_logFontFamily.
                    getSelectedItem(), Font.PLAIN,
                    Integer.parseInt((String) config_logFontSize.getSelectedItem())));
        }
    }

    private void setDumpSampleFont() {
        if (-1 != config_dumpFontSize.getSelectedIndex() &&
                -1 != config_dumpFontFamily.getSelectedIndex()) {
            dumpFontSample.setFont(new Font((String) config_dumpFontFamily.
                    getSelectedItem(), 0,
                    Integer.parseInt((String) config_dumpFontSize.getSelectedItem())));
        }
    }

    void config_logFontFamily_itemStateChanged(ItemEvent e) {
        setLogSampleFont();

    }

}

class ConfigDialog_this_windowAdapter extends WindowAdapter {
    final ConfigDialog adaptee;

    ConfigDialog_this_windowAdapter(ConfigDialog adaptee) {
        this.adaptee = adaptee;
    }

    public void windowActivated(WindowEvent e) {
        adaptee.this_windowActivated(e);
    }
}