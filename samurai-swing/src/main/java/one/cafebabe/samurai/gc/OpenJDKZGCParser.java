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
 * supports OpenJDK ZGC log
 */
public class OpenJDKZGCParser implements LineGraphDataSourceParser {
    public OpenJDKZGCParser() {
    }

    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private LineGraph lineGraph = null;
    private int memoryMax = 0;

    // [1.190s][info][gc] GC(0) Garbage Collection (Warmup) 14M(11%)->6M(5%)
    private final static Pattern pattern = Pattern.compile("([0-9]+)M\\(([0-9]+)%\\)->([0-9]+)M\\(([0-9]+)%\\)");

    /**
     * parse
     *
     * @param line String
     * @return boolean
     */
    public boolean parse(String line, LineGraphRenderer renderer) {
        int gcIndex = line.indexOf(") Garbage Collection (");
        if (gcIndex != -1) {
            try {
                Matcher matcher = pattern.matcher(line);
                if (!matcher.find() || matcher.groupCount() != 4) {
                    return false;
                }

                int memoryBefore = Integer.parseInt(matcher.group(1));
                int memoryAfter = Integer.parseInt(matcher.group(3));
                int percentageBefore = Integer.parseInt(matcher.group(2));

                int currentMemoryMax = memoryBefore * 100 / percentageBefore;
                if (null == lineGraph) {
                    lineGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{
                            resources.getMessage("GraphPanel.memoryBeforeGC"),
                            resources.getMessage("GraphPanel.memoryAfterGC")});
                    lineGraph.setColorAt(0, Color.RED);
                    lineGraph.setColorAt(1, Color.YELLOW);
                }
                try {
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        lineGraph.setYMax(0, memoryMax);
                        lineGraph.setYMax(1, memoryMax);
                    }
                    lineGraph.addValues(new double[]{memoryBefore, memoryAfter});
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