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
package one.cafebabe.samurai.web;

import one.cafebabe.samurai.core.ThreadDumpExtractor;
import one.cafebabe.samurai.core.ThreadDumpSequence;
import one.cafebabe.samurai.core.ThreadStatistic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;

@Execution(ExecutionMode.CONCURRENT)
class TestThreadFilter   {
    final ThreadStatistic statistic = new ThreadStatistic();

    @Test
    void testThreadFilter() throws IOException {

        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestThreadFilter.class.getResourceAsStream("/Apple/1.4.2_08Apple.dmp"));
        ThreadFilter filter = new ThreadFilter();
        filter.setThreadId("0x0050b3d0");
        filter.setMode(Constants.MODE_SEQUENCE);
        ThreadDumpSequence dumps = filter.doFilter(statistic);
        Assertions.assertEquals(3, dumps.size());
        Assertions.assertEquals("0x0050b3d0", dumps.get(0).getId());
        Assertions.assertEquals("DeadLockThread", dumps.get(0).getName());
        Assertions.assertFalse(dumps.get(0).isIdle());
        Assertions.assertTrue(dumps.get(0).isBlocked());
        Assertions.assertEquals("0x0050b3d0", dumps.get(1).getId());
        Assertions.assertEquals("DeadLockThread", dumps.get(1).getName());
        Assertions.assertFalse(dumps.get(1).isIdle());
        Assertions.assertTrue(dumps.get(1).isBlocked());
        Assertions.assertEquals("0x0050b3d0", dumps.get(2).getId());
        Assertions.assertEquals("DeadLockThread", dumps.get(2).getName());
        Assertions.assertFalse(dumps.get(2).isIdle());
        Assertions.assertTrue(dumps.get(2).isBlocked());

        filter.setMode(Constants.MODE_FULL);
        filter.setFullThreadIndex(0);
        dumps = filter.doFilter(statistic);
        Assertions.assertEquals(10, dumps.size());
        Assertions.assertEquals("0x0050b3d0", dumps.get(0).getId());
        Assertions.assertEquals("0x0050b110", dumps.get(1).getId());
        Assertions.assertEquals("0x0050af00", dumps.get(2).getId());
        Assertions.assertEquals("0x00508140", dumps.get(3).getId());
        Assertions.assertEquals("0x00506b20", dumps.get(4).getId());
        Assertions.assertEquals("0x005067f0", dumps.get(5).getId());
        Assertions.assertEquals("0x00500c70", dumps.get(6).getId());
        Assertions.assertEquals("0x00506070", dumps.get(7).getId());
        Assertions.assertEquals("0x005080b0", dumps.get(8).getId());
        Assertions.assertEquals("0x00500e00", dumps.get(9).getId());

        ThreadDumpSequence threadDumps = statistic.getStackTracesById("0x0050b3d0");
        Assertions.assertEquals(3, threadDumps.size());
        Assertions.assertEquals("0x0050b3d0", threadDumps.get(0).getId());
        Assertions.assertEquals("0x0050b3d0", threadDumps.get(1).getId());
        Assertions.assertEquals("0x0050b3d0", threadDumps.get(2).getId());
    }
}
