package samurai.gc;

import samurai.util.LineGraphDataSourceParser;
/**
 * <p>Title: Samurai</p>
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
public class GCParser implements LineGraphDataSourceParser {
    private LineGraphDataSourceParser[] gcParsers = new LineGraphDataSourceParser[]{new BEAGCParser(), new SunGCParser(), new IBMGCParser()};
    private LineGraphDataSourceParser finalParser = null;

    public GCParser() {
    }

    public boolean parse(String line, LineGraphRenderer renderer) {
        if (null != finalParser) {
            return finalParser.parse(line, renderer);
        } else {
            for (int i = 0; i < gcParsers.length; i++) {
                if (gcParsers[i].parse(line, renderer)) {
                    finalParser = gcParsers[i];
                    return true;
                }
            }
            return false;
        }
    }
}
