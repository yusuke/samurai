package samurai.swing;

import samurai.util.GUIResourceBundle;
import samurai.util.OSDetector;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class Samurai {
    private static GUIResourceBundle resources = GUIResourceBundle.getInstance();

    public static void main(String[] args) throws Exception {
        if (OSDetector.isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
//    System.setProperty("apple.awt.brushMetalLook", "true");
            System.setProperty("com.apple.macos.useSmallTabs", "true");
            System.setProperty("apple.awt.textantialiasing", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "false");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    resources.getMessage("Samurai"));
        }
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.JFrame frame = new MainFrame();
            frame.validate();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
