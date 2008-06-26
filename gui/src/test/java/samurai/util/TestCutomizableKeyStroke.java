/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.util;

import junit.framework.TestCase;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class TestCutomizableKeyStroke extends TestCase {
    private CustomizableKeyStroke cutomizableKeyStroke = null;

    protected void setUp() throws Exception {
        super.setUp();
        cutomizableKeyStroke = new CustomizableKeyStroke(GUIResourceBundle.getInstance());
    }

    protected void tearDown() throws Exception {
        cutomizableKeyStroke = null;
        super.tearDown();
    }

    public void testGetKeyStroke() {
        String key = "menu.edit.copy";
        KeyStroke expectedReturn = null;
        if (samurai.util.OSDetector.isWindows()) {
            expectedReturn = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
        }
        if (samurai.util.OSDetector.isMac()) {
            expectedReturn = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_MASK);
        }
        KeyStroke actualReturn = cutomizableKeyStroke.getKeyStroke(key);
        assertEquals("return value", expectedReturn, actualReturn);
    }

}
