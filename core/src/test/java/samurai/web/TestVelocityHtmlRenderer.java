package samurai.web;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import samurai.core.ThreadDumpExtractor;

import java.io.File;
import java.io.IOException;

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
public class TestVelocityHtmlRenderer extends TestCase {
    samurai.core.ThreadStatistic statistic = new samurai.core.ThreadStatistic();

    public TestVelocityHtmlRenderer(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(TestVelocityHtmlRenderer.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testSaveTo() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/Sun/1.4.2_03Sunstacked.dmp"));
        VelocityHtmlRenderer renderer = new VelocityHtmlRenderer("samurai/web/outcss.vm");
        renderer.saveTo(statistic, new File("savedhtml"), new ProgressListener() {
            public void notifyProgress(int finished, int all) {
                assertTrue(finished <= all);
            }
        });
    }

}
