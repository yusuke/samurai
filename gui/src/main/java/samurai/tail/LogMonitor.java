/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.tail;

import java.io.File;
import java.io.IOException;

public interface LogMonitor {
    void onLine(File file, String line, long filePointer);

    void logStarted(File file, long filePointer);

    void logWillEnd(File file, long filePointer);

    void logEnded(File file, long filePointer);

    void logContinued(File file, long filePointer);

    void onException(File file, IOException ioe);
}
