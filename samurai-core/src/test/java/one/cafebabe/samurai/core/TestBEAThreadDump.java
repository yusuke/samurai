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
package one.cafebabe.samurai.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class TestBEAThreadDump  {
    @Test 
    void test813JRockitweblogicadmin() throws IOException {
        final ThreadStatistic statistic = new ThreadStatistic();
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
        StackLine line = dump.getLine(0);
        assertTrue(line.isTryingToGetLock());
        assertEquals("java/lang/Object", line.getLockedClassName());
        assertEquals("13a92710", line.getLockedObjectId());
        assertEquals("0xb80", dump.getBlockerId());
        line = dump.getLine(6);
        assertTrue(line.isHoldingLock());
        assertEquals("java/lang/Object", line.getLockedClassName());
        assertEquals("13a92718", line.getLockedObjectId());

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

    }

}
