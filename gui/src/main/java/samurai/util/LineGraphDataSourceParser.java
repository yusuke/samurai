/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.util;

import samurai.gc.LineGraphRenderer;

public interface LineGraphDataSourceParser {
    boolean parse(String line, LineGraphRenderer renderer);
}
