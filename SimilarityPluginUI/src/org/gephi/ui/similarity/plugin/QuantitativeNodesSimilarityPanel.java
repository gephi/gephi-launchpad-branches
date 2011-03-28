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
package org.gephi.ui.similarity.plugin;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class QuantitativeNodesSimilarityPanel extends javax.swing.JPanel {
	private AttributeLine[] attrLines;

	private AttributeColumn[] columns;
	private boolean[] doNorm;
	private double[] lambdas;

    /** Creates new form QuantitativeNodesSimilarityPanel */
    public QuantitativeNodesSimilarityPanel() {
        initComponents();
    }

	public int getP() {
		return Integer.parseInt(pFormattedTextField.getText());
	}

	public void setP(int p) {
		pFormattedTextField.setText(p + "");
	}

	public AttributeColumn[] getColumns() {
		List<AttributeColumn> list = new ArrayList<AttributeColumn>();
		if (attrLines != null)
			for (AttributeLine l : attrLines)
				if (l.isSelected())
					list.add(l.getColumn());
		return list.toArray(new AttributeColumn[0]);
	}

	public void setColumns(AttributeColumn[] columns) {
		this.columns = columns;
		setAttrLines();
	}

	public boolean[] getDoNorm() {
		List<Boolean> list = new ArrayList<Boolean>();
		if (attrLines != null)
			for (AttributeLine l : attrLines)
				if (l.isSelected())
					list.add(l.getDoNorm());
		boolean[] table = new boolean[list.size()];
		for (int i = 0; i < list.size(); ++i)
			table[i] = list.get(i);
		return table;
	}

	public void setDoNorm(boolean[] doNorm) {
		this.doNorm = doNorm;
		setAttrLines();
	}

	public double[] getLambdas() {
		List<Double> list = new ArrayList<Double>();
		if (attrLines != null)
			for (AttributeLine l : attrLines)
				if (l.isSelected())
					list.add(l.getLambda());
		double[] table = new double[list.size()];
		for (int i = 0; i < list.size(); ++i)
			table[i] = list.get(i);
		return table;
	}

	public void setLambdas(double[] lambdas) {
		this.lambdas = lambdas;
		setAttrLines();
	}

	private void setAttrLines() {
		AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);

		List<AttributeColumn> availableColumns = new ArrayList<AttributeColumn>();
		List<AttributeColumn> selectedColumns = Arrays.asList(columns);
		for (AttributeColumn c : attributeController.getModel().getNodeTable().getColumns())
			if ((c.getOrigin().equals(AttributeOrigin.DATA) || c.getOrigin().equals(AttributeOrigin.COMPUTED)) &&
					(c.getType().equals(AttributeType.BIGDECIMAL) ||
					c.getType().equals(AttributeType.BIGINTEGER) ||
					c.getType().equals(AttributeType.BYTE) ||
					c.getType().equals(AttributeType.DOUBLE) ||
					c.getType().equals(AttributeType.FLOAT) ||
					c.getType().equals(AttributeType.INT) ||
					c.getType().equals(AttributeType.LONG) ||
					c.getType().equals(AttributeType.SHORT) ||
					c.getType().equals(AttributeType.LIST_BIGDECIMAL) ||
					c.getType().equals(AttributeType.LIST_BIGINTEGER) ||
					c.getType().equals(AttributeType.LIST_BYTE) ||
					c.getType().equals(AttributeType.LIST_DOUBLE) ||
					c.getType().equals(AttributeType.LIST_FLOAT) ||
					c.getType().equals(AttributeType.LIST_INTEGER) ||
					c.getType().equals(AttributeType.LIST_LONG) ||
					c.getType().equals(AttributeType.LIST_SHORT)))
				availableColumns.add(c);

		attrLines = new AttributeLine[availableColumns.size()];

		contentPanel.removeAll();
		contentPanel.setLayout(new GridBagLayout());
		for (int i = 0; i < availableColumns.size(); ++i) {
			AttributeColumn column = availableColumns.get(i);
			boolean selected = false;
			boolean doNorm = true;
			double lambda = 0.0;
			if (selectedColumns.contains(column)) {
				selected = true;
				int index = selectedColumns.indexOf(column);
				doNorm = this.doNorm[index];
				lambda = this.lambdas[index];
			}
			AttributeLine l = new AttributeLine(column, selected, doNorm, lambda);
			attrLines[i] = l;
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = i;
			c.gridx = 0;
			contentPanel.add(l.getCheckBox(), c);
			c.gridx = 1;
			contentPanel.add(l.getDoNormCB(), c);
			c.gridx = 2;
			contentPanel.add(l.getLabel(), c);
			c.gridx = 3;
			contentPanel.add(l.getLambdaCB(), c);
		}
		contentPanel.revalidate();
		contentPanel.repaint();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vf2Header = new org.jdesktop.swingx.JXHeader();
        columnsLabel = new javax.swing.JLabel();
        contentScrollPane = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        pFormattedTextField = new javax.swing.JFormattedTextField();
        pLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(458, 373));

        vf2Header.setDescription(org.openide.util.NbBundle.getMessage(QuantitativeNodesSimilarityPanel.class, "QuantitativeNodesSimilarityPanel.vf2Header.description")); // NOI18N
        vf2Header.setTitle(org.openide.util.NbBundle.getMessage(QuantitativeNodesSimilarityPanel.class, "QuantitativeNodesSimilarityPanel.vf2Header.title")); // NOI18N

        columnsLabel.setText(org.openide.util.NbBundle.getMessage(QuantitativeNodesSimilarityPanel.class, "QuantitativeNodesSimilarityPanel.columnsLabel.text")); // NOI18N

        contentPanel.setLayout(new java.awt.GridLayout(1, 0));
        contentScrollPane.setViewportView(contentPanel);

        pFormattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        pLabel.setText(org.openide.util.NbBundle.getMessage(QuantitativeNodesSimilarityPanel.class, "QuantitativeNodesSimilarityPanel.pLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(vf2Header, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pFormattedTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addGap(363, 363, 363))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(columnsLabel))
                .addGap(133, 133, 133))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vf2Header, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pLabel)
                    .addComponent(pFormattedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel columnsLabel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JFormattedTextField pFormattedTextField;
    private javax.swing.JLabel pLabel;
    private org.jdesktop.swingx.JXHeader vf2Header;
    // End of variables declaration//GEN-END:variables

	private static class AttributeLine {
		private JCheckBox checkBox;
		private AttributeColumn column;
		private JCheckBox doNorm;
		private JLabel label;
		private JFormattedTextField lambda;

		public AttributeLine(AttributeColumn column, boolean selected,
				boolean doNorm, double lambda) {
			checkBox = new JCheckBox(column.getTitle(), selected);
			this.column = column;
			this.doNorm = new JCheckBox("doNorm", doNorm);
			this.label = new JLabel("Lambda: ");
			NumberFormatter formatter = new NumberFormatter(new DecimalFormat("0.00"));
			formatter.setMinimum(0.0);
			formatter.setMaximum(1.0);
			formatter.setAllowsInvalid(false);
			this.lambda = new JFormattedTextField(new DefaultFormatterFactory(formatter));
			this.lambda.setPreferredSize(new Dimension(40, 20));
			this.lambda.setValue(lambda);
		}

		public JCheckBox getCheckBox() {
			return checkBox;
		}

		public boolean isSelected() {
			return checkBox.isSelected();
		}

		public AttributeColumn getColumn() {
			return column;
		}

		public JCheckBox getDoNormCB() {
			return doNorm;
		}

		public boolean getDoNorm() {
			return doNorm.isSelected();
		}

		public JLabel getLabel() {
			return label;
		}

		public JFormattedTextField getLambdaCB() {
			return lambda;
		}

		public double getLambda() {
			return (Double)lambda.getValue();
		}
	}
}
