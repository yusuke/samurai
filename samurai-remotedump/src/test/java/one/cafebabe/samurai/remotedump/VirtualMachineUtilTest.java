/*
 *  Copyright 2021 Yusuke Yamamoto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  Distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package one.cafebabe.samurai.remotedump;

import com.sun.tools.attach.AttachNotSupportedException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static one.cafebabe.samurai.remotedump.VirtualMachineUtil.getGCLogPath;
import static org.junit.jupiter.api.Assertions.*;

class VirtualMachineUtilTest {
    // https://docs.oracle.com/javase/jp/10/jrockit-hotspot/logging.htm
//    -Xlog:gc:file=
    //  -Xlog:gc:
    //  -Xloggc:

    // -Xlog:<opts>

    Process launchThreadDumpUtilTestMainInASeparateProcess(@Nullable String vmOption) throws IOException {
        String javaCommand = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        ProcessBuilder command = vmOption == null ?
                new ProcessBuilder().command(javaCommand, "-classpath", classpath, "one.cafebabe.samurai.remotedump.InfiniteGC") :
                new ProcessBuilder().command(javaCommand, vmOption, "-classpath", classpath, "one.cafebabe.samurai.remotedump.InfiniteGC");
        command.inheritIO();
        Process start = command.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }

        return start;
    }

    

    @Test
    void preconfigured() throws IOException, InterruptedException, AttachNotSupportedException {
        {
            File tempFile = File.createTempFile("temp", "temp");
            //-XX:+UnlockDiagnosticVMOptions 
            String vmOption = "-Xlog:gc=info:" + tempFile.getAbsolutePath();
            assertGCLogDetects(tempFile, vmOption);
        }
        {
            File tempFile = File.createTempFile("temp", "temp");
            //-XX:+UnlockDiagnosticVMOptions 
            String vmOption = "-Xloggc:" + tempFile.getAbsolutePath();
            assertGCLogDetects(tempFile, vmOption);
        }
    }

    private void assertGCLogDetects(File tempFile, String vmOption) throws IOException, AttachNotSupportedException, InterruptedException {
        Process process = null;
        try {
            tempFile.deleteOnExit();
            assertEquals(0, tempFile.length());
            process = launchThreadDumpUtilTestMainInASeparateProcess(vmOption);
            assertEquals(tempFile.getAbsolutePath(), getGCLogPath(process.pid()));
            Thread.sleep(1000);
            process.destroy();
//            // The file contains at least one line like:
//            // [0.975s][info][gc] GC(9) Pause Young (Normal) (G1 Evacuation Pause) 366M->0M(616M) 0.981ms
            assertTrue(Files.readAllLines(tempFile.toPath()).stream().anyMatch(e -> e.contains("[info][gc")));
            assertTrue(tempFile.length() != 0);
            process.waitFor();
        } finally {
            if (process != null) {
                process.destroy();
            }

        }
    }

    @Test
    void configureDynamically() throws IOException, InterruptedException, AttachNotSupportedException {
        Process process = null;
        try {
            File tempFile = File.createTempFile("temp", "temp");

            assertEquals(0, tempFile.length());
            process = launchThreadDumpUtilTestMainInASeparateProcess(null);
            assertNull(getGCLogPath(process.pid()));
            VirtualMachineUtil.setGCLogPath(process.pid(), tempFile.getAbsolutePath());
            assertEquals(tempFile.getAbsolutePath(), getGCLogPath(process.pid()));
            Thread.sleep(1000);
            process.destroy();
            // The file contains at least one line like:
            // [0.975s][info][gc] GC(9) Pause Young (Normal) (G1 Evacuation Pause) 366M->0M(616M) 0.981ms
            assertTrue(Files.readAllLines(tempFile.toPath())
                    .stream().anyMatch(e -> e.contains("[info][gc")));
            assertTrue(tempFile.length() != 0);
            process.waitFor();
            tempFile.deleteOnExit();
        } finally {
            if (process != null) {
                process.destroy();
            }

        }

    }

    @Test
    void extractLogFileName() {
        {
            String out = "jcmd 85954 VM.log list                                     \n" +
                    "85954:\n" +
                    "Available log levels: off, trace, debug, info, warning, error\n" +
                    "Available log decorators: time (t), utctime (utc), uptime (u), timemillis (tm), uptimemillis (um), timenanos (tn), uptimenanos (un), hostname (hn), pid (p), tid (ti), level (l), tags (tg)\n" +
                    "Available log tags: add, age, alloc, annotation, aot, arguments, attach, barrier, biasedlocking, blocks, bot, breakpoint, bytecode, cds, census, class, classhisto, cleanup, codecache, compaction, compilation, constantpool, constraints, container, coops, cpu, cset, data, datacreation, dcmd, decoder, defaultmethods, director, dump, ergo, event, exceptions, exit, fingerprint, free, freelist, gc, handshake, hashtables, heap, humongous, ihop, iklass, init, inlining, interpreter, itables, jfr, jit, jni, jvmti, liveness, load, loader, logging, malloc, mark, marking, membername, memops, metadata, metaspace, methodcomparator, mirror, mmu, module, monitorinflation, monitormismatch, nestmates, nmethod, normalize, objecttagging, obsolete, oldobject, oom, oopmap, oops, oopstorage, os, pagesize, parser, patch, path, perf, phases, plab, preorder, preview, promotion, protectiondomain, purge, redefine, ref, refine, region, reloc, remset, resolve, safepoint, sampling, scavenge, setting, smr, stackmap, stacktrace, stackwalk, start, startuptime, state, stats, stringdedup, stringtable, subclass, survivor, sweep, system, table, task, thread, time, timer, tlab, tracking, unload, unshareable, update, verification, verify, vmoperation, vmthread, vtables, vtablestubs, workgang\n" +
                    "Described tag sets:\n" +
                    " logging: Logging for the log framework itself\n" +
                    "Log output configuration:\n" +
                    " #0: stdout all=info uptime,level,tags (reconfigured)\n" +
                    " #1: stderr all=off uptime,level,tags\n" +
                    " #2: file=/tmp/gc.log all=off,gc=info uptime,level,tags filecount=5,filesize=20480K (reconfigured)\n" +
                    " #3: file=/tmp/class.log all=off,class=info uptime,level,tags filecount=5,filesize=20480K (reconfigured)";
            assertEquals("/tmp/gc.log", VirtualMachineUtil.extractGClogFileName(out));
        }
        {
            String out = "86433:\n" +
                    "Available log levels: off, trace, debug, info, warning, error\n" +
                    "Available log decorators: time (t), utctime (utc), uptime (u), timemillis (tm), uptimemillis (um), timenanos (tn), uptimenanos (un), hostname (hn), pid (p), tid (ti), level (l), tags (tg)\n" +
                    "Available log tags: add, age, alloc, annotation, aot, arguments, attach, barrier, biasedlocking, blocks, bot, breakpoint, bytecode, cds, census, class, classhisto, cleanup, codecache, compaction, compilation, constantpool, constraints, container, coops, cpu, cset, data, datacreation, dcmd, decoder, defaultmethods, director, dump, ergo, event, exceptions, exit, fingerprint, free, freelist, gc, handshake, hashtables, heap, humongous, ihop, iklass, init, inlining, interpreter, itables, jfr, jit, jni, jvmti, liveness, load, loader, logging, malloc, mark, marking, membername, memops, metadata, metaspace, methodcomparator, mirror, mmu, module, monitorinflation, monitormismatch, nestmates, nmethod, normalize, objecttagging, obsolete, oldobject, oom, oopmap, oops, oopstorage, os, pagesize, parser, patch, path, perf, phases, plab, preorder, preview, promotion, protectiondomain, purge, redefine, ref, refine, region, reloc, remset, resolve, safepoint, sampling, scavenge, setting, smr, stackmap, stacktrace, stackwalk, start, startuptime, state, stats, stringdedup, stringtable, subclass, survivor, sweep, system, table, task, thread, time, timer, tlab, tracking, unload, unshareable, update, verification, verify, vmoperation, vmthread, vtables, vtablestubs, workgang\n" +
                    "Described tag sets:\n" +
                    " logging: Logging for the log framework itself\n" +
                    "Log output configuration:\n" +
                    " #0: stdout all=warning uptime,level,tags\n" +
                    " #1: stderr all=off uptime,level,tags\n" +
                    " #2: file=/tmp/all.log all=info uptime,level,tags filecount=5,filesize=20480K (reconfigured)\n";
            assertEquals("/tmp/all.log", VirtualMachineUtil.extractGClogFileName(out));
        }

    }

}