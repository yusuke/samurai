package samurai.gc;

import java.awt.Color;

public interface LineGraphRenderer {
    public void addValues(double[] values);

    public void setColorAt(int index, Color color);

    public void setLabels(String[] labels);

    public void setMaxAt(int index, double max);
}
