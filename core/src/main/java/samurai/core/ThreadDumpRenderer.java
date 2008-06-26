/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;


public interface ThreadDumpRenderer {
    void onFullThreadDump(FullThreadDump fullThreadDump);

    void onThreadDump(ThreadDump threadDump);
}
