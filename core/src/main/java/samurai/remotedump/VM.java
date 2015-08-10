/*
 * *
 *  Copyright 2015 Yusuke Yamamoto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  Distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package samurai.remotedump;

import lombok.Data;

@Data
public class VM {
    private int pid;
    private String fqcn = "";
    private String fullCommandLine = "";

    public VM(int pid, String fullCommandLine) {
        this.pid = pid;
        if (fullCommandLine != null) {
            this.fullCommandLine = fullCommandLine;
            fullCommandLine = fullCommandLine.replaceFirst("^com\\.intellij\\.rt\\.execution\\.application\\.AppMain ", "");
            this.fqcn = fullCommandLine.replaceFirst(" .*$", "");
        }
    }
}
