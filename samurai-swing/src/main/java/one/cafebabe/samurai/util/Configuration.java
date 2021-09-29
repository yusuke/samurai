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

import one.cafebabe.samurai.swing.FontSizeFixer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

public final class Configuration implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private final Properties props;
    private final String fileName;
    private final String name;

    public Configuration(String name) {
        this.name = name;
        this.fileName = System.getProperty("user.home") + File.separator + "." +
                name + ".properties";
        GUIResourceBundle resources = GUIResourceBundle.getInstance("default");
        props = resources.getProperties();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            props.load(fis);
            fis.close();
        } catch (IOException ioe) {
            //
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this));

    }

    public void run() {
        for (String key : watchRectangles.keySet()) {
            storeRectangle(key, watchRectangles.get(key));
        }
        for (String key : watchLocations.keySet()) {
            storeLocation(key, watchLocations.get(key));
        }
        try {
            this.save();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public int getInt(String key) {
        String theValue = props.getProperty(key);
        int returnValue;
        try {
            returnValue = Integer.parseInt(theValue);
        } catch (NumberFormatException nfe) {
            returnValue = -1;
        }
        return returnValue;
    }

    public void setInt(String key, int value) {
        props.setProperty(key, String.valueOf(value));
    }

    public String getString(String key) {
        String theValue = props.getProperty(key);
        String returnValue;
        returnValue = Objects.requireNonNullElse(theValue, "");
        return returnValue;
    }

    public void setString(String key, String value) {
        props.setProperty(key, String.valueOf(value));
    }

    public boolean getBoolean(String key) {
        String theValue = props.getProperty(key);
        boolean returnValue;
        if (null == theValue) {
            returnValue = false;
        } else {
            returnValue = theValue.equalsIgnoreCase("true");
        }
        return returnValue;
    }

    public void setBoolean(String key, boolean value) {
        props.setProperty(key, String.valueOf(value));
    }

    public Rectangle getRectangle(String key) {
        String theValue = props.getProperty(key);
        Rectangle returnValue;
        if (null == theValue) {
            returnValue = null;
        } else {
            String[] splitted = theValue.split(",");
            double x = Double.parseDouble(splitted[0]);
            double y = Double.parseDouble(splitted[1]);
            double width = Double.parseDouble(splitted[2]);
            double height = Double.parseDouble(splitted[3]);
            returnValue = new Rectangle((int) x, (int) y, (int) width, (int) height);
        }
        return returnValue;
    }

    public Point getLocation(String key) {
        String theValue = props.getProperty(key);
        Point returnValue;
        if (null == theValue) {
            returnValue = null;
        } else {
            String[] splitted = theValue.split(",");
            double x = Double.parseDouble(splitted[0]);
            double y = Double.parseDouble(splitted[1]);
            returnValue = new Point((int) x, (int) y);
        }
        return returnValue;
    }

    public void setLocation(String key, Point point) {
        props.setProperty(key, point.getX() + "," + point.getY());

    }

    private void centerComponent(Component component) {
        Dimension frameSize = component.getSize();
        Point frameLocation = component.getLocation();
        if (10000 < frameLocation.getX() && 10000 < frameLocation.getY()) {
            //Center the window
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            component.setLocation((screenSize.width - frameSize.width) / 2,
                    (screenSize.height - frameSize.height) / 2);
        }
    }

    public void setRectangle(String key, Rectangle value) {
        props.setProperty(key,
                value.getX() + "," + value.getY() + "," + value.getWidth() + "," +
                        value.getHeight());
    }

    private final Map<String, Component> watchRectangles = new HashMap<>();

    public void applyRectangle(String key, Component component) {
        component.setBounds(getRectangle(key));
        setRectangle(key, component.getBounds());
        centerComponent(component);
        setRectangle(key, component.getBounds());
        watchRectangles.put(key, component);
    }

    private final Map<String, Component> watchLocations = new HashMap<>();

    public void applyLocation(String key, Component component) {
        component.setLocation(getLocation(key));
        setLocation(key, component.getLocation());
        centerComponent(component);
        setLocation(key, component.getLocation());
        watchLocations.put(key, component);
    }

    public void storeRectangle(String key, Component component) {
        setRectangle(key, component.getBounds());
    }

    public void storeLocation(String key, Component component) {
        setLocation(key, component.getLocation());
    }

    public void save() throws IOException {
        logger.info("Saving configuration.[{}]", this.fileName);
        props.store(new FileOutputStream(this.fileName), this.name);
    }

    public void apply(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.startsWith("config_")) {
                String property = fieldName.substring(7);
                Class<?> type = field.getType();
                try {
                    if (type.equals(boolean.class)) {
                        field.setBoolean(obj, getBoolean(property));
                    } else if (type.equals(int.class)) {
                        field.setInt(obj, getInt(property));
                    } else if (type.equals(String.class)) {
                        field.set(obj, getString(property));
                    } else if (type.equals(JCheckBox.class)) {
                        ((JCheckBox) field.get(obj)).setSelected(getBoolean(
                                property));
                    } else if (type.equals(JComboBox.class)) {
                        JComboBox comboBox = (JComboBox) field.get(obj);
                        comboBox.setSelectedItem(getString(property));
                        comboBox.setRenderer(new FontFixCellRenderer());
                    } else if (type.equals(JTextField.class)) {
                        JTextField textField = (JTextField) field.get(obj);
                        textField.setText(getString(property));
                    } else if (type.equals(JEditorPane.class)) {
                        JEditorPane jEditorPane = (JEditorPane) field.get(obj);
                        jEditorPane.setText(getString(property));
                    }else if(type.equals(JScrollBar.class)){
                        JScrollBar scrollBar = (JScrollBar) field.get(obj);
                    }else if(type.equals(JLabel.class)){
                        JLabel scrollBar = (JLabel) field.get(obj);
                    }else if(type.equals(JTabbedPane.class)){
                        JTabbedPane scrollBar = (JTabbedPane) field.get(obj);
                    }
                } catch (IllegalAccessException iae) {
                    throw new AssertionError(iae.getMessage());
                }
            }
        }
        if (obj instanceof ConfigurationListener) {
            ((ConfigurationListener) obj).onConfigurationChanged(this);
        }

    }

    static class FontFixCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            FontSizeFixer.setFont(label);
            return label;
        }
    }

    public void store(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.startsWith("config_")) {
                String property = fieldName.substring(7);
                Class type = field.getType();
                try {
                    if (type.equals(boolean.class)) {
                        setBoolean(property, field.getBoolean(obj));
                    } else if (type.equals(int.class)) {
                        setInt(property, field.getInt(obj));
                    } else if (type.equals(String.class)) {
                        setString(property, (String) field.get(obj));
                    } else if (type.equals(JCheckBox.class)) {
                        setBoolean(property,
                                ((JCheckBox) field.get(obj)).isSelected());
                    } else if (type.equals(JComboBox.class)) {
                        setString(property, (String) ((JComboBox) field.get(obj)).getSelectedItem());
                    } else if (type.equals(JTextField.class)) {
                        setString(property, ((JTextField) field.get(obj)).getText());
                    }
                } catch (IllegalAccessException iae) {
                    throw new AssertionError(iae.getMessage());
                }
            }
        }

    }

    private final List<Object> listenerList = new ArrayList<>();

    public void addTarget(Object listener) {
        listenerList.add(listener);
        apply(listener);
    }

    public void notifyChange() {
        for (Object obj : listenerList) {
            apply(obj);
            if (obj instanceof ConfigurationListener) {
                ((ConfigurationListener) obj).onConfigurationChanged(this);
            }
        }
    }

}
