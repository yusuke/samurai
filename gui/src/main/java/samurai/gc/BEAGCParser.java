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
package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.LineGraphDataSourceParser;

import java.awt.Color;

public class BEAGCParser implements LineGraphDataSourceParser {
    public BEAGCParser() {
    }

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private LineGraph lineGraph = null;
    private double memoryMax = 0;
    private double currentMemoryMax = 0;
    private double time = 0;
    private double memoryBefore = 0;
    private double memoryAfter = 0;

    int timeStart;

    /**
     * parse
     *
     * @param line String
     * @return boolean
     */
    public boolean parse(String line, LineGraphRenderer renderer) {
        if (line.startsWith("[memory ] ")) {
            try {
                if (null == lineGraph) {
                    lineGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{resources.getMessage("GraphPanel.time") + "(ms)",
                            resources.getMessage("GraphPanel.memoryBeforeGC"),
                            resources.getMessage("GraphPanel.memoryAfterGC")});
                    lineGraph.setColorAt(0, Color.GRAY);
                    lineGraph.setColorAt(1, Color.RED);
                    lineGraph.setColorAt(2, Color.YELLOW);
                }
//[memory ] 14.210: Nursery GC 34347K->26260K (141312K), 197.487 ms
//[memory ] 1437.757-1445.118: GC 125428K->43203K (141312K), 7.361 s (6388.739 ms)

//[memory ] 72.250-72.390: GC 97781K->37613K (98304K), 58.556 ms
                if (-1 != line.indexOf(" s ")) {
                    timeStart = line.lastIndexOf("(") + 1;
                } else {
                    timeStart = line.indexOf(", ") + 2;
                }
                try {
                    currentMemoryMax = Double.parseDouble(line.substring(line.indexOf("K (") + 3,
                            line.indexOf("K)")));
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        lineGraph.setYMax(1, memoryMax);
                        lineGraph.setYMax(2, memoryMax);
                    }
                    time = Double.parseDouble(line.substring(timeStart, line.lastIndexOf(" ms")));
                    memoryBefore = Double.parseDouble(line.substring(line.indexOf("GC ") + 3,
                            line.indexOf("K->")));
                    memoryAfter = Double.parseDouble(line.substring(line.indexOf("->") + 2,
                            line.indexOf("K (")));
                    lineGraph.addValues(new double[]{time, memoryBefore, memoryAfter});
                    return true;
                } catch (NumberFormatException wasNotGC) {
//         wasNotGC.printStackTrace();
                    //does nothing
                }
            } catch (StringIndexOutOfBoundsException sioobe) {
//        System.err.println("unexpected format:" + line);
            }
        }
        return false;
    }
}
