/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import java.util.List;

public class IBMThreadDump extends ThreadDump {
    private static final long serialVersionUID = 8076724752742673662L;

    /*package*/ IBMThreadDump(String header, IBMLockInfos lockInfos) {
        super(header.substring(header.indexOf("\"")));
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
        IS_BLOCKED = null != lockInfos && lockInfos.isWaiting(getHeaderParameter("sys_thread_t"));

    }

    /*package*/ void addStackLine(String stackLine) {
//    if (stackLine.startsWith("4XESTACKTRACE") || stackLine.startsWith("3XMTHREADINFO")) {
        super.addStackLine(new SunStackLine(stackLine.substring(stackLine.indexOf(" ") + 1)));
//    }
        IS_DAEMON = false;

    }



    public String getId() {
        return this.getHeaderParameter("sys_thread_t");
    }
    @Override public boolean isIdle(){
        return !isBlocked() && -1 != getHeader().indexOf("state:CW");
    }

}
