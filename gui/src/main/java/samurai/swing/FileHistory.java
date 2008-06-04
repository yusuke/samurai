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

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public final class FileHistory {
    private List<File> files = new ArrayList<File>();
    private final int numberToRemember = 10;
    public String config_RecentlyUsedFiles = "";
    private Configuration config;
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
//    if(numberToRemember < 0){
//      numberToRemember = 10;
//    }
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
            openMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JFileChooser.APPROVE_OPTION == fileChooser
                            .showOpenDialog(component)) {
                        File file = fileChooser.getSelectedFile();
                        open(file);
                    }
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
    public void clearHistory(){
        files.clear();
    }

    public void addHistory(File file) {
        delete(file);
        if (file.exists() && file.isFile()) {
            files.add(0, file);
            if (files.size() > numberToRemember) {
                files.remove(files.size() - 1);
            }
        }
        save();
        updateChildMenuItems();
    }

    public void open(File file) {
        addHistory(file);
        updateChildMenuItems();
        save();
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
    public void enableCleaningOrphans(){
        this.cleanOrphans = true;
    }
    public void disableCleaningOrphans(){
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
        private File file;

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
