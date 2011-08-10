/*
Copyright 2008-2011 Gephi
Authors : Taras Klaskovsky <megaterik@gmail.com>
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
package org.gephi.lsg_generator;

import java.awt.LayoutManager;
import java.lang.Integer;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.gephi.lib.validation.ValidationClient;
import org.gephi.statistics.plugin.ChartUtils;
import org.gephi.ui.components.SimpleHTMLReport;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 * @author megaterik
 */
public class LargeSimpleGraphPanel extends javax.swing.JPanel implements ValidationClient {

    SimpleHTMLReport htmlReport;

    /** Creates new customizer LargeSimpleGraphPanel */
    public LargeSimpleGraphPanel() {
        initComponents();
    }

    void getFields(LargeSimpleGraph generator) {
        try {
            generator.setExponent(Double.parseDouble(exponentTextField.getText()));
            generator.setMaxDegree(Integer.parseInt(maxDegreeTextField.getText()));
            generator.setMinDegree(Integer.parseInt(minDegreeTextField.getText()));
            generator.setNumberOfNodes(Integer.parseInt(nodesTextField.getText()));
        } catch (Exception ex) {
        }
    }

    void setFields(LargeSimpleGraph generator) {
        exponentTextField.setText(Double.toString(generator.getExponent()));
        minDegreeTextField.setText(Integer.toString(generator.getMinDegree()));;
        maxDegreeTextField.setText(Integer.toString(generator.getMaxDegree()));
        nodesTextField.setText(Integer.toString(generator.getNumberOfNodes()));
    }

    //html code with png picture of table for example button
    String generateReport() {
        HashMap<Integer, Integer> degreeDist = new HashMap<Integer, Integer>();

        int minDegree = Math.max(1, Integer.parseInt(minDegreeTextField.getText()));
        int maxDegree = Math.min(Integer.parseInt(nodesTextField.getText()) - 1, Integer.parseInt(maxDegreeTextField.getText()));
        int[] count = new int[maxDegree + 1];
        DistributionGenerator random = new DistributionGenerator();
        for (int i = 0; i < Integer.parseInt(nodesTextField.getText()); i++) {
            count[random.nextPowerLaw(minDegree, maxDegree, Double.parseDouble(exponentTextField.getText()))]++;
        }
        for (int i = 0; i < maxDegree; i++) {
            degreeDist.put(i, count[i]);
        }

        String report = "";
        //Distribution series
        XYSeries dSeries = ChartUtils.createXYSeries(degreeDist, "Degree Distribution");

        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(dSeries);

        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Degree Distribution",
                "Value",
                "Count",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        ChartUtils.decorateChart(chart1);
        ChartUtils.scaleChart(chart1, dSeries, false);
        String degreeImageFile = ChartUtils.renderChart(chart1, "w-degree-distribution.png");

        report = "<HTML> <BODY> <h1>Degree Distribution Report </h1> "
                + "<hr>"
                + "<br> <h2> Results: </h2>"
                + "<br /><br />" + degreeImageFile
                + "</BODY></HTML>";
        return report;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nodesTextField = new javax.swing.JTextField();
        minDegreeTextField = new javax.swing.JTextField();
        maxDegreeTextField = new javax.swing.JTextField();
        exponentTextField = new javax.swing.JTextField();
        nodesLabel = new javax.swing.JLabel();
        minDegreeLabel = new javax.swing.JLabel();
        maxDegreeLabel = new javax.swing.JLabel();
        exponentLabel = new javax.swing.JLabel();
        exampleButton = new javax.swing.JButton();

        nodesLabel.setText(org.openide.util.NbBundle.getMessage(LargeSimpleGraphPanel.class, "nodeLabel.text")); // NOI18N

        minDegreeLabel.setText(org.openide.util.NbBundle.getMessage(LargeSimpleGraphPanel.class, "minDegreeLabel.text")); // NOI18N

        maxDegreeLabel.setText(org.openide.util.NbBundle.getMessage(LargeSimpleGraphPanel.class, "maxDegreeLabel.text")); // NOI18N

        exponentLabel.setText(org.openide.util.NbBundle.getMessage(LargeSimpleGraphPanel.class, "exponentLabel.text")); // NOI18N

        exampleButton.setText(org.openide.util.NbBundle.getMessage(LargeSimpleGraphPanel.class, "ExampleButton.text")); // NOI18N
        exampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(nodesLabel)
                            .add(minDegreeLabel)
                            .add(maxDegreeLabel)
                            .add(exponentLabel))
                        .add(43, 43, 43)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(nodesTextField)
                            .add(exponentTextField)
                            .add(maxDegreeTextField)
                            .add(minDegreeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
                    .add(exampleButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nodesLabel)
                    .add(nodesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(minDegreeLabel)
                    .add(minDegreeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(maxDegreeLabel)
                    .add(maxDegreeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(exponentLabel)
                    .add(exponentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(exampleButton))
        );
    }// </editor-fold>//GEN-END:initComponents

private void exampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleButtonActionPerformed
    htmlReport = new SimpleHTMLReport(null, generateReport());
}//GEN-LAST:event_exampleButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exampleButton;
    private javax.swing.JLabel exponentLabel;
    private javax.swing.JTextField exponentTextField;
    private javax.swing.JLabel maxDegreeLabel;
    private javax.swing.JTextField maxDegreeTextField;
    private javax.swing.JLabel minDegreeLabel;
    private javax.swing.JTextField minDegreeTextField;
    private javax.swing.JLabel nodesLabel;
    private javax.swing.JTextField nodesTextField;
    // End of variables declaration//GEN-END:variables

    public static ValidationPanel createValidationPanel(LargeSimpleGraphPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        validationPanel.setInnerComponent(innerPanel);

        ValidationGroup group = validationPanel.getValidationGroup();
        innerPanel.validate(group);

        return validationPanel;
    }

    @Override
    public void validate(ValidationGroup group) {
        group.add(minDegreeTextField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_INTEGER, Validators.numberRange(1, Integer.MAX_VALUE));
        group.add(maxDegreeTextField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_INTEGER, Validators.numberRange(1, Integer.MAX_VALUE));
        group.add(exponentTextField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_NUMBER);
        group.add(nodesTextField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_INTEGER, Validators.numberRange(1, Integer.MAX_VALUE));
    }
}
