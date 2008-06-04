package samurai.util;

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
