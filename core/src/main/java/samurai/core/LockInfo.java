/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import java.util.ArrayList;
import java.util.List;

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
