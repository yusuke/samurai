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


/*package*/ class SunThreadDump extends ThreadDump {
    private final String STATE;

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
            this.STATE = RUNNABLE;
        } else if (state.equals("waiting on monitor")) {
            this.STATE = WAITING_ON_MONITOR;
        } else if (state.equals("waiting for monitor entry")) {
            this.STATE = WAITING_FOR_MONITOR_ENTRY;
        } else if (state.equals("waiting on condition")) {
            this.STATE = WAITING_ON_CONDITION;
        } else if (state.equals("suspended")) {
            this.STATE = SUSPENDED;

        } else {
            this.STATE = state;
        }

        //calculate thread stack range
        if (getHeader().endsWith("]")) {
            int stackbegin = getHeader().lastIndexOf("[");
            this.STACK_RANGE = getHeader().substring(stackbegin);
        } else {
            this.STACK_RANGE = "";
        }

        IS_BLOCKED = STATE.equals("waiting for monitor entry");
        IS_DAEMON = -1 != getHeader().indexOf(" daemon ", getHeader().lastIndexOf("\""));
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

    boolean isIdleAnalyzed = false;

    @Override public boolean isIdle() {
        if(!isIdleAnalyzed){
            //test if last method is Object.wait()
            if (STATE.equals(SunThreadDump.SUSPENDED) ||
                    STATE.equals("in Object.wait()")) {
                IS_IDLE = true;
            }else if (size() > 0) {
                IS_IDLE= (getLine(0).getClassName().equals("java.lang.Object")
                        && getLine(0).getMethodName().equals("wait")) ||
                        (getLine(0).getClassName().equals("java.lang.Thread")
                                && getLine(0).getMethodName().equals("sleep"));
            }else{
                IS_IDLE = false;
            }
            isIdleAnalyzed = true;
        }
        return IS_IDLE;
    }
}
