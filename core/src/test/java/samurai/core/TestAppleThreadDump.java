/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import java.io.File;
import java.io.IOException;

public class TestAppleThreadDump extends TestCase {
    samurai.core.ThreadStatistic statistic = new samurai.core.ThreadStatistic();

    public TestAppleThreadDump(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(TestAppleThreadDump.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testApple142_08() throws IOException {
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Apple/1.4.2_08Apple.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(10, statistic.getFullThreadDump(0).getThreadCount());
        assertEquals(10, statistic.getStackTracesAsArray().length);

//       "DeadLockThread" prio=5 tid=0x0050b3d0 nid=0x1858a00 waiting for monitor entry [f0d0a000..f0d0aac0]
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("DeadLockThread", dump.getName());
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x0050b3d0", dump.getHeaderParameter("tid"));
        assertEquals("0x1858a00", dump.getHeaderParameter("nid"));
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        //      "DeadLockThread" prio=5 tid=0x0050b110 nid=0x1858600 waiting for monitor entry [f0c89000..f0c89ac0]
        dump = statistic.getFullThreadDump(0).getThreadDump(1);
        assertEquals("DeadLockThread", dump.getName());
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x0050b110", dump.getHeaderParameter("tid"));
        assertEquals("0x1858600", dump.getHeaderParameter("nid"));
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());

        //      "DeadLockThread" prio=5 tid=0x0050af00 nid=0x1858200 waiting for monitor entry [f0c08000..f0c08ac0]
        dump = statistic.getFullThreadDump(0).getThreadDump(2);
        assertEquals("DeadLockThread", dump.getName());
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x0050af00", dump.getHeaderParameter("tid"));
        assertEquals("0x1858200", dump.getHeaderParameter("nid"));
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());

        //      "main" prio=5 tid=0x00500c70 nid=0x1804c00 waiting on condition [f0800000..f08002e8]
        dump = statistic.getFullThreadDump(0).getThreadDump(6);
        assertEquals("main", dump.getName());
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x00500c70", dump.getHeaderParameter("tid"));
        assertEquals("0x1804c00", dump.getHeaderParameter("nid"));
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }
    public void testWLS81SP1() throws IOException {
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Apple/wls81sp1-1.4.2-34apple.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertTrue(dump.isIdle());
    }


}
