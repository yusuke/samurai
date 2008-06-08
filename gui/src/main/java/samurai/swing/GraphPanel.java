package samurai.swing;

import samurai.gc.GCParser;
import samurai.util.CSVParser;
import samurai.util.GUIResourceBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */

public class GraphPanel extends LogRenderer implements ClipBoardOperationListener {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private LineGraphPanel lineGraphPanel = new LineGraphPanel() {
        public void setLabels(String[] labels) {
            super.setLabels(labels);
            if (isCSV) {
                showMe(resources.getMessage("GraphPanel.csv"));
            } else {
                showMe(resources.getMessage("GraphPanel.memory"));
            }
            if (!isCSV) {
                setColorAt(0, Color.GRAY);
                setColorAt(1, Color.RED);
                setColorAt(2, Color.YELLOW);
            }
        }

        public void setMaxAt(int index, double value) {
            super.setMaxAt(index, value);
        }
    };

    public GraphPanel(SamuraiPanel samuraiPanel, Context context) {
        super(true, samuraiPanel);
        context.getConfig().apply(lineGraphPanel.plotSetting);
        resources.inject(lineGraphPanel.plotSetting);
        context.getConfig().applyLocation("PlotSettingDialog.location", lineGraphPanel.plotSetting);
        this.setLayout(new BorderLayout());
        this.add(lineGraphPanel, BorderLayout.CENTER);
    }

    private boolean isCSV = false;
    private CSVParser csvParser = null;
    private GCParser gcParser = null;

    public void onLine(File file, String line, long filePointer) {
        super.onLine(file, line, filePointer);
        if (isCSV) {
            csvParser.parse(line, lineGraphPanel);
        } else {
            gcParser.parse(line, lineGraphPanel);
        }
    }

    public void logStarted(File file, long filePointer) {
        super.logStarted(file, filePointer);
        if (file.getName().endsWith(".csv")) {
            isCSV = true;
            csvParser = new CSVParser();
        } else {
            isCSV = false;
            gcParser = new GCParser();
        }
        lineGraphPanel.adjustScrollBar();
    }

    public void cut() {

    }

    public void copy() {
        this.lineGraphPanel.copy();
    }

    public void paste() {
    }
}

