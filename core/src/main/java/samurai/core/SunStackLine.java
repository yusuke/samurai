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

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class SunStackLine extends StackLine {
    private static final long serialVersionUID = 2404952046137420766L;
    private final boolean IS_WAITING_ON;
    private final String TARGET;

    /*package*/ SunStackLine(String line) {
        super(line);
        IS_WAITING_ON = getLine().contains("waiting on");
        if (isLine() || !getLine().contains("<")) {
            TARGET = "n/a";
        } else {
            TARGET = getLine().substring(getLine().indexOf("<"));
        }
    }

    public boolean isWaitingOn() {
        return IS_WAITING_ON;
    }

    public String getTarget() {
        return TARGET;
    }
}
