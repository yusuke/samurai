package samurai.util;

import samurai.gc.ScattergramRenderer;

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
public class CSVParser implements ScattergramDataSourceParser {
    public CSVParser() {
    }

    private String[] labels = null;
    private boolean labelSet = false;

    public boolean parse(String line, ScattergramRenderer renderer) {
        if (!labelSet) {
            labels = line.split(",");
            renderer.setLabels(labels);
            labelSet = true;
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
            renderer.addValues(datas);
        }
        return true;
    }
}
