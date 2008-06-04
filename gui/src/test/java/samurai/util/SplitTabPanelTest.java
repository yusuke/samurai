package samurai.util;

import samurai.swing.TileTabPanel;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 1.0
 */
public class SplitTabPanelTest extends JFrame {
    BorderLayout borderLayout1 = new BorderLayout();
    TileTabPanel splitTabPanel = new TileTabPanel();

    public SplitTabPanelTest() {
//    jPanel1.setFrame(this);
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        getContentPane().setLayout(borderLayout1);
        this.getContentPane().add(splitTabPanel, java.awt.BorderLayout.CENTER);
//        this.getContentPane().add(new SmartSplitPane(), java.awt.BorderLayout.CENTER);
//    this.getContentPane().add(new SplittedPanel(new JTextArea("hoge"),"hoge",null), java.awt.BorderLayout.CENTER);
    }

    public static void main(String[] args) {
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
//    test.jPanel1.removeComponent(ta0);
//    test.jPanel1.removeComponent(ta1);
//    test.jPanel1.removeComponent(ta2);
        if (test.splitTabPanel.getSelectedIndex() != 3) {
            System.out.println("error!:" + test.splitTabPanel.getSelectedIndex());
        }
    }
}
