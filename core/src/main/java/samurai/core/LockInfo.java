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
public class LockInfo implements java.io.Serializable{
    private String className;
    private String owner;
    private String hash;
    private List<String> waitingList = new ArrayList<String>();
    private static final long serialVersionUID = 1110522952428062892L;

    public LockInfo(String className, String owner) {
        this.className = className;
        this.owner = owner;
    }

    public void addWaiter(String id) {
        waitingList.add(id);
    }

    public String getClassName() {
        return this.className;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String id) {
        this.owner = id;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public boolean isWaiting(String id) {
        for (String aWaitingList : waitingList) {
            if (aWaitingList.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
