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
    protected void hideMe(){
        if(added){
            samuraiPanel.removePane(this);
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

    public void clearBuffer(){
        
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
