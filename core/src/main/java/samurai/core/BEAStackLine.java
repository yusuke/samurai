package samurai.core;

/**
 * JRockit's stackline
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class BEAStackLine extends StackLine {
    private static final long serialVersionUID = -8477301890129249380L;

    /*package*/ BEAStackLine(String line) {
        super(line);
    }

//  public boolean isCondition() {
//    return -1 != getLine().indexOf("-- ");
//  }

    public boolean isTryingToGetLock() {
        if (super.isTryingToGetLock()) {
            return true;
        } else {
            return -1 != line.indexOf("-- Blocked trying to get lock");
        }
    }

    public boolean isHoldingLock() {
        if (super.isHoldingLock()) {
            return true;
        } else {
            return -1 != line.indexOf("^-- Holding lock:");
        }
    }

    public String getLockedObjectId() {
        if (super.isTryingToGetLock() || super.isHoldingLock()) {
            return super.getLockedObjectId();
        } else {
            try {
                return line.substring(line.indexOf("@") + 1, line.indexOf("["));
            } catch (StringIndexOutOfBoundsException sioobe) {
                return null;
            }
        }
    }

    public String getLockedClassName() {
        if (super.isTryingToGetLock() || super.isHoldingLock()) {
            return super.getLockedClassName();
        } else {
            return line.substring(line.indexOf(" lock: ") + 7, line.indexOf("@"));
        }
    }
}
