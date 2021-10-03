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
import one.cafebabe.samurai.util.ImageLoader;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutSamuraiDialog extends JDialog implements ActionListener {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();

    private static final long serialVersionUID = -5834151733419398758L;
    public final JButton button1 = new JButton();
    final JLabel imageLabel = new JLabel();
    public final JLabel versionLabel = new JLabel(resources.getMessage("AboutSamuraiDialog.version"));
    public final JLabel copyrightLabel = new JLabel(resources.getMessage("AboutSamuraiDialog.copyright"));
    ImageIcon image1 = ImageLoader.get("/one/cafebabe/samurai/swing/images/samurai.png", 64, 64);
    final GridBagLayout gridBagLayout1 = new GridBagLayout();
    final JScrollPane jScrollPane1 = new JScrollPane();
    public final JTextPane releaseNote = new JTextPane();

    public AboutSamuraiDialog(Frame parent) {
        super(parent,resources.getMessage("AboutSamuraiDialog.title"));
        setPreferredSize(new Dimension(600,400));
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        imageLabel.setMaximumSize(new Dimension(64, 64));
        imageLabel.setMinimumSize(new Dimension(64, 64));
        imageLabel.setPreferredSize(new Dimension(64, 64));
        imageLabel.setIcon(image1);
        this.setModal(true);
        this.getContentPane().setLayout(gridBagLayout1);
        versionLabel.setRequestFocusEnabled(true);
        button1.setSelected(true);
        button1.setText("OK");
        button1.addActionListener(this);
        releaseNote.setContentType("text/html; charset=charset=UTF-8");
        releaseNote.setEditable(false);
        releaseNote.setText(resources.getMessage("AboutSamuraiDialog.releaseNote"));
        releaseNote.select(0, 0);
        releaseNote.addHyperlinkListener(e -> {
            try {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                    } 
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
//        releaseNote.setLineWrap(true);
        jScrollPane1.setMinimumSize(new Dimension(590, 260));
        jScrollPane1.setPreferredSize(new Dimension(590, 260));
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
