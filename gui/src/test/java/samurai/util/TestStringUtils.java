package samurai.util;

import junit.framework.TestCase;

import javax.swing.JTextArea;

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
public class TestStringUtils extends TestCase {
    public TestStringUtils() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestStringUtils.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIndexOfIgnoreCase() {
        assertEquals(2, "abcdef".indexOf("cde"));
        assertEquals(2, StringUtil.indexOfIgnoreCase("abcdef", "CdE", 0));
        assertEquals(2, StringUtil.indexOfIgnoreCase("abcdef", "CDE", 0));
        assertEquals(-1, StringUtil.indexOfIgnoreCase("abcdef", "CaE", 0));
    }

    public void testLastIndexOfIgnoreCase() {
        assertEquals(2, "abcdef".lastIndexOf("cde"));
        assertEquals(-1, StringUtil.lastIndexOfIgnoreCase("abcdef", "CdE", 0));
        assertEquals(2, StringUtil.lastIndexOfIgnoreCase("abcdef", "CDE", 9));
        assertEquals(2, StringUtil.lastIndexOfIgnoreCase("abcdef", "CdE", 3));
    }

    public void testLastIndexOfIgnoreCaseTextArea() {
        JTextArea ta = new JTextArea();
        ta.setText("abcdef");
//    assertEquals(-1,StringUtil.lastIndexOf(ta.getDocument(),"CdE",0,true));
        assertEquals(2, StringUtil.lastIndexOf(ta.getDocument(), "CdE", 4, true));
    }

    public void tesIndexOfIgnoreCaseTextArea() {
        JTextArea ta = new JTextArea();
        ta.setText("abcdef");
//    assertEquals(-1,StringUtil.lastIndexOf(ta.getDocument(),"CdE",0,true));
        assertEquals(2, StringUtil.indexOf(ta.getDocument(), "CdE", 0, true));
    }

}
