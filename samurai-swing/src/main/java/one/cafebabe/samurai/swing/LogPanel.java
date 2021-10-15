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

import one.cafebabe.samurai.util.Configuration;
import one.cafebabe.samurai.util.ConfigurationListener;
import one.cafebabe.samurai.util.GUIResourceBundle;
import one.cafebabe.samurai.util.OSDetector;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

public class LogPanel extends LogRenderer implements AdjustmentListener,
        ConfigurationListener, ClipBoardOperationListener {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    final JScrollPane jScrollPane1 = new JScrollPane();
    public final JTextArea textArea = new JTextArea();
    private int numberOfLines = 0;
    //    public UnlimitedTextArea textArea = new UnlimitedTextArea();
    final JScrollBar verticalScrollBar = jScrollPane1.getVerticalScrollBar();

    public LogPanel(SamuraiPanel samuraiPanel) {
        super(false, samuraiPanel);
        this.setLayout(borderLayout1);
        textArea.setBackground(Color.white);
        textArea.setBorder(null);
        textArea.setEditable(false);
        textArea.setSelectionEnd(0);
        textArea.setSelectionStart(0);
        textArea.setLineWrap(true);
        textArea.setTabSize(2);
        jScrollPane1.setAutoscrolls(true);
        if (OSDetector.isMac()) {
            // follow Apple UI guideline
            jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
        this.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
        this.add(jScrollPane1, BorderLayout.CENTER);

        verticalScrollBar.setAutoscrolls(true);
        verticalScrollBar.addAdjustmentListener(this);
        jScrollPane1.getViewport().add(textArea, null);
        close();
        showMe(resources.getMessage("MainFrame.log"));
    }


    private void initBuffer() {
        buffer = new String[30];
        filepointers = new long[30];
        count = 0;
        numberOfLines = 0;
    }


    public void close() {
        super.close();
        textArea.setToolTipText(null);
        SwingUtilities.invokeLater(clearTask);
        SwingUtilities.invokeLater(clearTask2);
        initBuffer();
    }

    public void clearBuffer() {
        textArea.setText("");
    }

    //  StringBuffer buffer = new StringBuffer(1024);
    String[] buffer;
    long[] filepointers;
    int count;

    public void onLine(File file, String line, long filePointer) {
        super.onLine(file, line, filePointer);
        buffer[count] = line + "\n";
        filepointers[count] = filePointer;
        count++;
        if (count == 30) {
            flushBuffer();
        }
    }

    final Runnable clearTask = () -> textArea.setText("");
    final Runnable clearTask2 = () -> textArea.setText(resources.getMessage("LogPanel.dropFileHere") + "\n");

    public void logStarted(File file, long filePointer) {
        super.logStarted(file, filePointer);
        SwingUtilities.invokeLater(clearTask);
        textArea.setToolTipText(file.getAbsolutePath());
    }

    public void logEnded(File file, long filePointer) {
        super.logEnded(file, filePointer);
        flushBuffer();
    }

    public void logWillEnd(File file, long filePointer) {
        flushBuffer();
    }

    class FlushTask implements Runnable {
        final String[] buf;
        final long[] pointers;
        final int counter;

        FlushTask(String[] buf, long[] pointers, int counter) {
            this.buf = buf;
            this.pointers = pointers;
            this.counter = counter;
        }

        public void run() {
            for (int i = 0; i < counter; i++) {
                textArea.append(buf[i]);
                numberOfLines++;
                try {
                    if (1000 <= numberOfLines) {
                        textArea.replaceRange("", 0, textArea.getLineEndOffset(0));
                        numberOfLines = 1000;
                    }
                } catch (BadLocationException ignored) {
                }
            }
        }
    }

    private void flushBuffer() {
        this.invokeLater(new FlushTask(buffer, filepointers, count));
        initBuffer();
    }

    int lastMax = 0;
    final BorderLayout borderLayout1 = new BorderLayout();

    public void adjustmentValueChanged(AdjustmentEvent event) {
        int max = verticalScrollBar.getMaximum();
        if (lastMax != max) {
            verticalScrollBar.setValue(max);
        }
        lastMax = max;
    }

    public String config_logFontFamily = "";
    public int config_logFontSize = 12;

    public void onConfigurationChanged(Configuration config) {
        textArea.setLineWrap(config_wrapLog);
        if (!this.config_wrapLog) {
            if (OSDetector.isMac()) {
                jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            } else {
                jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }
        } else {
            jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        this.textArea.setFont(new Font(this.config_logFontFamily, Font.PLAIN, config_logFontSize));
    }

    public boolean config_wrapLog = true;

    public void cut() {
    }

    public void copy() {
        textArea.copy();
    }

    public void paste() {
    }
}
