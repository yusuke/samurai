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
package samurai.swing;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/*package*/class TilePanel extends JPanel implements AncestorListener {

    GridBagLayout gridBagLayout = new GridBagLayout();
    private List<ComponentInfo> components;
    /**
     *
     */
    private boolean supportsFocusable;
    /**
     * flag for tiling orientation
     */
    private boolean horizontal = true;

    private int selectedIndex = 0;

    private List<Tile> tiles = new ArrayList<Tile>(3);
    private List<JPanel> dividers = new ArrayList<JPanel>(2);

    public TilePanel(boolean supportsFocusable) {
        this.supportsFocusable = supportsFocusable;
        this.components = new ArrayList<ComponentInfo>(3);
        this.setLayout(gridBagLayout);
        ensureOrientation();
    }

    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("Unsupported orientation");
        }
        if ((horizontal && HORIZONTAL == orientation) ||
                (!horizontal && VERTICAL == orientation)) {
            return;
        }
        horizontal = HORIZONTAL == orientation;
        ensureOrientation();
    }

    private void ensureOrientation() {
        for (int i = 0; i < constraints.size(); i++) {
            GridBagConstraints constraint = constraints.get(i);
            if (horizontal) {
                constraint.gridx = constraint.gridy;
                constraint.gridy = 0;
                constraint.weightx = constraint.weighty;
                constraint.weighty = 1;
            } else {
                constraint.gridy = constraint.gridx;
                constraint.gridx = 0;
                constraint.weighty = constraint.weightx;
                constraint.weightx = 1;
            }
            add(tiles.get(i), constraint);
        }

        for (int i = 0; i < dividers.size(); i++) {
            JPanel divider = dividers.get(i);
            if (horizontal) {
                divider.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            } else {
                divider.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            }
            add(divider, getDividerConstraints(i * 2 + 1));
        }

        validate();

    }


    private List<MouseListener> titleMouseListeners = new ArrayList<MouseListener>(1);
    private List<MouseMotionListener> titleMouseMotionListeners = new ArrayList<MouseMotionListener>(1);

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

    private Dimension dividerSize = new Dimension(3, 3);

    public void setDeviderSize(int size) {
        this.dividerSize = new Dimension(size, size);
        for (JPanel divider : dividers) {
            divider.setMinimumSize(dividerSize);
            divider.setMaximumSize(dividerSize);
            divider.setPreferredSize(dividerSize);
        }
    }

    private List<GridBagConstraints> constraints = new ArrayList<GridBagConstraints>(3);

    private GridBagConstraints getDividerConstraints(int grid) {
        GridBagConstraints dividerConstraint = new GridBagConstraints();
        dividerConstraint.weightx = 0;
        dividerConstraint.weighty = 0;
        if (horizontal) {
            dividerConstraint.gridx = grid;
            dividerConstraint.fill = GridBagConstraints.VERTICAL;
        } else {
            dividerConstraint.gridy = grid;
            dividerConstraint.fill = GridBagConstraints.HORIZONTAL;
        }
        return dividerConstraint;
    }

    public void addComponent(String title, ImageIcon icon, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100;
        gbc.weighty = 1;

        if (tiles.size() > 0) {
            JPanel divider = new JPanel();
            dividers.add(divider);
            divider.addMouseListener(dividerMouseListener);
            divider.addMouseMotionListener(dividerMouseListener);
            divider.setMinimumSize(dividerSize);
            divider.setMaximumSize(dividerSize);
            divider.setPreferredSize(dividerSize);
            if (horizontal) {
                gbc.gridx = tiles.size() * 2;
                divider.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                gbc.gridy = 0;
                double sum = 0;
                for (GridBagConstraints constraint : constraints) {
                    sum += constraint.weightx;
                }
                gbc.weightx = sum / constraints.size();
            } else {
                gbc.gridx = 0;
                gbc.gridy = tiles.size() * 2;
                divider.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                double sum = 0;
                for (GridBagConstraints constraint : constraints) {
                    sum += constraint.weighty;
                }
                gbc.weighty = sum / constraints.size();
            }
            add(divider, getDividerConstraints(tiles.size() * 2 - 1));
        }

        Tile splittedPanel = new Tile(component, title, icon);
        splittedPanel.setPreferredSize(new Dimension(0, 0));
        add(splittedPanel, gbc);

        constraints.add(gbc);
        ComponentInfo<JComponent> cf = new ComponentInfo<JComponent>(component, title, icon);
        components.add(cf);
        tiles.add(splittedPanel);
        setMouseListners();
        setSelectedIndex(this.getTileCount() - 1);
        validate();
    }

    DividerMouseListener dividerMouseListener = new DividerMouseListener();

    class DividerMouseListener extends MouseAdapter implements MouseMotionListener {
        private int originalWidths[];
        private int draggingDividerIndex;
        private int dragRangeMin;
        private int dragRangeMax;
        private int dragStartCoordinate;

        DividerMouseListener() {
        }

        public void mouseDragged(MouseEvent event) {
            JComponent divider = (JComponent) event.getSource();
            int dragDistance = (horizontal ? event.getX() + divider.getX() : event.getY() + divider.getY()) - dragStartCoordinate;
            if (dragDistance < dragRangeMin || dragDistance > dragRangeMax) {
                dragDistance = 0;
            }
            double rightRatio = 0;
            for (int i = 0; i < originalWidths.length; i++) {
                double newWeight;
                GridBagConstraints gbc = constraints.get(i);
                if (i < draggingDividerIndex) {
                    newWeight = originalWidths[i];
                } else {
                    if (i == draggingDividerIndex) {
                        newWeight = originalWidths[i] + dragDistance;
                        for (int j = i + 1; j < originalWidths.length; j++) {
                            rightRatio += originalWidths[j];
                        }
                        rightRatio = (rightRatio - dragDistance) / rightRatio;

                    } else {
                        newWeight = originalWidths[i] * rightRatio;
                    }
                }
                if (horizontal) {
                    gbc.weightx = newWeight;
                } else {
                    gbc.weighty = newWeight;
                }
                add(tiles.get(i), gbc);
            }
            validate();
        }

        public void mouseMoved(MouseEvent event) {
        }

        public void mousePressed(MouseEvent event) {
            JComponent divider = (JComponent) event.getSource();

            dragStartCoordinate = horizontal ? divider.getX() : divider.getY();

            for (int i = 0; i < dividers.size(); i++) {
                if (dividers.get(i).equals(divider)) {
                    draggingDividerIndex = i;
                    break;
                }
            }
            originalWidths = new int[tiles.size()];
            dragRangeMin = 0;
            dragRangeMax = 0;
            for (int i = 0; i < originalWidths.length; i++) {
                if (horizontal) {
                    originalWidths[i] = tiles.get(i).getWidth();
                } else {
                    originalWidths[i] = tiles.get(i).getHeight();
                }
                if (i == draggingDividerIndex) {
                    dragRangeMin = -originalWidths[i];
                }
                if (i > draggingDividerIndex) {
                    dragRangeMax += originalWidths[i];
                }
            }

        }

        public void mouseReleased(MouseEvent event) {
        }
    }

    public void setForegroundAt(int index, Color color) {
        tiles.get(index).title.setForeground(color);
//    tiles.get(index).rightLabel.seBackground(color);
    }

    public Color getForegroundAt(int index) {
        return tiles.get(index).title.getForeground();
    }

    public void removeTileAt(int index) {
        //remove the divider next to the specified component
        if (components.size() > 1) {
            if (index == 0) {
                remove(dividers.remove(0));
            } else {
                remove(dividers.remove(index - 1));
            }
        }
        remove(tiles.remove(index));
        constraints.remove(index);
        components.remove(index);
        for (int i = index; i < components.size(); i++) {
            GridBagConstraints gbc = constraints.get(i);
            if (horizontal) {
                gbc.gridx = i * 2;
            } else {
                gbc.gridy = i * 2;
            }
            add(tiles.get(i), gbc);
            if (i > 0) {
                add(dividers.get(i - 1), getDividerConstraints(i * 2 - 1));
            }
        }
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

    /*package*/ boolean isOnButton(int index, MouseEvent event) {
        return tiles.get(index).isOnRightLabel(event);
    }

    public void ancestorAdded(AncestorEvent event) {

    }

    public void ancestorRemoved(AncestorEvent event) {
        for (Tile tile : tiles) {
            if (tile.getInnerComponent() == event.getComponent()) {
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

}