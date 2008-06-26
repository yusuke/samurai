/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import samurai.gc.GCParser;
import samurai.gc.LineGraph;
import samurai.gc.LineGraphRenderer;
import samurai.util.CSVParser;
import samurai.util.GUIResourceBundle;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GraphPanel extends LogRenderer implements ClipBoardOperationListener , LineGraphRenderer{
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    List<LineGraphPanel> graphs = new ArrayList<LineGraphPanel>(1);
    private Context context;

    private TileTabPanel<LineGraphPanel> tileTabPanel = new TileTabPanel<LineGraphPanel>(true);

    public GraphPanel(SamuraiPanel samuraiPanel, Context context) {
        super(true, samuraiPanel);
        this.context = context;
        this.setLayout(new BorderLayout());
        tileTabPanel.setOrientation(TileTabPanel.TILE_VERTICAL);
        tileTabPanel.setDividerSize(0);
        this.add(tileTabPanel, BorderLayout.CENTER);
    }

    public LineGraph addLineGraph(String title,String[] labels){
        LineGraphPanel lineGraphPanel = new LineGraphPanel();
        lineGraphPanel.setLabels(labels);
        context.getConfig().applyLocation("PlotSettingDialog.location", lineGraphPanel.plotSetting);
        context.getConfig().apply(lineGraphPanel.plotSetting);
        resources.inject(lineGraphPanel.plotSetting);
        tileTabPanel.addComponent(title,lineGraphPanel);
        if(isCSV){
            showMe(resources.getMessage("GraphPanel.csv"));
        }else{
            showMe(resources.getMessage("GraphPanel.memory"));
        }

        return lineGraphPanel;
    }

    private boolean isCSV = false;
    private CSVParser csvParser = null;
    private GCParser gcParser = null;

    public void onLine(File file, String line, long filePointer) {
        super.onLine(file, line, filePointer);
        if (isCSV) {
            csvParser.parse(line, this);
        } else {
            gcParser.parse(line, this);
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
        invokeLater(new Runnable(){
            public void run(){
                while(tileTabPanel.getComponentSize() > 0){
                    tileTabPanel.removeComponentAt(0);
                }
                for (LineGraphPanel graphPanel : graphs) {
                    graphPanel.adjustScrollBar();
                }

            }
        });
    }

    public void cut() {

    }

    public void copy() {
        tileTabPanel.getSelectedComponent().copy();
    }

    public void paste() {
    }
}

