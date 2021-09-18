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

import one.cafebabe.samurai.util.OSDetector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*package*/class Tile extends JPanel {

    private final BorderLayout borderLayout1 = new BorderLayout();
    private final BorderLayout borderLayout2 = new BorderLayout();
    /*package*/ final JLabel title = new JLabel();
    JComponent component;
    /*package*/ final JPanel centerPanel = new JPanel();
    /*package*/ final JPanel northPanel = new JPanel();
    private final BorderLayout northPanelLayout = new BorderLayout();
    /*package*/ final JLabel rightLabel = new JLabel();

    /*package*/ Tile(JComponent component, String title, ImageIcon icon) {
        setLayout(borderLayout1);
        setBorder(null);
        this.title.setBorder(null);
        this.title.setText(title);
        this.title.setIcon(icon);
        this.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(borderLayout2);
        centerPanel.add(component, BorderLayout.CENTER);
        this.add(northPanel, java.awt.BorderLayout.NORTH);
        northPanel.setLayout(northPanelLayout);
        northPanel.add(this.title, java.awt.BorderLayout.CENTER);
        northPanel.add(rightLabel, java.awt.BorderLayout.EAST);
        this.component = component;
        this.title.setBorder(emptyBorder);
    }

    /*package*/ void addMouseListenerToTitle(MouseListener listener) {
        MouseListener[] listeners = title.getMouseListeners();
        for (MouseListener listener1 : listeners) {
            if (listener1 == listener) {
                return;
            }
        }
        this.title.addMouseListener(listener);
        this.rightLabel.addMouseListener(listener);
    }

    /*package*/ void addMouseMotionListenerToTitle(MouseMotionListener listener) {
        MouseMotionListener[] listeners = title.getMouseMotionListeners();
        for (MouseMotionListener listener1 : listeners) {
            if (listener1 == listener) {
                return;
            }
        }
        this.title.addMouseMotionListener(listener);
        this.rightLabel.addMouseMotionListener(listener);
    }

    /*package*/ void addMouseListenerToComponent(MouseListener listener) {
        MouseListener[] listeners = title.getMouseListeners();
        for (MouseListener listener1 : listeners) {
            if (listener1 == listener) {
                return;
            }
        }
        this.title.addMouseListener(listener);
        addMouseListenerToComponents(listener, this.component);
    }

    private void addMouseListenerToComponents(MouseListener listener, Component component) {
        component.addMouseListener(listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addMouseListenerToComponents(listener, container.getComponent(i));
            }
        }
    }

    /*package*/ void removeMouseListenerFromComponent(MouseListener listener) {
        this.title.removeMouseListener(listener);
        removeMouseListenerFromComponents(listener, this.component);
    }

    private void removeMouseListenerFromComponents(MouseListener listener, Component component) {
        component.removeMouseListener(listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                removeMouseListenerFromComponents(listener, container.getComponent(i));
            }
        }
    }


    /*package*/ JComponent getInnerComponent() {
        return this.component;
    }

    final Border focuedBorder = new LineBorder(SystemColor.textHighlight, 2, true);
    final Border emptyBorder = new EmptyBorder(2, 2, 2, 2);

    /*package*/ void setFocused(boolean focused) {
        if (focused) {
            this.title.setForeground(SystemColor.textText);
            this.centerPanel.setBorder(focuedBorder);
        } else {
            this.title.setForeground(SystemColor.textInactiveText);
            this.centerPanel.setBorder(emptyBorder);

        }
    }
//  /*private*/ void setUnfocused(){
//    this.title.setForeground(SystemColor.textInactiveText);
//    this.centerPanel.setBorder(emptyBorder);
//  }
//  /*private*/ void setFocued(){
//    this.title.setForeground(SystemColor.textText);
//    this.centerPanel.setBorder(focuedBorder);

    //  }
    /*package*/ void enableTitleWithoutFocus() {
        this.title.setForeground(SystemColor.textText);
        this.centerPanel.setBorder(null);
    }

    /*package*/ void setLeftIcon(ImageIcon icon) {
        this.title.setIcon(icon);
    }

    private ImageIcon rightIcon = null;

    /*package*/ void setRightIcon(ImageIcon icon) {
        this.rightLabel.setIcon(icon);
        rightIcon = icon;
    }

    /*package*/ ImageIcon getRightIcon() {
        return rightIcon;
    }

    /*package*/ void setTitle(String title) {
        this.title.setText(title);
    }

    /*package*/ void setComponent(JComponent component) {
        centerPanel.remove(this.component);
        centerPanel.add(component, BorderLayout.CENTER);
        this.component = component;

    }

    /*package*/ String getTitle() {
        return this.title.getText();
    }

    /*package*/ boolean isOnRightLabel(MouseEvent event) {
        if (OSDetector.isMac()) {
            return event.getSource() == title && 0 <= event.getX() && event.getX() < 18;
        } else {
            return event.getSource() == rightLabel;
        }
    }

    /*package*/ boolean isOnTitle(MouseEvent event) {
        if (event.getSource() == rightLabel) {
            Rectangle bounds = rightLabel.getBounds();
            //get absolute coordinate for the mouse event
            double fixedX = event.getX() + bounds.getX();
            double fixedY = event.getY() + bounds.getY();
            return rightLabel.getBounds().contains(fixedX, fixedY) ||
                    title.getBounds().contains(fixedX, fixedY);
        } else if (event.getSource() == title) {
            return title.getBounds().contains(event.getX(), event.getY());
        } else {
            return false;
        }
    }
}
