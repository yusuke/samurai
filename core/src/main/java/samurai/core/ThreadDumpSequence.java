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

import java.io.Serializable;


public final class ThreadDumpSequence implements Serializable{
    private ThreadDump[] threadDumps;
    private final String toStringName;
    private final String name;
    private final String id;
    private static final long serialVersionUID = -6039654177797487161L;

    public ThreadDumpSequence(ThreadDump threadDump, int size) {
        this.threadDumps = new ThreadDump[size];
        this.threadDumps[size - 1] = threadDump;
        this.name = threadDump.getName();
        this.id = threadDump.getId();
        this.toStringName = abbreviateWebLogicThreadName(name);
    }

    public void addThreadDump(ThreadDump threadDump) {
        ThreadDump[] old = this.threadDumps;
        threadDumps = new ThreadDump[old.length + 1];
        System.arraycopy(old, 0, threadDumps, 0, old.length);
        threadDumps[threadDumps.length - 1] = threadDump;
    }

    /**
     * tests if the specified thread's state is same as previous one
     * index starts with 1
     *
     * @param index int
     * @return boolean
     */
    public boolean sameAsBefore(int index) {
        //range check
        if (index < 2 || index > threadDumps.length) {
            return false;
        }
        ThreadDump previous = threadDumps[index - 2];
        ThreadDump specified = threadDumps[index - 1];
        return null != previous && previous.equals(specified) || null == specified;
    }

    public int size() {
        return threadDumps.length;
    }

    public ThreadDump get(int i) {
        return threadDumps[i];
    }

    public ThreadDump[] asArray() {
        return threadDumps;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return this.toStringName;
    }

    public static String abbreviateWebLogicThreadName(String name) {
        try {
            if (-1 != name.indexOf("ExecuteThread:") && -1 != name.indexOf("for queue:")) {
                int numberStartIndex = name.indexOf("'") + 1;
                String number = name.substring(numberStartIndex, name.indexOf("'", numberStartIndex));
                int queueNameStartIndex = name.indexOf("for queue:") + 12;
                String queueName = name.substring(queueNameStartIndex, name.indexOf("'", queueNameStartIndex));
                //for Diablo thread names
                if (queueName.endsWith(" (self-tuning)")) {
                    queueName = queueName.substring(0, queueName.length() - 14);
                }
                return queueName + "[" + number + "]";
            } else {
                return name;
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            return name;
        }
    }
}
