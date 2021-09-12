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
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestThreadDump {


    @Test
    void testSunThreadDump() throws Exception {
        dig2(new File("testCases" + File.separator + "Sun"));
    }

    @Test
    void testBEAThreadDump() throws Exception {
        dig2(new File("testCases" + File.separator + "BEA"));
    }

    @Test
    void testAppleThreadDump() throws Exception {
        dig2(new File("testCases" + File.separator + "Apple"));
    }

    @Test
    void testHPThreadDump() throws Exception {
        dig2(new File("testCases" + File.separator + "HP"));
    }

    @Test
    void testIBMThreadDump() throws Exception {
        dig2(new File("testCases" + File.separator + "IBM"));
    }

    public void dig2(File dir) throws IOException {
        File[] files = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".dmp") || file.getName().endsWith(".dump") || file.getName().endsWith(".txt")
        );
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                dig2(file);
            } else {
                examine(file);
            }
        }

    }

    /*package*/
    void examine(File in) throws IOException {
        File out = new File(in.getAbsolutePath() + ".expected");
        if (!out.exists()) {
            dumpAnalyzed(in);
        }

        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(in);
        Properties props = new Properties();
        props.load(new FileInputStream(out));

        for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
            String examining = in.getAbsolutePath() + ":";
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
    static void dumpAnalyzed(File in) throws IOException {
        dumpAnalyzed(in, new File(in.getAbsolutePath() + ".expected"));
    }

    /*package*/
    static void dumpAnalyzed(File in, File out) throws IOException {
        System.out.println("Analyzing: " + in.getAbsolutePath());
        if (out.exists()) {
            System.out.println("Output exists, skipping: " + out.getAbsolutePath());
        } else {
            ThreadStatistic statistic = new ThreadStatistic();
            ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
            dumpExtractor.analyze(in);
            PrintWriter pw = new PrintWriter(new FileOutputStream(out));

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
            System.out.println("Done analyzing: " + out.getAbsolutePath());
            System.out.println("Review and edit it if needed.");
        }
    }
}

