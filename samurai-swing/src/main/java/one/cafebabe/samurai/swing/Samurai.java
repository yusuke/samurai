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

import one.cafebabe.samurai.util.GUIResourceBundle;
import one.cafebabe.samurai.util.OSDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;


public class Samurai {
    private static final Logger logger = LogManager.getLogger();
    private static final GUIResourceBundle resources = GUIResourceBundle.getInstance();

    public static void main(String[] args) {
        if (OSDetector.isMac()) {
            System.setProperty("apple.awt.application.name", resources.getMessage("Samurai"));
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useSmallTabs", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "false");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    resources.getMessage("Samurai"));
        }
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
