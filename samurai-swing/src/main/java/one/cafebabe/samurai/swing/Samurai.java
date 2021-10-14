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
package one.cafebabe.samurai.swing;

import one.cafebabe.samurai.remotedump.ProcessUtil;
import one.cafebabe.samurai.util.GUIResourceBundle;
import one.cafebabe.samurai.util.OSDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.jvmstat.monitor.MonitorException;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class Samurai {
    private static final Logger logger = LogManager.getLogger();
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();

    public static void main(String[] args) {
        // relaunch Samurai if --add-exports / --add-opens options are not specified.
        if (args.length != 1 || !args[0].equals("nested-launch")) {
            try {
                // check access to sun.jvmstat.* is permitted.
                ProcessUtil.getVMs("localhost");
            } catch (IllegalAccessError error) {
                System.out.println("access class sun.jvmstat.monitor.HostIdentifier (in module jdk.internal.jvmstat)");
                List<String> command = new ArrayList<>();
                command.add(System.getProperty("java.home") + "/bin/java");
                command.add("-classpath");
                command.add(System.getProperty("java.class.path"));
                command.add("-Djdk.attach.allowAttachSelf=true");
                command.add("-Xmx128m");
                command.add("-Xms128m");
                command.add("--add-exports=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED");
                command.add("--add-exports=jdk.attach/sun.tools.attach=ALL-UNNAMED");
                command.add("--add-opens=java.desktop/javax.swing=ALL-UNNAMED");
                command.add("--add-opens=java.desktop/javax.swing.border=ALL-UNNAMED");
                command.add("--add-opens=java.desktop/javax.swing.colorchooser=ALL-UNNAMED");
                if (OSDetector.isMac()) {
                    command.add("--add-opens=java.desktop/com.apple.laf=ALL-UNNAMED");
                    command.add("-Dsun.java2d.metal=true");
                }
                if (OSDetector.isWindows()) {
                    command.add("--add-opens=java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED");
                }
                if (OSDetector.isLinux()) {
                    command.add("--add-exports=java.desktop/sun.awt.X11=ALL-UNNAMED");
                }
                command.add("one.cafebabe.samurai.swing.Samurai");
                command.add("nested-launch");

                System.out.println("relaunching samurai with the following command:");
                System.out.println(String.join(" ", command));
                ProcessBuilder builder = new ProcessBuilder().command(command.toArray(new String[0]));
                try {
                    Process process = builder.start();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException | MonitorException e) {
                e.printStackTrace();
            }
        }
        if (OSDetector.isMac()) {
            System.setProperty("apple.awt.application.name", resources.getMessage("Samurai"));
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useSmallTabs", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "false");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    resources.getMessage("Samurai"));
        }
        System.setProperty("jdk.attach.allowAttachSelf", "true");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                logger.warn("failed to setLookAndFeel", e);
            }
            javax.swing.JFrame frame = new MainFrame();
            frame.validate();
            frame.setVisible(true);
        });
    }
}
