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

public class SingleLogWatcher {
    private List<LogMonitor> logMonitors = new ArrayList<LogMonitor>(0);
    private boolean killed = false;
    private boolean debug = false;
    private String encoding = System.getProperty("file.encoding");

    public SingleLogWatcher(File file,String encoding) {
        this.file = file;
        this.encoding = encoding;
        if (null != file && file.exists()) {
            openFile();
        }
    }
    public SingleLogWatcher(File file) {
        this(file,System.getProperty("file.encoding"));
    }

    public synchronized void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public synchronized void addLogMonitor(LogMonitor logMonitor) {
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
    private ByteArrayOutputStream line = new ByteArrayOutputStream(128);
    private RandomAccessFile raf;

    private boolean checking = true;

    private long bedtime = 0;

    /*package*/boolean isCheckingUpdate() {
        return checking;
    }

    private synchronized void openFile() {
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(filePointer);
        } catch (IOException ex) {
            onException(ex);
        }
        checking = false;
    }

    private boolean started = false;

    public synchronized void start() {
        if (!started) {
            Tailer.getTailer().addLogWatcher(this);
            started = true;
        } else {
            throw new IllegalStateException("already started.");
        }
    }

    /*package*/
    synchronized void checkUpdate() {
        if (!checking) {
            try {
                if (readLine(raf, line)) {
                    if (!hasStarted) {
                        logStarted();
                    }
                    onLine(line.toByteArray());
                    line.reset();
                } else {
                    checking = true;
                    filePointer = raf.getFilePointer();
                    raf.close();
                    bedtime = System.currentTimeMillis();
                    sleeping();
                }
            } catch (IOException ex) {
                onException(ex);
            }
        } else {
            //read pending file if exists
            if (file.exists()) {
                if (file.length() == filePointer) {
                    if (hasStarted && !hasEnded) {
                        if (1000 < (System.currentTimeMillis() - bedtime)) {
                            if (debug) {
                                log("no new line detected for 1 second");
                            }
                            logEnded();
                            bedtime = 0;
                        }
                    }
                } else {
                    //file size increased or decreased
                    if (file.length() > filePointer) {
                        //assuming lines are added
                        logContinued();
                    } else {
                        //size decreased
                        if (!hasEnded) {
                            logEnded();
                        }
                        filePointer = 0;
                        logStarted();
                    }
                    openFile();
                }
            } else {
                //file disappeared
                if (!hasEnded) {
                    logEnded();
                }
                filePointer = 0;
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

    private void onLine(byte[] line) {
        if (debug)
            log("onLine(" + new String(line) + ")");
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

    private void logStarted() {
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

    private void logEnded() {
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

    private void logContinued() {
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

    private void onException(IOException ioe) {
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

    private void sleeping() {
        if (!hasEnded && hasStarted) {
            if (debug)
                log("logWillEnd()");
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
        System.out.println("logWatcher:" + msg);
    }
}
