package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.LineGraphDataSourceParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class SunGCParser implements LineGraphDataSourceParser {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();
    private boolean labelSet = false;
    private double memoryMax = 0;
    private boolean unloadingClasses = false;
    private Pattern heapSizePtn = Pattern.compile("(?<=K\\()[0-9]*");
    private Pattern memoryBeforePtn = Pattern.compile("(?<= )[0-9]*(?=K->)");
    private Pattern memoryAfterPtn = Pattern.compile("(?<=K->)[0-9]*(?=K\\()");
    private Pattern timeSpentPtn = Pattern.compile("(?<=, )[0-9\\.]*(?= secs)");

    public SunGCParser() {
    }
    /*
        [GC 115008K->103309K(129664K), 0.0254331 secs]
        [Full GC 61365K->51414K(129664K), 1.0320474 secs]
        [ParNew 226778K->33381K(1022400K), 0.3251635 secs]
     */

    public boolean parse(String line, LineGraphRenderer renderer) {
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
                        double currentMemoryMax = extract(line,heapSizePtn);
                        if (memoryMax < currentMemoryMax) {
                            memoryMax = currentMemoryMax;
                            renderer.setMaxAt(1, memoryMax);
                            renderer.setMaxAt(2, memoryMax);
                        }

                        double timeSpent = extract(line,timeSpentPtn);
                        double memoryBefore = extract(line,memoryBeforePtn);
                        double memoryAfter = extract(line,memoryAfterPtn);
                        renderer.addValues(new double[]{timeSpent, memoryBefore, memoryAfter});
                        return true;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            System.err.println("unexpected format:" + line);
        } catch (IllegalArgumentException iae) {
            System.err.println("unexpected format:" + line);
        }
        return false;
    }
    private double extract(String line,Pattern pattern){
        Matcher m = pattern.matcher(line);
        if(m.find()){
            return Double.parseDouble(m.group());
        }else{
            throw new IllegalArgumentException();
        }
    }
}
