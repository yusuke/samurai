package samurai.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import samurai.swing.TileTabPanel;

import javax.swing.JTextArea;
import java.awt.Dimension;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class TestSplitTabPanel extends TestCase {
    public TestSplitTabPanel(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(TestSplitTabPanel.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIt() throws Exception {
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
        assertEquals(4, test.splitTabPanel.getComponentSize());

    }
}
