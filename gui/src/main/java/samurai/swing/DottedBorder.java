/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.SystemColor;

public class DottedBorder extends EmptyBorder {
    private static final int borderWidth = 3;

    public DottedBorder() {
        super(borderWidth, borderWidth, borderWidth, borderWidth);
    }

    private int dotlength = 2;
    private int inset = 2;

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        //  c.paint(g);
//    g.setColor(SystemColor.textHighlight);
        for (int i = 0; i < width; i += dotlength) {
            if (i % (dotlength * 2) == dotlength) {
                g.setColor(SystemColor.textHighlight);
                g.drawLine(x + i, y, x + i + dotlength, y);
                g.drawLine(x + i, y + height - inset, x + i + dotlength, y + height - inset);
            } else {
//        g.setColor(SystemColor.inactiveCaptionBorder);
//        g.drawLine(x + i, y, x + i + 10, y);
//        g.drawLine(x + i, y + height - 1, x + i + 10, y + height - 1);
            }
        }
        for (int i = 0; i < height; i += dotlength) {
            if (i % (dotlength * 2) == dotlength) {
                g.setColor(SystemColor.textHighlight);
//        g.setColor(Color.black);
                g.drawLine(x, y + i, x, y + i + dotlength);
                g.drawLine(x + width - inset, y + i + dotlength, x + width - inset, y + i + dotlength);
            } else {
//        g.setColor(SystemColor.window);
//        g.drawLine(x, y + i, x, y + i + 10);
//        g.drawLine(x + width - 1, y + i, x + width - 1, y + i + 10);
            }
        }
    }
}
