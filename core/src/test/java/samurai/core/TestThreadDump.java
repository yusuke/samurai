/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.Properties;

public class TestThreadDump extends TestCase {

    public TestThreadDump(String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSunThreadDump() throws Exception{
        dig2(new File("testCases"+File.separator+"Sun"));
    }
    public void testBEAThreadDump() throws Exception{
        dig2(new File("testCases"+File.separator+"BEA"));
    }
    public void testAppleThreadDump() throws Exception{
        dig2(new File("testCases"+File.separator+"Apple"));
    }
    public void testHPThreadDump() throws Exception{
        dig2(new File("testCases"+File.separator+"HP"));
    }
    public void testIBMThreadDump() throws Exception{
        dig2(new File("testCases"+File.separator+"IBM"));
    }
    public void dig2(File dir) throws IOException {
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".dmp") || file.getName().endsWith(".dump")|| file.getName().endsWith(".txt");
            }
        }
        );
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
        File out  = new File(in.getAbsolutePath() + ".expected");
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
            assertEquals(examining + "ftd" + i + ".deadLockSize", String.valueOf(ftd.getDeadLockSize()), props.getProperty("ftd." + i + ".deadLockSize"));
            assertEquals(examining + "ftd" + i + ".threadCount", String.valueOf(ftd.getThreadCount()), props.getProperty("ftd." + i + ".threadCount"));
            for (int j = 0; j < ftd.getThreadCount(); j++) {
                ThreadDump td = ftd.getThreadDump(j);
                assertEquals(examining + "td." + i + j + ".blockedObjectId", String.valueOf(td.getBlockedObjectId()), props.getProperty("td." + i + "." + j + ".blockedObjectId"));
                assertEquals(examining + "td." + i + j + ".blockerId", String.valueOf(td.getBlockerId()), props.getProperty("td." + i + "." + j + ".blockerId"));
                assertEquals(examining + "td." + i + j + ".condition", String.valueOf(td.getCondition()), props.getProperty("td." + i + "." + j + ".condition"));
                assertEquals(examining + "td." + i + j + ".header", String.valueOf(td.getHeader()), props.getProperty("td." + i + "." + j + ".header"));
                assertEquals(examining + "td." + i + j + ".id", String.valueOf(td.getId()), props.getProperty("td." + i + "." + j + ".id"));
                assertEquals(examining + "td." + i + j + ".name", String.valueOf(td.getName()), props.getProperty("td." + i + "." + j + ".name"));
                assertEquals(examining + "td." + i + j + ".isBlocked", String.valueOf(td.isBlocked()), props.getProperty("td." + i + "." + j + ".isBlocked"));
                assertEquals(examining + "td." + i + j + ".isDaemon", String.valueOf(td.isDaemon()), props.getProperty("td." + i + "." + j + ".isDaemon"));
                assertEquals(examining + "td." + i + j + ".isDeadLocked", String.valueOf(td.isDeadLocked()), props.getProperty("td." + i + "." + j + ".isDeadLocked"));
                assertEquals(examining + "td." + i + j + ".isIdle", String.valueOf(td.isIdle()), props.getProperty("td." + i + "." + j + ".isIdle"));
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
                    for(StackLine line: td.getStackLines()){
                        pw.println("#" + line.toString());
                    }
                    pw.println("td." + i + "." + j + ".blockedObjectId=" + td.getBlockedObjectId());
                    pw.println("td." + i + "." + j + ".blockerId=" + td.getBlockerId());
                    pw.println("td." + i + "." + j + ".condition=" + td.getCondition());
                    pw.println("td." + i + "." + j + ".header=" + td.getHeader());
                    pw.println("td." + i + "." + j + ".id=" + td.getId());
                    pw.println("td." + i + "." + j + ".name=" + td.getName());
                    pw.println("td." + i + "." + j + ".isBlocked=" + td.isBlocked());
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

