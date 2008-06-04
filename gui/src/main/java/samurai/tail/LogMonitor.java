package samurai.tail;

import java.io.File;
import java.io.IOException;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public interface LogMonitor {
    void onLine(File file, String line, long filePointer);

    void logStarted(File file, long filePointer);

    void logWillEnd(File file, long filePointer);

    void logEnded(File file, long filePointer);

    void logContinued(File file, long filePointer);

    void onException(File file, IOException ioe);
}
