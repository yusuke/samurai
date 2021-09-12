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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;


/*package*/ class PlotData {
    private List<double[/*double ydata[]*//*double x */]>[] datas;
    private String[] labels;
    private int size;
    private double[] maxY;
    private double[] maxX;
    private double[] minY;
    private boolean[] visible;
    private Color[] colors;
    private boolean initialized = false;
    private final int DATA_CHUNK_SIZE = 256;
    private int xDataIndex;

    /*package*/ boolean isInitialized() {
        return initialized;
    }

    /*package*/ PlotData() {
    }

    /*package*/ void setLabels(String[] labels) {
        this.labels = labels;
        this.initialized = true;
        this.datas = new List[labels.length + 1];
        this.xDataIndex = labels.length;
        this.size = 0;
        this.maxY = new double[labels.length];
        this.minY = new double[labels.length];
        this.visible = new boolean[labels.length];
        this.colors = new Color[labels.length];
        for (int i = 0; i < labels.length; i++) {
            this.visible[i] = true;
            this.maxY[i] = 0;
            this.minY[i] = 0;
            this.colors[i] = DEFAULT_COLORS[i % DEFAULT_COLORS.length];
            this.datas[i] = new ArrayList<>(1);
        }
        this.datas[xDataIndex] = new ArrayList<>(1);
    }

    /*package*/ double getMaxAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return maxY[index];
    }

    /*package*/ double getMinAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return minY[index];
    }

    /*package*/ int size() {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return this.size;
    }

    /*package*/ int getLabelCount() {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return labels.length;
    }

    /*package*/ Color getColorAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return this.colors[index];
    }

    /*package*/ void setColorAt(int index, Color color) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        this.colors[index] = color;
    }

    /*package*/ double getValueAt(int index, int count) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return datas[index].get(count / DATA_CHUNK_SIZE)[count % DATA_CHUNK_SIZE];
    }

    /*package*/ void setMaxAt(int index, double max) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        this.maxY[index] = max;
    }

    /*package*/ void setMinAt(int index, double min) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        this.minY[index] = min;
    }

    /*package*/ String getLabelAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return this.labels[index];
    }

    /*package*/ void setLabelAt(int index, String label) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        this.labels[index] = label;
    }

    /*package*/ void addValues(double x, double[] newDatas) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        int chunkIndex = size / DATA_CHUNK_SIZE;
        int leftover = size % DATA_CHUNK_SIZE;
        if ((leftover) == 0) {
            for (int i = 0; i < this.labels.length; i++) {
                this.datas[i].add(new double[DATA_CHUNK_SIZE]);
            }
            this.datas[xDataIndex].add(new double[DATA_CHUNK_SIZE]);
        }
        for (int i = 0; i < this.labels.length; i++) {
            if (maxY[i] < newDatas[i]) {
                maxY[i] = newDatas[i];
            }
            if (minY[i] > newDatas[i]) {
                minY[i] = newDatas[i];
            }
            this.datas[i].get(chunkIndex)[leftover] = newDatas[i];
        }
        this.datas[xDataIndex].get(chunkIndex)[leftover] = x;
        size++;
    }

    /*package*/ void addValues(double[] newDatas) {
        addValues(size, newDatas);
    }

    private final Color[] DEFAULT_COLORS = new Color[]{
            Color.YELLOW,
            Color.BLUE,
            Color.CYAN,
            Color.GRAY,
            Color.GREEN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.WHITE,
    };

    /*package*/ boolean isVisibleAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        return this.visible[index];
    }

    /*package*/ void setVisibleAt(int index, boolean newVisible) {
        if (!initialized) {
            throw new IllegalStateException("not yet initialized");
        }
        this.visible[index] = newVisible;
    }

    private final Color background = Color.BLACK;
    private final Color gridColor = new Color(0, 70, 0);
    private final int GRID_INTERVAL = 10;
    private final DecimalFormat format = new DecimalFormat("####0.0#############################");

    public void drawGraph(GraphCanvas c, int x, int y, int width, int height, int scroll) {
        int maxY = y + height;
        int maxX = x + width;
        c.setColor(background);
        c.fillRect(x, y, width, height);
        c.setColor(gridColor);
        if (isInitialized()) {
            drawGrid(c, x, y, width, scroll, maxY, maxX);
            drawLegends(c, width, height, maxY);
            if (size() > 0) {
                for (int i = 0; i < getLabelCount(); i++) {
                    if (isVisibleAt(i)) {
                        c.setColor(getColorAt(i));
                        int index;
                        int drawWidth;
                        if (width >= size()) {
                            drawWidth = size();
                            index = size() - 1;
                        } else {
                            drawWidth = width;
                            index = scroll + width - 1;
                            if (index > size()) {
                                index = size() - 1;
                            }
                        }
                        int drawX = width - drawWidth;
                        int lastY = maxY - (int) ((double) height / getMaxAt(i) * getValueAt(i, index - drawWidth + 1));
                        for (int j = index - drawWidth + 2; j < index; j++) {
//                        for (int j = --index; j >= 0; j--) {
                            int drawY = maxY - (int) ((double) height / getMaxAt(i) * getValueAt(i, j));
                            c.drawLine(drawX, lastY, drawX + 1, drawY);
                            lastY = drawY;
                            drawX++;
                        }
                    }
                }
            }
        }
    }

    private void drawLegends(GraphCanvas c, int width, int height, int maxY) {
        int fontHeight = c.getFontHeight() + 2;
        for (int i = 0; i < getLabelCount(); i++) {
            if (isVisibleAt(i)) {
                c.setColor(getColorAt(i));
                c.drawString(getLabelAt(i) + ":" + format.format(getMaxAt(i)), 5,
                        fontHeight * (i + 1));
                c.drawString(format.format(getMinAt(i)), 5,
                        height - 5 - fontHeight * (getLabelCount() - 1) +
                                fontHeight * i);
                //draw latest value
                if (size() > 0) {
                    String currentValue = format.format(getValueAt(i,
                            size() - 1));
                    int currentValueY = maxY -
                            (int) ((double) height / getMaxAt(i) *
                                    getValueAt(i, size() - 1));
                    currentValueY = fontHeight > currentValueY ? fontHeight :
                            currentValueY;
                    c.drawString(currentValue,
                            width - c.getStringWidth(currentValue) - 5,
                            currentValueY);
                }
            }
        }
    }

    private void drawGrid(GraphCanvas c, int x, int y, int width, int scroll, int maxY, int maxX) {
        //draw grid
        //horizontal lines
        for (int i = maxY - (GRID_INTERVAL - 1); i > y; i -= GRID_INTERVAL) {
            c.drawLine(x, i, maxX, i);
        }
        //vertical lines
        int gridx;
        if (width >= size()) {
            gridx = GRID_INTERVAL - ((size() - width) % GRID_INTERVAL);
        } else {
            gridx = GRID_INTERVAL - ((size() + scroll) % GRID_INTERVAL);
        }
        for (int i = gridx; i < maxX; i += GRID_INTERVAL) {
            c.drawLine(i, y, i, maxY);
        }
    }

}
