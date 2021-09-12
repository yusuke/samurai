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

import samurai.gc.LineGraph;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


public class LineGraphPanel extends JPanel implements ClipBoardOperationListener, LineGraph {
    BorderLayout borderLayout1 = new BorderLayout();
    JScrollBar scrollBar = new JScrollBar();
    JPanel panel = new JPanel() {
        public void paint(Graphics g) {
            super.paint(g);
            draw(g, this.getBounds());
        }
    };
    private static final boolean isMac = (-1 != System.getProperty("os.name").toLowerCase().indexOf("mac"));

    public LineGraphPanel() {
        this.setInputVerifier(null);
        this.addMouseListener(new LineGraphPanel_this_mouseAdapter(this));
        this.addMouseMotionListener(new LineGraphPanel_this_mouseMotionAdapter(this));
        this.addComponentListener(new LineGraphPanel_this_componentAdapter(this));
        this.setLayout(borderLayout1);
        scrollBar.setMaximum(200);
        scrollBar.setMinimum(0);
        scrollBar.setOrientation(JScrollBar.HORIZONTAL);
        scrollBar.setValue(0);
        this.add(scrollBar, BorderLayout.SOUTH);
        this.add(panel, BorderLayout.CENTER);
        scrollBar.addAdjustmentListener(new LineGraphPanel_scrollBar_adjustmentAdapter(this));
        adjustScrollBar();
    }

    public void setLabels(String[] labels) {
        this.plotData.setLabels(labels);
    }

    public void addValues(double[] values) {
        this.plotData.addValues(values);
        adjustScrollBar();
        rightMost();
        repaint();
    }

    public void addValues(double x, double[] values) {
        this.plotData.addValues(x, values);
        adjustScrollBar();
        rightMost();
        repaint();
    }

    public void setColorAt(int index, Color color) {
        plotData.setColorAt(index, color);
    }

    public void setYMax(int index, double max) {
        plotData.setMaxAt(index, max);
    }

    public void rightMost() {
        this.scrollBar.setValue(scrollBar.getMaximum());
    }

    public void adjustScrollBar() {
        if (plotData.isInitialized()) {
            int width = (int) this.getSize().getWidth();
            if (width >= plotData.size()) {
                scrollBar.setVisible(isMac);
                scrollBar.setEnabled(false);
                scrollBar.setMaximum(2);
                scrollBar.setVisibleAmount(3);
                scrollBar.setValue(0);
            } else {
                scrollBar.setVisible(true);
                scrollBar.setEnabled(true);
                scrollBar.setMaximum(plotData.size());
                scrollBar.setVisibleAmount(width);
                scrollBar.setBlockIncrement(scrollBar.getVisibleAmount());
            }
        } else {
            scrollBar.setVisible(isMac);
        }
    }

    PlotData plotData = new PlotData();

    private boolean splitted = false;

    public void setSplitted(boolean splitted) {
        this.splitted = splitted;
    }

    public boolean isSplitted() {
        return splitted;
    }

    private void draw(Graphics g1, Rectangle bounds) {
        GraphCanvas c = new LineGraphCanvas(g1);

        plotData.drawGraph(c, bounds.x, bounds.y, bounds.width, bounds.height, scrollBar.getValue());
        adjustScrollBar();

    }

    class LineGraphCanvas implements GraphCanvas {
        Graphics g;

        LineGraphCanvas(Graphics g) {
            this.g = g;
        }

        public void drawLine(int x1, int y1, int x2, int y2) {
            g.drawLine(x1, y1, x2, y2);
        }

        public void fillRect(int x1, int y1, int x2, int y2) {
            g.fillRect(x1, y1, x2, y2);
        }

        public void setColor(Color color) {
            g.setColor(color);
        }

        public void drawString(String str, int x, int y) {
            g.drawString(str, x, y);
        }

        public int getFontHeight() {
            return getFontMetrics(g.getFont()).getHeight();
        }

        public int getStringWidth(String str) {
            return getFontMetrics(g.getFont()).stringWidth(str) - 5;
        }
    }

    void this_componentResized(ComponentEvent e) {
        adjustScrollBar();
    }

    void scrollBar_adjustmentValueChanged(AdjustmentEvent e) {
        this.repaint();

    }

    void this_mouseMoved(MouseEvent e) {
        if (plotData.isInitialized()) {
            if (0 < plotData.getLabelCount()) {
                int fontHeight = getGraphics().getFont().getSize() + 2;
                FontMetrics metrics = getFontMetrics(getGraphics().getFont());
                for (int i = 0; i < plotData.getLabelCount(); i++) {
                    if ((e.getY() <= fontHeight * (i + 1)) && e.getY() > fontHeight * (i)) {
                        if (e.getX() <= (metrics.stringWidth(plotData.getLabelAt(i) + ":" + plotData.getMaxAt(i)) + 5)) {
                            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            return;
                        }
                    }
                }
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    void this_mouseClicked(MouseEvent e) {
        if (plotData.isInitialized()) {
            if (0 < plotData.getLabelCount()) {
                int fontHeight = getGraphics().getFont().getSize() + 2;
                FontMetrics metrics = getFontMetrics(getGraphics().getFont());
                for (int i = 0; i < plotData.getLabelCount(); i++) {
                    if ((e.getY() <= fontHeight * (i + 1)) && e.getY() > fontHeight * (i)) {
                        if (e.getX() <= (metrics.stringWidth(plotData.getLabelAt(i) + ":" + plotData.getMaxAt(i)) + 5)) {
                            plotSetting.reset(plotData.getLabelAt(i), String.valueOf(plotData.getMaxAt(i)), plotData.getColorAt(i),
                                    plotData.isVisibleAt(i));
                            plotSetting.setVisible(true);
                            if (plotSetting.okPressed()) {
                                plotData.setLabelAt(i, plotSetting.getLabel());
                                plotData.setColorAt(i, plotSetting.getColor());
                                try {
                                    plotData.setMaxAt(i, Double.parseDouble(plotSetting.getMax()));

                                } catch (NumberFormatException ignore) {
                                }
                                plotData.setVisibleAt(i, plotSetting.isPlotVisible());
                                this.repaint();
                            }
                        }
                    }
                }
            }
        }
    }

    public PlotSettingDialog plotSetting = new PlotSettingDialog();

    public void cut() {
    }

    public void copy() {
        Rectangle rect;
        rect = panel.getBounds();
        if (plotData.size() > rect.getWidth()) {
            rect = new Rectangle(plotData.size(), rect.height);
        }
        BufferedImage buffer = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_3BYTE_BGR);
        draw(buffer.getGraphics(), rect);
        setClipboard(new ImageIcon(buffer).getImage());
    }

    public void paste() {
    }

// This method writes a image to the system clipboard.

    // otherwise it returns null.
    public static void setClipboard(Image image) {
        ImageSelection imgSel = new ImageSelection(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    // This class is used to hold an image while on the clipboard.
    public static class ImageSelection implements Transferable {
        private Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns image
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, java.io.IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }

}

class LineGraphPanel_this_componentAdapter extends java.awt.event.ComponentAdapter {
    LineGraphPanel adaptee;

    LineGraphPanel_this_componentAdapter(LineGraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void componentResized(ComponentEvent e) {
        adaptee.this_componentResized(e);
    }
}

class LineGraphPanel_scrollBar_adjustmentAdapter implements java.awt.event.AdjustmentListener {
    LineGraphPanel adaptee;

    LineGraphPanel_scrollBar_adjustmentAdapter(LineGraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        adaptee.scrollBar_adjustmentValueChanged(e);
    }
}

class LineGraphPanel_this_mouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
    LineGraphPanel adaptee;

    LineGraphPanel_this_mouseMotionAdapter(LineGraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseMoved(MouseEvent e) {
        adaptee.this_mouseMoved(e);
    }
}

class LineGraphPanel_this_mouseAdapter extends java.awt.event.MouseAdapter {
    LineGraphPanel adaptee;

    LineGraphPanel_this_mouseAdapter(LineGraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}

