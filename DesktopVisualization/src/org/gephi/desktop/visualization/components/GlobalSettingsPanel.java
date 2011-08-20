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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.VisualizationController;
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
                } else if (evt.getPropertyName().equals(VizConfig.CAMERA_USE_3D)) {
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
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.getCamera().zoom(-10);
            }
        });
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.getCamera().zoom(10);
            }
        });
        use3dCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                vizModel.setUse3d(use3dCheckbox.isSelected());
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
    }

    private void setEnable(boolean enable) {
        autoSelectNeigborCheckbox.setEnabled(enable);
        hightlightCheckBox.setEnabled(enable);
        labelZoom.setEnabled(enable);
        zoomInButton.setEnabled(enable);
        zoomOutButton.setEnabled(enable);
        use3dCheckbox.setEnabled(enable);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hightlightCheckBox = new javax.swing.JCheckBox();
        autoSelectNeigborCheckbox = new javax.swing.JCheckBox();
        use3dCheckbox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton = new javax.swing.JButton();
        labelZoom = new javax.swing.JLabel();

        hightlightCheckBox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.hightlightCheckBox.text")); // NOI18N
        hightlightCheckBox.setBorder(null);
        hightlightCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        hightlightCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        autoSelectNeigborCheckbox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.autoSelectNeigborCheckbox.text")); // NOI18N
        autoSelectNeigborCheckbox.setBorder(null);
        autoSelectNeigborCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        autoSelectNeigborCheckbox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        use3dCheckbox.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.use3dCheckbox.text")); // NOI18N
        use3dCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 2));
        jToolBar1.setFloatable(false);
        jToolBar1.setOpaque(false);

        zoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/zoomOut.png"))); // NOI18N
        zoomOutButton.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.zoomOutButton.text")); // NOI18N
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomOutButton);

        zoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/visualization/components/zoomIn.png"))); // NOI18N
        zoomInButton.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.zoomInButton.text")); // NOI18N
        zoomInButton.setFocusable(false);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomInButton);

        labelZoom.setText(org.openide.util.NbBundle.getMessage(GlobalSettingsPanel.class, "GlobalSettingsPanel.labelZoom.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(labelZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelZoom, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autoSelectNeigborCheckbox)
                    .addComponent(hightlightCheckBox))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(use3dCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(430, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hightlightCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(use3dCheckbox)
                    .addComponent(autoSelectNeigborCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoSelectNeigborCheckbox;
    private javax.swing.JCheckBox hightlightCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelZoom;
    private javax.swing.JCheckBox use3dCheckbox;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables
}
