package samurai.gc;

import samurai.util.GUIResourceBundle;
import samurai.util.LineGraphDataSourceParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;

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
    private LineGraph newGraph = null;
    private LineGraph oldGraph = null;
    private LineGraph permGraph = null;
    private double newSize = 0;
    private double oldSize = 0;
    private double permSize = 0;
    private boolean unloadingClasses = false;
    /*
    [GC 115008K->103309K("129664K"), 0.0254331 secs]
    */
    private Pattern heapSizePtn = Pattern.compile("(?<=K\\()[0-9]*");
    /*
    [GC "115008K"->103309K(129664K), 0.0254331 secs]
    */
    private Pattern heapBeforeGCPtn = Pattern.compile("(?<= )[0-9]*(?=K->)");
    /*
    [GC 115008K->"103309K"(129664K), 0.0254331 secs]
    */
    private Pattern heapAfterGCPtn = Pattern.compile("(?<=K->)[0-9]*(?=K\\()");
    /*
    [GC 115008K->103309K(129664K), [0.0254331] secs]
    */
    private Pattern timeSpentPtn = Pattern.compile("(?<=, )[0-9\\.]*(?= secs\\]$)");
    /*
8.017: [Full GC 8.017: [Tenured: 0K->4271K(786432K), 0.1114785 secs] 79759K->4271K(1022400K), [Perm : "9999"K->9999K(131072K)], 0.1117439 secs]
    */
    private Pattern permBeforeGCPtn = Pattern.compile("(?<=m : )[0-9]*(?=K->)");
    /*
8.017: [Full GC 8.017: [Tenured: 0K->4271K(786432K), 0.1114785 secs] 79759K->4271K(1022400K), [Perm : 9999K->"9999"K(131072K)], 0.1117439 secs]
    */
    private Pattern permAfterGCPtn = Pattern.compile("[0-9]*(?=K\\([0-9]*K\\)\\])");
    /*
8.017: [Full GC 8.017: [Tenured: 0K->4271K(786432K), 0.1114785 secs] 79759K->4271K(1022400K), [Perm : 9999K->9999K("131072"K)], 0.1117439 secs]
    */
    private Pattern permSizePtn = Pattern.compile("(?<=K\\()[0-9]*(?=K\\)\\])");

    private boolean gcTypeDetected = false;
    private boolean printGCDetails = false;

    public SunGCParser() {
    }
    /*
        [GC 115008K->103309K(129664K), 0.0254331 secs]
        [Full GC 61365K->51414K(129664K), 1.0320474 secs]
        [ParNew 226778K->33381K(1022400K), 0.3251635 secs]

-XX:+PrintGCDetails -XX:+PrintGCTimeStamps
8.017: [Full GC 8.017: [Tenured: 0K->4271K(786432K), 0.1114785 secs] 79759K->4271K(1022400K), [Perm : 9999K->9999K(131072K)], 0.1117439 secs]
25.505: [GC 25.505: [DefNew: 209792K->12642K(235968K), 0.1573065 secs] 214063K->16913K(1022400K), 0.1573849 secs]

-XX:+PrintGCDetails
[Full GC [Tenured: 0K->4271K(786432K), 0.1163157 secs] 79759K->4271K(1022400K), [Perm : 9991K->9991K(131072K)], 0.1165657 secs]
[GC [DefNew: 209792K->12630K(235968K), 0.1971975 secs] 214063K->16902K(1022400K), 0.2291682 secs]

 -XX:+UseParNewGC -XX:+PrintGCDetails
[Full GC [Tenured: 0K->4270K(786432K), 0.0986367 secs] 75563K->4270K(1022400K), [Perm : 9988K->9988K(131072K)], 0.0987355 secs]
[GC [ParNew: 209792K->12597K(235968K), 0.0741744 secs] 214062K->16868K(1022400K), 0.0742407 secs]

-XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+PrintGCDetails
[Full GC [CMS: 0K->4275K(786432K), 0.1640437 secs] 89008K->4275K(1048384K), [CMS Perm : 9993K->9982K(131072K)], 0.1641700 secs]
[GC [ParNew: 261760K->0K(261952K), 0.1481358 secs] 266035K->18129K(1048384K), 0.1488370 secs]
[GC [ParNew: 261760K->0K(261952K), 0.1638750 secs] 279889K->42443K(1048384K), 0.1640296 secs]
     */

    public boolean parse(String line, LineGraphRenderer renderer) {
        try {
            if (-1 != line.indexOf("[GC ") || -1 != line.indexOf("[Full GC ") ||-1 != line.indexOf("[ParNew ") ||  unloadingClasses || -1 != line.indexOf("[Unloading class")) {
                if(!gcTypeDetected){
                    if (-1 != line.indexOf("[Tenured: ") || -1 != line.indexOf("[DefNew: ") || -1 != line.indexOf("[CMS: ") || -1 != line.indexOf("[ParNew: ")) {
                        printGCDetails = true;
                    }
                    gcTypeDetected = true;
                }
                if (!printGCDetails) {
                    //-verbose:gc / -verbosegc
                    if (null == newGraph) {
                        newGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.memory"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                                resources.getMessage("GraphPanel.heapBeforeGC"),
                                resources.getMessage("GraphPanel.heapAfterGC")});
                        newGraph.setColorAt(0, Color.GRAY);
                        newGraph.setColorAt(1, Color.RED);
                        newGraph.setColorAt(2, Color.YELLOW);
                    }

                    unloadingClasses = -1 != line.indexOf("[Unloading class");
                    if (!unloadingClasses) {
                        //finished unloading classes
                        try {
                            parseMinorGC(line);
                            return true;
                        } catch (NumberFormatException ignore) {
                        }
                    }
                }else{
                    //-XX:+PrintGCDetails
                    if (null == newGraph) {
                        newGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.new"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                                resources.getMessage("GraphPanel.newBeforeGC"),
                                resources.getMessage("GraphPanel.newAfterGC")});
                        newGraph.setColorAt(0, Color.GRAY);
                        newGraph.setColorAt(1, Color.RED);
                        newGraph.setColorAt(2, Color.YELLOW);
                        oldGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.old"), new String[]{resources.getMessage("GraphPanel.time") + "(secs)",
                                resources.getMessage("GraphPanel.oldBeforeGC"),
                                resources.getMessage("GraphPanel.oldAfterGC")});
                        oldGraph.setColorAt(0, Color.GRAY);
                        oldGraph.setColorAt(1, Color.RED);
                        oldGraph.setColorAt(2, Color.YELLOW);
                        permGraph = renderer.addLineGraph(resources.getMessage("GraphPanel.permanent"), new String[]{
                                resources.getMessage("GraphPanel.permBeforeGC"),
                                resources.getMessage("GraphPanel.permAfterGC")});
                        permGraph.setColorAt(0, Color.RED);
                        permGraph.setColorAt(1, Color.YELLOW);
                    }
                    if(-1 != line.indexOf("[GC ")){
                        // minor GC
                        try {
                            parseMinorGC(line);
                            return true;
                        } catch (NumberFormatException ignore) {
                        }
                    }else{
                        // Full GC
                        unloadingClasses = -1 != line.indexOf("[Unloading class");
                        if (!unloadingClasses) {
                            //finished unloading classes
                            try {
                                double currentSize = extract(line, heapSizePtn);
                                if (oldSize < currentSize) {
                                    oldSize = currentSize;
                                    oldGraph.setMaxAt(1, oldSize);
                                    oldGraph.setMaxAt(2, oldSize);
                                }

                                double timeSpent = extract(line, timeSpentPtn);
                                double heapBeforeGC = extract(line, heapBeforeGCPtn);
                                double heapAfterGC = extract(line, heapAfterGCPtn);
                                oldGraph.addValues(new double[]{timeSpent, heapBeforeGC, heapAfterGC});


                                currentSize = extract(line, permSizePtn);
                                if (permSize < currentSize) {
                                    permSize = currentSize;
                                    permGraph.setMaxAt(0, permSize);
                                    permGraph.setMaxAt(1, permSize);
                                }

                                heapBeforeGC = extract(line, permBeforeGCPtn);
                                heapAfterGC = extract(line, permAfterGCPtn);
                                permGraph.addValues(new double[]{heapBeforeGC, heapAfterGC});
                                return true;
                            } catch (NumberFormatException ignore) {
                                ignore.printStackTrace();
                            }
                        }
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

    private void parseMinorGC(String line){
        double currentSize = extract(line, heapSizePtn);
        if (newSize < currentSize) {
            newSize = currentSize;
            newGraph.setMaxAt(1, newSize);
            newGraph.setMaxAt(2, newSize);
        }

        double timeSpent = extract(line, timeSpentPtn);
        double heapBeforeGC = extract(line, heapBeforeGCPtn);
        double heapAfterGC = extract(line, heapAfterGCPtn);
        newGraph.addValues(new double[]{timeSpent, heapBeforeGC, heapAfterGC});
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
