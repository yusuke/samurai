package samurai.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public abstract class ThreadDump implements Serializable {
    private List<StackLine> lockList = null;
    private StackLine blockedLine = null;

    private final List<StackLine> stackLines = new ArrayList<StackLine>();
    private final String header;
    private static final long serialVersionUID = -7984606567041592064L;

    public ThreadDump(String header) {
        this.header = header;
    }

    public final String getHeader() {
        return this.header;
    }

    private String condition = null;

    Pattern p = Pattern.compile("(?<= *)[^\"]+$");
    public String getCondition() {
        if (null == this.condition) {
            Matcher m = p.matcher(this.header);
            m.find();
            this.condition = m.group();
//            int lastQuote = this.header.lastIndexOf("\"");
//
//            this.condition = this.header.substring(this.header.);
        }
        return this.condition;
    }

    public final List<StackLine> getStackLines() {
        return this.stackLines;
    }

    public final int size() {
        return getStackLines().size();
    }

    /*package*/
    abstract void addStackLine(String line);

    protected void addStackLine(StackLine stackLine) {
        getStackLines().add(stackLine);
        if (stackLine.isTryingToGetLock()) {
            blockedLine = stackLine;
        }
        if (stackLine.isHoldingLock()) {
            if (null == lockList) {
                lockList = new ArrayList<StackLine>();
            }
            lockList.add(stackLine);
        }
    }

    public String getBlockerId() {
        if (null != blockedLine) {
            return this.blockedLine.getBlockerId();
        }
        return null;
    }

    /*package*/ void setBlockerId(String id) {
        this.blockedLine.setBlockerId(id);
    }

    public List<StackLine> getLockedLines() {
        return lockList;
    }


    public String getBlockedObjectId() {
        if (null != blockedLine) {
            return blockedLine.getLockedObjectId();
        } else {
            return null;
        }
    }

    /**
     * returns the thread's name.
     *
     * @return name
     */
    public final String getName() {
        //extract thread name
        int headerBeginIndex = getHeader().indexOf("\"") + 1;
        int headerEndIndex = getHeader().indexOf("\"", headerBeginIndex);
        return getHeader().substring(headerBeginIndex, headerEndIndex);
    }

    public final StackLine getLine(int i) {
        return getStackLines().get(i);
    }

    public abstract boolean isBlocked();

    public abstract boolean isIdle();

    public abstract boolean isDaemon();

    public abstract String getId();

    public final boolean equals(Object obj) {
        boolean isEqual = false;
        if (null != obj && obj instanceof ThreadDump) {
            ThreadDump that = (ThreadDump) obj;
            if (that == this) {
                isEqual = true;
            } else {
                if (that.size() == this.size()) {
                    List thatList = that.getStackLines();
                    List thisList = this.getStackLines();
                    isEqual = true;
                    for (int i = 0; i < this.size(); i++) {
                        if (!thisList.get(i).equals(thatList.get(i))) {
                            isEqual = false;
                            break;
                        }
                    }
                }
            }
        }
        return isEqual;
    }

    public String getHeaderParameter(String name) {
        int index = getCondition().indexOf(name + "=");
        int paramStart;
        int paramEnd;
        if (-1 != index) {
            //Sun
            paramStart = index + name.length() + 1;
            paramEnd = getCondition().indexOf(" ", paramStart);
        } else if (-1 != (index = getCondition().indexOf(name + ": "))) {
            //JRockit viking
            paramStart = index + name.length() + 2;
            paramEnd = getCondition().indexOf(" ", paramStart);
        } else if (-1 != (index = getCondition().indexOf(name + ":"))) {
            //IBM and JRockit Ariane & Dragon
            paramStart = index + name.length() + 1;
            paramEnd = getCondition().indexOf(" ", paramStart);
            int tmp = getCondition().indexOf(")", paramStart);
            if (tmp != -1 && tmp < paramEnd) {
                paramEnd = tmp;
            }
            tmp = getCondition().indexOf(",", paramStart);
            if (tmp != -1 && tmp < paramEnd) {
                paramEnd = tmp;
            }
        } else {
            return "";
        }
        if (-1 != paramEnd) {
            return getCondition().substring(paramStart, paramEnd);
        } else {
            return getCondition().substring(paramStart);
        }
    }

    private boolean deadLocked = false;

    public boolean isDeadLocked() {
        return this.deadLocked;
    }

    /*package*/ void setDeadLocked(boolean deadLocked) {
        this.deadLocked = deadLocked;
    }
}
