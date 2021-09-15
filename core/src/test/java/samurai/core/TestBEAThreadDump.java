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
package samurai.core;

import org.junit.jupiter.api.Test;
import samurai.web.VelocityHtmlRenderer;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestBEAThreadDump  {
    final samurai.core.ThreadStatistic statistic = new ThreadStatistic();

    /*    @Test\nvoid testJRockit142_03stacked() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.4.2_03BEAstacked.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(11, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(9);
        assertEquals("Thread-1",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        dump = statistic.getFullThreadDump(0).getThreadDump(10);
        assertEquals("Thread-2",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
    }
    @Test\nvoid testJRockit142_03idle() throws IOException {

        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.4.2_03BEAidle.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(9, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("Main Thread",dump.getName());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }
    @Test\nvoid testJRockit142_05stacked() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.4.2_05BEAstacked.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(11, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(9);
        assertEquals("Thread-1",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        dump = statistic.getFullThreadDump(0).getThreadDump(10);
        assertEquals("Thread-2",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
    }
    @Test\nvoid testJRockit142_05idle() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.4.2_05BEAidle.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(9, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("Main Thread",dump.getName());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }
    @Test\nvoid testJRockit150_03stacked() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.5.0_03BEAstacked.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(11, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(9);
        assertEquals("Thread-0",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        dump = statistic.getFullThreadDump(0).getThreadDump(10);
        assertEquals("Thread-1",dump.getName());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
    }
    @Test\nvoid testJRockit150_03idle() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/1.5.0_03BEAidle.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(9, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("Main Thread",dump.getName());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }

    @Test\nvoid testBEAStacked() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/jrockit81sp2_141_05stack.dmp"), false);
        assertEquals(1, statistic.getFullThreadDumpCount());
        assertEquals(8,statistic.getFullThreadDump(0).getThreadCount());
        assertTrue(statistic.getFullThreadDump(0).getThreadDump(5).isBlocked());
        assertFalse(statistic.getFullThreadDump(0).getThreadDump(5).isDaemon());
        assertTrue(statistic.getFullThreadDump(0).getThreadDump(6).isBlocked());
        assertTrue(statistic.getFullThreadDump(0).getThreadDump(0).isIdle());
        assertTrue(statistic.getFullThreadDump(0).getThreadDump(0).isDaemon());
        assertFalse(statistic.getFullThreadDump(0).getThreadDump(1).isIdle());
        assertFalse(statistic.getFullThreadDump(0).getThreadDump(1).isBlocked());
        assertTrue(statistic.getFullThreadDump(0).getThreadDump(3).isIdle());
    }

    @Test\nvoid testBEAStackLine(){
        BEAStackLine line = new BEAStackLine("    at java.lang.Thread.doYield(Native Method)@116C0A68");
        assertTrue(line.isNativeMethod());
        assertEquals("java.lang.Thread",line.getClassName());
        assertTrue(line.isLine());
        assertEquals("doYield",line.getMethodName());
        assertEquals("Native Method",line.getSource());

        line = new BEAStackLine("    at java.lang.Object.wait0(Unknown Source)@116C2103");
        assertFalse(line.isNativeMethod());
        assertEquals("java.lang.Object",line.getClassName());
        assertTrue(line.isLine());
        assertEquals("wait0",line.getMethodName());
        assertEquals("Unknown Source",line.getSource());
        assertEquals("Unknown Source",line.getLineNumber());

        line = new BEAStackLine("    at weblogic/kernel/ExecuteThread.waitForRequest(ExecuteThread.java:145)@19163A91");
        assertFalse(line.isNativeMethod());
        assertEquals("weblogic/kernel/ExecuteThread",line.getClassName());
        assertTrue(line.isLine());
        assertEquals("waitForRequest",line.getMethodName());
        assertEquals("ExecuteThread.java",line.getSource());
        assertEquals("145",line.getLineNumber());

    }

    @Test\nvoid test811JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin811JRockit.dmp"), false);
        assertEquals(4, statistic.getFullThreadDumpCount());
        assertEquals(42, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("ExecuteThread: '0' for queue: 'weblogic.kernel.Default'",dump.getName());
        assertEquals("0x80",dump.getId());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());
    }
//    @Test\nvoid test812JRockitweblogicadmin() throws IOException {
//        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
//        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin812JRockit.dmp"), false);
//        assertEquals(4, statistic.getFullThreadDumpCount());
//        assertEquals(42, statistic.getFullThreadDump(0).getThreadCount());
//        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
//        assertEquals("ExecuteThread: '0' for queue: 'weblogic.kernel.Default'",dump.getName());
//        assertEquals("0x80",dump.getId());
//        assertFalse(dump.isStacked());
//        assertTrue(dump.isIdle());
//    }*/
    @Test 
    void test813JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestBEAThreadDump.class.getResourceAsStream("/BEA/weblogic.admin813JRockit.dmp"));
        assertEquals(5, statistic.getFullThreadDumpCount());
        assertEquals(46, statistic.getFullThreadDump(0).getThreadCount());

        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDumpById("0xc80");
        assertEquals("ExecuteThread: '14' for queue: 'weblogic.kernel.Default'", dump.getName());
        assertEquals("0xb80", dump.getBlockerId());
        assertEquals("0xc80", dump.getId());
        assertTrue(dump.isBlocked());
        assertTrue(statistic.getFullThreadDump(0).isDeadLocked());
        assertTrue(dump.isDeadLocked());
        assertFalse(dump.isIdle());
        samurai.core.StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java/lang/Object", line.getLockedClassName());
        assertEquals("13a92710", line.getLockedObjectId());
        //noinspection HtmlUnknownAnchorTarget
        assertEquals("   -- Blocked trying to get lock: java/lang/Object@<a href=\"#13a92710\">13a92710</a>[thin lock]",new VelocityHtmlRenderer.Util().asHTML(line));
        assertEquals("0xb80", dump.getBlockerId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertTrue(new VelocityHtmlRenderer.Util().asHTML(line).contains("<a name"));
        assertEquals("java/lang/Object", line.getLockedClassName());
        assertTrue(new VelocityHtmlRenderer.Util().asHTML(line).contains("<a name"));
        assertEquals("13a92718", line.getLockedObjectId());
        System.out.println("[" + new VelocityHtmlRenderer.Util().asHTML(line) + "]");
        //noinspection HtmlDeprecatedAttribute
        assertEquals("   ^-- Holding lock: java/lang/Object@<a name=\"13a92718\"></a>13a92718[thin lock]", new VelocityHtmlRenderer.Util().asHTML(line));

        dump = statistic.getFullThreadDump(0).getThreadDumpById("0xb80");
        assertEquals("ExecuteThread: '12' for queue: 'weblogic.kernel.Default'", dump.getName());
        assertTrue(dump.isBlocked());
        assertEquals("0xc80", dump.getBlockerId());
        assertTrue(dump.isDeadLocked());
        assertFalse(dump.isIdle());

        dump = statistic.getFullThreadDump(0).getThreadDumpById("0xa80");
        assertEquals("ExecuteThread: '10' for queue: 'weblogic.kernel.Default'", dump.getName());
        assertTrue(dump.isBlocked());
        assertEquals("0xc80", dump.getBlockerId());
        assertFalse(dump.isDeadLocked());
        assertFalse(dump.isIdle());

    }/*
    @Test\nvoid test814JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin814JRockit.dmp"), false);
        assertEquals(6, statistic.getFullThreadDumpCount());
        assertEquals(50, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(1).getThreadDumpById("0x700");
        assertEquals("ExecuteThread: '3' for queue: 'weblogic.kernel.Default'",dump.getName());
        assertEquals("0x700",dump.getId());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        assertEquals("0xb80",dump.getBlockerId());

        StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java/lang/Object",line.getLockedClassName());
        assertEquals("43d07c8",line.getLockedObjectId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertEquals("java/lang/Object",line.getLockedClassName());
        assertEquals("43d07d0",line.getLockedObjectId());
    }
    @Test\nvoid test815JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin815JRockit.dmp"), false);
        assertEquals(3, statistic.getFullThreadDumpCount());
        assertEquals(46, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDumpById("0xb80");
        assertEquals("ExecuteThread: '12' for queue: 'weblogic.kernel.Default'",dump.getName());
        assertEquals("0xb80",dump.getId());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java/lang/Object",line.getLockedClassName());
        assertEquals("60ef830",line.getLockedObjectId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertEquals("java/lang/Object",line.getLockedClassName());
        assertEquals("60ef828",line.getLockedObjectId());
    }
    @Test\nvoid test900JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin900JRockit.dmp"), false);
        assertEquals(6, statistic.getFullThreadDumpCount());
        assertEquals(25, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("Main Thread",dump.getName());
        assertEquals("1",dump.getId());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());

        dump = statistic.getFullThreadDump(1).getThreadDumpById("17");
        assertEquals("[ACTIVE] ExecuteThread: '1' for queue: 'weblogic.kernel.Default (self-tuning)'",dump.getName());
        assertEquals("17",dump.getId());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java.lang.Object",line.getLockedClassName());
        assertEquals("847a42",line.getLockedObjectId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertEquals("java.lang.Object",line.getLockedClassName());
        assertEquals("847a41",line.getLockedObjectId());

    }
    @Test\nvoid test910JRockitweblogicadmin() throws IOException {
        ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
        analyzer.analyze(new File("testcases/weblogic.admin/weblogic.admin910JRockit.dmp"), false);
        assertEquals(1, statistic.getFullThreadDumpCount());
        assertEquals(29, statistic.getFullThreadDump(0).getThreadCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertEquals("Main Thread",dump.getName());
        assertEquals("1",dump.getId());
        assertFalse(dump.isBlocked());
        assertTrue(dump.isIdle());

        dump = statistic.getFullThreadDump(0).getThreadDumpById("28");
        assertEquals("[ACTIVE] ExecuteThread: '4' for queue: 'weblogic.kernel.Default (self-tuning)'",dump.getName());
        assertEquals("28",dump.getId());
        assertTrue(dump.isBlocked());
        assertFalse(dump.isIdle());
        StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java.lang.Object",line.getLockedClassName());
        assertEquals("99c0a5",line.getLockedObjectId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertEquals("java.lang.Object",line.getLockedClassName());
        assertEquals("99c099",line.getLockedObjectId());
    }
*/

}
