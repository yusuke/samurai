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


import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

@Execution(ExecutionMode.CONCURRENT)
class SplitTabPanelTest extends JFrame {
    final GridBagLayout gbl = new GridBagLayout();
    final BorderLayout borderLayout1 = new BorderLayout();
    final TilePanel splitTabPanel = new TilePanel(true);
    int orientation = 1;

    public SplitTabPanelTest() {
        useTilePanel();
//        useGBL();
    }

    private final Dimension dividerSize = new Dimension(3,3);
    private JPanel getPanel(){
        JPanel divider = new JPanel();
        divider.setMinimumSize(dividerSize);
        divider.setMaximumSize(dividerSize);
        divider.setPreferredSize(dividerSize);

        return divider;
    }

    final JTextArea ta1 = new JTextArea("1");
    final JTextArea ta2 = new JTextArea("2");
    final JButton ta3 = new JButton("switch");
    private void useGBL(){
//        JTextArea ta3 = new JTextArea("3");
        ta3.addActionListener(e -> dump());
        ta3.setActionCommand("okButton");


        getContentPane().setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();
        ta1.setMinimumSize(new Dimension(0,0));
        ta2.setMinimumSize(new Dimension(0,0));
        ta3.setMinimumSize(new Dimension(0,0));
        ta1.setPreferredSize(new Dimension(0,0));
        ta2.setPreferredSize(new Dimension(0,0));
        ta3.setPreferredSize(new Dimension(0,0));
           c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weighty = 100;

        c.gridx = 0;
        c.weightx = 131;
        getContentPane().add(ta1,c);
        c.gridx = 2;
        c.weightx = 215;
        getContentPane().add(ta2,c);
        c.gridx = 4;
        c.weightx = 47;
        getContentPane().add(ta3,c);


        c.gridx = 1;
        c.weightx = 0;
        getContentPane().add(getPanel(),c);
        c.gridx = 3;
        c.weightx = 0;
        getContentPane().add(getPanel(),c);

        setSize(new Dimension(393, 300));
        dump();
        validate();
        dump();



    }
    private void dump(){
        System.out.println(ta1.getWidth()+":"+ta2.getWidth()+":"+ta3.getWidth()+" "+getWidth());
    }
    private void useTilePanel(){
        JTextArea ta1 = new JTextArea("1");
        JTextArea ta2 = new JTextArea("2");
        JTextArea ta3 = new JTextArea("3");


        getContentPane().setLayout(borderLayout1);
        this.getContentPane().add(splitTabPanel, java.awt.BorderLayout.CENTER);
        splitTabPanel.setOrientation(TilePanel.HORIZONTAL);
        splitTabPanel.addComponent("1", ta1);
        splitTabPanel.addComponent("2", ta2);
        splitTabPanel.addComponent("3", ta3);
        JButton btn = new JButton("switch");
        btn.addActionListener(e -> {
            orientation++;
            splitTabPanel.setOrientation(orientation %2);
        });
        btn.setActionCommand("okButton");
        splitTabPanel.addComponent("2", btn);
        splitTabPanel.setSelectedIndex(3);

        setSize(new Dimension(306, 300));
    }

    public static void main(String[] args) {
        SplitTabPanelTest test = new SplitTabPanelTest();
//        test.setSize(new Dimension(300, 300));
        test.setVisible(true);
        test.validate();
//    test.jPanel1.removeComponent(ta0);
//    test.jPanel1.removeComponent(ta1);
//    test.jPanel1.removeComponent(ta2);
//        if (test.splitTabPanel.getSelectedIndex() != 3) {
//            System.out.println("error!:" + test.splitTabPanel.getSelectedIndex());
//        }
    }
}
