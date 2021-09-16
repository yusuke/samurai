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

import java.util.regex.Pattern;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class SunFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = 5278627297157708511L;

    /*package*/ SunFullThreadDump(String header) {
        super(header);
    }
    
    boolean isSpringBootActuator = false;

    int blankLineCount = 0;

    /*package*/ boolean isThreadHeader(String line) {
        boolean startsWithDoubleQuote = line.startsWith("\"");
        boolean containsPrio = line.contains("prio");
        boolean springBootActuator = line.contains(" - Thread t@");

        if (startsWithDoubleQuote && (
                containsPrio // ordinary Sun thread dump 
                        || springBootActuator // Spring Boot Actuator thread dump
        )) {
            blankLineCount = 0;
            isSpringBootActuator = springBootActuator;
            return true;
        }
        return false;
    }

    /*package*/ boolean isThreadFooter(String line) {
        if ("".equals(line)) {
            blankLineCount++;
        }
        if (!isSpringBootActuator) {
            return blankLineCount == 1;
        } else {
            return blankLineCount == 2;
        }
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return true;
    }
}
