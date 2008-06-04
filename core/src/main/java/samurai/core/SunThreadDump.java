package samurai.core;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003 BEA Systems Japan, Inc.</p>
 * <p>Company: BEA Systems Japan, Inc.</p>
 *
 * @author Yusuke Yamamoto
 * @version 0.1
 */

/*package*/ class SunThreadDump extends ThreadDump {
    private String state;
    private String stackRange;

    private final boolean debug = false;

    public static final String RUNNABLE = "runnable";
    public static final String WAITING_ON_MONITOR = "waiting on monitor";
    public static final String WAITING_FOR_MONITOR_ENTRY =
            "waiting for monitor entry";
    public static final String WAITING_ON_CONDITION = "waiting on condition";
    public static final String SUSPENDED = "suspended";
    private static final long serialVersionUID = -906873270679566722L;

    /*package*/ SunThreadDump(String header) {
        super(header);

        //calculate thread state
        int stateBeginIndex = getHeader().lastIndexOf("=");
        stateBeginIndex = getHeader().indexOf(" ", stateBeginIndex) + 1;
        String state;
        if (getHeader().endsWith("]")) {
            int endIndex = getHeader().lastIndexOf("[") - 1;
            state = getHeader().substring(stateBeginIndex, endIndex);
        } else {
            state = getHeader().substring(stateBeginIndex).trim();
        }

        if (state.equals("runnable")) {
            this.state = RUNNABLE;
        } else if (state.equals("waiting on monitor")) {
            this.state = WAITING_ON_MONITOR;
        } else if (state.equals("waiting for monitor entry")) {
            this.state = WAITING_FOR_MONITOR_ENTRY;
        } else if (state.equals("waiting on condition")) {
            this.state = WAITING_ON_CONDITION;
        } else if (state.equals("suspended")) {
            this.state = SUSPENDED;

        } else {
            this.state = state;
        }

        //calculate thread stack range
        if (getHeader().endsWith("]")) {
            int stackbegin = getHeader().lastIndexOf("[");
            this.stackRange = getHeader().substring(stackbegin);
        } else {
            this.stackRange = "";
        }
    }


    /*package*/ void addStackLine(String line) {
        addStackLine(new SunStackLine(line));
    }

    public String getId() {
        return this.getHeaderParameter("tid");
    }

    public String toString() {
        StringBuffer toStringed = new StringBuffer(128);
        toStringed.append(getHeader());
        if (debug)
            System.out.println("sizetostring1:" + getStackLines().size());
        if (debug)
            System.out.println("header:[" + getHeader() + "]");
        for (int i = 0; i < getStackLines().size(); i++) {
            if (debug)
                System.out.println("sizetostring2:" + getStackLines().size());
            toStringed.append('\n').append(getStackLines().get(i));
        }
        if (debug)
            System.out.println("sizetostring3:" + toStringed.toString());
        return toStringed.toString();
    }

    /**
     * returns the thread's priority
     *
     * @return priority
     */
    public int getPriority() {
        return Integer.parseInt(getHeaderParameter("prio"));
    }

    /**
     * returns the thread's tid
     *
     * @return tid
     */
    public String getTid() {
        return getHeaderParameter("tid");
    }

    /**
     * returns the thread's nid
     *
     * @return nid
     */
    public String getNid() {
        return getHeaderParameter("nid");
    }

    /**
     * returns the thread's lwp_id.
     * This parameter is available only on HP-UX.
     *
     * @return lwp_id
     */
    public String getLwp_id() {
        return getHeaderParameter("lwp_id");
    }


    /**
     * test if thread is a daemon thread.
     *
     * @return if the thread is a daemon thread.
     */
    public boolean isDaemon() {
        return -1 !=
                getHeader().indexOf(" daemon ", getHeader().lastIndexOf("\""));
    }

    /**
     * returns the thread's stack range.
     *
     * @return name
     */
    public String getStackRange() {
        return this.stackRange;
    }

//    public String getState() {
//        return this.state;
//    }


    public boolean isBlocked() {
        return state.equals("waiting for monitor entry");
    }

    public boolean isIdle() {
        //test if last method is Object.wait()
        if (state.equals(SunThreadDump.SUSPENDED) ||
                state.equals("in Object.wait()")) {
            return true;
        }
        if (size() > 0) {
            return (getLine(0).getClassName().equals("java.lang.Object")
                    && getLine(0).getMethodName().equals("wait")) ||
                    (getLine(0).getClassName().equals("java.lang.Thread")
                            && getLine(0).getMethodName().equals("sleep"));
        }
        return false;
    }
}
