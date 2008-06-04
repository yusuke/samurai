package samurai.core;

import junit.framework.TestCase;
import junit.textui.TestRunner;

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
public class TestIBMThreadDump extends TestCase {
    ThreadStatistic statistic = new samurai.core.ThreadStatistic();

    public TestIBMThreadDump(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(TestIBMThreadDump.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    public void testIBM140idle() throws IOException {
//      statistic = new ThreadStatistic();
//        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
//        analyzer.analyze(new File("testcases/1.4.0IBMidle.dmp"), false);
//        assertEquals(1, statistic.getFullThreadDumpCount());
//        assertEquals(5, statistic.getFullThreadDump(0).getThreadCount());
//        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(1);
//        assertEquals("Finalizer",dump.getName());
//        assertFalse(dump.isBlocked());
//        assertTrue(dump.isIdle());
//        //3XMTHREADINFO      "Finalizer" (TID:0x9108C0, sys_thread_t:0x22943A20, state:CW, native ID:0x9B8) prio=8
//        assertEquals("0x9108C0",dump.getHeaderParameter("TID"));
//        assertEquals("0x22943A20",dump.getHeaderParameter("sys_thread_t"));
//        assertEquals("CW",dump.getHeaderParameter("state"));
//        assertEquals("0x9B8",dump.getHeaderParameter("native ID"));
//        assertEquals("8",dump.getHeaderParameter("prio"));
//
//        dump = statistic.getFullThreadDump(0).getThreadDump(4);
//        assertEquals("main",dump.getName());
//        assertFalse(dump.isBlocked());
//        assertTrue(dump.isIdle());
//        //3XMTHREADINFO      "main" (TID:0x9109C8, sys_thread_t:0x284E48, state:CW, native ID:0x990) prio=5
//        assertEquals("0x9109C8",dump.getHeaderParameter("TID"));
//        assertEquals("0x284E48",dump.getHeaderParameter("sys_thread_t"));
//        assertEquals("CW",dump.getHeaderParameter("state"));
//        assertEquals("0x990",dump.getHeaderParameter("native ID"));
//        assertEquals("5",dump.getHeaderParameter("prio"));
//
//
//    }

    /*      public void testIBM140stacked() throws IOException {
      statistic = new ThreadStatistic();
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.4.0IBMstacked.dmp"), false);
        assertEquals(1, statistic.getFullThreadDumpCount());
        assertEquals(7, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(1);
        assertEquals("Thread-2",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        assertEquals("- locked &lt;<a name=\"9B3D78/9B3D80\"></a>9B3D78/9B3D80&gt; (java.lang.Object)",dump.getLine(1).asHTML());


        dump = statistic.getFullThreadDump(0).getThreadDump(2);
        assertEquals("Thread-1",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());

        dump = statistic.getFullThreadDump(0).getThreadDump(6);

        assertEquals("Signal dispatcher",dump.getName());
        assertFalse(dump.isBlocked());
        assertFalse(dump.isIdle());
        ThreadDumpSequence traces =  statistic.getStackTracesById("0x880E28");
        assertEquals(1,traces.size());
    }*/
    public void testIBM142racing() throws IOException {
        statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/IBM/1.4.2IBM/javacore.20060511.172914.516.txt"));
        assertEquals(1, statistic.getFullThreadDumpCount());
        assertEquals(47, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDumpById("0x6BB3598");
        assertEquals("ExecuteThread: '9' for queue: 'weblogic.kernel.Default'", dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
//        assertEquals("- locked &lt;<a name=\"2AD2BF8/2AD2C00\"></a>9B3D78/9B3D80&gt; (java.lang.Class)", dump.getLine(1).asHTML());

        dump = statistic.getFullThreadDump(0).getThreadDumpById("0x6ECB1E8");
        assertEquals("ExecuteThread: '6' for queue: 'weblogic.kernel.Default'", dump.getName());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());

    }
//    public void testIBMLockInfos(){
//      IBMLockInfos lockInfos = new IBMLockInfos(lockInfoLine[0]);
//      for (int i = 01; i < lockInfoLine.length; i++) {
//        lockInfos.addLine(lockInfoLine[i]);
//      }
//      assertEquals(2,lockInfos.size());
//      assertTrue(lockInfos.isWaiting("0x22AAACA8"));
//      assertTrue(lockInfos.isWaiting("0x22AAAED0"));
//      assertFalse(lockInfos.isWaiting("0x00284CC0"));
//
//      LockInfo lockInfo = lockInfos.getLockInfo(0);
//      assertEquals("java.lang.Object",lockInfo.getClassName());
//      assertEquals("9B3D78/9B3D80",lockInfo.getHash());
//      assertEquals("0x22AAAED0",lockInfo.getOwner());
//      lockInfo = lockInfos.getLockInfo(1);
//      assertEquals("java.lang.Object",lockInfo.getClassName());
//      assertEquals("9B3D88/9B3D90",lockInfo.getHash());
//      assertEquals("0x22AAACA8",lockInfo.getOwner());
//
//    }

    String[] lockInfoLine = {
            "1LKMONPOOLDUMP Monitor Pool Dump (flat & inflated object-monitors):",
            "2LKMONINUSE      sys_mon_t:0x0028E7F8 infl_mon_t: 0x0028E2D0:",
            "3LKMONOBJECT       java.lang.ref.Reference$Lock@920900/920908: <unowned>",
            "3LKNOTIFYQ            Waiting to be notified:",
            "3LKWAITNOTIFY            \"Reference Handler\" (0x2292BF28)",
            "2LKMONINUSE      sys_mon_t:0x0028E888 infl_mon_t: 0x0028E320:",
            "3LKMONOBJECT       java.lang.ref.ReferenceQueue$Lock@920638/920640: <unowned>",
            "3LKNOTIFYQ            Waiting to be notified:",
            "3LKWAITNOTIFY            \"Finalizer\" (0x22943A20)\",",
            "2LKMONINUSE      sys_mon_t:0x0028E8D0 infl_mon_t: 0x00000000:\",",
            "3LKMONOBJECT       java.lang.Object@9B3D78/9B3D80: Flat locked by thread ident 0x08, entry count 1",
            "3LKNOTIFYQ            Waiting to be notified:\",",
            "3LKWAITNOTIFY            \"Thread-1\" (0x22AAACA8)\",",
            "2LKMONINUSE      sys_mon_t:0x0028E918 infl_mon_t: 0x00000000:\",",
            "3LKMONOBJECT       java.lang.Object@9B3D88/9B3D90: Flat locked by thread ident 0x07, entry count 1",
            "3LKNOTIFYQ            Waiting to be notified:\",",
            "3LKWAITNOTIFY            \"Thread-2\" (0x22AAAED0)\",",
            "NULL           ",
            "1LKREGMONDUMP  JVM System Monitor Dump (registered monitors):",
            "2LKREGMON          JITC CHA lock: <unowned>",
            "2LKREGMON          JITC Global_Compile lock: <unowned>",
            "2LKREGMON          Evacuation Region lock: <unowned>",
            "2LKREGMON          Heap Promotion lock: <unowned>",
            "2LKREGMON          Sleep lock: <unowned>",
            "2LKREGMON          Method trace lock: <unowned>",
            "2LKREGMON          Heap lock: owner \"Signal dispatcher\" (0x880E28), entry count 1",
            "2LKREGMON          Monitor Cache lock: owner \"Signal dispatcher\" (0x880E28), entry count 1",
            "2LKREGMON          JNI Pinning lock: <unowned>",
            "2LKREGMON          JNI Global Reference lock: <unowned>",
            "2LKREGMON          Classloader lock: <unowned>",
            "2LKREGMON          Binclass lock: <unowned>",
            "2LKREGMON          Thread queue lock: owner \"Signal dispatcher\" (0x880E28), entry count 1",
            "3LKNOTIFYQ            Waiting to be notified:",
            "3LKWAITNOTIFY            \"Thread-3\" (0x284E48)",
            "2LKREGMON          Monitor Registry lock: owner \"Signal dispatcher\" (0x880E28), entry count 1",
            "NULL           ",
            "1LKFLATMONDUMP Thread identifiers (as used in flat monitors):",
            "2LKFLATMON         ident 0x02 \"Thread-3\" (0x284E48) ee 0x00284CC0",
            "2LKFLATMON         ident 0x08 \"Thread-2\" (0x22AAAED0) ee 0x22AAAD48",
            "2LKFLATMON         ident 0x07 \"Thread-1\" (0x22AAACA8) ee 0x22AAAB20",
            "2LKFLATMON         ident 0x06 \"JIT PProfiler thread\" (0x22A572C8) ee 0x22A57140",
            "2LKFLATMON         ident 0x05 \"Finalizer\" (0x22943A20) ee 0x22943898",
            "2LKFLATMON         ident 0x04 \"Reference Handler\" (0x2292BF28) ee 0x2292BDA0",
            "2LKFLATMON         ident 0x03 \"Signal dispatcher\" (0x880E28) ee 0x00880CA0"};
}
