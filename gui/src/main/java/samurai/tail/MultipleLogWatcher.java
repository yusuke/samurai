package samurai.tail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class MultipleLogWatcher implements LogMonitor {
    private SingleLogWatcher currentLogWatcher = null;
    private List<File> files;
    private List<LogMonitor> logMonitors = new ArrayList<LogMonitor>();

    public MultipleLogWatcher(File[] files) {
        this.files = new ArrayList<File>(files.length);
        for (int i = 0; i < files.length; i++) {
            this.files.add(files[i]);
        }
    }

    boolean ticket = true;

    public synchronized void addFiles(File[] files) {
        for (int i = 0; i < files.length; i++) {
            this.files.add(files[i]);
        }
        synchronized (currentLogWatcher) {
            if (currentLogWatcher.isCheckingUpdate()) {
                nextLogWatcher();
            }
        }
    }

    private synchronized boolean nextLogWatcher() {
        if (files.size() > 0) {
            if (null != currentLogWatcher) {
                currentLogWatcher.kill();
            }
            currentLogWatcher = new SingleLogWatcher(this.files.remove(0));
            currentLogWatcher.addLogMonitor(this);
            currentLogWatcher.setDebug(debug);
            currentLogWatcher.start();
            return true;
        }
        return false;
    }

    public synchronized void addLogMonitor(LogMonitor monitor) {
//    ticket = true;
        logMonitors.add(monitor);
    }

    private boolean started = false;

    public synchronized void start() {
        if (!started) {
            nextLogWatcher();
            started = true;
        } else {
            new IllegalStateException("Already started.");
        }
    }

    public synchronized void kill() {
        currentLogWatcher.kill();
    }

    /**
     * logContinued
     *
     * @param file        File
     * @param filePointer long
     */
    public void logContinued(File file, long filePointer) {
        for (int i = logMonitors.size() - 1; i >= 0; i--) {
            logMonitors.get(i).logContinued(file, filePointer);
        }
    }

    /**
     * logEnded
     *
     * @param file        File
     * @param filePointer long
     */
    public void logEnded(File file, long filePointer) {
        for (int i = logMonitors.size() - 1; i >= 0; i--) {
            logMonitors.get(i).logEnded(file, filePointer);
        }
    }

    boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (null != currentLogWatcher) {
            currentLogWatcher.setDebug(debug);
        }
    }

    /**
     * logStarted
     *
     * @param file        File
     * @param filePointer long
     */
    public void logStarted(File file, long filePointer) {
        if (ticket) {
            for (int i = logMonitors.size() - 1; i >= 0; i--) {
                logMonitors.get(i).logStarted(file, filePointer);
            }
            ticket = false;
        }
    }

    /**
     * logWillEnd
     *
     * @param file        File
     * @param filePointer long
     */
    public void logWillEnd(File file, long filePointer) {
        if (!nextLogWatcher()) {
            ticket = true;
            for (int i = logMonitors.size() - 1; i >= 0; i--) {
                logMonitors.get(i).logWillEnd(file, filePointer);
            }
        }
    }

    /**
     * onException
     *
     * @param file File
     * @param ioe  IOException
     */
    public void onException(File file, IOException ioe) {
        for (int i = logMonitors.size() - 1; i >= 0; i--) {
            logMonitors.get(i).onException(file, ioe);
        }
    }

    /**
     * onLine
     *
     * @param file        File
     * @param line        String
     * @param filePointer long
     */
    public void onLine(File file, String line, long filePointer) {
        for (int i = logMonitors.size() - 1; i >= 0; i--) {
            logMonitors.get(i).onLine(file, line, filePointer);
        }
    }
}
