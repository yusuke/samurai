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
package samurai.util;

import junit.framework.TestCase;
import samurai.tail.LogMonitor;
import samurai.tail.LogWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class TestLogWatcher extends TestCase implements LogMonitor {
    LogWatcher logWatcher;
    boolean DEBUG = false;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestLogWatcher.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    StringBuffer buf;
    int logStartedCount = 0;
    int logEndedCount = 0;
    int logContinuedCount = 0;
    int logWillEndCount = 0;

    public void notestMultiFile() throws Exception {
        logWatcher = new LogWatcher();
        logWatcher.setFiles(new File[]{new File("testcases/1.txt"), new File("testcases/2.txt"), new File("testcases/3.txt")});
        buf = new StringBuffer();
        logWatcher.setDebug(DEBUG);
        logStartedCount = 0;
        logEndedCount = 0;
        logContinuedCount = 0;
        logWillEndCount = 0;
        logWatcher.addLogMonitor(new LogMonitor() {
            public void onLine(File file, String line, long filepointer) {
                buf.append(line);
            }

            public void onException(File file, IOException ioe) {
            }

            public void logStarted(File file, long filepointer) {
                logStartedCount++;
            }

            public void logEnded(File file, long filepointer) {
                logEndedCount++;
            }

            public void logContinued(File file, long filepointer) {
                logContinuedCount++;
            }

            public void logWillEnd(File file, long filepointer) {
                logWillEndCount++;
            }
        });
        logWatcher.start();
        Thread.sleep(3000);
        assertEquals("123", buf.toString());
        assertEquals(1, logStartedCount);
        assertEquals(0, logContinuedCount);
        assertEquals(1, logWillEndCount);
        assertEquals(1, logEndedCount);
        logWatcher.kill();
    }
    public void debug(String message){
        if(DEBUG){
            System.out.println(message);
        }

    }

    public void testSingleFile() throws Exception {
        resetFlags();
        File file = new File("hogehogehoge.txt");
        file.delete();
        logWatcher = new LogWatcher();
        logWatcher.setFile(file);
        logWatcher.addLogMonitor(this);
//    logWatcher.setFile(file);
        debug("testLogWatcher start");
        logWatcher.setDebug(DEBUG);

        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(fos);
        debug("testLogWatcher test1");
        assertFalse(logStartedCalled);
        assertFalse(logEndedCalled);
        assertFalse(logContinuedCalled);
        debug("testLogWatcher write");
        writer.println("log:1");
        writer.flush();
//    fail("hoge");
        debug("testLogWatcher start");
        logWatcher.start();
        Thread.sleep(200);
        debug("testLogWatcher test2");
        assertEquals("log:1", onLine);
        assertTrue(logStartedCalled);
        assertFalse(logEndedCalled);
        Thread.sleep(3000);
        assertTrue(logEndedCalled);
        resetFlags();
        debug("testLogWatcher write2");
        writer.println("log:2");
        writer.println("log:2");
        writer.println("log:2");
        writer.flush();
        writer.close();
        fos.close();
        Thread.sleep(3000);

        debug("testLogWatcher test3");
        assertEquals("log:2", onLine);
        assertTrue(logContinuedCalled);
//    assertTrue(logStartedCalled);
        assertTrue(logEndedCalled);
        Thread.sleep(300);
        assertTrue(file.delete());
        assertFalse(file.exists());

        resetFlags();
        fos = new FileOutputStream(file);
        writer = new PrintWriter(fos);
        assertFalse(logStartedCalled);
        assertFalse(logEndedCalled);
        assertFalse(logContinuedCalled);
        writer.println("log:1");
        writer.println("log:1");
        writer.println("log:1");
        Thread.sleep(200);
        assertTrue(logStartedCalled);
        writer.flush();
        writer.close();
        Thread.sleep(500);
        assertEquals("log:1", onLine);
        logWatcher.kill();
    }

    String onLine = null;
    private boolean logStartedCalled = false;
    private boolean logEndedCalled = false;
    private boolean logContinuedCalled = false;
    private IOException ioe;

    private void resetFlags() {
        logStartedCalled = false;
        logEndedCalled = false;
        logContinuedCalled = false;
        ioe = null;
        onLine = null;
    }

    public void onLine(File file, String line, long filePointer) {
        onLine = line;
        debug("testLogWatcher online:" + onLine);
    }

    public void onException(File file, IOException ioe) {
        ioe.printStackTrace();
        this.ioe = ioe;
    }

    public void logStarted(File file, long filePointer) {
        logStartedCalled = true;
    }

    public void logEnded(File file, long filePointer) {
        logEndedCalled = true;
    }

    public void logContinued(File file, long filePointer) {
        logContinuedCalled = true;
    }

    public void logWillEnd(File file, long filePointer) {
        //does noting
//    logWatcher.destroy();
    }
}

