/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 *
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ui.generator.plugin;

import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class WattsStrogatzAlphaPanel extends javax.swing.JPanel {

    /** Creates new form BarabasiAlbertPanel */
    public WattsStrogatzAlphaPanel() {
        initComponents();
    }

	public static ValidationPanel createValidationPanel(WattsStrogatzAlphaPanel innerPanel) {
		ValidationPanel validationPanel = new ValidationPanel();
		if (innerPanel == null)
			innerPanel = new WattsStrogatzAlphaPanel();
		validationPanel.setInnerComponent(innerPanel);

		ValidationGroup group = validationPanel.getValidationGroup();

		group.add(innerPanel.nField, Validators.REQUIRE_NON_EMPTY_STRING,
				new nkValidator(innerPanel));
		group.add(innerPanel.kField, Validators.REQUIRE_NON_EMPTY_STRING,
				new nkValidator(innerPanel));
		group.add(innerPanel.alphaField, Validators.REQUIRE_NON_EMPTY_STRING,
				new alphaValidator(innerPanel));

		return validationPanel;
	}

	private static class nkValidator implements Validator<String> {
		private WattsStrogatzAlphaPanel innerPanel;

		public nkValidator(WattsStrogatzAlphaPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer n = Integer.parseInt(innerPanel.nField.getText());
				Integer k = Integer.parseInt(innerPanel.kField.getText());
				result = n > k && k > 0;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>n &gt; k &gt; 0</html>";
				problems.add(message);
			}

			return result;
		}
    }

	private static class alphaValidator implements Validator<String> {
		private WattsStrogatzAlphaPanel innerPanel;

		public alphaValidator(WattsStrogatzAlphaPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Double alpha = Double.parseDouble(innerPanel.alphaField.getText());
				result = alpha >= 0;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>alpha &gt;= 0</html>";
				problems.add(message);
			}

			return result;
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

        alphaLabel = new javax.swing.JLabel();
        alphaField = new javax.swing.JTextField();
        nField = new javax.swing.JTextField();
        kField = new javax.swing.JTextField();
        nLabel = new javax.swing.JLabel();
        kLabel = new javax.swing.JLabel();
        constraintsLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(364, 128));

        alphaLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.alphaLabel.text")); // NOI18N

        alphaField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.alphaField.text")); // NOI18N

        nField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.nField.text")); // NOI18N

        kField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.kField.text")); // NOI18N

        nLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.nLabel.text")); // NOI18N

        kLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.kLabel.text")); // NOI18N

        constraintsLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzAlphaPanel.class, "WattsStrogatzAlphaPanel.constraintsLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nLabel)
                            .addComponent(kLabel)
                            .addComponent(alphaLabel))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(kField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(alphaField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(nField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alphaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alphaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField alphaField;
    private javax.swing.JLabel alphaLabel;
    private javax.swing.JLabel constraintsLabel;
    protected javax.swing.JTextField kField;
    private javax.swing.JLabel kLabel;
    protected javax.swing.JTextField nField;
    private javax.swing.JLabel nLabel;
    // End of variables declaration//GEN-END:variables

}
