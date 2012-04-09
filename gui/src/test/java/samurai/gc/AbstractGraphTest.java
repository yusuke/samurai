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
package samurai.gc;

import junit.framework.TestCase;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractGraphTest extends TestCase implements LineGraph,LineGraphRenderer{
    protected int count = 0;

    List<double[]> expected = new ArrayList<double[]>();
    protected List<Double> expectedMax = new ArrayList<Double>();

    public LineGraph addLineGraph(String line, String labels[]) {
        this.setLabels(labels);
        return this;
    }

    public void addValues(double[] values) {
        double[] ex = expected.remove(0);
        for (int i = 0; i < ex.length; i++) {
            assertEquals(ex[i], values[i]);
        }
        count++;
    }

    public void addValues(double x,double[] values) {
        addValues(values);
    }

    public void setColorAt(int index, Color color) {

    }

    public void setLabels(String[] labels) {
    }

    public void setYMax(int index, double max) {
        assertEquals(expectedMax.remove(0).doubleValue(), max);
    }

}
