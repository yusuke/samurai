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
package samurai.web;

import samurai.core.ThreadDumpSequence;
import samurai.core.ThreadStatistic;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ThreadFilter implements Serializable {
    private String mode;
    private int fullThreadIndex;
    private String threadId;
    public boolean config_shrinkIdleThreads = false;
    private static final long serialVersionUID = 34941357376786143L;

    public ThreadFilter() {
        reset();
    }

    public void reset() {
        mode = Constants.MODE_TABLE;
        fullThreadIndex = 0;
        threadId = "";
        config_shrinkIdleThreads = true;
    }

    public String getMode() {
        return this.mode;
    }

    public boolean isTableView() {
        return mode.equals(Constants.MODE_TABLE);
    }

    public boolean isSequenceView() {
        return mode.equals(Constants.MODE_SEQUENCE);
    }

    public boolean isThreadDumpView() {
        return mode.equals(Constants.MODE_FULL);
    }

    public ThreadDumpSequence doFilter(ThreadStatistic statistic) {
        ThreadDumpSequence sequence = null;
        if (this.mode.equals(Constants.MODE_FULL)) {
            samurai.core.FullThreadDump fullThreadDump = statistic.getFullThreadDump(this.
                    fullThreadIndex);
//      threadDumps = new StackTrace[fullThreadDump.getThreadCount()];
            for (int i = 0; i < fullThreadDump.getThreadCount(); i++) {
                if (i == 0) {
                    sequence = new ThreadDumpSequence(fullThreadDump.getThreadDump(i), 1);
                } else {
                    sequence.addThreadDump(fullThreadDump.getThreadDump(i));
                }
//        threadDumps[i] = ;
            }
        } else {
            sequence = statistic.getStackTracesById(threadId);
//      List threadList = new ArrayList();
//      for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
//        FullThreadDump fullThreadDump = statistic.getFullThreadDump(i);
//        for (int j = 0; j < fullThreadDump.getThreadCount(); j++) {
//          StackTrace threadDump = fullThreadDump.getThreadDump(j);
//          if (threadDump.getId().equals(this.threadId)) {
//            threadList.add(threadDump);
//          }
//        }
//      }
//      threadDumps = new StackTrace[threadList.size()];
//      for (int i = 0; i < threadDumps.length; i++) {
//        threadDumps[i] = (StackTrace) threadList.get(i);
//      }
        }
        return sequence;
    }

    public int getFullThreadIndex() {
        return this.fullThreadIndex;
    }

    public void setFullThreadIndex(int index) {
        this.fullThreadIndex = index;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setQuery(String query) {
//    if (debug)
//      System.out.println("query:" + query);
//    String mode = getParameter(query,"mode");
//    if(null != mode){
//      setMode(mode);
//    }
        if (-1 != query.indexOf(Constants.MODE_TABLE)) {
            mode = Constants.MODE_TABLE;
        }
        if (-1 != query.indexOf(Constants.MODE_FULL)) {
            mode = Constants.MODE_FULL;
        }
        if (-1 != query.indexOf(Constants.MODE_SEQUENCE)) {
            mode = Constants.MODE_SEQUENCE;
        }
        if (getMode().equals(Constants.MODE_FULL)) {
            String fullThreadIndex = getParameter(query, Constants.FULL_THREAD_INDEX);
            try {
                if (null != fullThreadIndex) {
                    setFullThreadIndex(Integer.parseInt(fullThreadIndex));
                }
            } catch (NumberFormatException ignore) {
            }
        } else {
            String threadId = getParameter(query, Constants.THREAD_ID);
            if (null != threadId) {
                setThreadId(threadId);
            }
        }

        String shrinkIdle = getParameter(query, Constants.SHRINK_IDLE);
        if (null != shrinkIdle) {
            setShrinkIdle(Boolean.valueOf(shrinkIdle));
        }


    }

    public static String getParameter(String query, String name) {
        int index = query.indexOf(name);
        if (-1 == index) {
            return null;
        } else {
            int valuebegin = query.indexOf("-", index) + 1;
            int valueend = query.indexOf("_", index);
            if (-1 == valueend) {
                valueend = query.lastIndexOf(".");
            }
//      System.out.println(parambegin+":"+paramend);
            if (-1 != valueend) {
                return URLDecoder.decode(query.substring(valuebegin, valueend), StandardCharsets.UTF_8);
            } else {
                return URLDecoder.decode(query.substring(valuebegin), StandardCharsets.UTF_8);
            }
        }
    }

    public boolean getShrinkIdle() {
        return config_shrinkIdleThreads;
    }

    public void setShrinkIdle(boolean shrinkIdle) {
        this.config_shrinkIdleThreads = shrinkIdle;
    }

}
