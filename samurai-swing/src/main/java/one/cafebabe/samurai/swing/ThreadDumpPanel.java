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

import one.cafebabe.samurai.core.FullThreadDump;
import one.cafebabe.samurai.core.ThreadDumpExtractor;
import one.cafebabe.samurai.core.ThreadDumpSequence;
import one.cafebabe.samurai.core.ThreadStatistic;
import one.cafebabe.samurai.util.*;
import one.cafebabe.samurai.web.ThreadFilter;
import one.cafebabe.samurai.web.ThymeleafHtmlRenderer;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadDumpPanel extends LogRenderer implements HyperlinkListener,
        ConfigurationListener, ClipBoardOperationListener {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    public String config_dumpFontFamily = "Monospace";
    public String config_dumpFontSize = "12";
    private final Map<String, Object> webContext = new HashMap<>();

    private final JProgressBar progressBar = new JProgressBar();

    final public JButton saveButton = new JButton();
    public final JButton openButton = new JButton();
    public final JButton trashButton = new JButton();
    final ImageIcon saveButtonIcon = ImageLoader.get("/one/cafebabe/samurai/swing/images/save.gif");
    ImageIcon openButtonIcon;
    final ImageIcon trashButtonIcon = ImageLoader.get("/one/cafebabe/samurai/swing/images/trash.gif");
    final BorderLayout borderLayout1 = new BorderLayout();
    private String referer = null;
    final JEditorPane threadDumpPanel = new JEditorPane() {
        public void paint(Graphics g) {
            super.paint(g);
            if (referer != null) {
                SwingUtilities.invokeLater(() -> {
                    threadDumpPanel.scrollToReference(referer);
                    referer = null;
                });
            }
        }
    };
    final JPanel settingPanel = new JPanel();
    private final ThymeleafHtmlRenderer renderer = new ThymeleafHtmlRenderer();

    public ThreadDumpPanel(SamuraiPanel samuraiPanel, Context context) {
        super(true, samuraiPanel);
        this.setLayout(borderLayout1);
        this.setMaximumSize(new Dimension(2147483647, 2147483647));
        this.setMinimumSize(new Dimension(0, 0));
        this.setPreferredSize(new Dimension(400, 800));
        this.setLayout(borderLayout1);
        threadDumpPanel.setDoubleBuffered(true);
        threadDumpPanel.setEditable(false);
        threadDumpPanel.setText(resources.getMessage("ThreadDumpPanel.threadDumpHere"));
        threadDumpPanel.setContentType("text/html; charset=charset=UTF-8");
        settingPanel.setEnabled(true);
        settingPanel.setMaximumSize(new Dimension(2147483647, 40));
        settingPanel.setMinimumSize(new Dimension(10, 40));
        settingPanel.setPreferredSize(new Dimension(10, 40));
        settingPanel.setLayout(gridBagLayout1);

        progressBar.setStringPainted(true);

        saveButton.setBorderPainted(false);
        saveButton.setMaximumSize(new Dimension(20, 20));
        saveButton.setMinimumSize(new Dimension(20, 20));
        saveButton.setPreferredSize(new Dimension(20, 20));
        saveButton.setToolTipText(resources.getMessage("ThreadDumpPanel.saveAsHtml"));
        saveButton.setFocusPainted(false);
        saveButton.setIcon(saveButtonIcon);
        saveButton.addActionListener(event -> {
            if (saveButton.isEnabled()) {
                synchronized (saveButton) {
                    if (saveButton.isEnabled()) {
                        saveButton.setEnabled(false);
                        progressBar.setString("");
                        progressBar.setVisible(true);
                        context.invokeLater(() -> {
                            ThymeleafHtmlRenderer renderer = new ThymeleafHtmlRenderer("../");
                            try {
                                synchronized (statistic) {
                                    savedLocation = getTargetDirectory(currentFile);
                                    String[] filesToSave = {"blocked.gif", "blocking.gif", "deadlocked.gif",
                                            "expandable_win.gif", "fullButton.gif", "running.gif", "same-h.gif",
                                            "same-v.gif", "sequenceButton.gif", "shrinkable_win.gif", "idle.gif",
                                            "space.gif", "tableButton.gif"};
                                    renderer.saveTo(statistic, savedLocation, (finished, all) -> SwingUtilities.invokeLater(new ProgressTask(finished, all + filesToSave.length)));

                                    File cssDir = new File(savedLocation.getAbsolutePath() + "/css/");
                                    //noinspection ResultOfMethodCallIgnored
                                    cssDir.mkdir();
                                    saveStreamAsFile(savedLocation, "css/style.css");


                                    File imageDir = new File(savedLocation.getAbsolutePath() + "/images/");
                                    //noinspection ResultOfMethodCallIgnored
                                    imageDir.mkdir();
                                    for (int i = 0; i < filesToSave.length; i++) {
                                        String file = filesToSave[i];
                                        saveStreamAsFile(savedLocation, "images/" + file);
                                        SwingUtilities.invokeLater(new ProgressTask(progressBar.getMaximum() - filesToSave.length + i, progressBar.getMaximum()));
                                    }
                                    SwingUtilities.invokeLater(new ProgressTask(progressBar.getMaximum(), progressBar.getMaximum()));

                                }
                                context.setTemporaryStatus(resources.getMessage("ThreadDumpPanel.saved", savedLocation.getAbsolutePath()));
                            } catch (Exception ioe) {
                                ioe.printStackTrace();
                                SwingUtilities.invokeLater(new ProgressTask(progressBar.getMaximum(), progressBar.getMaximum()));
                                context.setTemporaryStatus(ioe.getMessage());
                            } finally {
                                context.invokeLater(() -> {
                                    progressBar.setVisible(false);
                                    progressBar.setValue(0);
                                    saveButton.setEnabled(true);
                                }, 2);
                            }
                        });
                    }
                }
            }

        });
        saveButton.addMouseListener(new RolloverBorder(saveButton));

        if (OSDetector.isMac()) {
            openButtonIcon = ImageLoader.get("/one/cafebabe/samurai/swing/images/folder_mac.gif");
        }
        if (OSDetector.isWindows()) {
            openButtonIcon = ImageLoader.get("/one/cafebabe/samurai/swing/images/folder_win.gif");
        }

        openButton.setBorderPainted(false);
        openButton.setMaximumSize(new Dimension(20, 20));
        openButton.setMinimumSize(new Dimension(20, 20));
        openButton.setPreferredSize(new Dimension(20, 20));
        openButton.setToolTipText(resources.getMessage("ThreadDumpPanel.openFolder"));
        openButton.setFocusPainted(false);
        openButton.setIcon(openButtonIcon);
        openButton.addActionListener(event -> {
            String[] command = null;
            if (OSDetector.isMac()) {
                command = new String[]{"open", savedLocation.getAbsolutePath()};
            } else if (OSDetector.isWindows()) {
                command = new String[]{"cmd.exe", "/C", "start", savedLocation.getAbsolutePath()};
            }
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException ioe) {
                context.setTemporaryStatus(ioe.getMessage());
            }
        });
        openButton.setEnabled(false);
        openButton.addMouseListener(new RolloverBorder(openButton));


        trashButton.setBorderPainted(false);
        trashButton.setMaximumSize(new Dimension(20, 20));
        trashButton.setMinimumSize(new Dimension(20, 20));
        trashButton.setPreferredSize(new Dimension(20, 20));
        trashButton.setToolTipText(resources.getMessage("ThreadDumpPanel.clear"));
        trashButton.setFocusPainted(false);
        trashButton.setIcon(trashButtonIcon);
        trashButton.addActionListener(event -> {
            if (JOptionPane.YES_OPTION ==
                    JOptionPane.showConfirmDialog(THIS, resources.getMessage("ThreadDumpPanel.confirmClear")
                            , resources.getMessage("ThreadDumpPanel.clear"), JOptionPane.YES_NO_OPTION)) {
                synchronized (statistic) {
                    init();
                }
            }
        });
        trashButton.setEnabled(true);
        trashButton.addMouseListener(new RolloverBorder(trashButton));

        threadDumpPanel.addHyperlinkListener(this);
        progressBar.setMaximumSize(new Dimension(80, 20));
        progressBar.setPreferredSize(new Dimension(80, 20));
        progressBar.setMinimumSize(new Dimension(80, 20));
        progressBar.setVisible(false);
        this.add(settingPanel, BorderLayout.CENTER);
        settingPanel.add(threadDumpPanelScrollPane, new GridBagConstraints(0, 1, 10, 1, 1.0, 1.0
                , GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        settingPanel.add(new JLabel(), new GridBagConstraints(6, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));
        settingPanel.add(trashButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        settingPanel.add(progressBar, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        settingPanel.add(saveButton, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        if (OSDetector.isMac() || OSDetector.isWindows()) {
            settingPanel.add(openButton, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0
                    , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        threadDumpPanelScrollPane.getViewport().add(threadDumpPanel, null);
    }

    static class RolloverBorder extends MouseAdapter {
        private final JButton button;

        RolloverBorder(JButton button) {
            this.button = button;
        }

        public void mouseEntered(MouseEvent event) {
            button.setBorderPainted(button.isEnabled());
        }

        public void mouseExited(MouseEvent event) {
            button.setBorderPainted(false);
        }

        public void mouseReleased(MouseEvent event) {
            button.setBorderPainted(button.isEnabled());
        }
    }

    public static File getTargetDirectory(File file) {
        String target = file.getAbsoluteFile().getParent();
        String fileName = file.getName();
        String directoryName;
        if (-1 == fileName.lastIndexOf(".")) {
            directoryName = fileName;
        } else {
            directoryName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        target = target + File.separator + directoryName;
        File targetFile = new File(target);
        if (targetFile.exists()) {
            int count = 0;
            while (targetFile.exists()) {
                count++;
                targetFile = new File(target + "." + count);
            }
        }
        return targetFile;
    }

    File savedLocation = null;
    final JPanel THIS = this;


    private void saveStreamAsFile(File parentDir, String fileName) throws IOException {
        try (InputStream is = ThreadDumpPanel.class.getResourceAsStream("/one/cafebabe/samurai/web/" + fileName);
             FileOutputStream fos = new FileOutputStream(parentDir.getAbsolutePath() + "/" + fileName)) {
            byte[] buf = new byte[256];
            int count;
            if (is != null) {
                while (-1 != (count = is.read(buf))) {
                    fos.write(buf, 0, count);
                }
            }
        }
    }

    class ProgressTask implements Runnable {
        final int finished;
        final int all;

        ProgressTask(int finished, int all) {
            this.finished = finished;
            this.all = all;
        }

        public void run() {
            progressBar.setValue(finished);
            progressBar.setMaximum(all);
            if (!(finished == all)) {
                int progress = finished * 100 / all;
                progressBar.setString(progress + "%");
            } else {
                progressBar.setString("done.");
                openButton.setEnabled(true);
            }
        }
    }

    private String uri = "";

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            uri = e.getDescription();
            if (uri.startsWith("#")) {
                threadDumpPanel.scrollToReference(e.getDescription().substring(1));
            } else {
                filter.setQuery(uri);
                updateHtml();
            }
        }
    }

    final GridBagLayout gridBagLayout1 = new GridBagLayout();
    private final ThreadFilter filter = new ThreadFilter();

    private List<ThreadDumpSequence> threadList = null;

    public void changeButtonFeel() {
        invokeLater(() -> {
                    if (null != threadList && threadList.size() != 0) {
                        if (filter.mode != ThreadFilter.View.full) {
                            if (filter.mode == ThreadFilter.View.sequence) {
                                for (ThreadDumpSequence sequence : threadList) {
                                    if (filter.getThreadId().equals(sequence.getId())) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
        );
    }

    private void updateHtml() {
        invokeLater(() -> {
            synchronized (statistic) {
                if (statistic.getFullThreadDumpCount() > 0) {
                    if ("".equals(filter.getThreadId())) {
                        filter.setThreadId(statistic.getStackTracesAsArray().get(0).getId());
                    }
                    threadDumpPanel.setText(renderer.render(filter, statistic, webContext));
                    threadDumpPanel.select(0, 0);
                    if (uri.contains("#")) {
                        referer = uri.substring(uri.indexOf("#") + 1);
                    }
                    changeButtonFeel();
                }
            }
        });
    }

    final ThreadStatistic statistic = new ThreadStatistic() {
        private static final long serialVersionUID = 198789311977731508L;

        public synchronized void onFullThreadDump(FullThreadDump fullThreadDump) {
            super.onFullThreadDump(fullThreadDump);
            invokeLater(() -> {
                showMe(resources.getMessage("ThreadDumpPanel.threadDump"));
                threadList = statistic.getStackTracesAsArray();
            });
            updateHtml();
        }
    };
    private ThreadDumpExtractor analyzer = new ThreadDumpExtractor(statistic);
    final JScrollPane threadDumpPanelScrollPane = new JScrollPane();

    File currentFile;

    public void onLine(File file, String line, long filePointer) {
        super.onLine(file, line, filePointer);
        analyzer.analyzeLine(line);
    }

    public void logStarted(File file, long filePointer) {
        super.logStarted(file, filePointer);
        currentFile = file;
    }

    public void logEnded(File file, long filePointer) {
        super.logEnded(file, filePointer);
        analyzer.finish();
    }

    public synchronized void clearBuffer() {
        init();
        analyzer = new ThreadDumpExtractor(statistic);
        hideMe();
    }

    public synchronized void close() {
        super.close();
        clearBuffer();
    }

    private void init() {
        statistic.reset();
        filter.reset();
        threadDumpPanel.setText(resources.getMessage(
                "ThreadDumpPanel.threadDumpHere"));
        changeButtonFeel();

    }


    public synchronized void onConfigurationChanged(Configuration config) {
        config.apply(renderer);
        webContext.put("fontFamily", config_dumpFontFamily);
        webContext.put("fontSize", config_dumpFontSize);
        updateHtml();
    }

    public void cut() {
    }

    public void copy() {
        this.threadDumpPanel.copy();
    }

    public void paste() {
    }
}

