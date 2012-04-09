/*
 * Copyright 2003-2012 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
