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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.*;

public class GUIResourceBundle extends ResourceBundle {
    private static final Logger logger = LogManager.getLogger();
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
            logger.warn("specify directory.");
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
            logger.warn("dir doesn't exist");
            System.exit(-1);
        }
        File[] javaFiles = specifiedDir.listFiles(file -> file.getName().endsWith(".java")
        );
        for (File javaFile : javaFiles) {
            logger.info("processing:{}", javaFile);
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
                        line = line.replaceFirst("\"\\*", "resources.getMessage(\"");
                        line = line.replaceFirst("\\*\"", "\")");
                    }
                } else {
                    if (-1 != (index = line.indexOf("resources.getMessage("))) {
                        int commaIndex = line.indexOf(",", index);
                        int parenthesIndex = line.indexOf(")", index);
                        if (-1 == commaIndex) {

                            line = line.replaceFirst("resources.getMessage\\(\"", "\"*");
                            line = line.replaceFirst("\"\\)", "*\"");

                        } else if (commaIndex > parenthesIndex && -1 != parenthesIndex) {
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

