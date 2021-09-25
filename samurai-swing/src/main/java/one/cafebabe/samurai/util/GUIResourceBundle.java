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

import one.cafebabe.samurai.swing.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.*;

public class GUIResourceBundle extends ResourceBundle {
    private static final Map<String, Map<String, GUIResourceBundle>> resourceses = new HashMap<>();
    private static final String DEFAULT_RESOURCE_NAME = "messages";

    private final Properties props = new Properties();

    public static synchronized GUIResourceBundle getInstance() {
        Map<String, GUIResourceBundle> defaultResource = resourceses.computeIfAbsent(DEFAULT_RESOURCE_NAME, k -> new HashMap<>());
        String packageName = getCallerPackage();
        GUIResourceBundle theResources = defaultResource.get(
                packageName);
        if (null == theResources) {
            defaultResource.put(packageName,
                    theResources = new GUIResourceBundle(
                            DEFAULT_RESOURCE_NAME,
                            packageName));
        }
        return theResources;
    }

    /*package*/ Properties getProperties() {
        return this.props;
    }

    public static synchronized GUIResourceBundle getInstance(String resourceName) {
        Map<String, GUIResourceBundle> defaultResource = resourceses.computeIfAbsent(resourceName, k -> new HashMap<>());
        String packageName = getCallerPackage();
        GUIResourceBundle theResources = defaultResource.get(
                packageName);
        if (null == theResources) {
            defaultResource.put(packageName,
                    theResources = new GUIResourceBundle(resourceName,
                            packageName));
        }
        return theResources;
    }

    private static String getCallerPackage() {
        String callerClass = new Throwable().getStackTrace()[2].getClassName();
        return callerClass.substring(0,
                callerClass.lastIndexOf(".")).replaceAll("\\.", "/");
    }

    /*package*/ GUIResourceBundle(String resourceName, String packageName) {
        String location = packageName + "/" + resourceName + "_" +
                System.getProperty("user.language") + ".properties";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                location);
        if (null == is) {
            //try the default resource
            location = packageName + "/" + resourceName + ".properties";
            is = this.getClass().getClassLoader().getResourceAsStream(location);
        }
        try {
            props.load(is);
        } catch (NullPointerException npe) {
            throw new MissingResourceException("Message resource not foundn:" + location, packageName, resourceName);
        } catch (IOException ioe) {
            throw new MissingResourceException("Message resource not found:" + location, packageName, resourceName);
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    public String getMessage(String key) {
        return props.getProperty(key);
    }


    public String getMessage(String key, Object[] arguments) {
        MessageFormat formatter = new MessageFormat(getMessage(key));
        StringBuffer message = new StringBuffer();
        formatter.format(arguments, message, new FieldPosition(0));
        return message.toString();
    }

    public String getMessage(String key, String arg1) {
        return getMessage(key, new Object[]{arg1});
    }

    public String getMessage(String key, String arg1, String arg2) {
        return getMessage(key, new Object[]{arg1, arg2});
    }

    public String getMessage(String key, String arg1, String arg2, String arg3) {
        return getMessage(key, new Object[]{arg1, arg2, arg3});
    }

    public String getMessage(String key, String arg1, String arg2, String arg3,
                             String arg4) {
        return getMessage(key, new Object[]{arg1, arg2, arg3, arg4});
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("specify directory.");
            System.exit(-1);
        }

        File specifiedDir;
        boolean reverse;
        if ("-reverse".equals(args[0])) {
            reverse = true;
            specifiedDir = new File(args[1]);
        } else {
            reverse = false;
            specifiedDir = new File(args[0]);
        }
        if (!specifiedDir.exists()) {
            System.out.println("dir doesn't exist");
            System.exit(-1);
        }
        File[] javaFiles = specifiedDir.listFiles(file -> file.getName().endsWith(".java")
        );
        for (File javaFile : javaFiles) {
            System.out.println("processing:" + javaFile);
            BufferedReader br;
            BufferedWriter bw;
            File modified = new File(javaFile + ".modified");
            br = new BufferedReader(new FileReader(javaFile));
            bw = new BufferedWriter(new FileWriter(modified));
            String line;
            while (null != (line = br.readLine())) {
                int index;
                if (reverse) {
                    if (line.contains("(\"*")) {
//          line = line.replaceAll("(\\\"*","resources.getMessage(\\\"");
//            line = line.replaceFirst("\"\\*", "resources.getMessage(\"");
//            line = line.replaceFirst("\"\\)", "\"))");
//
                        line = line.replaceFirst("\"\\*", "resources.getMessage(\"");
                        line = line.replaceFirst("\\*\"", "\")");
                    }
                } else {
                    if (-1 != (index = line.indexOf("resources.getMessage("))) {
                        int commaIndex = line.indexOf(",", index);
                        int parenthesIndex = line.indexOf(")", index);
                        if (-1 == commaIndex) {
//             line = line.replaceFirst("resources.getMessage\\(\"","\"*");
//              line = line.replaceFirst("\"\\)","\"");

                            line = line.replaceFirst("resources.getMessage\\(\"", "\"*");
                            line = line.replaceFirst("\"\\)", "*\"");

//              line = line.replaceAll("resources.getMessage(\"\"", "(\"*");
                        } else if (commaIndex > parenthesIndex && -1 != parenthesIndex) {
//              line = line.replaceFirst("resources.getMessage\\(\"","\"*");
//              line = line.replaceFirst("\"\\)","\"");
                            line = line.replaceFirst("resources.getMessage\\(\"", "\"*");
                            line = line.replaceFirst("\"\\)", "*\"");

                        }
                    }
                }
                bw.write(line);
                bw.newLine();
            }
            br.close();
            bw.close();
            File renameTo = new File(javaFile + ".original");
            //noinspection ResultOfMethodCallIgnored
            renameTo.delete();
            //noinspection ResultOfMethodCallIgnored
            javaFile.renameTo(renameTo);
            //noinspection ResultOfMethodCallIgnored
            modified.renameTo(javaFile);
        }

    }

    /*
       import javax.swing.JTextArea;
       import javax.swing.JLabel;
       import javax.swing.JButton;
       import javax.swing.JCheckBox;
       import javax.swing.JComponent;
    */

    ArrayList configured = new ArrayList();
    /**
     * Inject localized message resources.<br>
     *
     * @param obj Object to be injected.
     */
    public void inject(Object obj) {
        if (configured.contains(obj)) {
            return;
        }
        configured.add(obj);
        if (obj instanceof javax.swing.JDialog) {
            JDialog dialog = (JDialog) obj;
            dialog.setTitle(getLocalizedMessage(dialog.getTitle()));
        } else if (obj instanceof javax.swing.JFrame) {
            JFrame frame = (JFrame) obj;
            frame.setTitle(getLocalizedMessage(frame.getTitle()));
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            try {
                field.setAccessible(true);
                Object theObject = field.get(obj);
                if (theObject instanceof JFrame) {
                    inject(theObject);
                } else if (theObject instanceof JDialog) {
                    inject(theObject);
                } else if (theObject instanceof JComponent) {
                    JComponent component = (JComponent) theObject;
                    for (Component innerComponent : component.getComponents()) {
                        inject(innerComponent);
                    }
                    if (null != component.getToolTipText() && !"".equals(component.getToolTipText())) {
                        component.setToolTipText(getLocalizedMessage(component.getToolTipText()));
                    }
                    if (type.equals(JLabel.class)) {
                        JLabel label = (JLabel) theObject;
                        label.setText(getLocalizedMessage(label.getText()));
                    } else if (type.equals(JButton.class)) {
                        JButton button = (JButton) theObject;
                        button.setText(getLocalizedMessage(button.getText()));
                    } else if (type.equals(JCheckBox.class)) {
                        JCheckBox checkBox = (JCheckBox) theObject;
                        checkBox.setText(getLocalizedMessage(checkBox.getText()));
                    } else if (type.equals(JTextArea.class)) {
                        JTextArea textArea = (JTextArea) theObject;
                        textArea.setText(getLocalizedMessage(textArea.getText()));
                    } else if (type.equals(JEditorPane.class)) {
                        JEditorPane editorPane = (JEditorPane) theObject;
                        editorPane.setText(getLocalizedMessage(editorPane.getText()));
                    } else if (type.equals(JTextPane.class)) {
                        JTextPane textPane = (JTextPane) theObject;
                        textPane.setText(getLocalizedMessage(textPane.getText()));
                    } else if (type.equals(JTabbedPane.class)) {
                        JTabbedPane tabbedPane = (JTabbedPane) theObject;
                        for (int j = 0; j < tabbedPane.getTabCount(); j++) {
                            tabbedPane.setTitleAt(j, getLocalizedMessage(tabbedPane.getTitleAt(j)));
                        }
                    } else if (theObject instanceof JPanel) {
                        inject(theObject);
                    }
                }
            } catch (IllegalAccessException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    String getLocalizedMessage(String key) {
        String[] split = key.split("\\*");
        if (split.length == 2 || split.length == 3) {
            return getMessage(split[1]);
        } else {
            return key;
        }
    }

    protected Object handleGetObject(String s) {
        return getMessage(s);
    }

    public Enumeration<String> getKeys() {
        return (Enumeration<String>) props.propertyNames();
    }
}

