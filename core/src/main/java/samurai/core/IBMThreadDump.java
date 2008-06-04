package samurai.core;

import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class IBMThreadDump extends ThreadDump {
    private final IBMLockInfos lockInfos;
    private static final long serialVersionUID = 8076724752742673662L;

    /*package*/ IBMThreadDump(String header, IBMLockInfos lockInfos) {
        super(header.substring(header.indexOf("\"")));
        this.lockInfos = lockInfos;
        if (null != lockInfos) {
            LockInfo waiting = lockInfos.getWaitingLockInfoByThreadId(this.getId());
            if (null != waiting) {
                //	- waiting on <0x10010388> (a java.lang.ref.Reference$Lock)
                super.addStackLine(new SunStackLine("- waiting to lock <" + waiting.getHash() + "> (" + waiting.getClassName() + ")"));
            }
            List<LockInfo> info = lockInfos.getLockedInfoListByThreadId(this.getId());
            for (LockInfo theInfo : info) {
                //	- locked <0x10010388> (a java.lang.ref.Reference$Lock)
                super.addStackLine(new SunStackLine("- locked <" + theInfo.getHash() + "> (" + theInfo.getClassName() + ")"));
            }
        }
    }

    /*package*/ void addStackLine(String stackLine) {
//    if (stackLine.startsWith("4XESTACKTRACE") || stackLine.startsWith("3XMTHREADINFO")) {
        super.addStackLine(new SunStackLine(stackLine.substring(stackLine.indexOf(" ") + 1)));
//    }
    }

//    public String getState() {
//        return "";
//    }

    public boolean isBlocked() {
        return null != lockInfos && lockInfos.isWaiting(getHeaderParameter("sys_thread_t"));
    }

    public boolean isIdle() {
        return !isBlocked() && -1 != getHeader().indexOf("state:CW");
    }

    public boolean isDaemon() {
        return false;
    }

    public String getId() {
        return this.getHeaderParameter("sys_thread_t");
    }

}
