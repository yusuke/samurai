package samurai.util;

import junit.framework.TestCase;
import samurai.gc.ScattergramRenderer;

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
public class TestCSVParser extends TestCase implements ScattergramRenderer {
    public TestCSVParser() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCSVParser.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        count = 0;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int count = 0;
    private int passed = 0;

    public void testLogContinued() throws Exception {
        CSVParser parser = new CSVParser();
        parser.parse("column1,column2,column3", this);
        parser.parse("1,2,3", this);
        parser.parse("3,3,1", this);
        parser.parse("7,5", this);
        parser.parse("9,6,79,6,79,6,7", this);
        parser.parse("11,7,9", this);
        assertEquals(7, passed);
    }

    public void addValues(double[] values) {
        if (count == 0) {
            assertEquals(1d, values[0]);
            assertEquals(2d, values[1]);
            assertEquals(3d, values[2]);
            passed++;
        }
        assertEquals(3, values.length);
        passed++;
        count++;
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {

        assertEquals("column1", labels[0]);
        assertEquals("column2", labels[1]);
        assertEquals("column3", labels[2]);
        assertEquals(0, passed);
        passed++;
    }

    public void setMaxAt(int index, double max) {

    }
}
