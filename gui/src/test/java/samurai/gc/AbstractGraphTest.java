package samurai.gc;

import junit.framework.TestCase;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractGraphTest extends TestCase implements LineGraph,LineGraphRenderer{
    protected int count = 0;

    List<double[]> expected = new ArrayList<double[]>();
    protected List<Double> expectedMax = new ArrayList<Double>();

    public LineGraph addLineGraph(String line, String labels[]) {
        this.setLabels(labels);
        return this;
    }

    public void addValues(double[] values) {
        double[] ex = expected.remove(0);
        for (int i = 0; i < ex.length; i++) {
            assertEquals(ex[i], values[i]);
        }
        count++;
    }

    public void addValues(double x,double[] values) {
        addValues(values);
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {
    }

    public void setYMax(int index, double max) {
        assertEquals(expectedMax.remove(0).doubleValue(), max);
    }

}
