package samurai.swing;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import samurai.core.ThreadStatistic;

import java.io.File;

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
 * @version 2.0.0
 */
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
