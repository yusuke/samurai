package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.ScattergramDataSourceParser;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class SunGCParser implements ScattergramDataSourceParser {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private boolean labelSet = false;
    private double memoryMax = 0;
    private boolean unloadingClasses = false;

    public SunGCParser() {
    }

    public boolean parse(String line, ScattergramRenderer renderer) {
        try {
            if (-1 != line.indexOf("[GC ") || -1 != line.indexOf("[Full GC ") ||-1 != line.indexOf("[ParNew ") ||  unloadingClasses || -1 != line.indexOf("[Unloading class")) {
                if (!labelSet) {
                    renderer.setLabels(new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                            resources.getMessage("GraphPanel.memoryBeforeGC"),
                            resources.getMessage("GraphPanel.memoryAfterGC")});
                    labelSet = true;
                }

                unloadingClasses = -1 != line.indexOf("[Unloading class");
                if (!unloadingClasses) {
                    //finished unloading classes

                    try {
                        double currentMemoryMax = Double.parseDouble(line.substring(line.indexOf("K(") + 2,
                                line.indexOf("K)")));
                        if (memoryMax < currentMemoryMax) {
                            memoryMax = currentMemoryMax;
                            renderer.setMaxAt(1, memoryMax);
                            renderer.setMaxAt(2, memoryMax);
                        }

                        double time = Double.parseDouble(line.substring(line.indexOf(", ") + 2, line.lastIndexOf(" secs")));
                        int memoryBeforeBegin = line.indexOf("GC ");
                        if (-1 == memoryBeforeBegin) {
                            memoryBeforeBegin = line.indexOf("ParNew ")+7;
                            if (-1 == memoryBeforeBegin) {
                                memoryBeforeBegin = 1;
                            }
                        } else {
                            memoryBeforeBegin += 3;
                        }
                        double memoryBefore = Double.parseDouble(line.substring(memoryBeforeBegin,
                                line.indexOf("K->")));
                        double memoryAfter = Double.parseDouble(line.substring(line.indexOf("->") + 2,
                                line.indexOf("K(")));
                        renderer.addValues(new double[]{time, memoryBefore, memoryAfter});
                        return true;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            System.err.println("unexpected format:" + line);
        }
        return false;
    }
}
