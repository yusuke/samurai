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

import samurai.util.Configuration;
import samurai.util.CustomizableKeyStroke;
import samurai.util.ExecuteThread;
import samurai.util.GUIResourceBundle;
import samurai.util.Task;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Component;

public class Context {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private final Configuration config = new Configuration("samurai");
    private final FileHistory fileHistory = new FileHistory(config);

    private  LocalProcesses localProcesses = null;
    private final CustomizableKeyStroke keyStroke = new CustomizableKeyStroke(resources);
    ExecuteThread executeThread = new ExecuteThread();
    private final JLabel statusBar;
    private TileTabPanel<SamuraiPanel> tab;
    private SearchPanel searchPanel;

    public Context(JLabel statusBar, TileTabPanel<SamuraiPanel> tab) {
        this.statusBar = statusBar;
        this.tab = tab;
        this.searchPanel = new SearchPanel(this);
        this.resources.inject(searchPanel);
        this.config.apply(searchPanel);
        try {

            this.localProcesses = new LocalProcesses(config, fileHistory);
        }catch(java.lang.NoClassDefFoundError toolsJarNotFound){
        }

        executeThread.start();
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public LocalProcesses getLocalProcesses() {
        return this.localProcesses;
    }

    public FileHistory getFileHistory() {
        return this.fileHistory;
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
        executeThread.invokeLater(new Task() {
            public void execute() {
                setStatusBar();
            }
        }, 2);
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

    public CustomizableKeyStroke getKeyStroke() {
        return this.keyStroke;
    }
}
