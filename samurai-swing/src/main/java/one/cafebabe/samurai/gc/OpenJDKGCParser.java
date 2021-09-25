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

/**
 * supports OpenJDK G1GC log, parallel gc log
 */
public class OpenJDKGCParser implements LineGraphDataSourceParser {
    public OpenJDKGCParser() {
    }

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private LineGraph lineGraph = null;
    private double memoryMax = 0;


    /**
     * parse
     *
     * @param line String
     * @return boolean
     */
    public boolean parse(String line, LineGraphRenderer renderer) {
//[2.226s][info][gc] GC(0) Pause Young (Metadata GC Threshold) 22M->4M(123M) 5.476ms
        if (line.contains("[gc] GC(")) {
            try {
                int pauseIndex = line.indexOf(") Pause ");
                if (pauseIndex == -1) {
                    return false;
                }
                int beforeEnd = line.indexOf("M->");
                if (beforeEnd == -1) {
                    return false;
                }

                int beforeStart = -1;
                for (int i = beforeEnd; i >pauseIndex; i--) {
                    if (line.charAt(i) == ' ') {
                        beforeStart = i+1; 
                        break;
                    }
                }
                if (beforeStart == -1) {
                    return false;
                }
                int afterStart = beforeEnd + 3;
                int afterEnd = line.indexOf("M(", afterStart);
                if (afterEnd == -1) {
                    return false;
                }
                int memoryMaxEnd = line.lastIndexOf("M)");
                int memoryMaxStart = afterEnd + 2;
                if (memoryMaxEnd == -1) {
                    return false;
                }
                int milliSecondsStart = memoryMaxEnd + 3;
                int milliSecondsEnd = line.indexOf("ms", milliSecondsStart);
                if (milliSecondsEnd == -1) {
                    return false;
                }
                int memoryBefore = Integer.parseInt(line.substring(beforeStart, beforeEnd));
                int memoryAfter = Integer.parseInt(line.substring(afterStart, afterEnd));
                double time = Double.parseDouble(line.substring(milliSecondsStart, milliSecondsEnd));
                double currentMemoryMax = Double.parseDouble(line.substring(memoryMaxStart, memoryMaxEnd));
                if (null == lineGraph) {
                    lineGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{resources.getMessage("GraphPanel.time") + "(ms)",
                            resources.getMessage("GraphPanel.memoryBeforeGC"),
                            resources.getMessage("GraphPanel.memoryAfterGC")});
                    lineGraph.setColorAt(0, Color.GRAY);
                    lineGraph.setColorAt(1, Color.RED);
                    lineGraph.setColorAt(2, Color.YELLOW);
                }
                try {
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        lineGraph.setYMax(1, memoryMax);
                        lineGraph.setYMax(2, memoryMax);
                    }
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