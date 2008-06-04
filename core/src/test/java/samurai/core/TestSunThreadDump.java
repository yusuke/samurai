package samurai.core;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;


/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.0
 */
public class TestSunThreadDump extends TestCase {

    public TestSunThreadDump(String name) {
        super(name);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSun142_03stacked() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Sun/1.4.2_03Sunstacked.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(9, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(1);
        assertEquals("Thread-1", dump.getName());
        //"Thread-1" prio=5 tid=0x0028ea18 nid=0x604 waiting for monitor entry [182ef000..182efd88]
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x0028ea18", dump.getHeaderParameter("tid"));
        assertEquals("0x604", dump.getHeaderParameter("nid"));
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        dump = statistic.getFullThreadDump(0).getThreadDump(2);
        assertEquals("Thread-0", dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
    }

    public void testSun142_03idle() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Sun/1.4.2_03Sunidle.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(7, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(3);
        //"main" prio=5 tid=0x00285c40 nid=0x438 waiting on condition [6f000..6fc3c]
        assertEquals("main", dump.getName());
        assertEquals("5", dump.getHeaderParameter("prio"));
        assertEquals("0x00285c40", dump.getHeaderParameter("tid"));
        assertEquals("0x438", dump.getHeaderParameter("nid"));
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());

    }

    public void testHPUX() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/HP/hp.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(75, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);

        SunStackLine stackLine = new SunStackLine("       at java.lang.Object.wait(Native Method)");
        assertTrue(stackLine.isLine());
        assertEquals("java.lang.Object", stackLine.getClassName());
        assertEquals("wait", stackLine.getMethodName());

        assertEquals("InactiveAgentCheckThread", dump.getName());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
        dump = statistic.getFullThreadDump(0).getThreadDump(4);
        assertEquals("ExecuteThread: '14' for queue: 'JmsDispatcher'", dump.getName());
        //"ExecuteThread: '14' for queue: 'JmsDispatcher'" daemon prio=10 tid=0x0003f690 nid=74 lwp_id=3573131 waiting on monitor [0x1f33a000..0x1f33a500]
        assertEquals("10", dump.getHeaderParameter("prio"));
        assertEquals("0x0003f690", dump.getHeaderParameter("tid"));
        assertEquals("74", dump.getHeaderParameter("nid"));
        assertEquals("3573131", dump.getHeaderParameter("lwp_id"));

        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }

    public void testSunStackLine() {
        ThreadStatistic statistic = new ThreadStatistic();
        SunStackLine line = new SunStackLine("	at java.lang.Object.wait(Native Method)");
        assertTrue(line.isNativeMethod());
        assertEquals("java.lang.Object", line.getClassName());
//        assertFalse(line.isCondition());
        assertTrue(line.isLine());
        assertFalse(line.isHoldingLock());
        assertFalse(line.isWaitingOn());
        assertFalse(line.isTryingToGetLock());
        assertEquals("wait", line.getMethodName());
        assertEquals("Native Method", line.getSource());
        assertEquals("n/a", line.getTarget());

        line = new SunStackLine("	- waiting on <0x67c76938> (a com.octetstring.vde.backend.standard.TransactionProcessor)");
        assertFalse(line.isNativeMethod());
        assertEquals("n/a", line.getClassName());
//        assertTrue(line.isCondition());
        assertFalse(line.isLine());
        assertFalse(line.isHoldingLock());
        assertTrue(line.isWaitingOn());
        assertFalse(line.isTryingToGetLock());
        assertEquals("n/a", line.getMethodName());
        assertEquals("n/a", line.getSource());
        assertEquals("<0x67c76938> (a com.octetstring.vde.backend.standard.TransactionProcessor)", line.getTarget());
    }
    private SunThreadDump threadDump = null;

    public void testDaemonThread() {
        threadDump = new SunThreadDump("\"Reference Handler\" daemon prio=10 tid=0x000915d0 nid=0x51c60 in Object.wait() [f0203000..f0203b70]");
        threadDump.addStackLine("\tat java.lang.Object.wait(Native Method)");
        threadDump.addStackLine(
                "\t- waiting on <0x67090190> (a java.lang.ref.Reference$Lock)");
        threadDump.addStackLine("\tat java.lang.Object.wait(Object.java:426)");
        threadDump.addStackLine(
                "\tat java.lang.ref.Reference$ReferenceHandler.run(Reference.java:113)");
        threadDump.addStackLine(
                "\t- locked <0x67090190> (a java.lang.ref.Reference$Lock)");
        assertEquals("tid", "0x000915d0", threadDump.getTid());
        assertEquals("nid", "0x51c60", threadDump.getNid());
        assertEquals("priority", 10, threadDump.getPriority());
        assertEquals("isDaemon", true, threadDump.isDaemon());
        assertEquals("thread name", "Reference Handler", threadDump.getName());
        assertEquals("stack range", "[f0203000..f0203b70]",
                threadDump.getStackRange());
//        assertEquals("state", "in Object.wait()",
//                threadDump.getState());
        assertEquals("size", 5, threadDump.size());

        samurai.core.SunStackLine line0 = (samurai.core.SunStackLine) threadDump.getLine(0);
        assertEquals("className", "java.lang.Object", line0.getClassName());
        System.out.println(line0.getMethodName());
        assertEquals("methodName", "wait", line0.getMethodName());
        assertEquals("line", "Native Method", line0.getLineNumber());
        assertEquals("line", "Native Method", line0.getSource());
//    assertEquals("waiting on", true,threadDump.isWaitingOn());
        assertEquals("waiting to lock", false, line0.isTryingToGetLock());
        assertEquals("lock", 1, threadDump.getLockedLines().size());

        samurai.core.SunStackLine line1 = (samurai.core.SunStackLine) threadDump.getLine(1);
        assertEquals("waiting on", "<0x67090190> (a java.lang.ref.Reference$Lock)", line1.getTarget());

        samurai.core.StackLine line2 = threadDump.getLine(2);
        assertEquals("className", "java.lang.Object", line2.getClassName());
        System.out.println(line2.getMethodName());
        assertEquals("methodName", "wait", line2.getMethodName());
        assertEquals("line", "426", line2.getLineNumber());
        assertEquals("line", "Object.java", line2.getSource());
        assertEquals("line", 1, threadDump.getLockedLines().size());
    }

    public void testNonDaemonThread() {
        threadDump = new SunThreadDump("\"Reference Handler daemon\" prio=10 tid=0x000915d0 nid=0x51c60 in Object.wait() ");
        assertEquals("isDaemon", false, threadDump.isDaemon());
        assertEquals("stack range", "",
                threadDump.getStackRange());

//        assertEquals("state", "in Object.wait()",
//                threadDump.getState());
    }

}
