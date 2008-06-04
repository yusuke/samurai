package samurai.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SUN's fullthreaddump
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
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

    public ObjectLock[] getObjectLocks() {

        List<ObjectLock> objectLocks = new ArrayList<ObjectLock>();
        for (Iterator iter = threadDumps.iterator(); iter.hasNext();) {
            SunThreadDump threadDump = (SunThreadDump) iter.next();
            List theLockList = threadDump.getLockedLines();
            if (0 != theLockList.size()) {
                objectLocks.add(new ObjectLock(threadDump,
                        threadDump.getLockedLines()));
            }
        }
        ObjectLock[] locks = new ObjectLock[objectLocks.size()];
        for (int i = 0; i < objectLocks.size(); i++) {
            locks[i] = objectLocks.get(i);
        }
        return locks;
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
