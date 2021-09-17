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
package samurai.util;


import org.junit.jupiter.api.*;

import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestCutomizableKeyStroke {
    @Test
    void testGetKeyStroke() {
        CustomizableKeyStroke cutomizableKeyStroke = new CustomizableKeyStroke(GUIResourceBundle.getInstance());
        String key = "menu.edit.copy";
        KeyStroke expectedReturn = null;
        if (samurai.util.OSDetector.isWindows()) {
            expectedReturn = KeyStroke.getKeyStroke(KeyEvent.VK_C, CTRL_DOWN_MASK);
        }
        if (samurai.util.OSDetector.isMac()) {
            expectedReturn = KeyStroke.getKeyStroke(KeyEvent.VK_C, META_DOWN_MASK);
        }
        
        KeyStroke actualReturn = cutomizableKeyStroke.getKeyStroke(key);
        assertEquals(expectedReturn, actualReturn, "return value");
    }

}
