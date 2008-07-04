/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
public abstract class ThreadDump implements Serializable {
    private List<StackLine> lockList = null;
    private StackLine blockedLine = null;

    private final List<StackLine> stackLines = new ArrayList<StackLine>();
    private final String HEADER;
    private static final long serialVersionUID = -7984606567041592064L;
    private final String NAME;

    protected String STACK_RANGE;
    protected boolean IS_BLOCKED;
    protected boolean IS_IDLE;
    protected boolean IS_DAEMON;
    protected boolean IS_BLOCKING;
    private Pattern conditionCatchPtn = Pattern.compile("(?<= *)[^\"]+$");
    private final String CONDITION;

    public ThreadDump(String header) {
        this.HEADER = header;
        //extract thread name
        int headerBeginIndex = getHeader().indexOf("\"") + 1;
        int headerEndIndex = getHeader().indexOf("\"", headerBeginIndex);
        NAME = getHeader().substring(headerBeginIndex, headerEndIndex);
        Matcher m = conditionCatchPtn.matcher(this.HEADER);
        m.find();
        this.CONDITION = m.group();
    }

    public final String getHeader() {
        return this.HEADER;
    }


    public String getCondition() {
        return this.CONDITION;
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
//            this.IS_BLOCKING = true;
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
        return NAME;
    }

    public final StackLine getLine(int i) {
        return getStackLines().get(i);
    }

    /**
     * test if thread is a daemon thread.
     *
     * @return if the thread is a daemon thread.
     */
    public boolean isDaemon() {
        return IS_DAEMON;
    }

    /**
     * returns the thread's stack range.
     *
     * @return name
     */
    public String getStackRange() {
        return this.STACK_RANGE;
    }


    public boolean isBlocked() {
        return IS_BLOCKED;
    }
    public boolean isIdle() {
        return IS_IDLE;
    }

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
    public boolean isBlocking() {
        return this.IS_BLOCKING;
    }

    /*package*/ void setDeadLocked(boolean deadLocked) {
        this.deadLocked = deadLocked;
    }
    /*package*/ void setBlocking(boolean blocking) {
        this.IS_BLOCKING = blocking;
    }
}
