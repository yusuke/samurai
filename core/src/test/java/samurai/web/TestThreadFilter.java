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
package samurai.web;

import junit.framework.TestCase;
import samurai.core.ThreadDumpExtractor;
import samurai.core.ThreadDumpSequence;
import samurai.core.ThreadStatistic;

import java.io.File;
import java.io.IOException;

public class TestThreadFilter  extends TestCase {
    final ThreadStatistic statistic = new ThreadStatistic();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testThreadFilter() throws IOException {

        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(new File("testcases/Apple/1.4.2_08Apple.dmp"));
        ThreadFilter filter = new ThreadFilter();
        filter.setThreadId("0x0050b3d0");
        filter.setMode(Constants.MODE_SEQUENCE);
        ThreadDumpSequence dumps = filter.doFilter(statistic);
        assertEquals(3, dumps.size());
        assertEquals("0x0050b3d0", dumps.get(0).getId());
        assertEquals("DeadLockThread", dumps.get(0).getName());
        assertFalse(dumps.get(0).isIdle());
        assertTrue(dumps.get(0).isBlocked());
        assertEquals("0x0050b3d0", dumps.get(1).getId());
        assertEquals("DeadLockThread", dumps.get(1).getName());
        assertFalse(dumps.get(1).isIdle());
        assertTrue(dumps.get(1).isBlocked());
        assertEquals("0x0050b3d0", dumps.get(2).getId());
        assertEquals("DeadLockThread", dumps.get(2).getName());
        assertFalse(dumps.get(2).isIdle());
        assertTrue(dumps.get(2).isBlocked());

        filter.setMode(Constants.MODE_FULL);
        filter.setFullThreadIndex(0);
        dumps = filter.doFilter(statistic);
        assertEquals(10, dumps.size());
        assertEquals("0x0050b3d0", dumps.get(0).getId());
        assertEquals("0x0050b110", dumps.get(1).getId());
        assertEquals("0x0050af00", dumps.get(2).getId());
        assertEquals("0x00508140", dumps.get(3).getId());
        assertEquals("0x00506b20", dumps.get(4).getId());
        assertEquals("0x005067f0", dumps.get(5).getId());
        assertEquals("0x00500c70", dumps.get(6).getId());
        assertEquals("0x00506070", dumps.get(7).getId());
        assertEquals("0x005080b0", dumps.get(8).getId());
        assertEquals("0x00500e00", dumps.get(9).getId());

        ThreadDumpSequence threadDumps = statistic.getStackTracesById("0x0050b3d0");
        assertEquals(3, threadDumps.size());
        assertEquals("0x0050b3d0", threadDumps.get(0).getId());
        assertEquals("0x0050b3d0", threadDumps.get(1).getId());
        assertEquals("0x0050b3d0", threadDumps.get(2).getId());
    }
}
