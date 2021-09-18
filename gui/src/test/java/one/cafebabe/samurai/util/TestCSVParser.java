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
package one.cafebabe.samurai.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import one.cafebabe.samurai.gc.LineGraphRenderer;
import one.cafebabe.samurai.gc.LineGraph;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class TestCSVParser implements LineGraph, LineGraphRenderer {


    @BeforeEach
    void setUp() {
        columnCount = 0;
        count = 0;
    }


    private int count = 0;

    double[] expected;
    private int columnCount = 0;

    public LineGraph addLineGraph(String line, String[] labels) {
        this.setLabels(labels);
        columnCount += labels.length;
        return this;
    }

    private int index = 0;

    public void addValues(double[] values) {
        for (int i = 0; i < values.length; i++) {
            assertEquals(expected[i + index], values[i]);
        }
        index += values.length;
        if (index == columnCount) {
            index = 0;
            count++;
        }
    }

    public void addValues(double x, double[] values) {
        addValues(values);
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {
    }

    public void setYMax(int index, double max) {

    }

    @Test
    void testCSVParser() {
        CSVParser parser = new CSVParser();
        parser.parse("column1,column2,column3", this);
        expected = new double[]{1d, 2d, 3d};
        parser.parse("1,2,3", this);
        expected = new double[]{3d, 3d, 1d};
        parser.parse("3,3,1", this);
        expected = new double[]{7d, 5d, 0d};
        parser.parse("7,5", this);
        expected = new double[]{9d, 6d, 79d};
        parser.parse("9,6,79,6,79,6,7", this);
        expected = new double[]{11d, 7d, 9d};
        parser.parse("11,7,9", this);
        assertEquals(5, count);
    }
}
