/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import samurai.core.ThreadStatistic;

import java.io.File;

public class SaveAsHtmlTestUnit extends TestCase {
    ThreadStatistic statistic = new ThreadStatistic();

    public SaveAsHtmlTestUnit(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(SaveAsHtmlTestUnit.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testFileName() {
        File file = new File("index.html");
        File target = ThreadDumpPanel.getTargetDirectory(file);
        assertEquals("index", target.getName());

        file = new File("src");
        target = ThreadDumpPanel.getTargetDirectory(file);
        assertEquals("src.1", target.getName());

    }
}
