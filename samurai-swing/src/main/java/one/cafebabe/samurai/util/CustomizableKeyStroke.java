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
package one.cafebabe.samurai.util;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CustomizableKeyStroke {
    private final GUIResourceBundle resources;
    private final Properties props = new Properties();
    public final String KEY_STROKE_FILE = "keystroke";

    public CustomizableKeyStroke(GUIResourceBundle resources) {
        String packageName = getCallerPackage();
        String location;
        this.resources = resources;
        if (OSDetector.isMac()) {
            location = packageName + "/" + KEY_STROKE_FILE + "_mac.properties";
        } else {
            location = packageName + "/" + KEY_STROKE_FILE +
                    "_win.properties";
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                location);
        if (null != is) {
            try {
                props.load(is);
                props.keySet().forEach(e-> {
                    String message = resources.getMessage((String) e);
                    if (message != null) {
                        props.setProperty(message,
                                props.getProperty((String) e));
                    }
                });
            } catch (IOException ioe) {
                //don't care if it exists
                //throw new AssertionError("The keystroke resource must be exist:" + location);
            }
        }
    }

    public void setKeyStroke(JMenuItem menuItem, String key) {
        String property = getProperty(key);
        if (null != property && !"".equals(property)) {
            if (property.startsWith("_")) {
                menuItem.setMnemonic(property.charAt(1));
                if (-1 == menuItem.getText().toUpperCase().indexOf(property.charAt(1))) {
                    menuItem.setText(menuItem.getText() + "(" +
                            property.charAt(1) + ")");

                }
//                if (System.getProperty("user.language").equals("ja")) {
//                    menuItem.setText(menuItem.getText() + "(" +
//                                     property.charAt(1) + ")");
//                }
            } else {
                menuItem.setAccelerator(toKeyStroke(property));
            }
        }
    }

    private static String getCallerPackage() {
        String callerClass = new Throwable().getStackTrace()[2].getClassName();
        return callerClass.substring(0,
                callerClass.lastIndexOf(".")).replaceAll(
                "\\.",
                "/");
    }

    public char getMnemonic(String key) {
        return getProperty(key).charAt(0);
    }

    public KeyStroke getKeyStroke(String key) {
        return toKeyStroke(getProperty(key));
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public static KeyStroke toKeyStroke(String keyStrokeText) {
        if (null != keyStrokeText) {
            keyStrokeText = keyStrokeText.replaceAll("command", "meta")
                    .replaceAll("cmd", "meta")
                    .replaceAll("option", "alt")
                    .replaceAll("ctl", "control")
                    .replaceAll("ctrl", "control")
                    .replaceAll("opt", "alt");
        }
        return KeyStroke.getKeyStroke(keyStrokeText);
    }

    public void apply(Object obj) {
        if (obj instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) obj;
            String key = menuItem.getText();
            if (null != resources.getMessage(key)) {
                menuItem.setText(resources.getMessage(key));
            }
            this.setKeyStroke(menuItem, key);
        }
        if (obj instanceof JFrame) {
            JMenuBar menuBar = ((JFrame) obj).getJMenuBar();
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                apply(menuBar.getMenu(i));
            }
        } else if (obj instanceof JMenu) {
            JMenu menu = (JMenu) obj;
            for (Component menuItem : menu.getMenuComponents()) {
                apply(menuItem);
            }
        } else if (obj instanceof JPopupMenu) {
            JPopupMenu menu = (JPopupMenu) obj;
            for (Component menuItem : menu.getComponents()) {
                apply(menuItem);
            }
        }
    }

    public boolean isPressed(String key, KeyEvent event) {
        return getKeyStroke(key).equals(KeyStroke.getKeyStrokeForEvent(event));
    }

    public boolean isPressed(String key1, String key2, KeyEvent event) {
        return isPressed(key1, event) || isPressed(key2, event);
    }
}
