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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class FullThreadDump implements Serializable {
    private List<ThreadDump> threadDumps;
    private String header;
    private static final long serialVersionUID = 3698912680951716481L;

    public FullThreadDump(String header) {
        this.header = header;
        threadDumps = new ArrayList<ThreadDump>();
    }

    /*package*/ void addThreadDump(ThreadDump threadDump) {
        threadDumps.add(threadDump);
    }

    public String getHeader() {
        return header;
    }

    public int getThreadCount() {
        return threadDumps.size();
    }

    public String toString() {
        StringBuffer toStringed = new StringBuffer(256);
        toStringed.append(this.header);
        for (ThreadDump threadDump :  threadDumps) {
            toStringed.append("\n").append(threadDump.toString());
        }
        return toStringed.toString();
    }

    ObjectLock[] objectLocks = null;

    public ObjectLock[] getObjectLocks() {
        if(this.objectLocks == null){
            List<ObjectLock> objectLockList = new ArrayList<ObjectLock>();
            for (Iterator iter = threadDumps.iterator(); iter.hasNext();) {
                SunThreadDump threadDump = (SunThreadDump) iter.next();
                List theLockList = threadDump.getLockedLines();
                if (0 != theLockList.size()) {
                    objectLockList.add(new ObjectLock(threadDump,
                            threadDump.getLockedLines()));
                }
            }
            this.objectLocks = new ObjectLock[objectLockList.size()];
            for (int i = 0; i < objectLockList.size(); i++) {
                this.objectLocks[i] = objectLockList.get(i);
            }
        }
        return this.objectLocks;
    }

    public ThreadDump getThreadDump(int i) {
        return threadDumps.get(i);
    }

    public ThreadDump getThreadDumpById(String id) {
        ThreadDump threadDump;
        for (int i = 0; i < threadDumps.size(); i++) {
            threadDump = getThreadDump(i);
            if (id.equals(threadDump.getId())) {
                return threadDump;
            }
        }
        return null;
    }

    /*package*/
    abstract boolean isThreadHeader(String line);

    /*package*/
    abstract boolean isThreadFooter(String line);

    /*package*/
    abstract boolean isThreadDumpContinuing(String line);

    List<List<ThreadDump>> deadLockChains = new ArrayList<List<ThreadDump>>();
    private boolean deadLocked = false;

    public boolean isDeadLocked() {
        return deadLocked;
    }

    public int getDeadLockSize() {
        return deadLockChains.size();
    }

    public List<List<ThreadDump>> getDeadLockChains() {
        return deadLockChains;
    }

    /*package*/ void finish() {
        Map<String, ThreadDump> blockers = new HashMap<String, ThreadDump>();
        for (ThreadDump threadDump : threadDumps) {
            List<StackLine> locked = threadDump.getLockedLines();
            if (null != locked) {
                for (StackLine line : locked) {
                    blockers.put(line.getLockedObjectId(), threadDump);
                }
            }
        }


        Map<String, List<ThreadDump>> locked = new HashMap<String, List<ThreadDump>>();
        for (ThreadDump threadDump : threadDumps) {
            if (threadDump.isBlocked()) {
                List<ThreadDump> list = locked.get(threadDump.getBlockedObjectId());
                if (null == list) {
                    list = new ArrayList<ThreadDump>();
                }
                list.add(threadDump);
                locked.put(threadDump.getBlockedObjectId(), list);
                ThreadDump blocker = blockers.get(threadDump.getBlockedObjectId());
                if (null != blocker) {
                    threadDump.setBlockerId(blocker.getId());
                    this.getThreadDumpById(blocker.getId()).setBlocking(true);
                }
            }
        }

        List<ThreadDump> deadLockChain = new ArrayList<ThreadDump>();
        for (ThreadDump threadDump:this.threadDumps) {
            if (!threadDump.isDeadLocked() && threadDump.isBlocked()) {
                deadLockChain.clear();
                deadLockChain.add(threadDump);
                while (threadDump.isBlocked()) {
                    String blockerId = threadDump.getBlockerId();
                    if (null == blockerId) {
                        break;
                    }
                    threadDump = this.getThreadDumpById(blockerId);
                    if (threadDump.isDeadLocked()) {
                        break;
                    }
                    if (deadLockChain.contains(threadDump)) {
                        //deadLockDetected
                        deadLocked = true;
                        int deadLockBegin = deadLockChain.indexOf(threadDump);
                        for (int j = 0; j < deadLockBegin; j++) {
                            deadLockChain.remove(0);
                        }
                        deadLockChains.add(deadLockChain);
                        for (ThreadDump deadLocked : deadLockChain) {
                            deadLocked.setDeadLocked(true);
                        }
                        break;
                    }
                    deadLockChain.add(threadDump);
                }
            }
        }
    }
}
