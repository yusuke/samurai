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

package one.cafebabe.samurai.remotedump;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadDumpUtil {
    public static @Nullable
    String getGCLogPath(long pid) throws AttachNotSupportedException, IOException {
        return extractGClogFileName(new String(invoke(pid, "VM.log list")));
    }

    public static void setGCLogPath(long pid, @NotNull String path) throws IOException, AttachNotSupportedException {
        String format = String.format("output=\"file=%s\" what=\"gc=info\"", path);
        invoke(pid, "VM.log " + format);
    }


    public static byte[] getThreadDump(int pid) throws AttachNotSupportedException, IOException {
        return invoke(pid, null);
    }

    public static byte[] invoke(long pid, @Nullable String command) throws AttachNotSupportedException, IOException {
        HotSpotVirtualMachine virtualMachine = null;
        try {
            virtualMachine = (HotSpotVirtualMachine) VirtualMachine.attach(String.valueOf(pid));
            try (InputStream in = command == null ? virtualMachine.remoteDataDump() :
                    virtualMachine.executeCommand("jcmd", command)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int count;
                byte[] buf = new byte[256];
                while ((count = in.read(buf)) != -1) {
                    baos.write(buf, 0, count);
                }
                return baos.toByteArray();
            }
        } finally {
            if (virtualMachine != null) {
                try {
                    virtualMachine.detach();
                } catch (IOException ignore) {
                }
            }
        }
    }


    public static String getThreadDumpAsString(int pid) throws AttachNotSupportedException, IOException {
        return new String(getThreadDump(pid), StandardCharsets.UTF_8);
    }

    public static @Nullable
    String extractGClogFileName(String out) {
        String fallback = null;
        for (String line : out.split("\n")) {
            if (line.contains("gc=")) {
                String fileName = extractFileName(line);
                if (fileName != null) {
                    return fileName;
                }
            }
            if (line.contains("all=")) {
                fallback = extractFileName(line);
            }
        }
        return fallback;
    }

    private static @Nullable
    String extractFileName(String line) {
        Pattern filePattern = Pattern.compile("file=([^ ]+) ");
        Matcher matcher = filePattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}