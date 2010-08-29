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
package org.gephi.desktop.hierarchy;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphSettings;
import org.gephi.graph.api.HierarchicalGraph;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Mathieu Bastian
 */
public class HierarchyControlPanel extends javax.swing.JPanel {

    public HierarchyControlPanel() {
        initComponents();
        initEvents();
        showTreeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void initEvents() {
        autoMetaEdgeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
                model.settings().putClientProperty(GraphSettings.AUTO_META_EDGES, autoMetaEdgeButton.isSelected());
            }
        });

        showTreeLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                TopComponent tc = WindowManager.getDefault().findTopComponent("HierarchyTopComponent");
                if (tc != null) {
                    tc.open();
                    tc.requestActive();
                    HierarchyTopComponent hierarchyTopComponent = (HierarchyTopComponent) tc;
                    hierarchyTopComponent.refresh();
                }
            }
        });

        metaEdgesSettings.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
                GraphSettings settings = model.settings();
                MetaEdgesSettingsPanel pnl = new MetaEdgesSettingsPanel();
                pnl.setAutoEdgesEnabled((Boolean) settings.getClientProperty(GraphSettings.AUTO_META_EDGES));
                pnl.setSumBuilding(settings.getClientProperty(GraphSettings.METAEDGE_BUILDER).equals("sum"));
                pnl.setMinimum((Float) settings.getClientProperty(GraphSettings.METAEDGE_BUILDER_MIN));
                pnl.setMaximum((Float) settings.getClientProperty(GraphSettings.METAEDGE_BUILDER_MAX));
                DialogDescriptor dd = new DialogDescriptor(pnl, NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.metaEdgesSettings.title"));
                if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
                    settings.putClientProperty(GraphSettings.AUTO_META_EDGES, pnl.isAutoEdgesEnabled());
                    settings.putClientProperty(GraphSettings.METAEDGE_BUILDER, pnl.isSumBuilding() ? "sum" : "average");
                    settings.putClientProperty(GraphSettings.METAEDGE_BUILDER_MIN, pnl.getMinimum());
                    settings.putClientProperty(GraphSettings.METAEDGE_BUILDER_MAX, pnl.getMaximum());
                }
            }
        });
    }

    public void setup() {
        GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
        HierarchicalGraph graph = model.getHierarchicalGraphVisible();
        initLevelsLinks(graph);

        //Init status
        GraphSettings settings = model.settings();
        autoMetaEdgeButton.setSelected((Boolean) settings.getClientProperty(GraphSettings.AUTO_META_EDGES));

        //Stats
        heightLabel.setText("" + (graph.getHeight() + 1));
    }

    private void initLevelsLinks(HierarchicalGraph graph) {
        int height = graph.getHeight();
        levelViewPanel.setLayout(new GridLayout(height + 2, 1));
        levelViewPanel.removeAll();
        String levelStr = NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.linkLevel");
        String nodesStr = NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.linkLevel.nodes");
        String leavesStr = NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.linkLevel.leaves");

        //Level links
        for (int i = 0; i < height + 1; i++) {
            graph.readLock();
            int levelSize = graph.getLevelSize(i);
            graph.readUnlock();
            JXHyperlink link = new JXHyperlink();
            link.setClickedColor(new java.awt.Color(0, 51, 255));
            link.setText(levelStr + " " + i + " (" + levelSize + " " + nodesStr + ")");
            link.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
            final int lvl = i;
            link.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
                    HierarchicalGraph graph = model.getHierarchicalGraphVisible();
                    graph.resetViewToLevel(lvl);
                }
            });
            levelViewPanel.add(link);
        }

        //Leaves
        JXHyperlink link = new JXHyperlink();
        link.setClickedColor(new java.awt.Color(0, 51, 255));
        link.setText(leavesStr);
        link.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        link.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphModel model = Lookup.getDefault().lookup(GraphController.class).getModel();
                HierarchicalGraph graph = model.getHierarchicalGraphVisible();
                graph.resetViewToLeaves();
            }
        });
        levelViewPanel.add(link);
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

        showTreeLabel = new javax.swing.JLabel();
        labelHeight = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jToolBar4 = new javax.swing.JToolBar();
        autoMetaEdgeButton = new javax.swing.JToggleButton();
        metaEdgesSettings = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        labelView = new javax.swing.JLabel();
        levelViewPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(214, 300));

        showTreeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/hierarchy/resources/tree.png"))); // NOI18N
        showTreeLabel.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.showTreeLabel.text")); // NOI18N
        showTreeLabel.setToolTipText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.showTreeLabel.toolTipText")); // NOI18N
        showTreeLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelHeight.setFont(new java.awt.Font("Tahoma", 1, 11));
        labelHeight.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.labelHeight.text")); // NOI18N

        heightLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        heightLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        heightLabel.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.heightLabel.text")); // NOI18N

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        autoMetaEdgeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/hierarchy/resources/on.png"))); // NOI18N
        autoMetaEdgeButton.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.autoMetaEdgeButton.text")); // NOI18N
        autoMetaEdgeButton.setFocusable(false);
        autoMetaEdgeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(autoMetaEdgeButton);

        metaEdgesSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/hierarchy/resources/gear.png"))); // NOI18N
        metaEdgesSettings.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.metaEdgesSettings.text")); // NOI18N
        metaEdgesSettings.setFocusable(false);
        metaEdgesSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        metaEdgesSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(metaEdgesSettings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jToolBar4, gridBagConstraints);

        labelView.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelView.setText(org.openide.util.NbBundle.getMessage(HierarchyControlPanel.class, "HierarchyControlPanel.labelView.text")); // NOI18N

        levelViewPanel.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(showTreeLabel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHeight)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(heightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelView)
                .addContainerGap(179, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(levelViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(showTreeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHeight)
                    .addComponent(heightLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelViewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton autoMetaEdgeButton;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JLabel labelHeight;
    private javax.swing.JLabel labelView;
    private javax.swing.JPanel levelViewPanel;
    private javax.swing.JButton metaEdgesSettings;
    private javax.swing.JLabel showTreeLabel;
    // End of variables declaration//GEN-END:variables
}
