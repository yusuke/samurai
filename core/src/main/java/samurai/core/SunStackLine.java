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
    private final boolean IS_WAITING_ON;
    private final String TARGET;

    /*package*/ SunStackLine(String line) {
        super(line);
        IS_WAITING_ON = -1 != getLine().indexOf("waiting on");
        if (isLine() || -1 == getLine().indexOf("<")) {
            TARGET = "n/a";
        } else {
            TARGET = getLine().substring(getLine().indexOf("<"));
        }
    }

    public boolean isWaitingOn() {
        return IS_WAITING_ON;
    }

    public String getTarget() {
        return TARGET;
    }
}
