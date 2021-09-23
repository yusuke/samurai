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

import java.io.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class TestThreadDump {


    @Test
    void testSunThreadDump() throws Exception {
        dig2("Sun");
    }

    @Test
    void testBEAThreadDump() throws Exception {
        dig2("BEA");
    }

    @Test
    void testAppleThreadDump() throws Exception {
        dig2("Apple");
    }

    @Test
    void testHPThreadDump() throws Exception {
        dig2("HP");
    }

    @Test
    void testIBMThreadDump() throws Exception {
        dig2("IBM");
    }

    @Test
    void testOpenJDKThreadDump() throws Exception {
        dig2("OpenJDK");
    }

    public void dig2(String path) throws IOException {
        System.out.println("digging:"+path);
        String base = "/" + path;
        InputStream resourceAsStream = TestThreadDump.class.getResourceAsStream(base);
        if (resourceAsStream == null) {
            System.out.println(path + " is not a directory.");
            return;
        }
        String[] split = new String(resourceAsStream.readAllBytes()).split("\n");
        for (String file : split) {
            if (!file.matches("^.*\\.(txt|dmp|expected)")) {
                dig2(path + "/" + file);
            } else if (file.matches("^.*\\.(txt|dmp)")) {
                examine(base + "/" + file);
            }
        }

    }

    /*package*/
    void examine(String in) throws IOException {

        String expected = in + ".expected";

        if (TestThreadDump.class.getResourceAsStream(expected) == null) {
            dumpAnalyzed(in);
        }

        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(TestThreadDump.class.getResourceAsStream(in));
        Properties props = new Properties();
        props.load(TestThreadDump.class.getResourceAsStream(expected));

        for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
            String examining = in + ":";
            FullThreadDump ftd = statistic.getFullThreadDump(i);
            assertEquals(props.getProperty("ftd." + i + ".deadLockSize"), String.valueOf(ftd.getDeadLockSize()), examining + "ftd" + i + ".deadLockSize");
            assertEquals(props.getProperty("ftd." + i + ".threadCount"), String.valueOf(ftd.getThreadCount()), examining + "ftd" + i + ".threadCount");
            for (int j = 0; j < ftd.getThreadCount(); j++) {
                ThreadDump td = ftd.getThreadDump(j);
                assertEquals(props.getProperty("td." + i + "." + j + ".blockedObjectId"), String.valueOf(td.getBlockedObjectId()), examining + "td." + i + "." + j + ".blockedObjectId");
                assertEquals(props.getProperty("td." + i + "." + j + ".blockerId"), String.valueOf(td.getBlockerId()), examining + "td." + i + "." + j + ".blockerId");
                assertEquals(props.getProperty("td." + i + "." + j + ".condition"), String.valueOf(td.getCondition()), examining + "td." + i + "." + j + ".condition");
                assertEquals(props.getProperty("td." + i + "." + j + ".header"), String.valueOf(td.getHeader()), examining + "td." + i + "." + j + ".header");
                assertEquals(props.getProperty("td." + i + "." + j + ".id"), String.valueOf(td.getId()), examining + "td." + i + "." + j + ".id");
                assertEquals(props.getProperty("td." + i + "." + j + ".name"), String.valueOf(td.getName()), examining + "td." + i + "." + j + ".name");
                assertEquals(props.getProperty("td." + i + "." + j + ".isBlocked"), String.valueOf(td.isBlocked()), examining + "td." + i + "." + j + ".isBlocked");
                assertEquals(props.getProperty("td." + i + "." + j + ".isBlocking"), String.valueOf(td.isBlocking()), examining + "td." + i + "." + j + ".isBlocking");
                assertEquals(props.getProperty("td." + i + "." + j + ".isDaemon"), String.valueOf(td.isDaemon()), examining + "td." + i + "." + j + ".isDaemon");
                assertEquals(props.getProperty("td." + i + "." + j + ".isDeadLocked"), String.valueOf(td.isDeadLocked()), examining + "td." + i + "." + j + ".isDeadLocked");
                assertEquals(props.getProperty("td." + i + "." + j + ".isIdle"), String.valueOf(td.isIdle()), examining + "td." + i + "." + j + ".isIdle");
            }
        }
    }

    /*package*/
    static void dumpAnalyzed(String in) throws IOException {
        dumpAnalyzed(in, in + ".expected");
    }

    /*package*/
    static void dumpAnalyzed(String in, String out) throws IOException {
        System.out.println("Analyzing: " + in);
        if (TestThreadDump.class.getResourceAsStream(out) != null) {
            System.out.println("Output exists, skipping: " + out);
        } else {
            ThreadStatistic statistic = new ThreadStatistic();
            ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
            dumpExtractor.analyze(TestThreadDump.class.getResourceAsStream(in));
            String workingDirectory = new File("").getAbsolutePath();
            String outDirectory = workingDirectory;
            if (!workingDirectory.endsWith("core")) {
                outDirectory += "/core";
            }
            outDirectory += "/src/test/resources" + out;
            PrintWriter pw = new PrintWriter(new FileOutputStream(outDirectory));

            for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
                FullThreadDump ftd = statistic.getFullThreadDump(i);
                pw.println("ftd." + i + ".deadLockSize=" + ftd.getDeadLockSize());
                pw.println("ftd." + i + ".threadCount=" + ftd.getThreadCount());
                for (int j = 0; j < ftd.getThreadCount(); j++) {
                    ThreadDump td = ftd.getThreadDump(j);
                    pw.println("#" + td.getHeader());
                    for (StackLine line : td.getStackLines()) {
                        pw.println("#" + line.toString());
                    }
                    pw.println("td." + i + "." + j + ".blockedObjectId=" + td.getBlockedObjectId());
                    pw.println("td." + i + "." + j + ".blockerId=" + td.getBlockerId());
                    pw.println("td." + i + "." + j + ".condition=" + td.getCondition());
                    pw.println("td." + i + "." + j + ".header=" + td.getHeader());
                    pw.println("td." + i + "." + j + ".id=" + td.getId());
                    pw.println("td." + i + "." + j + ".name=" + td.getName());
                    pw.println("td." + i + "." + j + ".isBlocked=" + td.isBlocked());
                    pw.println("td." + i + "." + j + ".isBlocking=" + td.isBlocking());
                    pw.println("td." + i + "." + j + ".isDaemon=" + td.isDaemon());
                    pw.println("td." + i + "." + j + ".isDeadLocked=" + td.isDeadLocked());
                    pw.println("td." + i + "." + j + ".isIdle=" + td.isIdle());
                    pw.println();
                }
            }
            pw.close();
            System.out.println("Done analyzing: " + out);
            System.out.println("Review and edit it if needed.");
        }
    }
}

