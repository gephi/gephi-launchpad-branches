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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class GlobalSettingsPanel extends javax.swing.JPanel {

    /** Creates new form GlobalSettingsPanel */
    public GlobalSettingsPanel() {
        initComponents();
    }

    public void setup() {
        VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
        vizModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("init")) {
                    refreshSharedConfig();
                } else if (evt.getPropertyName().equals(VizConfig.AUTO_SELECT_NEIGHBOUR)) {
                    refreshSharedConfig();
                } else if (evt.getPropertyName().equals(VizConfig.HIGHLIGHT_NON_SELECTED)) {
                    refreshSharedConfig();
                } else if (evt.getPropertyName().equals(VizConfig.USE_3D)) {
                    refreshSharedConfig();
                } else if (evt.getPropertyName().equals(VizConfig.ZOOM_FACTOR)) {
                    refreshSharedConfig();
                }
            }
        });
        refreshSharedConfig();
        hightlightCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.setHighlightNonSelectedEnabled(hightlightCheckBox.isSelected());
            }
        });
        autoSelectNeigborCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.setAutoSelectNeighbor(autoSelectNeigborCheckbox.isSelected());
            }
        });
        zoomSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                float zoom = zoomSlider.getValue() / (float) zoomSlider.getMaximum();
                vizModel.setZoomFactor(zoom);
                Lookup.getDefault().lookup(VisualizationController.class).getCamera().setZoom(zoom);
            }
        });
        use3dCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.setUse3d(use3dCheckbox.isSelected());
                Lookup.getDefault().lookup(VisualizationController.class).modeChanged();
            }
        });
    }

    private void refreshSharedConfig() {
        VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
        setEnable(!vizModel.isDefaultModel());
        if (vizModel.isDefaultModel()) {
            return;
        }
        if (autoSelectNeigborCheckbox.isSelected() != vizModel.isAutoSelectNeighbor()) {
            autoSelectNeigborCheckbox.setSelected(vizModel.isAutoSelectNeighbor());
        }
        if (hightlightCheckBox.isSelected() != vizModel.isHighlightNonSelectedEnabled()) {
            hightlightCheckBox.setSelected(vizModel.isHighlightNonSelectedEnabled());
        }
        if (use3dCheckbox.isSelected() != vizModel.isUse3d()) {
            use3dCheckbox.setSelected(vizModel.isUse3d());
        }
        refreshZoom();
    }

    private void setEnable(boolean enable) {
        autoSelectNeigborCheckbox.setEnabled(enable);
        hightlightCheckBox.setEnabled(enable);
        labelZoom.setEnabled(enable);
        zoomSlider.setEnabled(enable);
    }

    private void refreshZoom() {
        float zoomValue = Lookup.getDefault().lookup(VisualizationController.class).getVizModel().getZoomFactor();
        if ((int) (zoomValue * zoomSlider.getMaximum()) != zoomSlider.getValue()) {
            zoomSlider.setValue((int) (zoomValue * zoomSlider.getMaximum()));
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

        hightlightCheckBox = new javax.swing.JCheckBox();
        autoSelectNeigborCheckbox = new javax.swing.JCheckBox();
        zoomPanel = new javax.swing.JPanel();
        labelZoom = new javax.swing.JLabel();
        zoomSlider = new javax.swing.JSlider();
        use3dCheckbox = new javax.swing.JCheckBox();

        hightlightCheckBox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.hightlightCheckBox.text")); // NOI18N
        hightlightCheckBox.setBorder(null);
        hightlightCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        hightlightCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        autoSelectNeigborCheckbox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.autoSelectNeigborCheckbox.text")); // NOI18N
        autoSelectNeigborCheckbox.setBorder(null);
        autoSelectNeigborCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        autoSelectNeigborCheckbox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        zoomPanel.setOpaque(false);
        zoomPanel.setLayout(new java.awt.GridBagLayout());

        labelZoom.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.labelZoom.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 0);
        zoomPanel.add(labelZoom, gridBagConstraints);

        zoomSlider.setMaximum(10000);
        zoomSlider.setValue(5000);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        zoomPanel.add(zoomSlider, gridBagConstraints);

        use3dCheckbox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.use3dCheckbox.text")); // NOI18N
        use3dCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autoSelectNeigborCheckbox)
                    .addComponent(hightlightCheckBox))
                .addGap(27, 27, 27)
                .addComponent(zoomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(use3dCheckbox)
                .addGap(202, 202, 202))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(use3dCheckbox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(zoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hightlightCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(autoSelectNeigborCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoSelectNeigborCheckbox;
    private javax.swing.JCheckBox hightlightCheckBox;
    private javax.swing.JLabel labelZoom;
    private javax.swing.JCheckBox use3dCheckbox;
    private javax.swing.JPanel zoomPanel;
    private javax.swing.JSlider zoomSlider;
    // End of variables declaration//GEN-END:variables
}
