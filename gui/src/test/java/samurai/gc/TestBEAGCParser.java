package samurai.gc;

import junit.framework.TestCase;

import java.awt.Color;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class TestBEAGCParser extends TestCase {

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
                    assertEquals(197.487d, values[0]);
                    assertEquals(34347d, values[1]);
                    assertEquals(26260d, values[2]);
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
        BEAGCParser parser = new BEAGCParser();
        parser.parse("[memory ] 14.210: Nursery GC 34347K->26260K (141312K), 197.487 ms", renderer);
        parser.parse("[memory ] 1437.757-1445.118: GC 125428K->43203K (141312K), 7.361 s (6388.739 ms)", renderer);
        parser.parse("[memory ] 1437.757-1445.118: GC 125428K->43203K (141312K), 7.361 s (6388.739 ms)", renderer);
        assertEquals(5, passed);
    }

    public void testNewLog() throws Exception {
        BEAGCParser parser = new BEAGCParser();
        ScattergramRenderer renderer = new ScattergramRenderer() {
            public void addValues(double[] values) {
                if (count == 0) {
                    assertEquals(58.556d, values[0]);
                    assertEquals(97781d, values[1]);
                    assertEquals(37613d, values[2]);
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
        parser.parse("[memory ] 72.250-72.390: GC 97781K->37613K (98304K), 58.556 ms", renderer);
        parser.parse("[memory ] 72.453-72.562: GC 43810K->37089K (98304K), 61.533 ms", renderer);
        parser.parse("[memory ] 72.625-72.687: GC 48748K->38384K (98304K), 62.000 ms", renderer);
        assertEquals(5, passed);
    }
}
