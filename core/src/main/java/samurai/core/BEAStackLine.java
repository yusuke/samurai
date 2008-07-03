/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

public class BEAStackLine extends StackLine {
    private static final long serialVersionUID = -8477301890129249380L;
    private final boolean IS_TRYING_TO_GET_LOCK;
    private final boolean IS_HOLDING_LOCK;
    private final String LOCKED_OBJECT_ID;
    private final String LOCKED_CLASS_NAME;

    /*package*/ BEAStackLine(String line) {
        super(line);
        this.IS_TRYING_TO_GET_LOCK = super.isTryingToGetLock() || -1 != line.indexOf("-- Blocked trying to get lock");
        this.IS_HOLDING_LOCK = super.isHoldingLock() || -1 != line.indexOf("^-- Holding lock:");

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
