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
package org.gephi.desktop.similarity;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityBuilder;
import org.gephi.similarity.api.SimilarityController;
import org.gephi.similarity.api.SimilarityModel;
import org.gephi.similarity.spi.SimilarityUI;
import org.gephi.ui.components.SimpleHTMLReport;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.longtask.api.LongTaskListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class SimilarityFrontEnd extends javax.swing.JPanel {
	private SimilarityUI similarityUI;
	
	private final String    RUN;
	private final String    CANCEL;
	private final ImageIcon RUN_ICON;
	private final ImageIcon STOP_ICON;
	
	private Similarity      currentSimilarity;
	private SimilarityModel currentModel;

	public SimilarityFrontEnd(SimilarityUI ui) {
		initComponents();
		RUN    = NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.runStatus.run");
		CANCEL = NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.runStatus.cancel");
		initUI(ui);

		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (runButton.getText().equals(RUN))
					run();
				else cancel();
			}
		});

		reportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReport();
			}
		});

		RUN_ICON  = ImageUtilities.loadImageIcon("org/gephi/desktop/similarity/resources/run.png", false);
		STOP_ICON = ImageUtilities.loadImageIcon("org/gephi/desktop/similarity/resources/stop.png", false);
	}

	private void initUI(SimilarityUI ui) {
		this.similarityUI = ui;
		displayLabel.setText(ui.getDisplayName());
		busyLabel.setVisible(false);
		runButton.setEnabled(false);
		runButton.setText(RUN);
		// runButton.setIcon(RUN_ICON);
		reportButton.setEnabled(false);
	}

	public void refreshModel(SimilarityModel model) {
		currentModel = model;
		if (model == null) {
			runButton.setText(RUN);
			// runButton.setIcon(RUN_ICON);
			runButton.setEnabled(false);
			busyLabel.setBusy(false);
			busyLabel.setVisible(false);
			reportButton.setEnabled(false);
			resultLabel.setText("");
			currentSimilarity = null;
			return;
		}
		runButton.setEnabled(true);
		if (model.isRunning(similarityUI)) {
			runButton.setText(CANCEL);
			// runButton.setIcon(STOP_ICON);
			busyLabel.setVisible(true);
			busyLabel.setBusy(true);
			reportButton.setEnabled(false);
			resultLabel.setText("");
			if (currentSimilarity == null)
				currentSimilarity = currentModel.getRunning(similarityUI);
		}
		else {
			runButton.setText(RUN);
			// runButton.setIcon(RUN_ICON);
			busyLabel.setBusy(false);
			busyLabel.setVisible(false);
			currentSimilarity = null;
			refreshResult(model);
		}
	}

	private void refreshResult(SimilarityModel model) {
		String result = model.getResult(similarityUI);

		if (result != null)
			resultLabel.setText(result);
		else resultLabel.setText("");

		String report = model.getReport(similarityUI.getSimilarityClass());
		reportButton.setEnabled(report != null);
	}

	private void run() {
		// Create Similarity
		SimilarityController controller = Lookup.getDefault().lookup(SimilarityController.class);
		SimilarityBuilder    builder    = controller.getBuilder(similarityUI.getSimilarityClass());
		currentSimilarity = builder.getSimilarity();
		if (currentSimilarity != null) {
			LongTaskListener listener = new LongTaskListener() {
				@Override
				public void taskFinished(LongTask task) {
					showReport();
				}
			};

			JPanel settingsPanel = similarityUI.getSettingsPanel();
			if (settingsPanel != null) {
				similarityUI.setup(currentSimilarity);
				DialogDescriptor dd = new DialogDescriptor(settingsPanel, NbBundle.getMessage(
						SimilarityTopComponent.class, "SimilarityFrontEnd.settingsPanel.title", builder.getName()));
				if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
					similarityUI.unsetup();
					controller.execute(currentSimilarity, listener);
				}
			}
			else {
				similarityUI.setup(currentSimilarity);
				controller.execute(currentSimilarity, listener);
			}
		}
	}

	private void cancel() {
		if (currentSimilarity != null && currentSimilarity instanceof LongTask) {
			LongTask longTask = (LongTask)currentSimilarity;
			longTask.cancel();
		}
	}

	private void showReport() {
		final String report = currentModel.getReport(similarityUI.getSimilarityClass());
		if (report != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					SimpleHTMLReport dialog = new SimpleHTMLReport(WindowManager.getDefault().getMainWindow(), report);
				}
			});
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
        java.awt.GridBagConstraints gridBagConstraints;

        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new Dimension(16, 16));
        displayLabel = new javax.swing.JLabel();
        resultLabel = new javax.swing.JLabel();
        toolbar = new javax.swing.JToolBar();
        runButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        setMinimumSize(new java.awt.Dimension(169, 25));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(169, 25));
        setLayout(new java.awt.GridBagLayout());

        busyLabel.setText(org.openide.util.NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.busyLabel.text")); // NOI18N
        busyLabel.setMinimumSize(new java.awt.Dimension(16, 16));
        busyLabel.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(busyLabel, gridBagConstraints);

        displayLabel.setText(org.openide.util.NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.displayLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(displayLabel, gridBagConstraints);

        resultLabel.setText(org.openide.util.NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.resultLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 3);
        add(resultLabel, gridBagConstraints);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setMaximumSize(new java.awt.Dimension(50, 25));
        toolbar.setMinimumSize(new java.awt.Dimension(50, 25));
        toolbar.setOpaque(false);

        runButton.setText(org.openide.util.NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.runButton.text")); // NOI18N
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setOpaque(false);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(runButton);

        reportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/similarity/resources/report.png"))); // NOI18N
        reportButton.setToolTipText(org.openide.util.NbBundle.getMessage(SimilarityFrontEnd.class, "SimilarityFrontEnd.reportButton.toolTipText")); // NOI18N
        reportButton.setFocusable(false);
        reportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reportButton.setOpaque(false);
        reportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbar.add(reportButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(toolbar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JLabel displayLabel;
    private javax.swing.JButton reportButton;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JButton runButton;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

}
