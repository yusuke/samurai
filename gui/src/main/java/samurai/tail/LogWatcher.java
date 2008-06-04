package samurai.tail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class LogWatcher extends Thread {
    private List<LogMonitor> logMonitors = new ArrayList<LogMonitor>(3);
    private boolean killed = false;
    private boolean debug = false;
    private List<File> pendingFiles = new ArrayList<File>(0);
    private List<File> newPendingFiles = null;
    private String encoding = System.getProperty("file.encoding");

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
        init();
    }

    private void init() {
//    file = pendingFiles.remove(0);
        super.setName("LogWatcher Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        encoding = System.getProperty("file.encoding");
    }
//  public LogWatcher(File file) {
//    this(new File[]{file});
//  }
//  public LogWatcher(File file,String encoding) {
//    this(new File[]{file});
//    this.encoding = encoding;
//  }
//  public LogWatcher(File[] files) {
//    pendingFiles = new ArrayList<File>();
//    for (int i = 0; i < files.length; i++) {
//      pendingFiles.add(files[i]);
//    }
//    init();
//  }
//  public LogWatcher(File[] files,String encoding) {
//    this(files);
//    this.encoding = encoding;
//  }
//  public LogWatcher(List<File> files){
//    pendingFiles = files;
//    init();
//  }
//  public LogWatcher(List<File> files,String encoding){
//    this(files);
//    this.encoding = encoding;
//  }

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
//  private final OPENED = 0;
//  private final
//  int status

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
//              line = new ByteArrayOutputStream(128);
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
