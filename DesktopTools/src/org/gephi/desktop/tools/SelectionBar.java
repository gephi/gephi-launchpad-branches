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
package org.gephi.desktop.tools;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.ui.utils.UIUtils;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class SelectionBar extends javax.swing.JPanel {

    private boolean mouseSelection;

    /** Creates new form SelectionBar */
    public SelectionBar() {
        initComponents();
        Lookup.getDefault().lookup(SelectionManager.class).addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                refresh();
            }
        });
        refresh();

        statusLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (mouseSelection && statusLabel.isEnabled()) {
                    JPopupMenu menu = createPopup();
                    menu.show(statusLabel, 0, statusLabel.getHeight());
                }
            }
        });

        if (UIUtils.isAquaLookAndFeel()) {
            setBackground(UIManager.getColor("NbExplorerView.background"));
        }
    }

    public JPopupMenu createPopup() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
        final MouseSelectionPopupPanel popupPanel = new MouseSelectionPopupPanel();
        popupPanel.setDiameter(vizConfig.getIntProperty(VizConfig.MOUSE_SELECTION_DIAMETER));
        popupPanel.setProportionnalToZoom(vizConfig.getBooleanProperty(VizConfig.MOUSE_SELECTION_ZOOM_PROPORTIONAL));
        popupPanel.setChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
                vizConfig.setProperty(VizConfig.MOUSE_SELECTION_DIAMETER, popupPanel.getDiameter());
                vizConfig.setProperty(VizConfig.MOUSE_SELECTION_ZOOM_PROPORTIONAL, popupPanel.isProportionnalToZoom());
            }
        });

        JPopupMenu menu = new JPopupMenu();
        menu.add(popupPanel);
        return menu;
    }

    public void refresh() {
        SelectionManager manager = Lookup.getDefault().lookup(SelectionManager.class);
        if (manager.isDirectMouseSelection()) {
            statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.mouseSelection"));
            mouseSelection = true;
        } else if (manager.isNodeDraggingEnabled()) {
            statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.dragging"));
            mouseSelection = true;
        } else if (manager.getSelectionType() != SelectionType.NONE) {
            switch (manager.getSelectionType()) {
                case ELLIPSE:
                    statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.ellipseSelection"));
                    break;
                case POLYGON:
                    statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.polygonSelection"));
                    break;
                case RECTANGLE:
                    statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.rectangleSelection"));
                    break;
            }
            mouseSelection = manager.getSelectionType() != SelectionType.NONE;
        } else {
            statusLabel.setText(NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.noSelection"));
            mouseSelection = false;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        statusLabel = new javax.swing.JLabel();
        endSeparator = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(150, 28));
        setLayout(new java.awt.GridBagLayout());

        statusLabel.setFont(new java.awt.Font("Tahoma", 0, 10));
        statusLabel.setText(org.openide.util.NbBundle.getMessage(SelectionBar.class, "SelectionBar.statusLabel.text")); // NOI18N
        statusLabel.setPreferredSize(new java.awt.Dimension(34, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        add(statusLabel, gridBagConstraints);

        endSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        endSeparator.setPreferredSize(new java.awt.Dimension(3, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        add(endSeparator, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator endSeparator;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setEnabled(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                for (Component c : getComponents()) {
                    c.setEnabled(enabled);
                }
            }
        });
    }
}
