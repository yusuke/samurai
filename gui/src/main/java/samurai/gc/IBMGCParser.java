/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.LineGraphDataSourceParser;

import java.awt.Color;

public class IBMGCParser implements LineGraphDataSourceParser {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private boolean labelSet = false;
    private LineGraph lineGraph;
    private double memoryMax = 0;
    private double currentMemoryMax = 0;
    private double memoryFreed = 0;
    private double memoryAfter = 0;
    private double time = 0;

    public IBMGCParser() {
    }

    int freedIndex = 0;

    public boolean parse(String line, LineGraphRenderer renderer) {
        try {
            if (line.startsWith("  <GC(") && -1 != (freedIndex = line.indexOf("): freed"))) {
                if (!labelSet) {
                    lineGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"),new String[]{resources.getMessage("GraphPanel.time") + "(ms)",
                            resources.getMessage("GraphPanel.heapFreed"),
                            resources.getMessage("GraphPanel.heapAfterGCbytes")});
                    labelSet = true;
                    lineGraph.setColorAt(0, Color.GRAY);
                    lineGraph.setColorAt(1, Color.RED);
                    lineGraph.setColorAt(2, Color.YELLOW);
                }
                try {
                    currentMemoryMax = Double.parseDouble(line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(")")));
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        lineGraph.setYMax(1, memoryMax);
                        lineGraph.setYMax(2, memoryMax);
                    }
                    memoryFreed = Double.parseDouble(line.substring(freedIndex + 9, line.indexOf(" byte")));
                    memoryAfter = Double.parseDouble(line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf("/")));
                    time = Double.parseDouble(line.substring(line.lastIndexOf("in ") + 3, line.lastIndexOf(" ms")));
                    lineGraph.addValues(new double[]{time, memoryFreed, memoryAfter});
                    return true;
                } catch (NumberFormatException wasNotGC) {
//         wasNotGC.printStackTrace();
                    //does nothing
                }
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            System.err.println("unexpected format:" + line);
        }
        return false;
    }
}
