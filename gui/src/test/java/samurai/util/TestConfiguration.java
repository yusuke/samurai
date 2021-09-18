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
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.swing.JCheckBox;
import java.awt.Rectangle;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
class TestConfiguration {


    @Test
    void testGetInt() throws IOException {
        Configuration configuration = new Configuration("samuraitest");
        int expectedReturn = 10;
        int actualReturn = configuration.getInt("intvalue");
        assertEquals(expectedReturn, actualReturn, "return value");
        configuration.save();

    }

    @Test
    void testGetRectangle() throws IOException {
        Configuration configuration = new Configuration("samuraitest");
        Rectangle expectedReturn = new Rectangle(1, 2, 3, 4);
        Rectangle actualReturn = configuration.getRectangle("rectangle");
        assertEquals(expectedReturn, actualReturn, "return value");
        configuration.save();

    }

    static class ObjectTobeConfigured {
        public boolean config_ignoreCase = false;
        public int config_intvalue = 0;
        public String config_string = null;
        public JCheckBox config_checkbox = new JCheckBox();
    }

    @Test
    void testApply() {
        ObjectTobeConfigured objectTobeConfigured = new ObjectTobeConfigured();
        Configuration configuration = new Configuration("samuraitest");
        objectTobeConfigured.config_checkbox.setSelected(false);
        configuration.apply(objectTobeConfigured);
        assertTrue(objectTobeConfigured.config_ignoreCase, "return value");
        assertEquals(10, objectTobeConfigured.config_intvalue, "return value");
        assertEquals("string", objectTobeConfigured.config_string, "return value");
        assertTrue(objectTobeConfigured.config_checkbox.isSelected(), "return value");
    }

}
