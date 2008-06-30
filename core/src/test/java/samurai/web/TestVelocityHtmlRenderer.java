/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.web;

import junit.framework.TestCase;
import samurai.core.ThreadDumpExtractor;
import samurai.core.ThreadStatistic;

import java.io.File;
import java.io.IOException;

public class TestVelocityHtmlRenderer extends TestCase {
    ThreadStatistic statistic = new samurai.core.ThreadStatistic();

    public TestVelocityHtmlRenderer(String name) {
        super(name);
    }

    public static void main(String[] args) throws IOException{
        //do a performance test
        ThreadStatistic statistic = new samurai.core.ThreadStatistic();
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/BEA/910JRockit.dmp"));
        VelocityHtmlRenderer renderer = new VelocityHtmlRenderer("samurai/web/outcss.vm");
        //warm up
        System.out.println("Warming up.");
        for (int i = 0; i < 100; i++) {
            renderer.saveTo(statistic, null, new ProgressListener() {
                public void notifyProgress(int finished, int all) {
                }
            });
        }
        System.out.println("Testing...");
        long before = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            renderer.saveTo(statistic, null, new ProgressListener() {
                public void notifyProgress(int finished, int all) {
                }
            });
        }
        long timeSpent = System.currentTimeMillis() - before;
        System.out.println("time spent:" + (timeSpent / 1000d) + " secs");
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
