package samurai.core;

import java.util.ArrayList;
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
public class IBMLockInfos implements java.io.Serializable {
    public static final String HEADER = "1LKPOOLINFO    Monitor pool info:";
    public static final String FOOTER = "1LKOBJMONDUMP";

    private List<LockInfo> lockInfoList = new ArrayList<LockInfo>();
    private static final long serialVersionUID = -1934815474658577019L;

    /*package*/ IBMLockInfos(String header) {
    }

    private static final String LOCK_INDICATOR0 = "3LKMONOBJECT";
    private static final String LOCK_INDICATOR = "Flat locked by thread ident ";
    private static final String MAPPING_INDICATOR = "2LKFLATMON         ident ";
    private static final String WAITING_INDICATOR = "3LKWAITNOTIFY            ";
    private static final String WAITING_INDICATOR2 = "3LKWAITER                ";
    private static final String LOCK_INDICATOR_END = "NULL           ";

    private LockInfo lastLockInfo = null;

    public void addLine(String line) {
        if (-1 != line.indexOf(LOCK_INDICATOR)) {
            String className = line.substring(19, line.indexOf("@"));
            String hash = line.substring(line.indexOf("@") + 1, line.indexOf(":"));
            String owner = line.substring(line.indexOf(LOCK_INDICATOR) + LOCK_INDICATOR.length(), line.lastIndexOf(","));
            lastLockInfo = new LockInfo(className, owner);
            lastLockInfo.setHash(hash);
            lockInfoList.add(lastLockInfo);
        } else
        if (line.startsWith(LOCK_INDICATOR0) && -1 != line.indexOf(": owner \"")) {
            String className = line.substring(19, line.indexOf("@"));
            String hash = line.substring(line.indexOf("@") + 1, line.indexOf(":"));
            String owner = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
            lastLockInfo = new LockInfo(className, owner);
            lastLockInfo.setHash(hash);
            lockInfoList.add(lastLockInfo);
        } else if (line.startsWith(LOCK_INDICATOR_END)) {
            lastLockInfo = null;
        } else
        if ((line.startsWith(WAITING_INDICATOR) || line.startsWith(WAITING_INDICATOR2)) && null != lastLockInfo) {
            lastLockInfo.addWaiter(line.substring(line.indexOf("(") + 1, line.indexOf(")")));
        } else if (-1 != line.indexOf(MAPPING_INDICATOR)) {
//      2LKFLATMON         ident 0x02 "Thread-3" (0x284E48) ee 0x00284CC0
            String temporaryId = line.substring(MAPPING_INDICATOR.length(), line.indexOf(" ", MAPPING_INDICATOR.length()));
            for (int i = 0; i < lockInfoList.size(); i++) {
                LockInfo lockInfo = lockInfoList.get(i);
                if (lockInfo.getOwner().equals(temporaryId)) {
                    String actualId = line.substring(line.indexOf("(") + 1, line.indexOf(")"));//)  line.lastIndexOf(" ")+1);
                    lockInfoList.get(i).setOwner(actualId);
                    break;
                }
            }
        }
    }

    public boolean isWaiting(String id) {
        for (LockInfo aLockInfoList : lockInfoList) {
            if (aLockInfoList.isWaiting(id)) {
                return true;
            }
        }
        return false;
    }

    public LockInfo getWaitingLockInfoByThreadId(String id) {
        for (LockInfo info : lockInfoList) {
            if (info.isWaiting(id)) {
                return info;
            }
        }
        return null;
    }

    public LockInfo getLockInfo(int index) {
        return lockInfoList.get(index);
    }

    public int size() {
        return lockInfoList.size();
    }

    public List<LockInfo> getLockedInfoListByThreadId(String threadId) {
        List<LockInfo> found = new ArrayList<LockInfo>();
        for (LockInfo info : lockInfoList) {
            if (info.getOwner().equals(threadId)) {
                found.add(info);
            }
        }
        return found;
    }
}
