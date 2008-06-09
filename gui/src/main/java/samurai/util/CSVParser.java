package samurai.util;

import samurai.gc.LineGraph;
import samurai.gc.LineGraphRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>i
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class CSVParser implements LineGraphDataSourceParser {
    public CSVParser() {
    }

    private String[] labels = null;
    private List<LineGraph> lineGraphs = null;

    public boolean parse(String line, LineGraphRenderer renderer) {
        if (null == lineGraphs) {
            labels = line.split(",");
            lineGraphs = new ArrayList<LineGraph>(labels.length);
            for(String label:labels){
                lineGraphs.add(renderer.addLineGraph(label, new String[]{label}));
            }
        } else {
            String[] splitted = line.split(",");
            double[] datas = new double[labels.length];
            for (int i = 0; i < labels.length; i++) {
                try {
                    datas[i] = Double.parseDouble(splitted[i]);
                } catch (NumberFormatException nfe) {
                    datas[i] = splitted[i].hashCode();
                } catch (ArrayIndexOutOfBoundsException aioobe) {
                    datas[i] = 0d;
                }
            }
            for(int i=0;i<labels.length;i++){
                lineGraphs.get(i).addValues(new double[]{datas[i]});
            }
        }
        return true;
    }
}
