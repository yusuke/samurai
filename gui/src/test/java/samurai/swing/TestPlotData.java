package samurai.swing;

import junit.framework.TestCase;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yusukey
 * Date: Jun 5, 2008
 * Time: 11:15:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPlotData extends TestCase implements GraphCanvas {
    PlotData plotData;

    protected void setUp() throws Exception {
        super.setUp();
        plotData = new PlotData();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPlotData() throws Exception {
        plotData.setLabels(new String[]{"red", "green", "blue"});
        for (int i = 0; i < 20000; i++) {
            plotData.addValues(new double[]{i, i + 1, i + 2});
        }
        for (int i = 0; i < 20000; i++) {
            assertEquals((double) i, plotData.getValueAt(0, i));
            assertEquals((double) (i + 1), plotData.getValueAt(1, i));
            assertEquals((double) (i + 2), plotData.getValueAt(2, i));
        }
    }

    public void testDrawGraph() throws Exception {
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
    public void testDrawGraph1() throws Exception {
        plotData.setLabels(new String[]{"red"});
        plotData.setMaxAt(0,100);
        plotData.setColorAt(0,Color.YELLOW);
        setExpectedColor(Color.YELLOW);
        addValues(1, 33);
        addValues(2, 52);
        addValues(3, 87);
        assertLines = true;
        plotData.drawGraph(this, 0, 0, 100, 100, 0);
    }
    int lastY = -1;
    int x = 0;
    private void addValues(int x,double y){
        plotData.addValues(x, new double[]{y});
        if(lastY != -1){
            expected.add(new int[]{this.x, 100 - lastY, this.x + 1, 100 - (int) y});
            this.x++;
        }
        lastY = (int)y;
    }
    public void testDrawGraph2() throws Exception {
        PlotData plotData = new PlotData();
        plotData.setLabels(new String[]{"red"});
        plotData.setMaxAt(0,100);
        plotData.addValues(1, new double[]{33});
        plotData.addValues(2, new double[]{52});
        plotData.addValues(10, new double[]{87});
        assertLines = true;
        expected.add(new int[]{90,33,92,52});
        expected.add(new int[]{91,52,99,87});
        plotData.drawGraph(this, 0, 0, 100, 100, 0);
    }

    boolean lineDrawingStarted = false;
    private Color expectedColor = null;
    private void setExpectedColor(Color color){
        expectedColor = color;
    }

    List<int[]> expected = new ArrayList<int[]>();
    boolean assertLines = false;
    int count = 0;

    /* implementations for GraphCanvas */
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (lineDrawingStarted) {
            System.out.println(x1+","+y1+","+x2+","+y2);
            int[] ex = expected.remove(0);
            System.out.println(ex[0]+","+ex[1]+","+ex[2]+","+ex[3]);
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
