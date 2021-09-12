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
package samurai.tail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class LogWatcher extends Thread {
    private List<LogMonitor> logMonitors = new ArrayList<LogMonitor>(3);
    private boolean killed = false;
    private boolean debug = false;
    private List<File> pendingFiles = new ArrayList<File>(0);
    private List<File> newPendingFiles = null;
    private String encoding;

    public void setFiles(File[] files) {
        newPendingFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            newPendingFiles.add(files[i]);
        }
    }

    public void setFile(File file) {
        setFiles(new File[]{file});
    }

    public LogWatcher() {
        this(System.getProperty("file.encoding"));
    }

    public LogWatcher(String encoding) {
        super.setName("LogWatcher Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        this.encoding = encoding;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void addLogMonitor(LogMonitor logMonitor) {
        logMonitors.add(logMonitor);
    }

    public synchronized void kill() {
        if (!killed) {
            this.killed = true;
            synchronized (logMonitors) {
                logMonitors.clear();
            }
            try {
                this.wait();
            } catch (InterruptedException ignore) {
            }
        }
    }

    public boolean killed() {
        return killed;
    }

    private long filePointer = 0;
    private boolean hasEnded = false;
    private boolean hasStarted = false;
    private File file = null;
    boolean updateDetected = false;
    ByteArrayOutputStream line = new ByteArrayOutputStream(128);
    RandomAccessFile raf;
    int waitCount = 0;


    public void run() {
        log("analyze(" + file + ")");

        while (!killed) {
            checkUpdate();
        }
        log("dying");
        synchronized (this) {
            this.notify();
        }

    }

    public void checkUpdate() {
        if (debug)
            log("loop");
        if (null != newPendingFiles) {
            synchronized (this) {
                if (hasStarted && !hasEnded) {
                    logEnded();
                }
                hasStarted = false;
                waitCount = 0;
                pendingFiles = newPendingFiles;
                newPendingFiles = null;
            }
        }
        try {
            if (null != file) {
                if (file.exists()) {
                    raf = new RandomAccessFile(file, "r");
                    raf.seek(filePointer);

                    while (readLine(raf, line) && !killed) {
                        if (!hasStarted) {
                            logStarted();
                        }
                        onLine(line.toByteArray());
                        line.reset();
                    }
                    filePointer = raf.getFilePointer();
                    raf.close();
                }
            }
            updateDetected = false;
            do {
                //read pending file if exists
                if (pendingFiles.size() != 0) {
                    if (debug)
                        log("found pending file");
                    file = pendingFiles.remove(0);
                    filePointer = 0;
                    line = new ByteArrayOutputStream(128);
                    updateDetected = true;
                    hasEnded = false;
                    break;
                } else {
                    if (1 == waitCount) {
                        sleeping();
                    }
                    //skip sleep when pending file exists
                    try {
//                if (debug)
//                  log("sleep");
                        Thread.sleep(50);
                    } catch (InterruptedException ie) {
                    }
                }
                if (null != file && file.exists()) {
                    if (file.length() == filePointer) {
                        if (hasStarted && !hasEnded) {
                            if (debug)
                                log("waitcount:" + waitCount);
                            if (40 == waitCount++) {
                                if (debug) {
                                    log("no new line detected for 1 second");
                                }
                                logEnded();
                                waitCount = 0;
                            }
                        }
                    } else {
                        //file size increased or decreased
                        updateDetected = true;
                        if (file.length() > filePointer) {
                            //size increased
                            logContinued();
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
            } while (!updateDetected && !killed && null == newPendingFiles);
        } catch (IOException ex) {
            onException(ex);
        }
    }

    public final boolean readLine(RandomAccessFile raf, ByteArrayOutputStream line) throws IOException {
        int c = -1;
        boolean eol = false;
//    long lastFilePointer;
        while (!eol) {
//      lastFilePointer = raf.getFilePointer();
            c = raf.read();
            switch (c) {
                case -1:
                    return false;
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
//          lastFilePointer = raf.getFilePointer();
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

    //  private int i;
    private synchronized void onLine(byte[] line) {
        if (null == newPendingFiles) {
            if (debug)
                log("onLine(" + line + ")");
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.onLine(this.file, new String(line, encoding), this.filePointer);
                } catch (java.io.UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
            }
        }
    }

    private synchronized void logStarted() {
        if (null == newPendingFiles) {
            hasStarted = true;
            hasEnded = false;
            if (debug)
                log("logStarted()");
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.logStarted(this.file, this.filePointer);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
            }
        }
    }

    private synchronized void logEnded() {
        if (null == newPendingFiles) {
            hasEnded = true;
            if (debug)
                log("logEnded()");
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.logEnded(this.file, this.filePointer);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
            }
        }
    }

    private synchronized void logContinued() {
        if (null == newPendingFiles) {
            hasEnded = false;
            if (debug)
                log("logContinued()");
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.logContinued(this.file, this.filePointer);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
            }
        }
    }

    private synchronized void onException(IOException ioe) {
        if (null == newPendingFiles) {
            if (debug)
                log("onException()");
            for (LogMonitor monitor : logMonitors) {
                try {
                    monitor.onException(this.file, ioe);
                } catch (RuntimeException re) {
                    re.printStackTrace();
                }
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

    private void log(String msg) {
        if (debug) {
            System.out.println("logWatcher:" + msg);
        }
    }

}
