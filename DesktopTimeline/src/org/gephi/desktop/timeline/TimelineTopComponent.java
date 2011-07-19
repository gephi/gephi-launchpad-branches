/*
Copyright 2008-2011 Gephi
Authors : Julian Bilcke <julian.bilcke@gephi.org>
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
package org.gephi.desktop.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.gephi.timeline.api.TimelineAnimatorEvent;
import org.gephi.timeline.spi.TimelineDrawer;
import org.gephi.timeline.api.TimelineAnimatorListener;
import org.gephi.timeline.api.TimelineModel;
import org.gephi.timeline.api.TimelineModelEvent;
import org.gephi.timeline.api.TimelineModelListener;
import org.gephi.ui.components.CloseButton;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.joda.time.DateTime;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.ImageUtilities;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Lookup;

/**
 * Top component corresponding to the timeline component
 * 
 * @author Julian Bilcke, Daniel Bernardes
 */
@ConvertAsProperties(dtd = "-//org.gephi.desktop.timeline//Timeline//EN",
autostore = false)
public final class TimelineTopComponent extends TopComponent implements TimelineAnimatorListener, TimelineModelListener {

    private static TimelineTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/gephi/desktop/timeline/resources/ui-status-bar.png";
    private static final String PREFERRED_ID = "TimelineTopComponent";

    // model+animator
    private TimelineModel model;
    private TimelineAnimatorImpl animator;
    
    // bounds
    private double min;
    private double max;
    
    // drawer
    private JPanel drawerPanel;
    private MinimalDrawer mdrawer;
        
    // sparkline
    private XYDataset dataset;
    private ValueAxis timeline;
    private NumberAxis metric;
    private XYPlot plot;
    private JFreeChart chart;
    private JPanel chartpanel;
    
    // test
    private Random r = new Random();
    
        
    public TimelineTopComponent() {
        initComponents();
        
        setName(NbBundle.getMessage(TimelineTopComponent.class, "CTL_TimelineTopComponent"));
//        setToolTipText(NbBundle.getMessage(TimelineTopComponent.class, "HINT_TimelineTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE); 

        // Setup drawer
        TimelineDrawer drawer = Lookup.getDefault().lookup(TimelineDrawer.class);
        drawerPanel = (JPanel) drawer;
        drawerPanel.setOpaque(false);
        drawerPanel.setEnabled(false);
        timelinePanel.add(drawerPanel);
        mdrawer = (MinimalDrawer) drawer;

        // Animator
        animator = new TimelineAnimatorImpl();
        mdrawer.setAnimator(animator);
        animator.addListener(this);        
        
        // Setup sparkline
        
        // Defaut unit format: real numbers
        dataset = new XYSeriesCollection();
        timeline = new NumberAxis();

        timeline.setTickLabelsVisible(true);
        timeline.setTickMarksVisible(true);
        timeline.setAxisLineVisible(true);
        timeline.setNegativeArrowVisible(true);
        timeline.setPositiveArrowVisible(true);
        timeline.setVisible(true);

        metric = new NumberAxis();
        metric.setTickLabelsVisible(false);
        metric.setTickMarksVisible(false);
        metric.setAxisLineVisible(false);
        metric.setNegativeArrowVisible(false);
        metric.setPositiveArrowVisible(false);
        metric.setVisible(false);

        plot = new XYPlot();
        plot.setInsets(new RectangleInsets(-1, -1, 0, 0));
        plot.setDataset(dataset);
        plot.setDomainAxis(timeline);
        plot.setDomainGridlinesVisible(false);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setRangeAxis(metric);
        plot.setRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));

        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMinimumDrawHeight(50);
        chartPanel.setLayout(new java.awt.BorderLayout());
        chartPanel.setBounds(0, 2, 300, 30);
        chartpanel = (JPanel) chartPanel;
        chartpanel.setVisible(false);

        tlcontainer.add(chartpanel, JLayeredPane.DEFAULT_LAYER);
                        
}
    
    private void setTimeLineVisible(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (visible && !TimelineTopComponent.this.isOpened()) {
                    TimelineTopComponent.this.open();
                    TimelineTopComponent.this.requestActive();
                } else if (!visible && TimelineTopComponent.this.isOpened()) {
                    TimelineTopComponent.this.close();
                }
                timelinePanel.setSize(tlcontainer.getSize());
                chartpanel.setSize(new Dimension(tlcontainer.getWidth(), tlcontainer.getHeight()-6));
            }
        });
    }
    
    private void setupSparkline() {
        // change unit formats if needed and refresh data
        if (model.getUnit() == DateTime.class && dataset instanceof XYSeriesCollection) {
            dataset = new TimeSeriesCollection();
            timeline = new DateAxis();
            plot.setDataset(dataset);
            plot.setDomainAxis(timeline);
        } else if (model.getUnit() != DateTime.class && dataset instanceof TimeSeriesCollection) {
            dataset = new XYSeriesCollection();
            timeline = new NumberAxis();
            plot.setDataset(dataset);
            plot.setDomainAxis(timeline);
        }
        timeline.setRange(refreshModelData());
    }

    
    private Range refreshModelData() {
        //TODO provide metric as argument
        final int samplepoints = 365; // another candidate: 840
        final String metricId = "Number of nodes";

        if (dataset instanceof TimeSeriesCollection) {
            TimeSeriesCollection dataSet = (TimeSeriesCollection) dataset;
            TimeSeries data = new TimeSeries(metricId);

            long Min = (long) min; // model.getMinValue();
            long Max = (long) max; // model.getMaxValue();
            for (long t = Min; t <= Max; t += (Max-Min)/(samplepoints-1)) {
                data.add(new Millisecond(new Date(t)), model.getSnapshotGraph(t).getNodeCount());
            }
            dataSet.removeAllSeries();
            dataSet.addSeries(data);
            return dataSet.getDomainBounds(false);
        } else {
            XYSeriesCollection dataSet = (XYSeriesCollection) dataset;
            XYSeries data = new XYSeries(metricId);
            
            for (double t=min; t<=max; t+=(max-min)/(samplepoints-1)) {
                data.add(t,model.getSnapshotGraph(t).getNodeCount());
            }
            dataSet.removeAllSeries();
            dataSet.addSeries(data);
            return dataSet.getDomainBounds(false);
        } 
    }

    private void setMin(double min) {
        if (this.min != min) {
            this.min = min;
            setTimeLineVisible(!Double.isInfinite(min));
        }

    }

    private void setMax(double max) {
        if (this.max != max) {
            this.max = max;
            setTimeLineVisible(!Double.isInfinite(max));
        }
    }
        
    public void timelineModelChanged(TimelineModelEvent event) {
        setEnabled(event.getSource().isEnabled());
        TimelineDrawer drawer = (TimelineDrawer) drawerPanel;
        if (drawer.getModel() == null || drawer.getModel() != event.getSource()) {
            drawer.setModel(event.getSource());
        }
        if (model != event.getSource()) {
            model = event.getSource();
        }
        switch (event.getEventType()) {
            case MIN_CHANGED:
                setMin((Double) event.getData());
                break;
            case MAX_CHANGED:
                setMax((Double) event.getData());
                break;
            case VISIBLE_INTERVAL:
                break;
        }
    }

    public void timelineAnimatorChanged(TimelineAnimatorEvent event) {
        // check animator value, to update the buttons etc..
        playButton.setSelected(!event.getStopped());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        playButton = new javax.swing.JToggleButton();
        tlcontainer = new javax.swing.JLayeredPane();
        timelinePanel = new javax.swing.JPanel();
        closeButton = new CloseButton();
        resetButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(32767, 58));
        setMinimumSize(new java.awt.Dimension(414, 58));
        setLayout(new java.awt.GridBagLayout());

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/disabled.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(playButton, org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.playButton.text")); // NOI18N
        playButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/disabled.png"))); // NOI18N
        playButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/disabled.png"))); // NOI18N
        playButton.setEnabled(false);
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/enabled.png"))); // NOI18N
        playButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(playButton, gridBagConstraints);

        tlcontainer.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        tlcontainer.setMinimumSize(new java.awt.Dimension(300, 28));
        tlcontainer.setPreferredSize(new java.awt.Dimension(300, 31));
        tlcontainer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                tlcontainerResize(evt);
            }
        });

        timelinePanel.setEnabled(false);
        timelinePanel.setMinimumSize(new java.awt.Dimension(300, 28));
        timelinePanel.setOpaque(false);
        timelinePanel.setPreferredSize(new java.awt.Dimension(300, 30));
        timelinePanel.setLayout(new java.awt.BorderLayout());
        timelinePanel.setBounds(0, 0, 300, 30);
        tlcontainer.add(timelinePanel, javax.swing.JLayeredPane.MODAL_LAYER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tlcontainer, gridBagConstraints);

        closeButton.setToolTipText(org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.closeButton.toolTipText")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(closeButton, gridBagConstraints);

        resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/reset_icon.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.resetButton.text")); // NOI18N
        resetButton.setFocusable(false);
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetButton.setMaximumSize(new java.awt.Dimension(44, 44));
        resetButton.setMinimumSize(new java.awt.Dimension(44, 44));
        resetButton.setPreferredSize(new java.awt.Dimension(44, 44));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(resetButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if(playButton.isSelected()) {
            animator.play(model.getFromFloat(), model.getToFloat());
        } else {
            animator.stop();
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void tlcontainerResize(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tlcontainerResize
        timelinePanel.setSize(tlcontainer.getSize());
        chartpanel.setSize(new Dimension(tlcontainer.getWidth(), tlcontainer.getHeight()-6));
    }//GEN-LAST:event_tlcontainerResize

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        if (model != null) {
            model.setEnabled(true);
            TimelineTopComponent.this.setEnabled(true);
            setupSparkline();
        }
        animator.stop();
        model.setRangeFromFloat(0.0, 1.0);
        mdrawer.refreshBounds();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        TimelineTopComponent.this.close();
    }//GEN-LAST:event_closeButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JToggleButton playButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel timelinePanel;
    private javax.swing.JLayeredPane tlcontainer;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized TimelineTopComponent getDefault() {
        if (instance == null) {
            instance = new TimelineTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the TimelineTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized TimelineTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(TimelineTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof TimelineTopComponent) {
            return (TimelineTopComponent) win;
        }
        Logger.getLogger(TimelineTopComponent.class.getName()).warning(
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

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void setEnabled(boolean enable) {
        chartpanel.setVisible(enable);
        drawerPanel.setEnabled(enable);
        timelinePanel.setEnabled(enable);
        playButton.setEnabled(enable);
    }
    
}
