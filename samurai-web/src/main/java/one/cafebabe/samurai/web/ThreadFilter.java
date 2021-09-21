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
package one.cafebabe.samurai.web;

import one.cafebabe.samurai.core.FullThreadDump;
import one.cafebabe.samurai.core.ThreadDumpSequence;
import one.cafebabe.samurai.core.ThreadStatistic;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ThreadFilter implements Serializable {
    public View mode;
    private int fullThreadIndex;
    private String threadId;
    public boolean config_shrinkIdleThreads = false;
    private static final long serialVersionUID = 34941357376786143L;

    public ThreadFilter() {
        reset();
    }

    public enum View {
        table,
        sequence,
        full
    }

    public void reset() {
        mode = View.table;
        fullThreadIndex = 0;
        threadId = "";
        config_shrinkIdleThreads = true;
    }

    public boolean isTableView() {
        return mode == View.table;
    }

    public boolean isSequenceView() {
        return mode == View.sequence;
    }

    public boolean isThreadDumpView() {
        return mode == View.full;
    }

    public ThreadDumpSequence doFilter(ThreadStatistic statistic) {
        ThreadDumpSequence sequence = null;
        if (this.mode == View.full) {
            FullThreadDump fullThreadDump = statistic.getFullThreadDump(this.
                    fullThreadIndex);
//      threadDumps = new StackTrace[fullThreadDump.getThreadCount()];
            for (int i = 0; i < fullThreadDump.getThreadCount(); i++) {
                if (i == 0) {
                    sequence = new ThreadDumpSequence(fullThreadDump.getThreadDump(i), 1);
                } else {
                    sequence.addThreadDump(fullThreadDump.getThreadDump(i));
                }
            }
        } else {
            sequence = statistic.getStackTracesById(threadId);
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

    public void setQuery(String query) {
        if (query.contains(Constants.MODE_TABLE)) {
            mode = View.table;
        }
        if (query.contains(Constants.MODE_FULL)) {
            mode = View.full;
        }
        if (query.contains(Constants.MODE_SEQUENCE)) {
            mode = View.sequence;
        }
        if (mode == View.full) {
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
            setShrinkIdle(Boolean.parseBoolean(shrinkIdle));
        }


    }

    public static String getParameter(String query, String name) {
        int index = query.indexOf(name);
        if (-1 == index) {
            return null;
        } else {
            int valueBegin = query.indexOf("-", index) + 1;
            int valueEnd = query.indexOf("_", index);
            if (-1 == valueEnd) {
                valueEnd = query.lastIndexOf(".");
            }
            if (-1 != valueEnd) {
                return URLDecoder.decode(query.substring(valueBegin, valueEnd), StandardCharsets.UTF_8);
            } else {
                return URLDecoder.decode(query.substring(valueBegin), StandardCharsets.UTF_8);
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
