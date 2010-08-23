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
package org.gephi.visualization.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.gephi.ui.utils.UIUtils;

/**
 *
 * @author Mathieu Bastian
 */
public class CollapsePanel extends javax.swing.JPanel {

    private boolean extended;

    /** Creates new form CollapsePanel */
    public CollapsePanel() {
        initComponents();
        if (UIUtils.isAquaLookAndFeel()) {
            buttonPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
        }
    }

    public void init(JComponent topBar, final JComponent extendedPanel, boolean extended) {
        add(topBar, BorderLayout.CENTER);
        add(extendedPanel, BorderLayout.SOUTH);

        this.extended = extended;
        if (extended) {
            extendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowDown.png"))); // NOI18N
            extendButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowDown_rollover.png"))); // NOI18N

        } else {
            extendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowUp.png"))); // NOI18N
            extendButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowUp_rollover.png"))); // NOI18N
        }
        extendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                boolean ext = CollapsePanel.this.extended;
                ext = !ext;
                CollapsePanel.this.extended = ext;
                if (ext) {
                    extendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowDown.png"))); // NOI18N
                    extendButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowDown_rollover.png"))); // NOI18N
                } else {
                    extendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowUp.png"))); // NOI18N
                    extendButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/visualization/component/arrowUp_rollover.png"))); // NOI18N
                }
                extendedPanel.setVisible(ext);
                getParent().validate();
                getParent().repaint();
            }
        });
        if (!extended) {
            extendedPanel.setVisible(extended);
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

        buttonPanel = new javax.swing.JPanel();
        extendButton = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 3));

        extendButton.setText(org.openide.util.NbBundle.getMessage(CollapsePanel.class, "CollapsePanel.extendButton.text")); // NOI18N
        extendButton.setAlignmentY(0.0F);
        extendButton.setBorderPainted(false);
        extendButton.setContentAreaFilled(false);
        extendButton.setFocusable(false);
        buttonPanel.add(extendButton);

        add(buttonPanel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton extendButton;
    // End of variables declaration//GEN-END:variables
}
