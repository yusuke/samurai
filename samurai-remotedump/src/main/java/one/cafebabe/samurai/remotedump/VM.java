/*
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
package one.cafebabe.samurai.remotedump;

import java.util.Map;

public class VM {
    private final int pid;
    public final String version;
    private final String fqcn;
    private final String fullCommandLine;

    public int getPid() {
        return pid;
    }

    public String getFqcn() {
        return fqcn;
    }

    public String getFullCommandLine() {
        return fullCommandLine;
    }

    public VM(int pid, Map<String, String> map) {
        this.pid = pid;
        this.version = map.get("java.property.java.version");
        String fullCommandLine = map.get("sun.rt.javaCommand");
        this.fullCommandLine = fullCommandLine.replaceFirst("^com\\.intellij\\.rt\\.execution\\.application\\.AppMain ", "");
        this.fqcn = fullCommandLine.replaceFirst(" .*$", "");
    }

    static String toFQCN(String javaCommand) {
        return javaCommand.replaceFirst(" .*$", "");
    }

    public boolean isVersionGraterThan(int version) {
        return version < toIntVersion(this.version);
    }

    static int toIntVersion(String stringVersion) {
        String[] split = stringVersion.split("\\.");
        try {
            int firstNumber = Integer.parseInt(split[0]);
            if (firstNumber != 1) {
                return firstNumber;
            }
            return Integer.parseInt(split[1]);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }
}
