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

public class BEAFullThreadDump extends FullThreadDump {
    private static final long serialVersionUID = -1445294393744936922L;

    /*package*/ BEAFullThreadDump(String header) {
        super(header);
    }

    /*package*/ boolean isThreadHeader(String line) {
        return (line.startsWith("\"") || line.startsWith("Thread-")) && line.contains("prio");
    }

    /*package*/ boolean isThreadFooter(String line) {
        return "".equals(line) || line.contains("}");
    }

    /*package*/ boolean isThreadDumpContinuing(String line) {
        return !line.startsWith("======================================");
    }

}
