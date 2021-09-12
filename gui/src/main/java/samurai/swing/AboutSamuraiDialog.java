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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class AboutSamuraiDialog extends JDialog implements ActionListener {

    public final JButton button1 = new JButton();
    final JLabel imageLabel = new JLabel();
    public final JLabel versionLabel = new JLabel();
    public final JLabel copyrightLabel = new JLabel();
    ImageIcon image1 = new ImageIcon();
    final GridBagLayout gridBagLayout1 = new GridBagLayout();
    final JScrollPane jScrollPane1 = new JScrollPane();
    public final JTextArea releaseNote = new JTextArea();

    public AboutSamuraiDialog(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit() {
        image1 = new ImageIcon(Objects.requireNonNull(MainFrame.class.getResource("images/samurai64.gif")));
        imageLabel.setMaximumSize(new Dimension(64, 64));
        imageLabel.setMinimumSize(new Dimension(64, 64));
        imageLabel.setPreferredSize(new Dimension(64, 64));
        imageLabel.setIcon(image1);
        this.setModal(true);
        this.setTitle("*AboutSamuraiDialog.title*");
        this.getContentPane().setLayout(gridBagLayout1);
        versionLabel.setRequestFocusEnabled(true);
        versionLabel.setText("*AboutSamuraiDialog.version*");
        copyrightLabel.setText("*AboutSamuraiDialog.copyright*");
        button1.setSelected(true);
        button1.setText("OK");
        button1.addActionListener(this);
        releaseNote.setEditable(false);
        releaseNote.setText("*AboutSamuraiDialog.releaseNote*");
        releaseNote.select(0, 0);
        releaseNote.setLineWrap(true);
        jScrollPane1.setMinimumSize(new Dimension(400, 500));
        jScrollPane1.setPreferredSize(new Dimension(390, 200));
        this.getContentPane().add(imageLabel,
                new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0
                        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                        new Insets(10, 10, 10, 0), 0, 0));
        this.getContentPane().add(versionLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                        , GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(10, 10, 0, 10), 0, 0));
        this.getContentPane().add(copyrightLabel,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
                        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                        new Insets(5, 10, 0, 10), 0, 0));
        this.getContentPane().add(button1,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                        , GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(5, 0, 10, 5), 0, 0));
        this.getContentPane().add(jScrollPane1,
                new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
                        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        jScrollPane1.getViewport().add(releaseNote, null);
        setResizable(false);
        button1.getRootPane().setDefaultButton(button1);
    }

    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    //Close the dialog
    void cancel() {
        dispose();
    }

    //Close the dialog on a button event
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            cancel();
        }
    }
}
