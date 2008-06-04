package samurai.util;


import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
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

//  public static boolean isOther() {
//    //maybe Solaris, HP-UX, FreeBSD...
//    return!isWin() && !isLinux() && !isMac();
//  }

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
            keyStrokeText = keyStrokeText.replaceAll("command", "meta");
            keyStrokeText = keyStrokeText.replaceAll("cmd", "meta");
            keyStrokeText = keyStrokeText.replaceAll("option", "alt");
            keyStrokeText = keyStrokeText.replaceAll("ctl", "control");
            keyStrokeText = keyStrokeText.replaceAll("ctrl", "control");
            keyStrokeText = keyStrokeText.replaceAll("opt", "alt");
        }
        return KeyStroke.getKeyStroke(keyStrokeText);
    }

    public void apply(Object obj) {
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            Class type = fields[i].getType();
            try {
                Object theObject = fields[i].get(obj);
                if (theObject instanceof JMenuItem) {
                    JMenuItem menuItem = (JMenuItem) theObject;
                    String key = menuItem.getText();
                    menuItem.setText(resources.getMessage(key));
                    this.setKeyStroke(menuItem, key);
                }
            } catch (IllegalAccessException iae) {
//        throw new AssertionError(iae.getMessage());
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
