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

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestAppleThreadDump  {
    final samurai.core.ThreadStatistic statistic = new samurai.core.ThreadStatistic();

    @Test
    void testApple142_08() throws IOException {
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
    @Test
    void testWLS81SP1() throws IOException {
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Apple/wls81sp1-1.4.2-34apple.dmp"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        ThreadDump dump = statistic.getFullThreadDump(0).getThreadDump(0);
        assertTrue(dump.isIdle());
    }


}
