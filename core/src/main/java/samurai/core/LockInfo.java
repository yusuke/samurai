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

import java.util.ArrayList;
import java.util.List;

public class LockInfo implements java.io.Serializable{
    private final String className;
    private String owner;
    private String hash;
    private final List<String> waitingList = new ArrayList<>();
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
