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
public class BEAFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = -1445294393744936922L;

    /*package*/ BEAFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return (line.startsWith("\"") || line.startsWith("Thread-")) && -1 != line.indexOf("prio");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return "".equals(line) || -1 != line.indexOf("}");
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return !line.startsWith("======================================");
    }

}
