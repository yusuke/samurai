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
package one.cafebabe.samurai.gc;

import one.cafebabe.samurai.util.GUIResourceBundle;
import one.cafebabe.samurai.util.LineGraphDataSourceParser;

import java.awt.*;

public class IBMGCParser implements LineGraphDataSourceParser {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private boolean labelSet = false;
    private LineGraph lineGraph;
    private double memoryMax = 0;

    public IBMGCParser() {
    }

    int freedIndex = 0;

    public boolean parse(String line, LineGraphRenderer renderer) {
        try {
            if (line.startsWith("  <GC(") && -1 != (freedIndex = line.indexOf("): freed"))) {
                if (!labelSet) {
                    lineGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{resources.getMessage("GraphPanel.time") + "(ms)",
                            resources.getMessage("GraphPanel.heapFreed"),
                            resources.getMessage("GraphPanel.heapAfterGCbytes")});
                    labelSet = true;
                    lineGraph.setColorAt(0, Color.GRAY);
                    lineGraph.setColorAt(1, Color.RED);
                    lineGraph.setColorAt(2, Color.YELLOW);
                }
                try {
                    double currentMemoryMax = Double.parseDouble(line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(")")));
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        lineGraph.setYMax(1, memoryMax);
                        lineGraph.setYMax(2, memoryMax);
                    }
                    double memoryFreed = Double.parseDouble(line.substring(freedIndex + 9, line.indexOf(" byte")));
                    double memoryAfter = Double.parseDouble(line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf("/")));
                    double time = Double.parseDouble(line.substring(line.lastIndexOf("in ") + 3, line.lastIndexOf(" ms")));
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
