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
import samurai.util.StringUtil;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class SearchPanel extends JPanel {
    public final JTextField config_searchText = new JTextField();
    public final JCheckBox config_matchCase = new JCheckBox("*SearchPanel.matchCase*");
    public final JLabel find = new JLabel("*SearchPanel.find*");
    public boolean useRegexp = false;
    public final JButton btnPrevious = new JButton("*SearchPanel.previous*");
    public final JButton btnNext = new JButton("*SearchPanel.next*");
    public final JButton btnHide = new JButton();

    public SearchPanel() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void storeConfig() {
        context.getConfig().store(this);
    }

    FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

    private void jbInit() throws Exception {
        config_searchText.setPreferredSize(new Dimension(150, 20));
        config_searchText.setMaximumSize(new Dimension(1500, 20));
        config_searchText.addFocusListener(new FocusListener() {
                                               public void focusLost(FocusEvent e) {
                                                   JRootPane rootPane = getRootPane();
                                                   if (null != rootPane) {
                                                       rootPane.setDefaultButton(null);
                                                   }
                                               }

                                               public void focusGained(FocusEvent e) {
                                                   JRootPane rootPane = getRootPane();
                                                   if (null != rootPane) {
                                                       rootPane.setDefaultButton(btnNext);
                                                   }
                                               }
                                           }
        );
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
        btnHide.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnHide.setPreferredSize(new Dimension(24, 16));
        btnHide.setIcon(closeIcon);
        btnHide.setPressedIcon(closePushedIcon);
        btnHide.setRolloverIcon(closeHoverIcon);
        btnHide.setRolloverEnabled(true);
        this.setPreferredSize(new Dimension(429, 30));
        btnNext.setDefaultCapable(true);
        config_matchCase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeConfig();
            }
        });
        this.add(btnHide);
        this.add(find);
        this.add(config_searchText);
        this.add(btnPrevious);
        this.add(btnNext);
        this.add(config_matchCase);
    }

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();

    private Context context;

    public SearchPanel(Context context) {
        this();
        this.context = context;
        this.context.getConfig().apply(this);
        context.getConfig().addTarget(this);
    }

    public boolean searchNext(JTextComponent com) {
        return searchNext(com, config_searchText.getText(), com.getSelectionStart());
    }

    public boolean searchNext(JTextComponent com, String target, int startIndex) {
        int index = StringUtil.indexOf(com.getDocument(), target, startIndex, !config_matchCase.isSelected());
        if (-1 != index && index == com.getSelectionStart() && index + target.length() == com.getSelectionEnd()) {
            index = StringUtil.indexOf(com.getDocument(), target, startIndex + 1, !config_matchCase.isSelected());
        }
        if (-1 != index) {
            com.select(index, index + target.length());
            context.setTemporaryStatus(resources.getMessage("Searcher.searchFor", target));
            return true;
        } else {
            com.select(0, 0);
            context.setTemporaryStatus(resources.getMessage("Searcher.patternNotFound", target));
            return false;
        }
    }

    public boolean searchPrevious(JTextComponent com) {
        String target = config_searchText.getText();
        int index = StringUtil.lastIndexOf(com.getDocument(), target, com.getSelectionStart() - 1, !config_matchCase.isSelected());
        if (-1 != index) {
            com.select(index, index + target.length());
            context.setTemporaryStatus(resources.getMessage("Searcher.searchFor", target));
            return true;
        } else {
            int length = com.getDocument().getLength();
            com.select(length, length);
            context.setTemporaryStatus(resources.getMessage("Searcher.patternNotFound", target));
            return false;
        }
    }

    private static final ImageIcon closeIcon;
    private static final ImageIcon closePushedIcon;
    private static final ImageIcon closeHoverIcon;

    static {
        if (OSDetector.isMac()) {
            closeIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/close.gif"));
            closeHoverIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/close_hover.gif"));
            closePushedIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/close_push.gif"));
        } else {
            closeIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/winclose.gif"));
            closeHoverIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/winclose_hover.gif"));
            closePushedIcon = new ImageIcon(SearchPanel.class.getResource(
                    "images/winclose_push.gif"));
        }
    }
}
