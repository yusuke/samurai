/*
 * Copyright 2003-2012 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samurai.util;

import junit.framework.TestCase;

import javax.swing.JTextArea;

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
