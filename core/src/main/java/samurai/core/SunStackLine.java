package samurai.core;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class SunStackLine extends StackLine {
    private static final long serialVersionUID = 2404952046137420766L;

    /*package*/ SunStackLine(String line) {
        super(line);
    }

    /*methods for condition*/

//  public boolean isWaitingToLock() {
//    return -1 != getLine().indexOf("waiting to lock");
//  }

//  public boolean isLocked() {
//    return -1 != getLine().indexOf("locked");
//  }

    //
    public boolean isWaitingOn() {
        return -1 != getLine().indexOf("waiting on");
    }

    public String getTarget() {
        if (isLine()) {
            return "n/a";
        } else {
            return getLine().substring(getLine().indexOf("<"));
        }
    }

//  public boolean isCondition() {
//    return getLine().trim().startsWith("-");
//  }
}
