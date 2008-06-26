/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import java.awt.Color;

public interface GraphCanvas {
    void drawLine(int x1, int y1, int x2, int y2);

    void fillRect(int x1, int y1, int x2, int y2);

    void setColor(Color color);

    void drawString(String str, int x, int y);

    int getFontHeight();

    int getStringWidth(String str);

}
