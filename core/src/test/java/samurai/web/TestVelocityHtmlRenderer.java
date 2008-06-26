/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.web;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import samurai.core.ThreadDumpExtractor;

import java.io.File;
import java.io.IOException;

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
