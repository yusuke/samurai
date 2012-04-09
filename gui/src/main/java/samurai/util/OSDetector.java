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
package samurai.util;

public final class OSDetector {
    public final static boolean windows = -1 != System.getProperty("os.name").toLowerCase().indexOf("windows");

    public final static boolean linux = -1 != System.getProperty("os.name").toLowerCase().indexOf("linux");

    public final static boolean mac = -1 != System.getProperty("os.name").toLowerCase().indexOf("mac");

    public static boolean isMac() {
        return mac;
    }

    public static boolean isLinux() {
        return linux;
    }

    public static boolean isWindows() {
        return windows;
    }


}
