package one.cafebabe.samurai.swing;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class FontSizeFixer {
    static ArrayList<Object> configured = new ArrayList<>();

    public static void fixFontSizes(Object obj) {
        if (configured.contains(obj)) {
            return;
        }
        configured.add(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object theObject = field.get(obj);
                if (theObject instanceof JFrame) {
                    fixFontSizes(theObject);
                } else if (theObject instanceof JDialog) {
                    fixFontSizes(theObject);
                } else if (theObject instanceof JComponent) {
                    JComponent component = (JComponent) theObject;
                    setFont(component);
                    for (Component innerComponent : component.getComponents()) {
                        fixFontSizes(innerComponent);
                    }
                    if (theObject instanceof JMenu) {
                        JMenu menu = (JMenu) theObject;
                        for (Component menuComponent : menu.getMenuComponents()) {
                            fixFontSizes(menuComponent);
                        }
                    }
                }
                if (theObject instanceof JPanel) {
                    fixFontSizes(theObject);
                }
            } catch (IllegalAccessException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    public static void setFont(JComponent component) {
        Font font = component.getFont();
        component.setFont(new Font(font.getFontName(), font.getStyle(), 12));
    }
}
