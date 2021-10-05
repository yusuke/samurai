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
package one.cafebabe.samurai.swing;

import one.cafebabe.samurai.util.ExecuteThread;
import one.cafebabe.samurai.util.Task;

import javax.swing.*;
import java.awt.*;

public class Context {
    final ExecuteThread executeThread = new ExecuteThread();
    private final JLabel statusBar;
    TileTabPanel<SamuraiPanel> tab;

    public Context(JLabel statusBar) {
        this.statusBar = statusBar;
        executeThread.start();
    }

    public void setIcon(ImageIcon icon, JComponent component) {
        for (int i = 0; i < tab.getComponentSize(); i++) {
            Component theComponent = tab.getComponentAt(i);
            if (component == theComponent) {
                tab.setIconAt(i, icon);
                break;
            }
        }
    }

    public void setText(String text, JComponent component) {
        for (int i = 0; i < tab.getComponentSize(); i++) {
            Component theComponent = tab.getComponentAt(i);
            if (component == theComponent) {
                tab.setTitleAt(i, text);
                break;
            }
        }
    }

    public void setTemporaryStatus(String temporaryStatus) {
        this.statusBar.setText(temporaryStatus);
        executeThread.invokeLater(this::setStatusBar, 2);
    }

    private String statusText = "";

    public void setStatusBar() {
        this.statusBar.setText(statusText);
    }

    public void setStatus(String text) {
        this.statusText = text;
        setStatusBar();
    }

    public void invokeLater(Task task) {
        executeThread.addTask(task);
    }

    public void invokeLater(Task task, int delaySecs) {
        executeThread.invokeLater(task, delaySecs);
    }

}
