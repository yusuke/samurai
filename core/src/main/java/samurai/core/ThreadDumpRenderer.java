package samurai.core;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003 BEA Systems Japan, Inc.</p>
 * <p>Company: BEA Systems Japan, Inc.</p>
 *
 * @author Yusuke Yamamoto
 * @version 0.1
 */

public interface ThreadDumpRenderer {
    void onFullThreadDump(FullThreadDump fullThreadDump);

    void onThreadDump(ThreadDump threadDump);
}
