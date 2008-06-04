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
public class TestIBMGCParser extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        count = 0;
        passed = 0;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int count = 0;
    private int passed = 0;

    public void testOldLog() throws Exception {
        ScattergramRenderer renderer = new ScattergramRenderer() {
            public void addValues(double[] values) {
                if (count == 0) {
                    assertEquals(88d, values[0]);
                    assertEquals(38304336d, values[1]);
                    assertEquals(38821800d, values[2]);
                    passed++;
                }
                assertEquals(3, values.length);
                passed++;
                count++;
            }

            public void setColorAt(int index, Color color) {

            }

            public void setLabels(String[] labels) {
                assertEquals(0, passed);
                passed++;
            }

            public void setMaxAt(int index, double max) {

            }
        };
        IBMGCParser parser = new IBMGCParser();
        parser.parse("  <GC(74): freed 38304336 bytes, 52% free (38821800/73923072), in 88 ms>", renderer);
        parser.parse("  <GC(75): freed 37702960 bytes, 51% free (38146496/73923072), in 92 ms>", renderer);
        parser.parse("  <GC(76): freed 16412784 bytes, 51% free (38016120/73923072), in 89 ms>", renderer);
        assertEquals(5, passed);
    }
}
