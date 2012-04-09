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
