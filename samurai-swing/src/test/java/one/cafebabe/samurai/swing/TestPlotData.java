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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class TestPlotData implements GraphCanvas {
    PlotData plotData;

    @BeforeEach
    void setUp() {
        plotData = new PlotData();
    }


    @Test
    void testPlotData() {
        plotData.setLabels(new String[]{"red", "green", "blue"});
        for (int i = 0; i < 20000; i++) {
            plotData.addValues(new double[]{i, i + 1, i + 2});
        }
        for (int i = 0; i < 20000; i++) {
            assertEquals(i, plotData.getValueAt(0, i));
            assertEquals((i + 1), plotData.getValueAt(1, i));
            assertEquals((i + 2), plotData.getValueAt(2, i));
        }
    }

    @Test
    void testDrawGraph() {
        plotData.setLabels(new String[]{"red", "green", "blue"});
        for (int i = 0; i < 20; i++) {
            plotData.addValues(new double[]{i, i + 1, i + 2});
        }
        plotData.drawGraph(this, 0, 0, 100, 100, 0);
        for (int i = 0; i < 20000; i++) {
            plotData.addValues(new double[]{i, i + 1, i + 2});
        }
        plotData.drawGraph(this, 0, 0, 100, 100, 200);
    }

    @Disabled
    @Test
    void testDrawGraph1() {
        plotData.setLabels(new String[]{"red"});
        plotData.setMaxAt(0, 100);
        plotData.setColorAt(0, Color.YELLOW);
        setExpectedColor(Color.YELLOW);
        addValues(1, 33);
        addValues(2, 52);
        addValues(3, 87);
        assertLines = true;
        plotData.drawGraph(this, 0, 0, 100, 100, 0);
    }

    int lastY = -1;
    int x = 0;

    private void addValues(int x, double y) {
        plotData.addValues(x, new double[]{y});
        if (lastY != -1) {
            expected.add(new int[]{this.x, 100 - lastY, this.x + 1, 100 - (int) y});
            this.x++;
        }
        lastY = (int) y;
    }

    @Test
    void testDrawGraph2() {
        PlotData plotData = new PlotData();
        plotData.setLabels(new String[]{"red"});
        plotData.setMaxAt(0, 100);
        plotData.addValues(1, new double[]{33});
        plotData.addValues(2, new double[]{52});
        plotData.addValues(10, new double[]{87});
        assertLines = true;
        expected.add(new int[]{90, 33, 92, 52});
        expected.add(new int[]{91, 52, 99, 87});
        plotData.drawGraph(this, 0, 0, 100, 100, 0);
    }

    boolean lineDrawingStarted = false;
    private Color expectedColor = null;

    private void setExpectedColor(Color color) {
        expectedColor = color;
    }

    final List<int[]> expected = new ArrayList<>();
    boolean assertLines = false;
    int count = 0;

    /* implementations for GraphCanvas */
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (lineDrawingStarted) {
            System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
            int[] ex = expected.remove(0);
            System.out.println(ex[0] + "," + ex[1] + "," + ex[2] + "," + ex[3]);
            for (int i = 0; i < ex.length; i++) {
                assertEquals(ex[0], x1);
                assertEquals(ex[1], y1);
                assertEquals(ex[2], x2);
                assertEquals(ex[3], y2);
            }
        }
        count++;
    }

    public void fillRect(int x1, int y1, int x2, int y2) {

    }

    public void setColor(Color color) {
        lineDrawingStarted = color.equals(expectedColor);
    }

    public void drawString(String str, int x, int y) {

    }

    public int getFontHeight() {
        return 12;
    }

    public int getStringWidth(String str) {
        return str.length() * 12;
    }

}
