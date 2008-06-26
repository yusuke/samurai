/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NestedSplitPanes extends JFrame {
    JButton[] arrB = new JButton[16];
    JSplitPane[] arrS = new JSplitPane[15];

    public NestedSplitPanes() {
        super("Nested JSplitPanes");

        for (int i = 0; i < 16; i++) {
            arrB[i] = new JButton("Button " + (i + 1));
        }
        for (int i = 0; i < 8; i++) {
            arrS[i] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    true, arrB[i * 2], arrB[i * 2 + 1]);
        }
        for (int i = 8; i < 12; i++) {
            arrS[i] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    true, arrS[(i - 8) * 2],
                    arrS[(i - 8) * 2 + 1]);
        }
        arrS[12] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, arrS[8], arrS[9]);
        arrS[13] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, arrS[10], arrS[11]);
        arrS[14] = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, arrS[12], arrS[13]);
        for (int i = 0; i < 15; i++) {
            // The following works because the JButtons in the
            // JSplitPanes have their own Borders.
            // Borderless components would have to be given a
            // border before doing this, otherwise the whole thing
            // looks really ugly and would even be almost unusable
            // in the Windows L&F (e.g. try JPanels instead of
            // JButtons.
            // If you comment out the following line  you can
            // observe the stacking border problem (4131528).
            arrS[i].setBorder(null);
        }

        this.getContentPane().add(arrS[14]);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(0);
            }
        });

        this.pack();
        this.setVisible(true);
    }

    static public void main(String args[]) {
        new NestedSplitPanes();
    }
}
