package samurai.util;

import samurai.gc.ScattergramRenderer;

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
public interface ScattergramDataSourceParser {
    boolean parse(String line, ScattergramRenderer renderer);
}
