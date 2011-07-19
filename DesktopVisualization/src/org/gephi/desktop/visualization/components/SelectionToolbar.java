/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gephi.desktop.visualization.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.ui.utils.UIUtils;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.SelectionType;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class SelectionToolbar extends JToolBar {

    private ButtonGroup buttonGroup;

    public SelectionToolbar() {
        initDesign();
        buttonGroup = new ButtonGroup();
        initContent();
    }

    private void initContent() {

        //Mouse
        final JToggleButton mouseButton = new JToggleButton(new ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/mouse.png")));
        mouseButton.setToolTipText(NbBundle.getMessage(SelectionToolbar.class, "SelectionToolbar.mouse.tooltip"));
        mouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mouseButton.isSelected()) {
                    Lookup.getDefault().lookup(SelectionManager.class).setDirectMouseSelection();
                }
            }
        });
        add(mouseButton);

        //Rectangle
        final JToggleButton rectangleButton = new JToggleButton(new ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/rectangle.png")));
        rectangleButton.setToolTipText(NbBundle.getMessage(SelectionToolbar.class, "SelectionToolbar.rectangle.tooltip"));
        rectangleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rectangleButton.isSelected()) {
                    Lookup.getDefault().lookup(SelectionManager.class).setSelectionType(SelectionType.RECTANGLE);
                }
            }
        });
        add(rectangleButton);

        //Polygon
        final JToggleButton polygonButton = new JToggleButton(new ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/polygon.png")));
        polygonButton.setToolTipText(NbBundle.getMessage(SelectionToolbar.class, "SelectionToolbar.polygon.tooltip"));
        polygonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (polygonButton.isSelected()) {
                    Lookup.getDefault().lookup(SelectionManager.class).setSelectionType(SelectionType.POLYGON);
                }
            }
        });
        add(polygonButton);

        //Ellipse
        final JToggleButton ellipseButton = new JToggleButton(new ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/ellipse.png")));
        ellipseButton.setToolTipText(NbBundle.getMessage(SelectionToolbar.class, "SelectionToolbar.ellipse.tooltip"));
        ellipseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ellipseButton.isSelected()) {
                    Lookup.getDefault().lookup(SelectionManager.class).setSelectionType(SelectionType.ELLIPSE);
                }
            }
        });
        add(ellipseButton);

        //Drag
        final JToggleButton dragButton = new JToggleButton(new ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/hand.png")));
        dragButton.setToolTipText(NbBundle.getMessage(SelectionToolbar.class, "SelectionToolbar.drag.tooltip"));
        dragButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dragButton.isSelected()) {
                    Lookup.getDefault().lookup(SelectionManager.class).setDraggingEnable(true);
                }
            }
        });
        add(dragButton);
        addSeparator();

        buttonGroup.setSelected(rectangleButton.getModel(), Lookup.getDefault().lookup(SelectionManager.class).getSelectionType() == SelectionType.RECTANGLE);
        buttonGroup.setSelected(polygonButton.getModel(), Lookup.getDefault().lookup(SelectionManager.class).getSelectionType() == SelectionType.POLYGON);
        buttonGroup.setSelected(ellipseButton.getModel(), Lookup.getDefault().lookup(SelectionManager.class).getSelectionType() == SelectionType.ELLIPSE);
        buttonGroup.setSelected(mouseButton.getModel(), Lookup.getDefault().lookup(SelectionManager.class).isDirectMouseSelection());
        buttonGroup.setSelected(dragButton.getModel(), Lookup.getDefault().lookup(SelectionManager.class).isDraggingEnabled());

        //Init events
        Lookup.getDefault().lookup(SelectionManager.class).addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
                buttonGroup.clearSelection();
                /*
                if (selectionManager.isBlocked()) {
                    buttonGroup.clearSelection();
                } else if (!selectionManager.isSelectionEnabled()) {
                    buttonGroup.clearSelection();
                } else if (selectionManager.isDirectMouseSelection()) {
                    if (!buttonGroup.isSelected(mouseButton.getModel())) {
                        buttonGroup.setSelected(mouseButton.getModel(), true);
                    }
                }
                */
                if (selectionManager.isDirectMouseSelection()) {
                    buttonGroup.setSelected(mouseButton.getModel(), true);
                } else if (selectionManager.isDraggingEnabled()) {
                    buttonGroup.setSelected(dragButton.getModel(), true);
                } else {
                    switch (selectionManager.getSelectionType()) {
                        case ELLIPSE:
                            buttonGroup.setSelected(ellipseButton.getModel(), true);
                            break;
                        case POLYGON:
                            buttonGroup.setSelected(polygonButton.getModel(), true);
                            break;
                        case RECTANGLE:
                            buttonGroup.setSelected(rectangleButton.getModel(), true);
                            break;
                    }
                }
            }
        });
    }

    private void initDesign() {
        setFloatable(false);
        setOrientation(JToolBar.VERTICAL);
        putClientProperty("JToolBar.isRollover", Boolean.TRUE); //NOI18N
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    }

    @Override
    public void setEnabled(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Component c : getComponents()) {
                    c.setEnabled(enabled);
                }
            }
        });
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof JButton) {
            UIUtils.fixButtonUI((JButton) comp);
        }
        if (comp instanceof AbstractButton) {
            buttonGroup.add((AbstractButton) comp);
        }

        return super.add(comp);
    }
}
