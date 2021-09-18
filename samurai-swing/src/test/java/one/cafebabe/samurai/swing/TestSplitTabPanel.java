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

package one.cafebabe.samurai.swing;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.swing.JTextArea;
import java.awt.Dimension;

@Execution(ExecutionMode.CONCURRENT)
class TestSplitTabPanel {

    @Test
    void testIt() {
        SplitTabPanelTest test = new SplitTabPanelTest();
        test.setSize(new Dimension(400, 400));
        test.setVisible(true);
        test.splitTabPanel.setOrientation(TileTabPanel.TILE_HORIZONTAL);
        JTextArea ta0 = new JTextArea("00");
        JTextArea ta1 = new JTextArea("11e");
        JTextArea ta2 = new JTextArea("22");
        test.splitTabPanel.addComponent("00", ta0);
        test.splitTabPanel.addComponent("1", new JTextArea("hoge"));
        test.splitTabPanel.addComponent("01", ta1);
        test.splitTabPanel.addComponent("2", new JTextArea("foo"));
        test.splitTabPanel.addComponent("3", new JTextArea("foga"));
        test.splitTabPanel.addComponent("4", new JTextArea("uga"));
        test.splitTabPanel.addComponent("02", ta2);
        test.splitTabPanel.setSelectedIndex(3);
        test.splitTabPanel.removeComponent(ta0);
        test.splitTabPanel.removeComponent(ta1);
        test.splitTabPanel.removeComponent(ta2);
//        assertEquals(4, test.splitTabPanel.getComponentSize());

    }
}
