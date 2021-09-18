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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class TestLibericaThreadDump {

    @Test
    void stacklines() throws IOException {
        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestSpringBootActuatorJSONThreadDump.class.getResourceAsStream("/Liberica/11/deadlock-liberica11.dmp"));
        FullThreadDump fullThreadDump = statistic.getFullThreadDumps().get(0);
        ThreadDump deadLock1 = fullThreadDump.getThreadDumpById("0x00007fa89421a000");

        assertAll(() -> assertTrue(fullThreadDump.isDeadLocked())
                , () -> assertTrue(deadLock1.isBlocked())
                , () -> assertTrue(deadLock1.isBlocking())
                , () -> assertFalse(deadLock1.isIdle())
                , () -> assertTrue(deadLock1.isDeadLocked())
                , () -> {
                    List<StackLine> stackLines = deadLock1.getStackLines();
                    System.out.println(stackLines);
                    assertEquals("   java.lang.Thread.State: BLOCKED (on object monitor)", stackLines.get(0).line);
                    assertEquals("\tat samurai.core.DeadLockingThread.run(DeadLockingThreads.java:57)", stackLines.get(1).line);
                    assertEquals("\t- waiting to lock <0x000000061fcc9b90> (a java.lang.Object)", stackLines.get(2).line);
                    assertEquals("\t- locked <0x000000061fcc9b80> (a java.lang.Object)", stackLines.get(3).line);
                });
    }
}
