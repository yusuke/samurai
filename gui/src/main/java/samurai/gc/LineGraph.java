package samurai.gc;

import java.awt.Color;

/**
 * Created by IntelliJ IDEA.
 * User: yusukey
 * Date: Jun 8, 2008
 * Time: 11:25:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LineGraph {
    void addValues(double[] values);

    void setColorAt(int index, Color color);

    void setLabels(String[] labels);

    void setMaxAt(int index, double max);
}
