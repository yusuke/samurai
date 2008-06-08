package samurai.gc;

import junit.framework.TestCase;

import java.awt.Color;


/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class TestIBMGCParser extends TestCase implements LineGraph,LineGraphRenderer {

    protected void setUp() throws Exception {
        super.setUp();
        count = 0;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int count = 0;

    double[] expected;

    public LineGraph addLineGraph(String labels[]){
        this.setLabels(labels);
        return this;
    }

    public void addValues(double[] values) {
        assertEquals(expected.length, values.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], values[i]);
        }
        count++;
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {
    }

    public void setMaxAt(int index, double max) {

    }

    public void testOldLog() throws Exception {
        IBMGCParser parser = new IBMGCParser();
        expected = new double[]{88d, 38304336d, 38821800d};
        parser.parse("  <GC(74): freed 38304336 bytes, 52% free (38821800/73923072), in 88 ms>", this);
        expected = new double[]{92d, 37702960d, 38146496d};
        parser.parse("  <GC(75): freed 37702960 bytes, 51% free (38146496/73923072), in 92 ms>", this);
        expected = new double[]{89d, 16412784, 38016120d};
        parser.parse("  <GC(76): freed 16412784 bytes, 51% free (38016120/73923072), in 89 ms>", this);
        assertEquals(3, count);
    }
}
