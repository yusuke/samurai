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
public class IBMFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = 9102293976017908812L;

    /*package*/ IBMFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return line.startsWith("3XMTHREADINFO");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return false;
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return !line.startsWith("NULL");
    }
}
