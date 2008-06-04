package samurai.core;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class SunFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = 5278627297157708511L;

    /*package*/ SunFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return line.startsWith("\"") && -1 != line.indexOf("prio");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return "".equals(line);
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return true;
    }
}
