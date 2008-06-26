/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.gc;

import java.awt.Color;

public interface LineGraph {
    void addValues(double[] yvalues);
    void addValues(double x,double[] yvalues);

    void setColorAt(int index, Color color);

    void setLabels(String[] labels);

    void setYMax(int index, double ymax);
}
