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

import javax.swing.JCheckBox;
import java.awt.Rectangle;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestConfiguration {
    private Configuration configuration = null;


    @BeforeEach
    void setUp() {
        /*@todo verify the constructors*/
        configuration = new Configuration("samuraitest");
    }

    @AfterEach
    void tearDown() {
        configuration = null;
    }

    @Test
    void testGetInt() throws IOException {
        int expectedReturn = 10;
        int actualReturn = configuration.getInt("intvalue");
        assertEquals(expectedReturn, actualReturn, "return value");
        configuration.save();

    }

    @Test
    void testGetRectangle() throws IOException {
        Rectangle expectedReturn = new Rectangle(1, 2, 3, 4);
        Rectangle actualReturn = configuration.getRectangle("rectangle");
        assertEquals(expectedReturn, actualReturn, "return value");
        configuration.save();

    }

    public final boolean config_ignoreCase = false;
    public final int config_intvalue = 0;
    public final String config_string = null;
    public final JCheckBox config_checkbox = new JCheckBox();

    @Test
    void testApply() {
        config_checkbox.setSelected(false);
        configuration.apply(this);
        assertTrue(config_ignoreCase, "return value");
        assertEquals(10, config_intvalue, "return value");
        assertEquals("string", config_string, "return value");
        assertTrue(config_checkbox.isSelected(), "return value");
    }

}
