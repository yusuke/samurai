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
package one.cafebabe.samurai.tail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogWatcherThread extends Thread {
    private static final Logger logger = LogManager.getLogger();

    private final List<LogMonitor> logMonitors = new ArrayList<>(0);
    private boolean killed = false;
    private List<File> pendingFiles;
    private String encoding = System.getProperty("file.encoding");

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setFiles(File[] files) {
        pendingFiles = new ArrayList<>(files.length);
        pendingFiles.addAll(Arrays.asList(files));
    }

    public void appendFiles(File[] files) {
        pendingFiles.addAll(Arrays.asList(files));
    }

    public void setFile(File file) {
        setFiles(new File[]{file});
    }

    public LogWatcherThread() {
        init();
    }

    private void init() {
        super.setName("LogWatcher Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        encoding = System.getProperty("file.encoding");
    }

    public void addLogMonitor(LogMonitor logMonitor) {
        logMonitors.add(logMonitor);
    }

    public synchronized void kill() {
        if (killed) {
            throw new IllegalStateException("Thread has already killed.");
        }
        this.killed = true;
    }

    public boolean isDead() {
        return killed;
    }

    private long filePointer = 0;
    private boolean hasEnded = false;
    private boolean hasStarted = false;
    private File file = null;
    private final ByteArrayOutputStream line = new ByteArrayOutputStream(128);
    private RandomAccessFile raf;

    public void run() {
        while (!this.isDead()) {
            checkUpdate();
            if (this.isCheckingUpdate()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }

            }
        }
    }

    private final int NOT_OPENED = 0;
    private final int CHECKING_UPDATE = 1;
    private final int OPENED = 2;
    private int state = NOT_OPENED;

    private long bedtime = 0;

    /*package*/
    final boolean isCheckingUpdate() {
        return state == CHECKING_UPDATE;
    }

    private synchronized void openFile() {
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(filePointer);
        } catch (IOException ex) {
            onException(ex);
        }
        state = OPENED;
    }

    public void checkUpdate() {
        switch (state) {
            case NOT_OPENED:
                if (pendingFiles.size() != 0) {
                    logger.debug("found pending file");
                    file = pendingFiles.remove(0);
                    filePointer = 0;
                    line.reset();
                    hasEnded = false;
                    if (null != file && file.exists()) {
                        openFile();
                        break;
                    }
                }

                state = CHECKING_UPDATE;
            case CHECKING_UPDATE:
                //read pending file if exists
                if (0 == bedtime) {
                    bedtime = System.currentTimeMillis();
                    sleeping();
                }
                if (file.exists()) {
                    if (file.length() == filePointer) {
                        if (hasStarted && !hasEnded) {
                            if (1000 < (System.currentTimeMillis() - bedtime)) {
                                logger.debug("no new line detected for 1 second");
                                logEnded();
                                bedtime = 0;
                            }
                        }
                        if (pendingFiles.size() != 0) {
                            state = NOT_OPENED;
                        }
                    } else {
                        //file size increased or decreased
                        state = NOT_OPENED;
                        if (file.length() > filePointer) {
                            //assuming lines are added
                            logContinued();
                            openFile();
                        } else {
                            //size decreased
                            if (!hasEnded) {
                                logEnded();
                            }
                            filePointer = 0;
                            logStarted();
                        }
                    }
                } else {
                    //file disappeared
                    if (!hasEnded) {
                        logEnded();
                    }
                    filePointer = 0;
                }
                break;
            case OPENED:
                try {
                    if (readLine(raf, line)) {
                        if (!hasStarted) {
                            logStarted();
                        }
                        onLine(line.toByteArray());
                        line.reset();
                    } else {
                        state = NOT_OPENED;
                        filePointer = raf.getFilePointer();
                        raf.close();
                    }
                } catch (IOException ex) {
                    onException(ex);
                }

        }
    }

    private int c = -1;

    public final boolean readLine(RandomAccessFile raf, ByteArrayOutputStream line) throws IOException {
        boolean eol = false;
        while (!eol) {
            c = raf.read();
            switch (c) {
                case -1:
                    return false;
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    if ((raf.read()) != '\n') {
                        raf.seek(raf.getFilePointer() - 1);
                    }
                    break;
                default:
                    line.write(c);
                    break;
            }
        }
        return eol;
    }

    private synchronized void onLine(byte[] line) {
        logger.debug("onLine({})",  line);
        for (LogMonitor monitor : logMonitors) {
            try {
                monitor.onLine(this.file, new String(line, encoding), this.filePointer);
            } catch (UnsupportedEncodingException | RuntimeException uee) {
                uee.printStackTrace();
            }
        }
    }

    private synchronized void logStarted() {
        hasStarted = true;
        hasEnded = false;
        logger.debug("logStarted()");
        for (LogMonitor monitor : logMonitors) {
            try {
                monitor.logStarted(this.file, this.filePointer);
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }

    private synchronized void logEnded() {
        hasEnded = true;
        logger.debug("logEnded()");
        for (LogMonitor monitor : logMonitors) {
            try {
                monitor.logEnded(this.file, this.filePointer);
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }

    private synchronized void logContinued() {
        hasEnded = false;
        logger.debug("logContinued()");
        for (LogMonitor monitor : logMonitors) {
            try {
                monitor.logContinued(this.file, this.filePointer);
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }

    private synchronized void onException(IOException ioe) {
        logger.debug("onException()");
        for (LogMonitor monitor : logMonitors) {
            try {
                monitor.onException(this.file, ioe);
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }

    private synchronized void sleeping() {
        if (!hasEnded && hasStarted) {
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.logWillEnd(this.file, this.filePointer);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
            }
        }
    }
}
