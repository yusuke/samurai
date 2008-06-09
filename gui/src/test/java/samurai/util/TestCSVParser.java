package samurai.util;

import junit.framework.TestCase;
import samurai.gc.LineGraphRenderer;
import samurai.gc.LineGraph;

import java.awt.Color;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class TestCSVParser extends TestCase  implements LineGraph,LineGraphRenderer {


    protected void setUp() throws Exception {
        super.setUp();
        columnCount = 0;
        count = 0;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int count = 0;

    double[] expected;
    private int columnCount = 0;

    public LineGraph addLineGraph(String line, String labels[]) {
        this.setLabels(labels);
        columnCount+=labels.length;
        return this;
    }

    private int index = 0;

    public void addValues(double[] values) {
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i + index], values[i]);
        }
        index+=values.length;
        if(index == columnCount){
            index = 0;
            count++;
        }
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {
    }

    public void setMaxAt(int index, double max) {

    }

    public void testCSVParser() throws Exception {
        CSVParser parser = new CSVParser();
        parser.parse("column1,column2,column3", this);
        expected = new double[]{1d, 2d, 3d};
        parser.parse("1,2,3", this);
        expected = new double[]{3d, 3d, 1d};
        parser.parse("3,3,1", this);
        expected = new double[]{7d, 5d, 0d};
        parser.parse("7,5", this);
        expected = new double[]{9d, 6d, 79d};
        parser.parse("9,6,79,6,79,6,7", this);
        expected = new double[]{11d, 7d, 9d};
        parser.parse("11,7,9", this);
        assertEquals(5, count);
    }
}
