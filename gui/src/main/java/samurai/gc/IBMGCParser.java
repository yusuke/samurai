package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.ScattergramDataSourceParser;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class IBMGCParser implements ScattergramDataSourceParser {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private boolean labelSet = false;
    private double memoryMax = 0;
    private double currentMemoryMax = 0;
    private double memoryFreed = 0;
    private double memoryAfter = 0;
    private double time = 0;

    public IBMGCParser() {
    }

    int freedIndex = 0;

    public boolean parse(String line, ScattergramRenderer renderer) {
        try {
            if (line.startsWith("  <GC(") && -1 != (freedIndex = line.indexOf("): freed"))) {
                if (!labelSet) {
                    renderer.setLabels(new String[]{resources.getMessage("GraphPanel.time") + "(ms)",
                            resources.getMessage("GraphPanel.memoryFreed"),
                            resources.getMessage("GraphPanel.memoryAfterGCbytes")});
                    labelSet = true;
                }
                try {
                    currentMemoryMax = Double.parseDouble(line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(")")));
                    if (memoryMax < currentMemoryMax) {
                        memoryMax = currentMemoryMax;
                        renderer.setMaxAt(1, memoryMax);
                        renderer.setMaxAt(2, memoryMax);
                    }
                    memoryFreed = Double.parseDouble(line.substring(freedIndex + 9, line.indexOf(" byte")));
                    memoryAfter = Double.parseDouble(line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf("/")));
                    time = Double.parseDouble(line.substring(line.lastIndexOf("in ") + 3, line.lastIndexOf(" ms")));
                    renderer.addValues(new double[]{time, memoryFreed, memoryAfter});
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
