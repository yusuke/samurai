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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*package*/ class SpringBootActuatorThreadDump extends ThreadDump {

    private static final long serialVersionUID = -3467430527681750601L;
    private final String id;

    private static final Pattern springActuatorThreadIdPattern = Pattern.compile("^.* - Thread (t@[0-9]+)$");

    /*package*/ SpringBootActuatorThreadDump(String header) {
        super(header);
        Matcher matcher = springActuatorThreadIdPattern.matcher(header);
        //noinspection ResultOfMethodCallIgnored
        matcher.find();
        this.id = matcher.group(1);
    }


    /*package*/ void addStackLine(String line) {
        addStackLine(new SunStackLine(line));
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        StringBuilder toStringed = new StringBuilder(128);
        toStringed.append(getHeader());
        for (int i = 0; i < getStackLines().size(); i++) {
            toStringed.append('\n').append(getStackLines().get(i));
        }
        return toStringed.toString();
    }


    boolean stateAnalyzed = false;

    void ensureAnalyzed() {
        if (!stateAnalyzed) {
            if (size() > 0) {
                String line = getLine(0).getLine().trim();
                IS_BLOCKED = line.equals("java.lang.Thread.State: BLOCKED");
                IS_IDLE = line.equals("java.lang.Thread.State: TIMED_WAITING");
            }
            stateAnalyzed = true;
        }
    }

    @Override
    public boolean isBlocked() {
        ensureAnalyzed();
        return IS_BLOCKED;
    }



    @Override
    public boolean isIdle() {
        ensureAnalyzed();
        return IS_IDLE;
    }

}
