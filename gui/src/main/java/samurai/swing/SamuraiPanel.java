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
import samurai.tail.MultipleLogWatcher;
import samurai.util.GUIResourceBundle;
import samurai.util.ImageLoader;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.SystemColor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SamuraiPanel extends JPanel implements LogMonitor, RemoveListener {
    private static final ImageIcon monitoringIcon = ImageLoader.get("/samurai/swing/images/monitoring.png");
    private static final ImageIcon readingIcon = ImageLoader.get("/samurai/swing/images/reading.png");
    /*package*/static final ImageIcon stoppedIcon = ImageLoader.get("/samurai/swing/images/stopped.png");
    private final List<LogRenderer> logRenderers = new ArrayList<>(3);

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    final BorderLayout borderLayout1 = new BorderLayout();
    public final TileTabPanel<JPanel> tab = new TileTabPanel<>(true);
    //  private LogWatcher logWatcher = new LogWatcher();
    private MultipleLogWatcher logWatcher;// = new MultipleLogWatcher();

    private Context context;

    public SamuraiPanel(Context context, KeyListener listener, String encoding) {
        this.context = context;
        setEncoding(encoding);
        this.setLayout(borderLayout1);
        tab.setShowTitleWithSingleComponent(false);
        logRenderers.add(new ThreadDumpPanel(this, context));
        logRenderers.add(new GraphPanel(this, context));
        logRenderers.add(new LogPanel(this));
        this.add(tab, BorderLayout.CENTER);
        context.setIcon(stoppedIcon, this);
        addKeyListener(listener);
        tab.addKeyListener(listener);
        for (LogRenderer renderer : logRenderers) {
            addKeyListenerToComponents(listener, renderer);
            context.getConfig().addTarget(renderer);
        }
        setDragNotAccepting();

//    logWatcher.addLogMonitor(this);
//    for (LogRenderer renderer : logRenderers) {
//      logWatcher.addLogMonitor(renderer);
//    }
////    logWatcher.setDebug(true);
//    logWatcher.start();
    }

    private void addKeyListenerToComponents(KeyListener listener, Component component) {
        component.addKeyListener(listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addKeyListenerToComponents(listener, container.getComponent(i));
            }
        }
    }


    /*package*/ void setDroptargetListener(DropTargetListener listener) {
        DropTarget target = new DropTarget(this, DnDConstants.ACTION_REFERENCE, listener);
        for (LogRenderer renderer : logRenderers) {
            addDropTargetListenerToComponents(listener, renderer);
        }
        setDragNotAccepting();
    }

    private void addDropTargetListenerToComponents(DropTargetListener listener, Component component) {
        DropTarget target = new DropTarget(component, DnDConstants.ACTION_REFERENCE, listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addDropTargetListenerToComponents(listener, container.getComponent(i));
            }
        }
    }

    private boolean empty = true;

    public boolean isEmpty() {
        return empty;
    }

    public void clearBuffer() {
        if (!empty) {
            for (LogRenderer renderer : logRenderers) {
                renderer.clearBuffer();
            }
        }
    }

    public synchronized void close() {
//    if (null != logWatcher) {
//      this.logWatcher.kill();
//      logWatcher = null;
//    }

        if (null != logWatcher) {
            this.logWatcher.kill();
            logWatcher = null;
        }
//    logWatcher.setFiles(new File[0]);
        context.setText(resources.getMessage("MainFrame.untitled"), this);
        for (LogRenderer renderer : logRenderers) {
            renderer.close();
        }
        context.setIcon(stoppedIcon, this);
        empty = true;
        this.currentFile = null;
    }

    /*package*/ void addPane(final String name, final JPanel component) {
        if (SwingUtilities.isEventDispatchThread() || !this.isDisplayable()) {
            tab.addComponent(name, component);
        } else {
            SwingUtilities.invokeLater(() -> tab.addComponent(name, component));
        }
    }

    /*package*/ void removePane(final JPanel component) {
        if (SwingUtilities.isEventDispatchThread()) {
            tab.removeComponent(component);
        } else {
            javax.swing.SwingUtilities.invokeLater(() -> tab.removeComponent(component));
        }
    }

    private List<File> currentFile = null;

    public List<File> getCurrentFile() {
        return this.currentFile;
    }

    public synchronized void openFiles(File[] files) {
        if (0 < files.length) {
            List<File> fileList = new ArrayList<>(files.length);
            for (File file : files) {
                if (!file.isDirectory()) {
                    fileList.add(file);
                }
            }
            java.util.Collections.sort(fileList);
            openFiles(fileList);
        }
    }

    private String encoding;
    private String actualEncoding;

    public void setEncoding(String encoding) {
        if (!encoding.equals(this.encoding)) {
            this.encoding = encoding;
            if (encoding.equals("SYSTEM_DEFAULT")) {
                this.actualEncoding = System.getProperty("file.encoding");
            } else {
                this.actualEncoding = encoding;
            }

            if (null != logWatcher) {
                reload();
            }
        }
    }

    public String getEncoding() {
        return encoding;
    }

    private void openFiles(List<File> files) {
        this.close();
//    logWatcher = new LogWatcher((File[])files.toArray(new File[]{}));
//    logWatcher.addLogMonitor(this);
//    for (LogRenderer renderer : logRenderers) {
//      logWatcher.addLogMonitor(renderer);
//    }
//    logWatcher.setDebug(true);
//    logWatcher.start();
        currentFile = files;
        if (null != currentFile) {
            context.setText(files.get(0).getName(), this);
            context.setTemporaryStatus(resources.getMessage("LogPanel.monitoring", files.get(0).getName()));
            empty = false;
        } else {
            context.setText(resources.getMessage("MainFrame.untitled"), this);
            empty = true;
        }

        logWatcher = new MultipleLogWatcher(files.toArray(new File[]{}), actualEncoding);
        logWatcher.addLogMonitor(this);
        for (LogRenderer renderer : logRenderers) {
            logWatcher.addLogMonitor(renderer);
        }
//    logWatcher.setDebug(true);
        logWatcher.start();

//    logWatcher.setFiles((File[])files.toArray(new File[]{}));
    }

    public void reload() {
        openFiles(currentFile);
    }

    /*package*/ void setIcon(ImageIcon icon, JComponent component) {
        for (int i = 0; i < tab.getComponentSize(); i++) {
            Component theComponent = tab.getComponentAt(i);
            if (component == theComponent) {
                tab.setIconAt(i, icon);
                break;
            }
        }
    }

    public void onLine(File file, String line, long filePointer) {
    }

    public void logStarted(File file, long filepointer) {
        context.setIcon(readingIcon, this);
    }

    public void logEnded(File file, long filepointer) {
        context.setIcon(stoppedIcon, this);
    }

    public void logWillEnd(File file, long filepointer) {
        context.setIcon(monitoringIcon, this);
    }

    public void logContinued(File file, long filepointer) {
        context.setIcon(readingIcon, this);
    }

    public void onException(File file, IOException ioe) {
    }

    public Component getSelectedComponent() {
        return this.tab.getSelectedComponent();
    }

    public void willBeRemoved() {
        close();
    }

    final Border lineBorder = new LineBorder(SystemColor.textHighlight, 2, true);
    final Border emptyBorder = new EmptyBorder(2, 2, 2, 2);

    /*package*/void setDragAccepting() {
        setBorder(lineBorder);

    }

    /*package*/ void setDragNotAccepting() {
        setBorder(emptyBorder);
    }

}
