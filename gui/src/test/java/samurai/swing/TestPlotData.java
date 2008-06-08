package samurai.swing;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: yusukey
 * Date: Jun 5, 2008
 * Time: 11:15:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPlotData  extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void testPlotData() throws Exception{
        PlotData plotData = new PlotData();
        plotData.setLabels(new String[]{"red","green","blue"});
        for(int i=0;i<20000;i++){
            plotData.addValues(new double[]{i,i+1,i+2});
        }
        for(int i=0;i<20000;i++){
            assertEquals((double)i,plotData.getValueAt(0,i));
            assertEquals((double)(i+1),plotData.getValueAt(1,i));
            assertEquals((double)(i+2),plotData.getValueAt(2,i));
        }
    }

}
