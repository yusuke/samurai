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
import samurai.util.ConfigurationListener;
import samurai.util.GUIResourceBundle;
import samurai.util.OSDetector;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;

public class LogPanel extends LogRenderer implements AdjustmentListener,
        ConfigurationListener, ClipBoardOperationListener {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    JScrollPane jScrollPane1 = new JScrollPane();
    public JTextArea textArea = new JTextArea();
    //    public UnlimitedTextArea textArea = new UnlimitedTextArea();
    JScrollBar verticalScrollBar = jScrollPane1.getVerticalScrollBar();

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
        this.setFont(new java.awt.Font("Dialog", 0, 12));
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
    }


    public void close() {
        super.close();
        textArea.setToolTipText(null);
//    textArea.setText("");
        SwingUtilities.invokeLater(clearTask);
        SwingUtilities.invokeLater(clearTask2);
//    textArea.append(resources.getMessage("LogPanel.dropFileHere")+"\n");
//    System.out.println(textArea.getDocument().getLength()+textArea.getText());
        //    content.setCurrentFilePointer(0);
//        textArea.setCurrentFilePointer(0);
//    buffer.delete(0, buffer.length());
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
//    buffer.append(line).append("\n");
//    textArea.cheatAppend(line+"\n");
//    textArea.setCurrentFilePointer(filePointer);
        count++;
        if (count == 30) {
            flushBuffer();
        }
    }

    Runnable clearTask = new Runnable() {
        public void run() {
            textArea.setText("");
        }
    };
    Runnable clearTask2 = new Runnable() {
        public void run() {
            textArea.setText(resources.getMessage("LogPanel.dropFileHere") + "\n");
        }
    };

    public void logStarted(File file, long filePointer) {
        super.logStarted(file, filePointer);
//        try {
//      content.setFile(file);
//            textArea.setFile(file);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            @todo do something
//        }
//    content.setCurrentFilePointer(0);
//        textArea.setCurrentFilePointer(0);
//    textArea.setText("");
        SwingUtilities.invokeLater(clearTask);
        textArea.setToolTipText(file.getAbsolutePath());
//    buffer.delete(0, buffer.length());
    }

    public void logEnded(File file, long filePointer) {
        super.logEnded(file, filePointer);
        flushBuffer();
    }

    public void logWillEnd(File file, long filePointer) {
        flushBuffer();
    }

    class FlushTask implements Runnable {
        String[] buf;
        long[] pointers;
        int counter;

        FlushTask(String[] buf, long[] pointers, int counter) {
            this.buf = buf;
            this.pointers = pointers;
            this.counter = counter;
        }

        public void run() {
            for (int i = 0; i < counter; i++) {
                textArea.append(buf[i]);
//                textArea.setCurrentFilePointer(pointers[i]);
            }
        }
    }

    private void flushBuffer() {
//    if (0 != count) {
//      count = 0;
////      System.out.println("appending:"+buffer.toString());
//      textArea.append(buffer.toString());
////      content.setCurrentFilePointer(filePointer);
//    textArea.append("");
        this.invokeLater(new FlushTask(buffer, filepointers, count));
        initBuffer();

////      System.out.println("appended:"+textArea.getText());
//      buffer.delete(0, buffer.length());

//    }
//    textArea.finish();
    }

    //  private boolean fitlast = true;
    //  int lastValue = 0;
    int lastMax = 0;
    BorderLayout borderLayout1 = new BorderLayout();

    public void adjustmentValueChanged(AdjustmentEvent event) {
        int max = verticalScrollBar.getMaximum();
        //    int value = verticalScrollBar.getValue();
        //    System.out.println(fitlast+":"+max+":"+value+":"+lastValue+":"+verticalScrollBar.getBlockIncrement()+":"+verticalScrollBar.getVisibleAmount());
        if (lastMax != max) {
            //      //scrollbar expanded
            //      if (fitlast) {
            verticalScrollBar.setValue(max);
        }
        //
        //    }else{
        //      if (lastValue != value) {
        //        fitlast = max <= (value + verticalScrollBar.getVisibleAmount()*1.2);
        //      }
        //    }
        //    System.out.println(fitlast);
        //    lastValue = value;
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
        this.textArea.setFont(new Font(this.config_logFontFamily, 0, config_logFontSize));
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
