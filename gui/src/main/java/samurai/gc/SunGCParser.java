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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SunGCParser implements LineGraphDataSourceParser {
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private HeapGraph newGraph = null;
    private HeapGraph oldGraph = null;
    private HeapGraph permGraph = null;
    private boolean unloadingClasses = false;
    /*
    [GC 115008K->103309K("129664K"), 0.0254331 secs]
    */
    private final Pattern heapSizePtn = Pattern.compile("(?<=K\\()[0-9]+");
    /*
    [GC "115008K"->103309K(129664K), 0.0254331 secs]
    */
    private final Pattern heapBeforeGCPtn = Pattern.compile("(?<= )[0-9]+(?=K->)");
    /*
    [GC 115008K->"103309K"(129664K), 0.0254331 secs]
    */
    private final Pattern heapAfterGCPtn = Pattern.compile("(?<=K->)[0-9]*(?=K\\()");
    /*
    [GC 115008K->103309K(129664K), [0.0254331] secs]
    */
    private final Pattern timeSpentPtn = Pattern.compile("(?<=, )[0-9.]*(?= secs]$)");

    /*
   a pattern catches -verbosegc log
[GC 115008K->103309K(129664K), 0.0254331 secs]
[Full GC 61365K->51414K(129664K), 1.0320474 secs]
    */
    private final Pattern verboseGCPtn = Pattern.compile("\\[(ParNew|GC|Full GC) [0-9]+K->[0-9]+K\\([0-9]+K\\), [0-9.]+ secs]");
    private final Pattern printGCDetailsPtn = Pattern.compile("\\[(GC|Full GC) ([0-9.]+: )?\\[");
    /*
    a pattern catches new area gc log
[ParNew 226778K->33381K(1022400K), 0.3251635 secs]
[DefNew: 209792K->12630K(235968K), 0.1971975 secs]
[PSYoungGen: 2715K->0K(33344K)]
     */
    private final Pattern newGCPtn = Pattern.compile("\\[(ParNew|DefNew|PSYoungGen):? [0-9]+K->[0-9]+K\\([0-9]+K\\)(, [0-9.]+ secs)?]");

    /*
    a pattern catches old area gc log
[Tenured: 0K->4271K(786432K), 0.1163157 secs]
[CMS: 0K->4275K(786432K), 0.1640437 secs]
[ParOldGen: 65738K->36992K(99072K)]
[PSOldGen: 43812K->28039K(67968K)]
     */
    private final Pattern oldGCPtn = Pattern.compile("\\[(Tenured|CMS( \\(concurrent mode failure\\))?|ParOldGen|PSOldGen): [0-9]+K->[0-9]+K\\([0-9]+K\\)(, [0-9.]+ secs)?]");

    /*
    a pattern catches permanent area gc log
[Perm : 9991K->9991K(131072K)]
[CMS Perm : 9993K->9982K(131072K)]
[PSPermGen: 11772K->11755K(23552K)]
     */
    private final Pattern permGCPtn = Pattern.compile("\\[(Perm |CMS Perm |PSPermGen): [0-9]+K->[0-9]+K\\([0-9]+K\\)]");


    private boolean gcTypeDetected = false;
    private boolean printGCDetails = false;

    public SunGCParser() {
    }

    private String unloadingHeader = null;

    public boolean parse(String line, LineGraphRenderer renderer) {
        int unloadingIndex = line.indexOf("[Unloading class ");
        if (unloadingClasses) {
            if (-1 == unloadingIndex) {
                line = unloadingHeader + line;
                unloadingClasses = false;
            }
        } else {
            if (-1 != unloadingIndex) {
                unloadingClasses = true;
                unloadingHeader = line.substring(0, unloadingIndex);
            }
        }
        if (!unloadingClasses && line.contains("[") && (line.contains("[GC") || line.contains("[Full GC") || line.contains("[ParNew "))) {
            if (!gcTypeDetected) {
                printGCDetails = printGCDetailsPtn.matcher(line).find();
                initializeGraphs(renderer);
                gcTypeDetected = true;
            }
            try {
                if (!printGCDetails) {
                    //-verbose:gc / -verbosegc
                    newGraph.parse(line);
                    return true;
                } else {
                    //-XX:+PrintGCDetails
                    if (line.contains("[GC ")) {
                        // minor GC
                        newGraph.parse(line);
                        // rarely and strangely old gc happens with "[GC"
                        oldGraph.parse(line);
                        return true;
                    } else {
                        // Full GC
                        newGraph.parse(line);
                        oldGraph.parse(line);
                        permGraph.parse(line);
                        return true;
                    }

                }
            } catch (StringIndexOutOfBoundsException | IllegalArgumentException | IllegalStateException ignore) {
                System.err.println("unexpected format:" + line);
            }
        }
        return false;
    }

    private class HeapGraph {
        double maxSize = 0;
        final Pattern pattern;
        final LineGraph lineGraph;
        final boolean parseGCtime;

        HeapGraph(Pattern pattern, LineGraph lineGraph, boolean parseGCtime) {
            this.pattern = pattern;
            this.lineGraph = lineGraph;
            this.parseGCtime = parseGCtime;
            if (parseGCtime) {
                lineGraph.setColorAt(0, Color.GRAY);
                lineGraph.setColorAt(1, Color.RED);
                lineGraph.setColorAt(2, Color.YELLOW);
            } else {
                lineGraph.setColorAt(0, Color.RED);
                lineGraph.setColorAt(1, Color.YELLOW);
            }
        }

        void parse(String line) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String found = matcher.group();

                double currentSize = extract(found, heapSizePtn);
                if (maxSize < currentSize) {
                    maxSize = currentSize;
                    if (pattern == permGCPtn) {
                        lineGraph.setYMax(0, maxSize);
                        lineGraph.setYMax(1, maxSize);

                    } else {
                        lineGraph.setYMax(1, maxSize);
                        lineGraph.setYMax(2, maxSize);
                    }
                }
                double heapBeforeGC = extract(found, heapBeforeGCPtn);
                double heapAfterGC = extract(found, heapAfterGCPtn);
                if (parseGCtime) {
                    double timeSpent;
                    Matcher timeSpentMatcher = timeSpentPtn.matcher(found);
                    if (pattern == oldGCPtn || !timeSpentMatcher.find()) {
                        timeSpentMatcher = timeSpentPtn.matcher(line);
                        timeSpentMatcher.find();
                        timeSpent = Double.parseDouble(timeSpentMatcher.group());
                    } else {
                        timeSpent = Double.parseDouble(timeSpentMatcher.group());
                    }
                    lineGraph.addValues(new double[]{timeSpent, heapBeforeGC, heapAfterGC});
                } else {
                    lineGraph.addValues(new double[]{heapBeforeGC, heapAfterGC});
                }
            }
        }
    }

    private double extract(String line, Pattern pattern) {
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            return Double.parseDouble(m.group());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void initializeGraphs(LineGraphRenderer renderer) {
        if (printGCDetails) {
            newGraph = new HeapGraph(newGCPtn, renderer.addLineGraph(resources.getMessage("GraphPanel.new"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                    resources.getMessage("GraphPanel.newBeforeGC"),
                    resources.getMessage("GraphPanel.newAfterGC")}), true);
            oldGraph = new HeapGraph(oldGCPtn, renderer.addLineGraph(resources.getMessage("GraphPanel.old"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                    resources.getMessage("GraphPanel.oldBeforeGC"),
                    resources.getMessage("GraphPanel.oldAfterGC")}), true);
            permGraph = new HeapGraph(permGCPtn, renderer.addLineGraph(resources.getMessage("GraphPanel.permanent"), new String[]{
                    resources.getMessage("GraphPanel.permBeforeGC"),
                    resources.getMessage("GraphPanel.permAfterGC")}), false);
        } else {
            newGraph = new HeapGraph(verboseGCPtn, renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                    resources.getMessage("GraphPanel.heapBeforeGC"),
                    resources.getMessage("GraphPanel.heapAfterGC")}), true);
        }
    }
}
