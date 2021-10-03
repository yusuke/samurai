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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VM {
    public final int pid;
    public final String version;
    public final String fqcn;
    public final String fullCommandLine;
    private final Map<String, String> map;

    public String toLabel() {
        return String.format("%s (%s %s / pid %s)", getHumanFriendlyFqcn(), map.get("java.property.java.vm.vendor"),map.get("java.property.java.vm.version"), pid);
    }

    public String getHumanFriendlyFqcn() {
        if (fqcn.length() != 0) {
            return fqcn;
        }
        return getAppName(map.get("java.property.java.class.path"));
    }

    private String getAppName(String line) {
        Pattern appPattern = Pattern.compile("/([a-zA-Z. 0-9]+\\.app)/");
        Matcher matcher = appPattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public VM(int pid, Map<String, String> map) {
        this.pid = pid;
        this.map = map;
        this.version = map.get("java.property.java.version");
        String fullCommandLine = map.get("sun.rt.javaCommand");
        this.fullCommandLine = fullCommandLine.replaceFirst("^com\\.intellij\\.rt\\.execution\\.application\\.AppMain ", "");
        this.fqcn = fullCommandLine.replaceFirst(" .*$", "");
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
