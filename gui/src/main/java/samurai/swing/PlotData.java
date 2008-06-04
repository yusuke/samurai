package samurai.swing;

import java.awt.Color;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 2.0.5
 */

/*package*/ class PlotData {
    private double[][] datas;
    private String[] labels;
    private int size;
    private double[] max;
    private double[] min;
    private boolean[] visible;
    private Color[] colors;
    private boolean initialized = false;

    /*package*/ boolean isInitialized() {
        return initialized;
    }

    /*package*/ PlotData() {
    }

    /*package*/ void setLabels(String[] labels) {
        this.labels = labels;
        this.initialized = true;
        this.datas = new double[labels.length][256];
        this.size = 0;
        this.max = new double[labels.length];
        this.min = new double[labels.length];
        this.visible = new boolean[labels.length];
        this.colors = new Color[labels.length];
        for (int i = 0; i < labels.length; i++) {
            this.visible[i] = true;
            this.max[i] = 0;
            this.min[i] = 0;
            this.colors[i] = DEFAULT_COLORS[i % DEFAULT_COLORS.length];
        }
    }

    /*package*/ double getMaxAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return max[index];
    }

    /*package*/ double getMinAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return min[index];
    }

    /*package*/ int size() {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return this.size;
    }

    /*package*/ int getLabelCount() {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return labels.length;
    }

    /*package*/ Color getColorAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return this.colors[index];
    }

    /*package*/ void setColorAt(int index, Color color) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        this.colors[index] = color;
    }

    /*package*/ double getValueAt(int index, int count) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return datas[index][count];
    }

    /*package*/ void setMaxAt(int index, double max) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        this.max[index] = max;
    }

    /*package*/ void setMinAt(int index, double min) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        this.min[index] = min;
    }

    /*package*/ String getLabelAt(int index) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        return this.labels[index];
    }

    /*package*/ void setLabelAt(int index, String label) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        this.labels[index] = label;
    }

    /*package*/ void addValues(double[] newDatas) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        if ((size % 256) == 0) {
            for (int i = 0; i < this.datas.length; i++) {
                double[] oldData = this.datas[i];
                this.datas[i] = new double[size + 256];
                System.arraycopy(oldData, 0, this.datas[i], 0, size);
            }
        }
        for (int i = 0; i < this.datas.length; i++) {
            if (max[i] < newDatas[i]) {
                max[i] = newDatas[i];
            }
            if (min[i] > newDatas[i]) {
                min[i] = newDatas[i];
            }
            this.datas[i][size] = newDatas[i];
        }
        size++;
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
            throw new IllegalStateException("not initialized");
        }
        return this.visible[index];
    }

    /*package*/ void setVisibleAt(int index, boolean newVisible) {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }
        this.visible[index] = newVisible;
    }
}
