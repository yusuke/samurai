/*
 * Copyright 2021 Yusuke Yamamoto
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

import one.cafebabe.samurai.util.OSDetector;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitHandler;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuBar {
    Map<String, JMenuItem> menuItemMap = new HashMap<>();
    Map<String, JCheckBoxMenuItem> checkBoxMenuItemMap = new HashMap<>();
    Map<String, JMenu> menuMap = new HashMap<>();

    public JMenuItem getMenuItem(String label) {
        return menuItemMap.get(label);
    }

    public JCheckBoxMenuItem getCheckBoxMenuItem(String label) {
        return checkBoxMenuItemMap.get(label);
    }

    public JMenu getMenu(String label) {
        return menuMap.get(label);
    }

    private final JMenuBar menuBar = new JMenuBar();

    public static MenuBar newBuilder() {
        return new MenuBar();
    }

    public MenuBar addMenuIfNotMac(String label, Consumer<SwingMenu> menu) {
        if (!OSDetector.isMac()) {
            addMenu(label, menu);
        }
        return this;
    }

    public MenuBar addMenu(String label, Consumer<SwingMenu> menu) {
        SwingMenu swingMenu = new SwingMenu(label);
        menuBar.add(swingMenu.menu);
        menu.accept(swingMenu);
        return this;
    }

    public JMenuBar menuBar() {
        return menuBar;
    }

    public MenuBar addAboutHandlerIfMac(final AboutHandler aboutHandler) {
        if (OSDetector.isMac()) {
            Desktop.getDesktop().setAboutHandler(aboutHandler);
        }
        return this;
    }

    public MenuBar addPreferencesHandlerIfMac(final PreferencesHandler preferencesHandler) {
        if (OSDetector.isMac()) {
            Desktop.getDesktop().setPreferencesHandler(preferencesHandler);
        }
        return this;
    }

    public MenuBar addQuitHandlerIfMac(final QuitHandler quitHandler) {
        if (OSDetector.isMac()) {
            Desktop.getDesktop().setQuitHandler(quitHandler);
        }
        return this;
    }

    class SwingMenu {
        private final JMenu menu;

        SwingMenu(String label) {
            menu = new JMenu(label);
            menuMap.put(label, menu);
        }

        public SwingMenu addMenuItem(String label, ActionListener listener) {
            JMenuItem menuItem = new JMenuItem(label);
            menuItemMap.put(label, menuItem);
            menuItem.addActionListener(listener);
            menu.add(menuItem);
            return this;
        }

        public SwingMenu addCheckBoxMenuItem(String label, ActionListener listener) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(label);
            menuItemMap.put(label, menuItem);
            checkBoxMenuItemMap.put(label, menuItem);
            menuItem.addActionListener(listener);
            menu.add(menuItem);
            return this;
        }

        public SwingMenu addMenuItemIfNotMac(String label, ActionListener listener) {
            if (!OSDetector.isMac()) {
                return addMenuItem(label, listener);
            }
            return this;
        }

        public SwingMenu addMenu(String label, Consumer<JMenu> menuConsumer) {
            JMenu newMenu = new JMenu(label);
            menuMap.put(label, newMenu);
            menu.add(newMenu);
            menuConsumer.accept(getMenu(label));
            return this;
        }

        public SwingMenu addSeparator() {
            menu.add(new JSeparator());
            return this;
        }
    }
}
