/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.swing;

import java.io.File;

public interface FileHistoryListener {
    void fileOpened(File file);

    void filesOpened(File[] files);
}
