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
package org.gephi.ui.complexgenerator.plugin;

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
public class KleinbergPanel extends javax.swing.JPanel {

    /** Creates new form BarabasiAlbertPanel */
    public KleinbergPanel() {
        initComponents();
    }

	public static ValidationPanel createValidationPanel(KleinbergPanel innerPanel) {
		ValidationPanel validationPanel = new ValidationPanel();
		if (innerPanel == null)
			innerPanel = new KleinbergPanel();
		validationPanel.setInnerComponent(innerPanel);

		ValidationGroup group = validationPanel.getValidationGroup();

		group.add(innerPanel.nField, Validators.REQUIRE_NON_EMPTY_STRING,
				new nValidator(innerPanel));
		group.add(innerPanel.pField, Validators.REQUIRE_NON_EMPTY_STRING,
				new pValidator(innerPanel));
		group.add(innerPanel.qField, Validators.REQUIRE_NON_EMPTY_STRING,
				new qValidator(innerPanel));
		group.add(innerPanel.rField, Validators.REQUIRE_NON_EMPTY_STRING,
				new rValidator(innerPanel));

		return validationPanel;
	}

	private static class nValidator implements Validator<String> {
		private KleinbergPanel innerPanel;

		public nValidator(KleinbergPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer n = Integer.parseInt(innerPanel.nField.getText());
				result = n >= 2;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>n &gt;= 2</html>";
				problems.add(message);
			}

			return result;
		}
    }

	private static class pValidator implements Validator<String> {
		private KleinbergPanel innerPanel;

		public pValidator(KleinbergPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer n = Integer.parseInt(innerPanel.nField.getText());
				Integer p = Integer.parseInt(innerPanel.pField.getText());
				result = p >= 1 && p <= 2 * n - 2;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>1 &lt;= p &lt;= 2n - 2</html>";
				problems.add(message);
			}

			return result;
		}
    }

	private static class qValidator implements Validator<String> {
		private KleinbergPanel innerPanel;

		public qValidator(KleinbergPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer n = Integer.parseInt(innerPanel.nField.getText());
				Integer p = Integer.parseInt(innerPanel.pField.getText());
				Integer q = Integer.parseInt(innerPanel.qField.getText());
				if (p < n)
					result = q >= 0 && q <= n * n - p * (p + 3) / 2 - 1;
				else result = q >= 0 && q <= (2 * n - p - 3) * (2 * n - p) / 2 + 1;
			}
			catch (Exception e) { }
			if (!result) {
				String message =
					"<html>q &gt;= 0<br>" +
					"q &lt;= n^2 - p * (p + 3) / 2 - 1 for p &lt; n<br>" +
					"q &lt;= (2n - p - 3) * (2n - p) / 2 + 1 for p &gt;= n<br></html>";
				problems.add(message);
			}

			return result;
		}
    }

	private static class rValidator implements Validator<String> {
		private KleinbergPanel innerPanel;

		public rValidator(KleinbergPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer r = Integer.parseInt(innerPanel.rField.getText());
				result = r >= 0;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>r &gt;= 0</html>";
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

        qLabel = new javax.swing.JLabel();
        qField = new javax.swing.JTextField();
        nField = new javax.swing.JTextField();
        pField = new javax.swing.JTextField();
        nLabel = new javax.swing.JLabel();
        pLabel = new javax.swing.JLabel();
        constraintsLabel = new javax.swing.JLabel();
        rLabel = new javax.swing.JLabel();
        rField = new javax.swing.JTextField();
        torusCheckBox = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(399, 247));

        qLabel.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.qLabel.text")); // NOI18N

        qField.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.qField.text")); // NOI18N

        nField.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.nField.text")); // NOI18N

        pField.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.pField.text")); // NOI18N

        nLabel.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.nLabel.text")); // NOI18N

        pLabel.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.pLabel.text")); // NOI18N

        constraintsLabel.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.constraintsLabel.text")); // NOI18N

        rLabel.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.rLabel.text")); // NOI18N

        rField.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.rField.text")); // NOI18N

        torusCheckBox.setText(org.openide.util.NbBundle.getMessage(KleinbergPanel.class, "KleinbergPanel.torusCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nLabel)
                            .addComponent(pLabel)
                            .addComponent(qLabel)
                            .addComponent(rLabel))
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rField, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(pField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(qField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                            .addComponent(nField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(torusCheckBox)
                        .addContainerGap(306, Short.MAX_VALUE))))
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
                    .addComponent(pField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(qLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(torusCheckBox)
                .addGap(4, 4, 4)
                .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel constraintsLabel;
    protected javax.swing.JTextField nField;
    private javax.swing.JLabel nLabel;
    protected javax.swing.JTextField pField;
    private javax.swing.JLabel pLabel;
    protected javax.swing.JTextField qField;
    private javax.swing.JLabel qLabel;
    protected javax.swing.JTextField rField;
    private javax.swing.JLabel rLabel;
    protected javax.swing.JCheckBox torusCheckBox;
    // End of variables declaration//GEN-END:variables

}