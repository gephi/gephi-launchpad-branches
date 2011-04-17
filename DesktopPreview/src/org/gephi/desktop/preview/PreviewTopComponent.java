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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.gephi.preview.api.GraphSheet;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.ui.components.JColorButton;
import org.gephi.ui.utils.UIUtils;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class PreviewTopComponent extends TopComponent {

    private static PreviewTopComponent instance;
    static final String ICON_PATH = "org/gephi/desktop/preview/resources/preview.png";
    private static final String PREFERRED_ID = "PreviewTopComponent";
    private final ProcessingPreview sketch;
    private final ProcessingListener processingListener = new ProcessingListener();

    private PreviewTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(PreviewTopComponent.class, "CTL_PreviewTopComponent"));
//        setToolTipText(NbBundle.getMessage(PreviewTopComponent.class, "HINT_PreviewTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH));
        if (UIUtils.isAquaLookAndFeel()) {
            previewPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
        }
        if (UIUtils.isAquaLookAndFeel()) {
            southToolbar.setBackground(UIManager.getColor("NbExplorerView.background"));
        }

        bannerPanel.setVisible(false);

        // inits the preview applet
        sketch = new ProcessingPreview();
        sketch.init();
        sketch.registerPost(processingListener);
        sketch.registerPre(processingListener);
        sketchPanel.add(sketch, BorderLayout.CENTER);

        // forces the controller instanciation
        PreviewUIController.findInstance();

        //background color
        ((JColorButton) backgroundButton).addPropertyChangeListener(JColorButton.EVENT_COLOR, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
                controller.setBackgroundColor((Color) evt.getNewValue());
            }
        });
        southBusyLabel.setVisible(false);
        resetZoomButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sketch.resetZoom();
            }
        });
    }

    public void setRefresh(final boolean refresh) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                CardLayout cl = (CardLayout) previewPanel.getLayout();
                cl.show(previewPanel, refresh ? "refreshCard" : "previewCard");
                ((JXBusyLabel) busyLabel).setBusy(refresh);
            }
        });
    }

    public class ProcessingListener {

        public void post() {
            final boolean isRedraw = sketch.isRedraw();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    southBusyLabel.setVisible(isRedraw);
                    ((JXBusyLabel) southBusyLabel).setBusy(isRedraw);
                }
            });
        }

        public void pre() {
            final boolean isRedraw = sketch.isRedraw();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    southBusyLabel.setVisible(isRedraw);
                    ((JXBusyLabel) southBusyLabel).setBusy(isRedraw);
                }
            });
        }
    }

    /**
     * Shows the banner panel.
     *
     * @see PreviewUIController#showRefreshNotification()
     */
    public void showBannerPanel() {
        bannerPanel.setVisible(true);
    }

    public void setBackgroundColor(Color color) {
        ((JColorButton) backgroundButton).setColor(color);

    }

    /**
     * Hides the banner panel.
     *
     * @see PreviewUIController#hideRefreshNotification()
     */
    public void hideBannerPanel() {
        bannerPanel.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        southBusyLabel = new JXBusyLabel(new Dimension(14,14));
        bannerPanel = new javax.swing.JPanel();
        bannerLabel = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        previewPanel = new javax.swing.JPanel();
        sketchPanel = new javax.swing.JPanel();
        refreshPanel = new javax.swing.JPanel();
        busyLabel = new JXBusyLabel(new Dimension(20,20));
        southToolbar = new javax.swing.JToolBar();
        backgroundButton = new JColorButton(Color.WHITE);
        resetZoomButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(southBusyLabel, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.southBusyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(southBusyLabel, gridBagConstraints);

        bannerPanel.setBackground(new java.awt.Color(178, 223, 240));
        bannerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        bannerPanel.setLayout(new java.awt.GridBagLayout());

        bannerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/preview/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(bannerLabel, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.bannerLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        bannerPanel.add(bannerLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.refreshButton.text")); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 1);
        bannerPanel.add(refreshButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(bannerPanel, gridBagConstraints);

        previewPanel.setLayout(new java.awt.CardLayout());

        sketchPanel.setBackground(new java.awt.Color(255, 255, 255));
        sketchPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        sketchPanel.setLayout(new java.awt.BorderLayout());
        previewPanel.add(sketchPanel, "previewCard");

        refreshPanel.setOpaque(false);
        refreshPanel.setLayout(new java.awt.GridBagLayout());

        busyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(busyLabel, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.busyLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        refreshPanel.add(busyLabel, gridBagConstraints);

        previewPanel.add(refreshPanel, "refreshCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(previewPanel, gridBagConstraints);

        southToolbar.setFloatable(false);
        southToolbar.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(backgroundButton, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.backgroundButton.text")); // NOI18N
        backgroundButton.setFocusable(false);
        southToolbar.add(backgroundButton);

        org.openide.awt.Mnemonics.setLocalizedText(resetZoomButton, org.openide.util.NbBundle.getMessage(PreviewTopComponent.class, "PreviewTopComponent.resetZoomButton.text")); // NOI18N
        resetZoomButton.setFocusable(false);
        resetZoomButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetZoomButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        southToolbar.add(resetZoomButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(southToolbar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        PreviewUIController.findInstance().refreshPreview();
    }//GEN-LAST:event_refreshButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backgroundButton;
    private javax.swing.JLabel bannerLabel;
    private javax.swing.JPanel bannerPanel;
    private javax.swing.JLabel busyLabel;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel refreshPanel;
    private javax.swing.JButton resetZoomButton;
    private javax.swing.JPanel sketchPanel;
    private javax.swing.JLabel southBusyLabel;
    private javax.swing.JToolBar southToolbar;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized PreviewTopComponent getDefault() {
        if (instance == null) {
            instance = new PreviewTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the PreviewTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized PreviewTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(PreviewTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof PreviewTopComponent) {
            return (PreviewTopComponent) win;
        }
        Logger.getLogger(PreviewTopComponent.class.getName()).warning(
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
            return PreviewTopComponent.getDefault();
        }
    }

    /**
     * Refresh the preview applet.
     */
    public void refreshPreview() {
        sketch.refresh();
    }

    /**
     * Defines the preview graph to draw in the applet.
     *
     * @param graph  the preview graph to draw in the applet
     */
    public void setGraph(GraphSheet graphSheet) {
        sketch.setGraphSheet(graphSheet);
    }
}
