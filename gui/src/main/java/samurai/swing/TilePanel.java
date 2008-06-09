package samurai.swing;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a tabbed tail tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p/>
 * <p> </p>
 * mou
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
/*package*/class TilePanel extends JPanel implements PropertyChangeListener, AncestorListener {
    public void ancestorAdded(AncestorEvent event) {

    }

    public void ancestorRemoved(AncestorEvent event) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getInnerComponent() == event.getComponent()) {
                removeComponent(event.getComponent());
                break;
            }
        }
    }

    public void ancestorMoved(AncestorEvent event) {

    }

    public void removeAll() {
        super.removeAll();
        while (components.size() > 0) {
            removeComponent(components.get(0).getComponent());
        }
    }

    BorderLayout borderLayout1 = new BorderLayout();
    private List<ComponentInfo> components;
    TileTabPanel jPanel1;
    /**
     *
     */
    private boolean supportsFocusable;
    /**
     * flag for tiling orientation
     */
    private boolean tileHorizontal = false;

    private int selectedIndex = 0;

    private List<Tile> tiles = new ArrayList<Tile>(3);
    private List<JSplitPane> splitPanes = new ArrayList<JSplitPane>(2);

    public TilePanel(boolean supportsFocusable) {
        this.supportsFocusable = supportsFocusable;
        this.components = new ArrayList<ComponentInfo>(3);
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public int getTileCount() {
        return components.size();
    }

    public Component getSelectedComponent() {
        return tiles.get(selectedIndex).getInnerComponent();
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        if (supportsFocusable) {
            if (tiles.size() == 1) {
                tiles.get(0).enableTitleWithoutFocus();
            } else {
                for (int i = 0; i < tiles.size(); i++) {
                    if (i == index) {
                        tiles.get(i).setFocused(true);
                    } else {
                        tiles.get(i).setFocused(false);
                    }
                }
            }
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                this_componentResized(componentEvent);
            }
        });
    }

    public final static int TILE_HORIZONTAL = 0;
    public final static int TILE_VERTICAL = 1;

    public void setOrientation(int orientation) {
        if (orientation != TILE_HORIZONTAL && orientation != TILE_VERTICAL) {
            throw new IllegalArgumentException("Unsupported orientation");
        }
        if ((tileHorizontal && TILE_HORIZONTAL == orientation) ||
                (!tileHorizontal && TILE_VERTICAL == orientation)) {
            return;
        }
        tileHorizontal = TILE_HORIZONTAL == orientation;
        for (JSplitPane sp : splitPanes) {
            sp.setOrientation(tileHorizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
        }
        ensureOrientation();
    }

    private List<MouseListener> titleMouseListeners = new ArrayList<MouseListener>(1);
    private List<MouseMotionListener> titleMouseMotionListeners = new ArrayList<MouseMotionListener>(1);

    public void addMouseListnerToTitles(MouseListener listener) {
        titleMouseListeners.add(listener);
        setMouseListners();
    }

    public void addMouseMotionListnerToTitles(MouseMotionListener listener) {
        titleMouseMotionListeners.add(listener);
        setMouseListners();
    }

    private void setMouseListners() {
        for (Tile sp : tiles) {
            for (MouseListener theListener : titleMouseListeners) {
                sp.addMouseListenerToTitle(theListener);
            }
            for (MouseMotionListener theListener : titleMouseMotionListeners) {
                sp.addMouseMotionListenerToTitle(theListener);
            }
            if (supportsFocusable) {
                sp.addMouseListenerToComponent(mouseAdapter);
            }
        }
    }

    MouseAdapter mouseAdapter = new MouseAdapter() {
        public void mousePressed(MouseEvent mouseEvent) {
            this_mousePressed(mouseEvent);
        }
    };

    public void this_mousePressed(MouseEvent mouseEvent) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).isAncestorOf(mouseEvent.getComponent())) {
                setSelectedIndex(i);
                break;
            }
        }
    }

    private void ensureOrientation() {
        for (int i = (splitPanes.size() - 1); i >= 0; i--) {
            ComponentInfo cf = components.get(i);
            splitPanes.get(i).setDividerLocation((int) (tileHorizontal ? cf.getWidth() : cf.getHeight()));
            validate();
        }

    }

    public void addComponent(String title, JComponent component) {
        addComponent(title, null, component);
    }

    public int indexAtLocation(MouseEvent event) {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).isOnTitle(event)) {
                return i;
            }
        }
        return -1;
    }

    private int dividerSize = 3;
    public void setDeviderSize(int size){
        this.dividerSize = size;
    }

    public void addComponent(String title, ImageIcon icon, JComponent component) {
        resizing = true;
        ComponentInfo<JComponent> cf = new ComponentInfo<JComponent>(component, title, icon);
        cf.setIcon(icon);
        components.add(cf);
        Tile splittedPanel = new Tile(cf.getComponent(), cf.getName(), cf.getIcon());
        tiles.add(splittedPanel);
        setMouseListners();
        sizeCalcurated = false;
        calcComponentSize();
        switch (components.size()) {
            case 0:
                break;
            case 1:
                add(tiles.get(0), BorderLayout.CENTER);
                break;
            default:
                JSplitPane splitPane = null;
                int size = tiles.size();
                JComponent first = (size == 2) ? tiles.get(0) : splitPanes.get(size - 3);
                JComponent second = tiles.get(size - 1);
                splitPane = new JSplitPane(tileHorizontal ? JSplitPane.HORIZONTAL_SPLIT :
                        JSplitPane.VERTICAL_SPLIT, first, second);
                splitPane.setDividerSize(dividerSize);
                splitPane.setBorder(null);
                splitPane.setContinuousLayout(true);
                splitPanes.add(splitPane);
                if (components.size() == 2) {
                    remove(tiles.get(0));
                } else {
                    remove(splitPanes.get(splitPanes.size() - 2));
                }
                add(splitPane, BorderLayout.CENTER);
                ensureOrientation();

                splitPane.addPropertyChangeListener(this);
        }
        this.setSelectedIndex(this.getTileCount() - 1);
        validate();
        resizing = false;
    }

    public void setForegroundAt(int index, Color color) {
        tiles.get(index).title.setForeground(color);
//    tiles.get(index).rightLabel.seBackground(color);
    }

    public Color getForegroundAt(int index) {
        return tiles.get(index).title.getForeground();
    }

    private boolean sizeCalcurated = false;

    private void calcComponentSize() {
        if (!sizeCalcurated && (components.size() > 0)) {
            int width = getWidth() / components.size();
            int height = getHeight() / components.size();
            for (int i = 0; i < components.size(); i++) {
                components.get(i).setWidth(width * (i + 1));
                components.get(i).setHeight(height * (i + 1));
            }
            sizeCalcurated = true;
        }
    }

    public void removeTileAt(int index) {
        switch (components.size()) {
            case 0:
                break;
            case 1:
                remove(tiles.get(0));
                break;
            case 2:
                remove(splitPanes.get(0));
                splitPanes.remove(0);
                if (0 == index) {
                    add(tiles.get(1), BorderLayout.CENTER);
                } else {
                    add(tiles.get(0), BorderLayout.CENTER);
                }
                break;
            default:
                // (((0|0|1)|1|2)|2|3)
                if (0 == index) {
                    //the first component will be removed
                    JSplitPane toBeRemoved = splitPanes.get(0);
                    if (tileHorizontal) {
                        splitPanes.get(1).setLeftComponent(toBeRemoved.getRightComponent());
                    } else {
                        splitPanes.get(1).setTopComponent(toBeRemoved.getBottomComponent());
                    }
                    splitPanes.remove(0);
                } else if (index != (components.size() - 1)) {
                    //middle component will be removed
                    JSplitPane toBeRemoved = splitPanes.get(index - 1);
                    if (tileHorizontal) {
                        splitPanes.get(index).setLeftComponent(toBeRemoved.getLeftComponent());
                    } else {
                        splitPanes.get(index).setTopComponent(toBeRemoved.getTopComponent());
                    }
                    splitPanes.remove(index - 1);

                } else {
                    //the last component will be removed
                    JSplitPane toBeRemoved = splitPanes.get(index - 1);
                    remove(toBeRemoved);
                    this.add(splitPanes.get(splitPanes.size() - 2), BorderLayout.CENTER);
                    splitPanes.remove(index - 1);
                }
        }
        components.remove(index);
        tiles.remove(index);

        sizeCalcurated = false;
        calcComponentSize();
        for (int i = (splitPanes.size() - 1); i >= 0; i--) {
            ComponentInfo cf = components.get(i);
            splitPanes.get(i).setDividerLocation((int) (tileHorizontal ? cf.getWidth() : cf.getHeight()));
            validate();
        }
        validate();
        if (this.selectedIndex >= this.getTileCount()) {
            selectedIndex = this.getTileCount() - 1;
        }
        this.setSelectedIndex(selectedIndex);
    }

    public void removeComponent(Component component) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getComponent() == component) {
                removeTileAt(i);
                break;
            }
        }
    }

    /*package*/ void setLeftIconAt(int index, ImageIcon icon) {
        tiles.get(index).setLeftIcon(icon);
    }

    /*package*/ ImageIcon getLeftIconAt(int index) {
        return components.get(index).getIcon();
    }

    /*package*/ void setRightIconAt(int index, ImageIcon icon) {
        tiles.get(index).setRightIcon(icon);
    }

    /*package*/ ImageIcon getRightIconAt(int index) {
        return tiles.get(index).getRightIcon();
    }


    /*package*/ void setComponentAt(int index, JComponent component) {
        tiles.get(index).setComponent(component);
    }

    /*package*/ Component getComponentAt(int index) {
        return components.get(index).getComponent();
    }

    /*package*/ void setTitleAt(int index, String text) {
        tiles.get(index).setTitle(text);
    }

    /*package*/ String getTitleAt(int index) {
        return components.get(index).getName();
    }

    private boolean dividerDragging = false;

    public void propertyChange(PropertyChangeEvent evt) {
        if (!resizing && !dividerDragging) {
            for (int i = 0; i < splitPanes.size(); i++) {
                JSplitPane splitPane = splitPanes.get(i);
                if (splitPane.equals(evt.getSource()) && "lastDividerLocation".equals(evt.getPropertyName())) {
                    double newValue = (double) (Integer) evt.getNewValue();
                    double lastValue = tileHorizontal ? components.get(i).getWidth() : components.get(i).getHeight();
                    dividerDragging = true;
                    int width = this.getWidth();
                    int height = this.getHeight();
                    //set left divider sizes
                    double leftRatio = newValue / lastValue;
                    for (int j = 0; j < i; j++) {
                        JSplitPane target = splitPanes.get(j);
                        ComponentInfo cf = components.get(j);
                        if (tileHorizontal) {
                            double newWidth = cf.getWidth() * leftRatio;
                            target.setDividerLocation((int) (newWidth));
                            cf.setWidth(newWidth);
                        } else {
                            double newHeight = cf.getHeight() * leftRatio;
                            target.setDividerLocation((int) newHeight);
                            cf.setHeight(newHeight);
                        }
                        target.validate();
                    }
                    //set right divider sizes
                    double rightRatio = (width - newValue) / (width - lastValue);
                    for (int j = i + 1; j < splitPanes.size(); j++) {
                        JSplitPane target = splitPanes.get(j);
                        ComponentInfo cf = components.get(j);
                        if (tileHorizontal) {
                            double newWidth = width - (width - cf.getWidth()) * rightRatio;
                            target.setDividerLocation((int) newWidth);
                            cf.setWidth(newWidth);
                        } else {
                            double newHeight = height - (height - cf.getHeight()) * rightRatio;
                            target.setDividerLocation((int) newHeight);
                            cf.setHeight(newHeight);
                        }
                        target.validate();
                    }

                    if (tileHorizontal) {
                        components.get(i).setWidth(newValue);
                    } else {
                        components.get(i).setHeight(newValue);
                    }

                    dividerDragging = false;
                    break;
                }
            }
        }
    }

    private int lastWidth = -1;
    private int lastHeight = -1;
    private boolean resizing = false;

    public void this_componentResized(ComponentEvent componentEvent) {
        resizing = true;
        if (components.size() > 1 && lastWidth != -1 && lastHeight != -1
                && (tileHorizontal && lastWidth != this.getWidth() || !tileHorizontal && lastHeight != this.getHeight())) {
            double horizontalRatio = (double) this.getWidth() / (double) lastWidth;
            double verticalRatio = (double) this.getHeight() / (double) lastHeight;
            for (int i = 0; i < splitPanes.size(); i++) {
                JSplitPane splitPane = splitPanes.get(i);
                ComponentInfo cf = components.get(i);
                if (tileHorizontal) {
                    if (abs(cf.getWidth() - splitPane.getDividerLocation()) > 1) {
                        cf.setWidth(splitPane.getDividerLocation());
                    }
                } else {
                    if (abs(cf.getHeight() - splitPane.getDividerLocation()) > 1) {
                        cf.setHeight(splitPane.getDividerLocation());
                    }
                }
            }
            for (int i = 0; i < (components.size() - 1); i++) {
                JSplitPane splitPane = splitPanes.get(i);
                ComponentInfo cf = components.get(i);
                if (tileHorizontal) {
                    double newWidth = cf.getWidth() * horizontalRatio;
                    splitPane.setDividerLocation((int) newWidth);
                    cf.setWidth(newWidth);
                    cf.setHeight(cf.getHeight() * verticalRatio);
                } else {
                    double newHeight = cf.getHeight() * verticalRatio;
                    splitPane.setDividerLocation((int) newHeight);
                    cf.setWidth(cf.getWidth() * horizontalRatio);
                    cf.setHeight(newHeight);
                }
                splitPane.validate();
            }
        }
        lastWidth = this.getWidth();
        lastHeight = this.getHeight();
        resizing = false;
    }

    /*package*/ boolean isOnButton(int index, MouseEvent event) {
        return tiles.get(index).isOnRightLabel(event);
    }
}
