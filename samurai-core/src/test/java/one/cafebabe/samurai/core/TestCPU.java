/*
 * Copyright 2021 Yusuke Yamamoto
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Execution(ExecutionMode.CONCURRENT)
class TestCPU {

    @Test
    void testCpuUsage() throws IOException {
        final ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestCPU.class.getResourceAsStream("/Liberica/11/one.cafebabe.samurai.core.example.HighCPUExample-98091-2021-10-11.txt"));
        assertEquals(3, statistic.getFullThreadDumpCount());
        List<ThreadDumpSequence> stackTraces = statistic.getStackTracesAsArray();
        ThreadDumpSequence highCPU = stackTraces.get(9);
        assertNotNull(highCPU.cpuUsage(0));
        assertEquals(97, highCPU.cpuUsage(0));
        assertNotNull(highCPU.cpuUsage(1));
        assertEquals(97, highCPU.cpuUsage(1));
        assertNotNull(highCPU.cpuUsage(2));
        assertEquals(97, highCPU.cpuUsage(2));

        ThreadDumpSequence lowCPU = stackTraces.get(10);
        assertNotNull(lowCPU.cpuUsage(0));
        assertEquals(0, lowCPU.cpuUsage(0));
        assertNotNull(lowCPU.cpuUsage(1));
        assertEquals(0, lowCPU.cpuUsage(1));
        assertNotNull(lowCPU.cpuUsage(2));
        assertEquals(0, lowCPU.cpuUsage(2));
    }

    @Test
    void toMillis(){
        assertEquals(6380, ThreadDumpSequence.toMillis("6.38s"));
        assertEquals(34, ThreadDumpSequence.toMillis("34.47ms"));
        assertEquals(34, ThreadDumpSequence.toMillis("34.00ms"));
        assertEquals(34, ThreadDumpSequence.toMillis("34.49ms"));
        assertEquals(534, ThreadDumpSequence.toMillis("534.01ms"));
        assertEquals(135, ThreadDumpSequence.toMillis("134.50ms"));
        assertEquals(235, ThreadDumpSequence.toMillis("234.59ms"));
        assertEquals(435, ThreadDumpSequence.toMillis("434.99ms"));
    }

}
