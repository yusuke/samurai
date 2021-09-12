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

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileHistory {
    private final List<File> files = new ArrayList<>();
    private final int numberToRemember = 10;
    public String config_RecentlyUsedFiles = "";
    private final Configuration config;
    private FileHistoryListener listener = null;
    private final JMenu openRecentMenu;
    private JMenuItem openMenu = null;
    private final JFileChooser fileChooser = new JFileChooser();

    public FileHistory(Configuration config, FileHistoryListener listener) {
        this(config);
        setFileHistoryListener(listener);
    }

    public FileHistory(Configuration config) {
        this.config = config;
        config.apply(this);
        load();
        openRecentMenu = new JMenu();
        updateChildMenuItems();
    }

    public JMenu getOpenRecentMenu() {
        return this.openRecentMenu;
    }

    public synchronized JMenuItem getOpenMenu(final Component component) {
        if (null == openMenu) {
            openMenu = new JMenuItem();
            openMenu.addActionListener(e -> {
                if (JFileChooser.APPROVE_OPTION == fileChooser
                        .showOpenDialog(component)) {
                    File file = fileChooser.getSelectedFile();
                    open(file);
                }
            });
        }
        return this.openMenu;
    }


    public void setFileHistoryListener(FileHistoryListener listener) {
        this.listener = listener;
    }

    public List<File> getList() {
        return files;
    }

    public void open(File[] files) {
        if (files.length == 1) {
            open(files[0]);
        } else {
            addHistory(files);
            if (null != listener) {
                listener.filesOpened(files);
            }
        }
    }

    public void addHistory(File[] files) {
        for (int i = 0; i < files.length; i++) {
            addHistory(files[i]);
        }
    }

    public void clearHistory() {
        files.clear();
    }

    public void addHistory(File file) {
        delete(file);
        files.add(0, file);
        if (files.size() > numberToRemember) {
            files.remove(files.size() - 1);
        }
        save();
        updateChildMenuItems();
    }

    public void open(File file) {
        addHistory(file);
        if (null != listener) {
            listener.fileOpened(file);
        }
    }

    private void delete(File file) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).equals(file)) {
                files.remove(i);
            }
        }
        updateChildMenuItems();
        save();
    }

    private void save() {
        cleanOrphans();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < files.size(); i++) {
            buf.append(files.get(i).getAbsolutePath());
            if (i < (files.size() - 1)) {
                buf.append(":");
            }
        }
        config_RecentlyUsedFiles = buf.toString();
        config.store(this);
    }

    private void load() {
        String[] fileArray = config_RecentlyUsedFiles.split(":");
        for (int i = 0; i < fileArray.length; i++) {
            files.add(new File(fileArray[i]));
        }
    }

    private boolean cleanOrphans = false;

    public void enableCleaningOrphans() {
        this.cleanOrphans = true;
    }

    public void disableCleaningOrphans() {
        this.cleanOrphans = false;
    }

    private void cleanOrphans() {
        if (cleanOrphans) {
            for (int i = 0; i < files.size(); i++) {
                if (!files.get(i).exists() || !files.get(i).isFile()) {
                    files.remove(i);
                }
            }
        }
    }

    private void updateChildMenuItems() {
        cleanOrphans();
        openRecentMenu.removeAll();
        for (int i = 0; i < files.size(); i++) {
            JMenuItem aHistory = new HistoryMenu(files.get(i));
            openRecentMenu.add(aHistory);
        }
        openRecentMenu.setEnabled(files.size() != 0);
    }

    class HistoryMenu extends JMenuItem implements ActionListener {
        private final File file;

        public HistoryMenu(File file) {
            super(file.getName() + " - " + file.getAbsolutePath());
            this.file = file;
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            open(file);
        }
    }
}
