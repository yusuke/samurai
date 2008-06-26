/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.gc;

import samurai.util.LineGraphDataSourceParser;
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
