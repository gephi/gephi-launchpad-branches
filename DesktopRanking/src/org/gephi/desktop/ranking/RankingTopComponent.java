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
package org.gephi.desktop.ranking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import org.gephi.project.api.ProjectController;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.RankingModel;
import org.gephi.ranking.api.RankingUIModel;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.ui.utils.UIUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

final class RankingTopComponent extends TopComponent implements Lookup.Provider {

    private static RankingTopComponent instance;
    static final String ICON_PATH = "org/gephi/desktop/ranking/resources/small.png";
    private static final String PREFERRED_ID = "RankingTopComponent";
    //UI
    private JToggleButton listButton;
    //Model
    private RankingUIModel model;
    private RankingModel rankingModel;
    private boolean enabled = false;

    private RankingTopComponent() {
        setName(NbBundle.getMessage(RankingTopComponent.class, "CTL_RankingTopComponent"));
//        setToolTipText(NbBundle.getMessage(RankingTopComponent.class, "HINT_RankingTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH));

        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
        rankingModel = rankingController.getRankingModel();

        initEvents();
        initComponents();
        initSouth();
        if (UIUtils.isAquaLookAndFeel()) {
            mainPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
        }
        refreshModel();

        final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc.getCurrentWorkspace() != null) {
            ((RankingChooser) rankingChooser).refreshModel();
        }
    }

    private void initSouth() {
        listButton = new JToggleButton();
        listButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/ranking/resources/list.png"))); // NOI18N
        NbBundle.getMessage(RankingTopComponent.class, "RankingTopComponent.listButton.text");
        listButton.setEnabled(false);
        listButton.setFocusable(false);
        southToolbar.add(listButton);
        /*barChartButton = new JToggleButton();
        barChartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/ranking/resources/barchart.png"))); // NOI18N
        NbBundle.getMessage(RankingTopComponent.class, "RankingTopComponent.barchartButton.text");
        barChartButton.setEnabled(false);
        barChartButton.setFocusable(false);
        southToolbar.add(barChartButton);*/

        //BarChartPanel & ListPanel
        listResultPanel.setVisible(false);

        listButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                model.setListVisible(listButton.isSelected());
                refreshModel();
            }
        });

        /*barChartButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        model.setBarChartVisible(barChartButton.isSelected());
        refreshModel();
        }
        });*/
    }

    private void initEvents() {
        model = new RankingUIModel();

        final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
                workspace.add(new RankingUIModel());
            }

            public void select(Workspace workspace) {
                //Enable
                enabled = true;
                RankingUIModel newModel = workspace.getLookup().lookup(RankingUIModel.class);
                if (newModel != null) {
                    model.loadModel(newModel);
                }
                refreshModel();
            }

            public void unselect(Workspace workspace) {
                RankingUIModel oldModel = workspace.getLookup().lookup(RankingUIModel.class);
                if (oldModel != null) {
                    workspace.remove(oldModel);
                }
                workspace.add(model.saveModel());
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
                enabled = false;
                refreshModel();
            }
        });

        if (pc.getCurrentWorkspace() != null) {
            enabled = true;
            RankingUIModel newModel = pc.getCurrentWorkspace().getLookup().lookup(RankingUIModel.class);
            if (newModel != null) {
                model.loadModel(newModel);
            }
        }
    }

    private void refreshModel() {
        //South visible
        /*if (barChartPanel.isVisible() != model.isBarChartVisible()) {
        barChartPanel.setVisible(model.isBarChartVisible());
        revalidate();
        repaint();
        }*/
        if (listResultPanel.isVisible() != model.isListVisible()) {
            listResultPanel.setVisible(model.isListVisible());
            revalidate();
            repaint();
        }

        //Enabled
        //barChartButton.setEnabled(enabled);
        listButton.setEnabled(enabled);
        rankingChooser.setEnabled(enabled);
        rankingToolbar.setEnabled(enabled);
        listResultPanel.setEnabled(enabled);

        //barChartButton.setSelected(model.isBarChartVisible());
        listButton.setSelected(model.isListVisible());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        rankingToolbar = new RankingToolbar(model);
        rankingChooser = new RankingChooser(model, rankingModel);
        listResultContainerPanel = new javax.swing.JPanel();
        listResultPanel = new ResultListPanel();
        southToolbar = new javax.swing.JToolBar();

        setOpaque(true);
        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        rankingToolbar.setFloatable(false);
        rankingToolbar.setRollover(true);
        rankingToolbar.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(rankingToolbar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(rankingChooser, gridBagConstraints);

        listResultContainerPanel.setOpaque(false);
        listResultContainerPanel.setLayout(new java.awt.GridLayout(1, 0));

        listResultPanel.setBorder(null);
        listResultContainerPanel.add(listResultPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 1, 5);
        mainPanel.add(listResultContainerPanel, gridBagConstraints);

        southToolbar.setFloatable(false);
        southToolbar.setRollover(true);
        southToolbar.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(southToolbar, gridBagConstraints);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel listResultContainerPanel;
    private javax.swing.JScrollPane listResultPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel rankingChooser;
    private javax.swing.JToolBar rankingToolbar;
    private javax.swing.JToolBar southToolbar;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized RankingTopComponent getDefault() {
        if (instance == null) {
            instance = new RankingTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the RankingTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized RankingTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(RankingTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof RankingTopComponent) {
            return (RankingTopComponent) win;
        }
        Logger.getLogger(RankingTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return RankingTopComponent.getDefault();
        }
    }
}
