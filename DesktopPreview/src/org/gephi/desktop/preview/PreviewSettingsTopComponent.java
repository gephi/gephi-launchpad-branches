/*
Copyright 2008-2010 Gephi
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

package org.gephi.desktop.preview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.desktop.io.export.api.VectorialFileExporterUI;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewPreset;
import org.gephi.project.api.ProjectController;
import org.gephi.ui.utils.UIUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class PreviewSettingsTopComponent extends TopComponent {

    private static PreviewSettingsTopComponent instance;
    static final String ICON_PATH = "org/gephi/desktop/preview/resources/settings.png";
    private static final String PREFERRED_ID = "PreviewSettingsTopComponent";
    private final String NO_SELECTION = "---";
    //Component
    private PropertySheet propertySheet;
    //State
    private int defaultPresetLimit;

    private PreviewSettingsTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PreviewSettingsTopComponent.class, "CTL_PreviewSettingsTopComponent"));
//        setToolTipText(NbBundle.getMessage(PreviewSettingsTopComponent.class, "HINT_PreviewSettingsTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH));
        if (UIUtils.isAquaLookAndFeel()) {
            mainPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
        }

        // property sheet
        propertySheet = new PropertySheet();
        propertySheet.setNodes(new Node[]{new PreviewNode()});
        propertySheet.setDescriptionAreaVisible(false);
        propertiesPanel.add(propertySheet, BorderLayout.CENTER);

        // checks the state of the workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc.getCurrentWorkspace() != null) {
            enableRefreshButton();
        }

        // forces the controller instanciation
        PreviewUIController.findInstance();

        //Ratio
        ratioSlider.addChangeListener(new ChangeListener() {

            NumberFormat formatter = NumberFormat.getPercentInstance();

            public void stateChanged(ChangeEvent e) {
                float val = ratioSlider.getValue() / 100f;
                if (val == 0f) {
                    ratioLabel.setText(NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.ratio.minimum"));
                } else {
                    ratioLabel.setText(formatter.format(val));
                }
            }
        });

        //Presets
        presetComboBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                PreviewController pc = Lookup.getDefault().lookup(PreviewController.class);
                PreviewModel previewModel = pc.getModel();
                if (previewModel != null && presetComboBox.getSelectedItem() instanceof PreviewPreset) {
                    if (previewModel.getCurrentPreset() != presetComboBox.getSelectedItem()) {
                        pc.setCurrentPreset((PreviewPreset) presetComboBox.getSelectedItem());
                        propertySheet.setNodes(new Node[]{new PreviewNode()});
                    }
                }
            }
        });

        //Export
        svgExportButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                VectorialFileExporterUI ui = Lookup.getDefault().lookup(VectorialFileExporterUI.class);
                ui.action();
            }
        });
        refreshModel();
    }

    public void refreshModel() {
        propertySheet.setNodes(new Node[]{new PreviewNode()});
        PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
        if (previewModel != null) {
            ratioSlider.setValue((int) (previewModel.getVisibilityRatio() * 100));
        }

        //Presets
        if (previewModel == null) {
            saveButton.setEnabled(false);
            labelPreset.setEnabled(false);
            presetComboBox.setEnabled(false);
            presetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"---"}));
        } else {
            saveButton.setEnabled(true);
            labelPreset.setEnabled(true);
            presetComboBox.setEnabled(true);
            PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
            DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
            defaultPresetLimit = 0;
            for (PreviewPreset preset : controller.getDefaultPresets()) {
                comboBoxModel.addElement(preset);
                defaultPresetLimit++;
            }
            PreviewPreset[] userPresets = controller.getUserPresets();
            if (userPresets.length > 0) {
                comboBoxModel.addElement(NO_SELECTION);
                for (PreviewPreset preset : userPresets) {
                    comboBoxModel.addElement(preset);
                }
            }
            presetComboBox.setSelectedItem(previewModel.getCurrentPreset());
            presetComboBox.setModel(comboBoxModel);
        }
    }

    /**
     * Returns the graph visibility ratio set in the visibilityRatioSpinner
     * component.
     *
     * @return the graph visibility ratio
     */
    public float getVisibilityRatio() {
        float value = (Integer) ratioSlider.getValue();

        if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }

        return value / 100;
    }

    /**
     * Enables the refresh button.
     *
     * @see PreviewUIController#enableRefresh()
     */
    public void enableRefreshButton() {
        refreshButton.setEnabled(true);
        labelRatio.setEnabled(true);
        ratioLabel.setEnabled(true);
        ratioSlider.setEnabled(true);
        labelExport.setEnabled(true);
        svgExportButton.setEnabled(true);
    }

    /**
     * Disables the refresh button.
     *
     * @see PreviewUIController#disableRefresh()
     */
    public void disableRefreshButton() {
        refreshButton.setEnabled(false);
        labelRatio.setEnabled(false);
        ratioLabel.setEnabled(false);
        ratioSlider.setEnabled(false);
        labelExport.setEnabled(false);
        svgExportButton.setEnabled(false);
    }

    private boolean isDefaultPreset(PreviewPreset preset) {
        int i = 0;
        for (i = 0; i < presetComboBox.getItemCount(); i++) {
            if (presetComboBox.getModel().getElementAt(i).equals(preset)) {
                break;
            }
        }
        return i < defaultPresetLimit;
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
        presetPanel = new javax.swing.JPanel();
        presetComboBox = new javax.swing.JComboBox();
        presetToolbar = new javax.swing.JToolBar();
        box = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        labelPreset = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        propertiesPanel = new javax.swing.JPanel();
        labelRatio = new javax.swing.JLabel();
        ratioLabel = new javax.swing.JLabel();
        ratioSlider = new javax.swing.JSlider();
        southToolbar = new javax.swing.JToolBar();
        labelExport = new javax.swing.JLabel();
        svgExportButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        presetPanel.setOpaque(false);
        presetPanel.setLayout(new java.awt.GridBagLayout());

        presetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "---" }));
        presetComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        presetPanel.add(presetComboBox, gridBagConstraints);

        presetToolbar.setBorder(null);
        presetToolbar.setFloatable(false);
        presetToolbar.setRollover(true);
        presetToolbar.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(box, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.box.text")); // NOI18N
        box.setMaximumSize(new java.awt.Dimension(32767, 32767));
        presetToolbar.add(box);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/preview/resources/save.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(saveButton, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.saveButton.text")); // NOI18N
        saveButton.setToolTipText(org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.saveButton.toolTipText")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        presetToolbar.add(saveButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 5);
        presetPanel.add(presetToolbar, gridBagConstraints);

        labelPreset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/preview/resources/preset.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelPreset, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.labelPreset.text")); // NOI18N
        labelPreset.setEnabled(false);
        labelPreset.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        presetPanel.add(labelPreset, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainPanel.add(presetPanel, gridBagConstraints);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/preview/resources/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.refreshButton.text")); // NOI18N
        refreshButton.setEnabled(false);
        refreshButton.setMargin(new java.awt.Insets(10, 14, 10, 14));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 0, 10);
        mainPanel.add(refreshButton, gridBagConstraints);

        propertiesPanel.setOpaque(false);
        propertiesPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(propertiesPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(labelRatio, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.labelRatio.text")); // NOI18N
        labelRatio.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 3, 5);
        mainPanel.add(labelRatio, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(ratioLabel, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.ratioLabel.text")); // NOI18N
        ratioLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 0);
        mainPanel.add(ratioLabel, gridBagConstraints);

        ratioSlider.setEnabled(false);
        ratioSlider.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 5, 20);
        mainPanel.add(ratioSlider, gridBagConstraints);

        southToolbar.setFloatable(false);
        southToolbar.setRollover(true);
        southToolbar.setOpaque(false);

        labelExport.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelExport, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.labelExport.text")); // NOI18N
        labelExport.setEnabled(false);
        southToolbar.add(labelExport);

        svgExportButton.setFont(svgExportButton.getFont().deriveFont(svgExportButton.getFont().getSize()-1f));
        org.openide.awt.Mnemonics.setLocalizedText(svgExportButton, org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.svgExportButton.text")); // NOI18N
        svgExportButton.setToolTipText(org.openide.util.NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.svgExportButton.toolTipText")); // NOI18N
        svgExportButton.setEnabled(false);
        svgExportButton.setFocusable(false);
        svgExportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        svgExportButton.setMargin(new java.awt.Insets(2, 8, 2, 8));
        svgExportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        southToolbar.add(svgExportButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        mainPanel.add(southToolbar, gridBagConstraints);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        PreviewUIController.findInstance().refreshPreview();
}//GEN-LAST:event_refreshButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewPreset preset = previewController.getModel().getCurrentPreset();
        boolean saved = false;
        if (isDefaultPreset(preset)) {
            NotifyDescriptor.InputLine question = new NotifyDescriptor.InputLine(
                    NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.savePreset.input"),
                    NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.savePreset.input.title"));
            if (DialogDisplayer.getDefault().notify(question) == NotifyDescriptor.OK_OPTION) {
                String input = question.getInputText();
                if (input != null && !input.isEmpty()) {
                    previewController.savePreset(input);
                    saved = true;
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.savePreset.status", input));
                }
            }
        } else {
            previewController.savePreset(preset.getName());
            saved = true;
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PreviewSettingsTopComponent.class, "PreviewSettingsTopComponent.savePreset.status", preset.getName()));
        }

        if (saved) {
            //refresh combo
            PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
            DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
            defaultPresetLimit = 0;
            for (PreviewPreset p : controller.getDefaultPresets()) {
                comboBoxModel.addElement(p);
                defaultPresetLimit++;
            }
            PreviewPreset[] userPresets = controller.getUserPresets();
            if (userPresets.length > 0) {
                comboBoxModel.addElement(NO_SELECTION);
                for (PreviewPreset p : userPresets) {
                    comboBoxModel.addElement(p);
                }
            }
            comboBoxModel.setSelectedItem(previewController.getModel().getCurrentPreset());
            presetComboBox.setModel(comboBoxModel);
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel box;
    private javax.swing.JLabel labelExport;
    private javax.swing.JLabel labelPreset;
    private javax.swing.JLabel labelRatio;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox presetComboBox;
    private javax.swing.JPanel presetPanel;
    private javax.swing.JToolBar presetToolbar;
    private javax.swing.JPanel propertiesPanel;
    private javax.swing.JLabel ratioLabel;
    private javax.swing.JSlider ratioSlider;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JToolBar southToolbar;
    private javax.swing.JButton svgExportButton;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized PreviewSettingsTopComponent getDefault() {
        if (instance == null) {
            instance = new PreviewSettingsTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PreviewSettingsTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PreviewSettingsTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PreviewSettingsTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PreviewSettingsTopComponent) {
            return (PreviewSettingsTopComponent) win;
        }
        Logger.getLogger(PreviewSettingsTopComponent.class.getName()).warning(
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
            return PreviewSettingsTopComponent.getDefault();
        }
    }
}
