package samurai.swing;

import javax.swing.JSplitPane;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
public class SmartSplitPane extends JSplitPane {
    int lastDivider;
    int lastWidth;

    public SmartSplitPane() {
        super();
        setParameters();
        setListener();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SmartSplitPane(int orientation) {
        super(orientation);
        setParameters();
        setListener();
    }

    public SmartSplitPane(int orientation, boolean op1) {
        super(orientation, op1);
        setParameters();
        setListener();
    }

    public SmartSplitPane(int orientation, Component component1, Component component2) {
        super(orientation, component1, component2);
        setParameters();
        setListener();
    }

    public SmartSplitPane(int orientation, boolean op1, Component component1, Component component2) {
        super(orientation, op1, component1, component2);
        setParameters();
        setListener();
    }

    private void setListener() {
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                jSplitPane1_componentResized(componentEvent);
            }
        });

    }

    private void setParameters() {
        lastDivider = this.getDividerLocation();
        if (this.getOrientation() == this.HORIZONTAL_SPLIT) {
            lastWidth = this.getWidth();
        } else {
            lastWidth = this.getHeight();
        }
    }

    public void setOrientation(int newOrientation) {
        super.setOrientation(newOrientation);
        setParameters();
    }

    public void setDividerLocation(int location) {
        if (ticket != null) {
            super.setDividerLocation(location);
            ticket = null;
        }
        resizing--;
//    if(pressing){
//      System.out.println("last:" + lastDivider + "  new:" + location);
//    new Throwable().printStackTrace();
//    lastDivider = location;
//    if (resizing == 0) {
//      if (this.getWidth() > 0) {
//        lastProportion = (double) location / (double)this.getWidth();
//      } else {
//        lastProportion = 0.5;
//      }
//      System.out.println(location + ":" + this.getWidth() + ":propro:" + lastProportion);
//    }
    }

//  public void     setDividerLocation(double location){
//    super.setDividerLocation(location);
//    System.out.println("double");
//    lastDivider = (int)(this.getWidth()*location);
    //  }
    private int resizing = 0;
    private Object ticket = null;

    public void jSplitPane1_componentResized(ComponentEvent componentEvent) {
        ticket = new Object();
        resizing++;
//    int newWidth = this.getWidth();
//    double ratio = (double)newWidth / (double)lastWidth;
//    int newDivider =(int)(lastDivider * ratio);
//    super.setDividerLocation(newDivider);
//    System.out.println("newDivider:"+newDivider+" lastDivider:"+lastDivider+" newWidth:"+newWidth+" lastWidth:"+lastWidth);
//    setParameters();
//    super.setDividerLocation(lastProportion);
        super.setDividerLocation(0.5);
    }

    double lastProportion = 0.5;

    private void jbInit() throws Exception {
    }

}
