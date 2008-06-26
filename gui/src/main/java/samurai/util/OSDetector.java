/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.util;

public final class OSDetector {
    public final static boolean windows = -1 != System.getProperty("os.name").toLowerCase().indexOf("windows");

    public final static boolean linux = -1 != System.getProperty("os.name").toLowerCase().indexOf("linux");

    public final static boolean mac = -1 != System.getProperty("os.name").toLowerCase().indexOf("mac");

    public static boolean isMac() {
        return mac;
    }

    public static boolean isLinux() {
        return linux;
    }

    public static boolean isWindows() {
        return windows;
    }


}
