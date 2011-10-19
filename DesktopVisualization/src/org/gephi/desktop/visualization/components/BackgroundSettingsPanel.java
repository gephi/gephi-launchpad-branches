/*
Copyright 2008-2010 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import org.gephi.math.Vec2;
import org.gephi.ui.components.JColorButton;
import org.gephi.visualization.api.VisualizationController;
import org.gephi.visualization.api.rendering.background.Background;
import org.gephi.visualization.api.rendering.background.BackgroundAttachment;
import org.gephi.visualization.api.rendering.background.BackgroundPosition;
import org.gephi.visualization.api.rendering.background.BackgroundRepeat;
import org.gephi.visualization.api.rendering.background.BackgroundSize;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * @author Vojtech Bardiovsky
 */
public class BackgroundSettingsPanel extends javax.swing.JPanel {

    private File selectedFile;
    
    /** Creates new form BackgroundPanel */
    public BackgroundSettingsPanel() {
        initComponents();
    }

    public void setup() {
        VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
        vizModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("init")) {
                    refreshSharedConfig();
                } else if (evt.getPropertyName().equals(VizConfig.BACKGROUND)) {
                    refreshSharedConfig();
                }
            }
        });

        ((JColorButton) colorButton).addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!vizModel.getBackground().getColor().equals(((JColorButton) colorButton).getColor())) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        fileBrowseButton.addActionListener(new ActionListener() {
            private final String LAST_PATH = "BackgroundSettingsPanel.lastPath";
            @Override
            public void actionPerformed(ActionEvent e) {
                final String path = NbPreferences.forModule(BackgroundSettingsPanel.class).get(LAST_PATH, null);
                JFileChooser fileChooser = new JFileChooser(path);
                if (fileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    NbPreferences.forModule(BackgroundSettingsPanel.class).put(LAST_PATH, selectedFile.getAbsolutePath());
                    VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                    if (!selectedFile.getAbsolutePath().equals(vizModel.getBackground().image)) {
                        vizModel.setBackground(extractBackground());
                    }
                }
            }
        });
        
        ComboBoxModel repeatComboBoxModel = new DefaultComboBoxModel(BackgroundRepeat.values());
        repeatComboBox.setModel(repeatComboBoxModel);
        repeatComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!vizModel.getBackground().repeat.equals((BackgroundRepeat) repeatComboBox.getSelectedItem())) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        ComboBoxModel attachmentComboBoxModel = new DefaultComboBoxModel(BackgroundAttachment.values());
        attachmentComboBox.setModel(attachmentComboBoxModel);
        attachmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!vizModel.getBackground().attachment.equals((BackgroundAttachment) attachmentComboBox.getSelectedItem())) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        ComboBoxModel positionComboBoxModel = new DefaultComboBoxModel(BackgroundPosition.Mode.values());
        positionComboBox.setModel(positionComboBoxModel);
        positionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!vizModel.getBackground().position.mode.equals((BackgroundPosition.Mode) positionComboBox.getSelectedItem())) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        positionTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!((VectorTextField) positionTextField).getVector().equals(vizModel.getBackground().position.parameter)) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        positionSetVectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VectorInputPanel panel = new VectorInputPanel();
                Vec2 vector = ((VectorTextField) positionTextField).getVector();
                panel.setup((int) vector.x(), (int) vector.y());
                DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(VizBarController.class, "VectorInputPanel.label"), true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    ((VectorTextField) positionTextField).setup(panel.unsetup());
                    return;
                }
            }
        });
        
        ComboBoxModel sizeComboBoxModel = new DefaultComboBoxModel(BackgroundSize.Mode.values());
        sizeComboBox.setModel(sizeComboBoxModel);
        sizeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!vizModel.getBackground().size.mode.equals((BackgroundSize.Mode) sizeComboBox.getSelectedItem())) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });

        sizeTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
                if (!((VectorTextField) sizeTextField).getVector().equals(vizModel.getBackground().size.parameter)) {
                    vizModel.setBackground(extractBackground());
                }
            }
        });
        
        sizeSetVectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VectorInputPanel panel = new VectorInputPanel();
                Vec2 vector = ((VectorTextField) sizeTextField).getVector();
                panel.setup((int) vector.x(), (int) vector.y());
                DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(VizBarController.class, "VectorInputPanel.label"), true, NotifyDescriptor.OK_CANCEL_OPTION, null, null);
                if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    ((VectorTextField) sizeTextField).setup(panel.unsetup());
                    return;
                }
            }
        });
        
        refreshSharedConfig();
    }

    private Background extractBackground() {
        return new Background(((JColorButton) colorButton).getColor(), 
                              selectedFile == null ? null : selectedFile.getAbsolutePath(), 
                              new BackgroundPosition((BackgroundPosition.Mode) positionComboBox.getSelectedItem(), 
                                                    ((VectorTextField) positionTextField).getVector()), 
                              new BackgroundSize((BackgroundSize.Mode) sizeComboBox.getSelectedItem(), 
                                                ((VectorTextField) sizeTextField).getVector()),
                              (BackgroundRepeat) repeatComboBox.getSelectedItem(), 
                              (BackgroundAttachment) attachmentComboBox.getSelectedItem());
    }
    
    private void refreshSharedConfig() {
        VizModel vizModel = Lookup.getDefault().lookup(VisualizationController.class).getVizModel();
        setEnable(!vizModel.isDefaultModel());
        if (vizModel.isDefaultModel()) {
            return;
        }
        if (!vizModel.getBackground().getColor().equals(((JColorButton) colorButton).getColor())) {
            ((JColorButton) colorButton).setColor(vizModel.getBackground().getColor());
        }
        if (selectedFile == null) {
            if (vizModel.getBackground().image != null) {
                selectedFile = new File(vizModel.getBackground().image);
            }
        } else {
            if (vizModel.getBackground().image == null) {
                selectedFile = null;
            } else if (selectedFile.getAbsolutePath().equals(vizModel.getBackground().image)) {
                selectedFile = new File(vizModel.getBackground().image);
            }
        }
        if (!vizModel.getBackground().repeat.equals((BackgroundRepeat) repeatComboBox.getSelectedItem())) {
            repeatComboBox.setSelectedItem(vizModel.getBackground().repeat);
        }
        if (!vizModel.getBackground().attachment.equals((BackgroundAttachment) attachmentComboBox.getSelectedItem())) {
            attachmentComboBox.setSelectedItem(vizModel.getBackground().attachment);
        }
        if (!vizModel.getBackground().position.mode.equals((BackgroundPosition.Mode) positionComboBox.getSelectedItem())) {
            positionComboBox.setSelectedItem(vizModel.getBackground().position.mode);
        }
        if (!((VectorTextField) positionTextField).getVector().equals(vizModel.getBackground().position.parameter)) {
            ((VectorTextField) positionTextField).setup(vizModel.getBackground().position.parameter);
        }
        if (!vizModel.getBackground().size.mode.equals((BackgroundSize.Mode) sizeComboBox.getSelectedItem())) {
            sizeComboBox.setSelectedItem(vizModel.getBackground().size.mode);
        }
        if (!((VectorTextField) sizeTextField).getVector().equals(vizModel.getBackground().size.parameter)) {
            ((VectorTextField) sizeTextField).setup(vizModel.getBackground().size.parameter);
        }
    }
    
    private void setEnable(boolean enable) {
        attachmentComboBox.setEnabled(enable);
        colorButton.setEnabled(enable);
        fileBrowseButton.setEnabled(enable);
        positionComboBox.setEnabled(enable);
        positionSetVectorButton.setEnabled(enable);
        positionTextField.setEnabled(enable);
        repeatComboBox.setEnabled(enable);
        sizeComboBox.setEnabled(enable);
        sizeSetVectorButton.setEnabled(enable);
        sizeTextField.setEnabled(enable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        colorButton = new JColorButton(Color.WHITE);
        fileBrowseButton = new javax.swing.JButton();
        repeatComboBox = new javax.swing.JComboBox();
        attachmentComboBox = new javax.swing.JComboBox();
        positionComboBox = new javax.swing.JComboBox();
        sizeComboBox = new javax.swing.JComboBox();
        positionTextField = new VectorTextField();
        sizeTextField = new VectorTextField();
        positionSetVectorButton = new javax.swing.JButton();
        sizeSetVectorButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.color")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.file")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.repeat")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.attachment")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.position")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.size")); // NOI18N

        colorButton.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.colorButton.text")); // NOI18N

        fileBrowseButton.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.fileBrowseButton.text")); // NOI18N

        repeatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        attachmentComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        positionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        sizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        positionTextField.setColumns(6);
        positionTextField.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.positionTextField.text")); // NOI18N

        sizeTextField.setColumns(6);
        sizeTextField.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.sizeTextField.text")); // NOI18N

        positionSetVectorButton.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.positionSetVectorButton.text")); // NOI18N

        sizeSetVectorButton.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.sizeSetVectorButton.text")); // NOI18N

        jLabel7.setText(org.openide.util.NbBundle.getMessage(BackgroundSettingsPanel.class, "BackgroundSettingsPanel.parameters")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(repeatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(positionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(positionSetVectorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sizeSetVectorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel7))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(attachmentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(colorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel1))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(positionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionSetVectorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(fileBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(repeatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(sizeSetVectorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox attachmentComboBox;
    private javax.swing.JButton colorButton;
    private javax.swing.JButton fileBrowseButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JComboBox positionComboBox;
    private javax.swing.JButton positionSetVectorButton;
    private javax.swing.JTextField positionTextField;
    private javax.swing.JComboBox repeatComboBox;
    private javax.swing.JComboBox sizeComboBox;
    private javax.swing.JButton sizeSetVectorButton;
    private javax.swing.JTextField sizeTextField;
    // End of variables declaration//GEN-END:variables
}

class VectorTextField extends JTextField {
    
    private float x, y;
    
    public VectorTextField() {
        refreshText();
        setEditable(false);
    }
    
    public void setup(Vec2 vector) {
        this.x = vector.x();
        this.y = vector.y();
        refreshText();
    }
    
    public Vec2 getVector() {
        return new Vec2(x, y);
    }

    private void refreshText() {
        setText("[" + x + ", " + y + "]");
        //firePropertyChange("text", null, "[" + x + ", " + y + "]");
    }
    
}