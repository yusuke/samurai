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

import java.util.ArrayList;
import java.util.List;

public class BEAThreadDump extends ThreadDump {
    private static final long serialVersionUID = -6581661997485086419L;
    private final String[] STATUS_ARRAY;
    private final boolean IS_WEBLOGIC_ADMIN;
    private final String CONDITION;
    private final String ID;


    /*package*/ BEAThreadDump(String header) {
        super(header);
        int statusFrom;
        IS_WEBLOGIC_ADMIN = getHeader().startsWith("Thread-");
        if (!IS_WEBLOGIC_ADMIN) {
            CONDITION = super.getCondition();
        } else {
            int paramBegin = super.getCondition().lastIndexOf("<") + 1;
            int paramEnd = super.getCondition().lastIndexOf(">");
            CONDITION = super.getCondition().substring(paramBegin, paramEnd);
        }
        if (!IS_WEBLOGIC_ADMIN) {
            ID = this.getHeaderParameter("id");
        } else {
            int idBegin = getHeader().indexOf("-") + 1;
            int idEnd = getHeader().indexOf(" ");
            ID = getHeader().substring(idBegin, idEnd);
        }

        if (IS_WEBLOGIC_ADMIN) {
            //Thread-0xf80 "ExecuteThread: '0' for queue: 'weblogic.admin.RMI'" <Waiting, priority=5, DAEMON> {
            List<String> statusList = new ArrayList<>();
            String[] statusArray = getCondition().split(",");
            for (int i = 0; i < statusArray.length; i++) {
                statusArray[i] = statusArray[i].trim();
                if (!statusArray[i].contains("=")) {
                    statusList.add(statusArray[i]);
                }
            }
            STATUS_ARRAY = statusList.toArray(statusArray);
        } else {
            if (isAriane()) {
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
            STATUS_ARRAY = statusArray;
        }

        if (IS_WEBLOGIC_ADMIN) {
            IS_IDLE = hasStatus("Waiting") || hasStatus("waiting");
        } else if (isAriane()) {
            //maybe ariane
            //-8.1SP4 -> waiting
            //9.0 -> sleeping
            IS_IDLE = hasStatus("waiting") || hasStatus("sleeping") || hasStatus("native_waiting");
        } else {
            //maybe viking
            IS_IDLE = hasStatus("WAITING");
        }

        if (isAriane()) {
            //maybe ariane
            IS_DAEMON = hasStatus("daemon");
        } else {
            //maybe viking or weblogic.Admin thread dump
            IS_DAEMON = hasStatus("DAEMON");
        }

    }

    /*package*/ void addStackLine(String stackLine) {
        super.addStackLine(new BEAStackLine(stackLine.substring(stackLine.indexOf(" ") + 1)));
    }


    private boolean isAriane() {
        return !IS_WEBLOGIC_ADMIN && getCondition().contains("=");
    }

    public boolean isDaemon() {
        return IS_DAEMON;
    }


    private boolean isBlockedAnalyzed = false;
    public boolean isBlocked() {
        if (!isBlockedAnalyzed) {
            if (isAriane() || IS_WEBLOGIC_ADMIN) {
                //81SP3
                if (hasStatus("blocked")) {
                    IS_BLOCKED = true;
                } else if (this.size() >= 1) {
                    //81SP4 sometimes shows blocked thread as "active". need to check the 1st line to test if the thread is blocked
                    IS_BLOCKED = this.getLine(0).getLine().trim().startsWith(
                            "-- Blocked trying to get lock");
                } else {
                    IS_BLOCKED = false;
                }
            } else {
                //maybe viking
                if (this.size() >= 3) {
                    StackLine line = this.getLine(2);
                    if (line.isLine()) {
                        if ("COM.jrockit.vm.RNI".equals(line.getClassName()) &&
                                "waitOnThinLocker".equals(line.getMethodName())) {
                            IS_BLOCKED = true;
                        }
                    }
                }
                if (this.size() >= 4) {
                    StackLine line = this.getLine(3);
                    IS_BLOCKED = IS_BLOCKED || line.isLine() &&
                            ("jrockit/vm/Locks".equals(line.getClassName()) &&
                                    "monitorEnter".equals(line.getMethodName()));

                } else {
                    IS_BLOCKED = IS_BLOCKED || hasStatus("LOCKED");
                }
            }
            isBlockedAnalyzed = true;
        }
        return IS_BLOCKED;
    }

    public boolean isIdle() {
        return IS_IDLE;
    }

    public String getId() {
        return ID;
    }

    private boolean hasStatus(String status) {
        for (String aStatusArray : STATUS_ARRAY) {
            if (status.equals(aStatusArray)) {
                return true;
            }
        }
        return false;
    }

    public String getCondition() {
        return CONDITION;
    }
}
