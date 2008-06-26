/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import java.util.ArrayList;
import java.util.List;
public class BEAThreadDump extends ThreadDump {
    private static final long serialVersionUID = -6581661997485086419L;

    /*package*/ BEAThreadDump(String header) {
        super(header);
    }

    /*package*/ void addStackLine(String stackLine) {
        super.addStackLine(new BEAStackLine(stackLine.substring(stackLine.indexOf(" ") + 1)));
    }

//    public String getState() {
//        return "";
//    }

    private boolean isWebLogicAdmin() {
        return getHeader().startsWith("Thread-");
    }

    private boolean isAriane() {
        return !isWebLogicAdmin() && -1 != getCondition().indexOf("=");
    }

    public boolean isDaemon() {
        if (isAriane()) {
            //maybe ariane
            return hasStatus("daemon");
        } else {
            //maybe viking or weblogic.Admin thread dump
            return hasStatus("DAEMON");
        }
    }


    public boolean isBlocked() {
        if (isAriane() || isWebLogicAdmin()) {
            //81SP3
            if (hasStatus("blocked")) {
                return true;
            } else if (this.size() >= 1) {
                //81SP4 sometimes shows blocked thread as "active". need to check the 1st line to test if the thread is blocked
                return this.getLine(0).getLine().trim().startsWith(
                        "-- Blocked trying to get lock");
            } else {
                return false;
            }
        } else {
            //maybe viking
            if (this.size() >= 3) {
                StackLine line = this.getLine(2);
                if (line.isLine()) {
                    if ("COM.jrockit.vm.RNI".equals(line.getClassName()) &&
                            "waitOnThinLocker".equals(line.getMethodName())) {
                        return true;
                    }

                }
            }

            if (this.size() >= 4) {
                StackLine line = this.getLine(3);
                return line.isLine() &&
                        ("jrockit/vm/Locks".equals(line.getClassName()) &&
                                "monitorEnter".equals(line.getMethodName()));

            }
            return hasStatus("LOCKED");
        }
    }

    public boolean isIdle() {
        if (isWebLogicAdmin()) {
            return hasStatus("Waiting") || hasStatus("waiting");
        } else if (isAriane()) {
            //maybe ariane
            //-8.1SP4 -> waiting
            //9.0 -> sleeping
            return hasStatus("waiting") || hasStatus("sleeping") || hasStatus("native_waiting");
        } else {
            //maybe viking
            return hasStatus("WAITING");
        }
    }

    public String getId() {
        if (!isWebLogicAdmin()) {
            return this.getHeaderParameter("id");
        } else {
            int idBegin = getHeader().indexOf("-") + 1;
            int idEnd = getHeader().indexOf(" ");
            return getHeader().substring(idBegin, idEnd);
        }
    }

    private boolean hasStatus(String status) {
        String[] statusArray = getStatusArray();
        for (String aStatusArray : statusArray) {
            if (status.equals(aStatusArray)) {
                return true;
            }
        }
        return false;
    }

    public String getCondition() {
        if (!isWebLogicAdmin()) {
            return super.getCondition();
        } else {
            int paramBegin = super.getCondition().lastIndexOf("<") + 1;
            int paramEnd = super.getCondition().lastIndexOf(">");
            return super.getCondition().substring(paramBegin, paramEnd);
        }
    }

    private String[] getStatusArray() {
        int statusFrom;
        if (isWebLogicAdmin()) {
            //Thread-0xf80 "ExecuteThread: '0' for queue: 'weblogic.admin.RMI'" <Waiting, priority=5, DAEMON> {
            List<String> statusList = new ArrayList<String>();
            String[] statusArray = getCondition().split(",");
            for (int i = 0; i < statusArray.length; i++) {
                statusArray[i] = statusArray[i].trim();
                if (-1 == statusArray[i].indexOf("=")) {
                    statusList.add(statusArray[i]);
                }
            }
            return statusList.toArray(statusArray);
        } else if (isAriane()) {
            //assuming JRockit1.4.2_03
            //"Main Thread" prio=5 id=0x80 pid=13184 waiting
            int paramEnd = getCondition().indexOf("pid=");
            if (-1 == paramEnd) {
                //JRockit1.4.2_05
                //"Main Thread" prio=5 id=0x80 tid=0x1650 waiting
                paramEnd = getCondition().indexOf("tid=");

            }
            statusFrom = getCondition().indexOf(" ", paramEnd);
        } else {
            statusFrom = getCondition().indexOf(" ",
                    getCondition().indexOf("prio: ") + 6);
        }
        String[] statusArray = getCondition().substring(statusFrom).split(",");
        for (int i = 0; i < statusArray.length; i++) {
            statusArray[i] = statusArray[i].trim();
        }
        return statusArray;
    }

}
