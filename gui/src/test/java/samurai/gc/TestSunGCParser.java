package samurai.gc;

import junit.framework.TestCase;

import java.awt.Color;

public class TestSunGCParser extends TestCase implements ScattergramRenderer {

    protected void setUp() throws Exception {
        super.setUp();
        count = 0;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private int count = 0;

    double[] expected;


    public void testStandardVerbosegc() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected = new double[]{0.0254331d, 115008d, 103309d};
        assertTrue(parser.parse("[GC 115008K->103309K(129664K), 0.0254331 secs]", this));
        expected = new double[]{0.0328756d, 114957d, 105199d};
        assertTrue(parser.parse("[GC 114957K->105199K(129664K), 0.0328756 secs]", this));

        expected = new double[]{5.4551339d, 116847d, 48717d};
        assertFalse(parser.parse("[Full GC[Unloading class sun.reflect.GeneratedConstructorAccessor74]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._common._jsp.__403]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__comments]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedMethodAccessor224]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._styles.__global_css]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor78]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__trackbacks]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__blogentries]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor69]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor75]", this));
        assertTrue(parser.parse(" 116847K->48717K(129664K), 5.4551339 secs]", this));

        expected = new double[]{1.0320474d, 61365d, 51414d};
        assertTrue(parser.parse("[Full GC 61365K->51414K(129664K), 1.0320474 secs]", this));
        expected = new double[]{0.0320474d, 60365d, 50414d};
        assertTrue(parser.parse("[GC 60365K->50414K(129664K), 0.0320474 secs]", this));
        parser.parse("", this);
        assertEquals(5, count);
    }
    public void testUseParNewGC() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected = new double[]{.1106169d, 214067d, 16986d};
        assertTrue(parser.parse("[ParNew 214067K->16986K(1022400K), 0.1106169 secs]", this));
        expected = new double[]{.3251635d, 226778d, 33381d};
        assertTrue(parser.parse("[ParNew 226778K->33381K(1022400K), 0.3251635 secs]", this));
        assertEquals(2, count);
    }
    public void testPrintGCTimeStamps() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected = new double[]{.1489631d, 79733d, 4275d};
        assertTrue(parser.parse("8.861: [Full GC 79733K->4275K(1022400K), 0.1489631 secs]", this));
        expected = new double[]{.3251635d, 226778d, 33381d};
        assertTrue(parser.parse("42.062: [ParNew 226778K->33381K(1022400K), 0.3251635 secs]", this));
        expected = new double[]{.3251635d, 226778d, 33381d};
        assertTrue(parser.parse("42.062: [GC 226778K->33381K(1022400K), 0.3251635 secs]", this));
        assertEquals(3, count);
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

}
