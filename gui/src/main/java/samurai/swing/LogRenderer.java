/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;


import samurai.tail.LogMonitor;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;

public abstract class LogRenderer extends JPanel implements LogMonitor {
    private boolean hideOnClose;
    private SamuraiPanel samuraiPanel;
    private boolean added = false;

    public LogRenderer(boolean hideOnClose, SamuraiPanel samuraiPanel) {
        this.hideOnClose = hideOnClose;
        this.samuraiPanel = samuraiPanel;
    }

    public synchronized void close() {
        currentVersion++;
        if (added && hideOnClose) {
            samuraiPanel.removePane(this);
            added = false;
        }
    }

    protected void showMe(String message) {
        if (!added) {
            samuraiPanel.addPane(message, this);
            added = true;
        }
    }

    public void onLine(File file, String line, long filepointer) {
    }

    int currentVersion = 0;

    public void logStarted(File file, long filePointer) {
        if (hideOnClose) {
            close();
        }
        currentVersion++;
    }

    public void logEnded(File file, long filepointer) {
    }

    public void logWillEnd(File file, long filepointer) {
    }

    public void logContinued(File file, long filepointer) {
    }

    public void onException(File file, IOException ioe) {
    }

    protected void invokeLater(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread() || !this.isDisplayable()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(new VersionAwareTask(runnable, currentVersion));
        }
    }

    class VersionAwareTask implements Runnable {
        int version;
        Runnable target;

        VersionAwareTask(Runnable target, int version) {
            this.target = target;
            this.version = version;
        }

        public void run() {
            if (currentVersion == version) {
                target.run();
            }
        }
    }
}
