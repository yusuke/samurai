/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package samurai.util;

import junit.framework.TestCase;
import samurai.swing.FileHistory;

import java.io.File;
public class TestFileHistory extends TestCase {

    public TestFileHistory(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        String name = "samuraitest";
        String fileName = System.getProperty("user.home") + File.separator + "." +
                name + ".properties";
        new File(fileName).delete();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOpenFIFO() throws Exception {
        String name = "samuraitest";
        Configuration configuration = new Configuration(name);
        configuration.setInt("RecentlyUsedNumber", 4);
        FileHistory history = new FileHistory(configuration);
        history.clearHistory();
//        history.enableCleaningOrphans();
        File file1 = new File("src/main/resources/samurai/swing/close_hover.gif");
        File file2 = new File("src/main/resources/samurai/swing/close_push.gif");
        File file3 = new File("src/main/resources/samurai/swing/close.gif");
        File file4 = new File("src/main/resources/samurai/swing/css.vm");
        File file5 = new File("src/main/resources/samurai/swing/default.properties");
        history.open(file1);
        history.open(file2);
        history.open(file3);
        history.open(file4);
        history.open(file5);
        assertEquals(5, history.getList().size());
        assertEquals(file5, history.getList().get(0));
        assertEquals(file4, history.getList().get(1));
        assertEquals(file3, history.getList().get(2));
        assertEquals(file2, history.getList().get(3));
        assertEquals(file1, history.getList().get(4));
    }

    public void testValidation() throws Exception {
        Configuration configuration = new Configuration("samuraitest");
        configuration.setInt("RecentlyUsedNumber", 4);
        FileHistory history = new FileHistory(configuration);
        history.clearHistory();
        history.enableCleaningOrphans();
        File file1 = new File("src/main/resources/samurai/swing/close_hover.gif");
        File file2 = new File("src/main/resources/samurai/swing/close_push.gif");
        File file3 = new File("src/main/resources/samurai/swing/close.gif");
        File doesnotExist1 = new File("foobarfoobar");
        File file4 = new File("testcases/Sun/1.4.2_03Sunstacked.dmp");
        File doesnotExist2 = new File("foobarfoobarbar");
        File directory1 = new File("classes");
        File directory2 = new File("src");
        File file5 = new File("src/main/resources/samurai/swing/default.properties");
        history.open(file1);
        history.open(file2);
        history.open(directory1);
        history.open(file3);
        history.open(directory2);
        history.open(file4);
        history.open(file5);
        history.open(doesnotExist2);
        history.open(doesnotExist1);
        assertEquals(4, history.getList().size());
        assertEquals(file5, history.getList().get(0));
        assertEquals(file3, history.getList().get(1));
        assertEquals(file2, history.getList().get(2));
        assertEquals(file1, history.getList().get(3));
    }
}

