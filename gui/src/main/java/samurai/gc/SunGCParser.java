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

    public SunGCParser() {
    }

    public boolean parse(String line, ScattergramRenderer renderer) {
        try {
            if (-1 != line.indexOf("[GC ") || -1 != line.indexOf("[Full GC ")) {
                if (!labelSet) {
                    renderer.setLabels(new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                            resources.getMessage("GraphPanel.memoryBeforeGC"),
                            resources.getMessage("GraphPanel.memoryAfterGC")});
                    labelSet = true;
                }
//        int memoryBeforeGCStart;
//        int memoryBeforeGCEnd;
//        int memoryAfterGCStart;
//        int memoryAfterGCEnd;
//        int heapStart;
//        int heapEnd;
//        int timeStart;
//        int timeEnd;

//    [GC 1929K->1462K(1984K), 0.0036946 secs]
//    [Full GC 1462K->1353K(1984K), 0.1106755 secs]
//          memoryBeforeGCStart = line.indexOf("GC ") + 3;
//          memoryBeforeGCEnd = line.indexOf("K->");
//          memoryAfterGCStart = line.indexOf("->") + 2;
//          memoryAfterGCEnd = line.indexOf("K(");
//          heapStart = line.indexOf("K(") + 2;
//          heapEnd = line.indexOf("K)");
//          timeStart = ;
//          timeEnd = ;

                try {
                    double currentMemoryMax = Double.parseDouble(line.substring(line.indexOf("K(") + 2,
                            line.indexOf("K)")));
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        renderer.setMaxAt(1, memoryMax);
                        renderer.setMaxAt(2, memoryMax);
                    }
                    double time = Double.parseDouble(line.substring(line.indexOf(", ") + 2, line.lastIndexOf(" secs")));
                    double memoryBefore = Double.parseDouble(line.substring(line.indexOf("GC ") + 3,
                            line.indexOf("K->")));
                    double memoryAfter = Double.parseDouble(line.substring(line.indexOf("->") + 2,
                            line.indexOf("K(")));
                    renderer.addValues(new double[]{time, memoryBefore, memoryAfter});
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
