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
package one.cafebabe.samurai.core;

public class BEAStackLine extends StackLine {
    private static final long serialVersionUID = -8477301890129249380L;
    private final boolean IS_TRYING_TO_GET_LOCK;
    private final boolean IS_HOLDING_LOCK;
    private final String LOCKED_OBJECT_ID;
    private final String LOCKED_CLASS_NAME;

    /*package*/ BEAStackLine(String line) {
        super(line);
        this.IS_TRYING_TO_GET_LOCK = super.isTryingToGetLock() || line.contains("-- Blocked trying to get lock");
        this.IS_HOLDING_LOCK = super.isHoldingLock() || line.contains("^-- Holding lock:");

        if (super.isTryingToGetLock() || super.isHoldingLock()) {
            LOCKED_OBJECT_ID = super.getLockedObjectId();
            LOCKED_CLASS_NAME = super.getLockedClassName();
        } else {
            if (IS_TRYING_TO_GET_LOCK || IS_HOLDING_LOCK) {
                String lockedObjectID = null;
                try {
                    lockedObjectID = line.substring(line.indexOf("@") + 1, line.indexOf("["));
                } catch (StringIndexOutOfBoundsException ignore) {
                }
                LOCKED_OBJECT_ID = lockedObjectID;
                LOCKED_CLASS_NAME = line.substring(line.indexOf(" lock: ") + 7, line.indexOf("@"));

            }else{
                LOCKED_OBJECT_ID = null;
                LOCKED_CLASS_NAME = null;
            }
        }

    }

    public boolean isTryingToGetLock() {
        return IS_TRYING_TO_GET_LOCK;
    }

    public boolean isHoldingLock() {
        return IS_HOLDING_LOCK;
    }

    public String getLockedObjectId() {
        return LOCKED_OBJECT_ID;
    }

    public String getLockedClassName() {
        return LOCKED_CLASS_NAME;
    }
}
