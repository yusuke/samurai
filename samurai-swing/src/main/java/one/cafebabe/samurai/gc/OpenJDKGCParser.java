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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * supports OpenJDK G1GC log, parallel gc log
 */
public class OpenJDKGCParser implements LineGraphDataSourceParser {
    public OpenJDKGCParser() {
    }

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private LineGraph lineGraph = null;
    private double memoryMax = 0;

    //[2.226s][info][gc] GC(0) Pause Young (Metadata GC Threshold) 22M->4M(123M) 5.476ms
    private final static Pattern pattern = Pattern.compile("([0-9]+)M->([0-9]+)M\\(([0-9]+)M\\) ([0-9.]+)ms");

    /**
     * parse
     *
     * @param line String
     * @return boolean
     */
    public boolean parse(String line, LineGraphRenderer renderer) {
//[2.226s][info][gc] GC(0) Pause Young (Metadata GC Threshold) 22M->4M(123M) 5.476ms
        if (line.contains("[gc] GC(")) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find() || matcher.groupCount() != 4) {
                return false;
            }
            int memoryBefore = Integer.parseInt(matcher.group(1));
            int memoryAfter = Integer.parseInt(matcher.group(2));
            double currentMemoryMax = Double.parseDouble(matcher.group(3));
            double time = Double.parseDouble(matcher.group(4));
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
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }
}