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

import junit.framework.TestCase;
import junit.textui.TestRunner;

import javax.swing.JCheckBox;
import java.awt.Rectangle;

public class TestConfiguration extends TestCase {
    private Configuration configuration = null;

    public static void main(String[] args) {
        TestRunner.run(TestConfiguration.class);
    }

    public TestConfiguration(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        /**@todo verify the constructors*/
        configuration = new Configuration("samuraitest");
    }

    protected void tearDown() throws Exception {
        configuration = null;
        super.tearDown();
    }

    public void testGetInt() throws Exception {
        int expectedReturn = 10;
        int actualReturn = configuration.getInt("intvalue");
        assertEquals("return value", expectedReturn, actualReturn);
        configuration.save();

    }

    public void testGetRectangle() throws Exception {
        Rectangle expectedReturn = new Rectangle(1, 2, 3, 4);
        Rectangle actualReturn = configuration.getRectangle("rectangle");
        assertEquals("return value", expectedReturn, actualReturn);
        configuration.save();

    }

    public boolean config_ignoreCase = false;
    public int config_intvalue = 0;
    public String config_string = null;
    public JCheckBox config_checkbox = new JCheckBox();

    public void testApply() {
        config_checkbox.setSelected(false);
        configuration.apply(this);
        assertEquals("return value", true, config_ignoreCase);
        assertEquals("return value", 10, config_intvalue);
        assertEquals("return value", "string", config_string);
        assertEquals("return value", true, config_checkbox.isSelected());
    }

}
