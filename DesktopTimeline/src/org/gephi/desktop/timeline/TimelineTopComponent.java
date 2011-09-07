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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import org.gephi.desktop.timeline.graphmetrics.GraphMetricNodes;
import org.gephi.timeline.api.TimelineAnimatorEvent;
import org.gephi.timeline.spi.TimelineDrawer;
import org.gephi.timeline.api.TimelineAnimatorListener;
import org.gephi.timeline.api.TimelineModel;
import org.gephi.timeline.api.TimelineModelEvent;
import org.gephi.timeline.api.TimelineModelListener;
import org.gephi.timeline.api.GraphMetric;
import org.gephi.ui.components.CloseButton;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
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
 * Top component corresponding to the Timeline component
 * 
 * @author Julian Bilcke, Daniel Bernardes
 */
@ConvertAsProperties(dtd = "-//org.gephi.desktop.timeline//Timeline//EN",
autostore = false)
public final class TimelineTopComponent extends TopComponent implements TimelineAnimatorListener, TimelineModelListener, LongTask {

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
    private MinimalDrawer drawerPanel;
    
    // sparkline chart
    private final int samplepoints = 365; // another candidate: 840
    private XYDataset dataset;
    private ValueAxis timelineAxis;
    private NumberAxis metricAxis;
    private XYPlot plot;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private GraphMetric currentMetric;
    private HashMap<String, GraphMetric> metrics = new HashMap<String, GraphMetric>();
    private JRadioButtonMenuItem[] mitemMetrics;
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int axisoffset = 2;
    
    public TimelineTopComponent() {
        initComponents();

        setName(NbBundle.getMessage(TimelineTopComponent.class, "CTL_TimelineTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);

        // Setup drawer
        drawerPanel = Lookup.getDefault().lookup(MinimalDrawer.class);
        drawerPanel.setOpaque(false);
        drawerPanel.setEnabled(false);
        drawerPanel.setInheritsPopupMenu(true);
        drawerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        GridBagConstraints gridc = new GridBagConstraints();
        gridc.fill = GridBagConstraints.BOTH;
        gridc.weightx = 1;
        gridc.weighty = 1;
        gridc.gridx = 0;
        gridc.gridy = 0;
        timelinePanel.add(drawerPanel, gridc);

        // Animator
        animator = new TimelineAnimatorImpl();
        drawerPanel.setAnimator(animator);
        animator.addListener(this);

        // Setup sparkline

        // Defaut unit format: real numbers
        dataset = new XYSeriesCollection();
        timelineAxis = new NumberAxis();

        timelineAxis.setTickLabelsVisible(true);
        timelineAxis.setTickMarksVisible(true);
        timelineAxis.setAxisLineVisible(true);
        timelineAxis.setNegativeArrowVisible(true);
        timelineAxis.setPositiveArrowVisible(true);
        timelineAxis.setVisible(true);

        metricAxis = new NumberAxis();
        metricAxis.setTickLabelsVisible(true);
        metricAxis.setTickMarksVisible(true);
        metricAxis.setAxisLineVisible(true);
        metricAxis.setNegativeArrowVisible(false);
        metricAxis.setPositiveArrowVisible(false);
        metricAxis.setVerticalTickLabels(false);
        metricAxis.setAutoRangeIncludesZero(true);
        metricAxis.setUpperMargin(0.25);
        metricAxis.setVisible(true);

        plot = new XYPlot();
        plot.setInsets(new RectangleInsets(-1, -1, 0, 0));
        plot.setDataset(dataset);
        plot.setDomainAxis(timelineAxis);
        plot.setDomainGridlinesVisible(false);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setRangeAxis(metricAxis);
        plot.setRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));

        chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(Color.WHITE);

        chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable(false);
        chartPanel.setMinimumDrawHeight(50);
        chartPanel.setMinimumDrawWidth(100);
        chartPanel.setLayout(new java.awt.BorderLayout());
        chartPanel.setInheritsPopupMenu(true);
        chartPanel.setVisible(false);
        
        tlcontainer.add(chartPanel, JLayeredPane.DEFAULT_LAYER);
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                timelinePanel.setSize(tlcontainer.getWidth(), tlcontainer.getHeight());
                drawerPanel.setBounds(0,0,tlcontainer.getWidth(), tlcontainer.getHeight());
                resizeTimeline(tlcontainer.getWidth(), tlcontainer.getHeight());
            }
            
        });

        // sparkline currentMetric controls
        currentMetric = Lookup.getDefault().lookup(GraphMetricNodes.class);
        GraphMetric[] metricslist = Lookup.getDefault().lookupAll(GraphMetric.class).toArray(new GraphMetric[0]);
        mitemMetrics = new JRadioButtonMenuItem[metricslist.length];
        for (int i = 0; i < metricslist.length; i++) {
            metrics.put(metricslist[i].getName(), metricslist[i]);
            mitemMetrics[i] = new JRadioButtonMenuItem(metricslist[i].getName(), metricslist[i]==currentMetric);
            mitemMetrics[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setMetric(metrics.get(e.getActionCommand()));
                    setupSparkline(true);
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run() {
                            resizeTimeline(tlcontainer.getWidth(), tlcontainer.getHeight());
                        }
                    });
                }
            });
            bgroupMetrics.add(mitemMetrics[i]);
            menuMetrics.add(mitemMetrics[i]);
        }
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
            }
        });
    }

    private void setupSparkline(boolean refresh) {
        // change unit formats if needed and refresh data
        Thread refreshSparkline = new Thread(refreshSparklineData);
        if (model.getUnit() == DateTime.class && dataset instanceof XYSeriesCollection) {
            dataset = new TimeSeriesCollection();
            timelineAxis = new DateAxis();
            plot.setDataset(dataset);
            plot.setDomainAxis(timelineAxis);
            refreshSparkline.start();
        } else if (model.getUnit() != DateTime.class && dataset instanceof TimeSeriesCollection) {
            dataset = new XYSeriesCollection();
            timelineAxis = new NumberAxis();
            plot.setDataset(dataset);
            plot.setDomainAxis(timelineAxis);
            refreshSparkline.start();
        }
        if (refresh) 
            refreshSparkline.start();
    }

    private Runnable refreshSparklineData = new Runnable() {

        public void run() {
            double y, ymax = 0, ymin = Double.POSITIVE_INFINITY; // default positive values
            
            Progress.start(progressTicket, samplepoints);
            
            if (dataset instanceof TimeSeriesCollection) {
                TimeSeriesCollection dataSet = (TimeSeriesCollection) dataset;
                TimeSeries data = new TimeSeries(currentMetric.getName());

                long Min = (long) min; // model.getMinValue();
                long Max = (long) max; // model.getMaxValue();

                for (long t = Min; t <= Max; t += (Max - Min) / (samplepoints - 1)) {
                    y = currentMetric.measureGraph(model.getSnapshotGraph(t)).doubleValue();
                    if (ymax < y) {
                        ymax = y;
                    }
                    if (ymin > y) {
                        ymin = y;
                    }
                    data.add(new Millisecond(new Date(t)), y);
                    Progress.progress(progressTicket);
                }
                dataSet.removeAllSeries();
                dataSet.addSeries(data);
                
                // update axis
                timelineAxis.setRange(dataSet.getDomainBounds(false));
                metricAxis.setTickUnit(new NumberTickUnit(ymax));
            } else {
                XYSeriesCollection dataSet = (XYSeriesCollection) dataset;
                XYSeries data = new XYSeries(currentMetric.getName());

                for (double t = min; t <= max; t += (max - min) / (samplepoints - 1)) {
                    y = currentMetric.measureGraph(model.getSnapshotGraph(t)).doubleValue();
                    if (ymax < y) {
                        ymax = y;
                    }
                    if (ymin > y) {
                        ymin = y;
                    }
                    data.add(t, y);
                    Progress.progress(progressTicket);
                }
                dataSet.removeAllSeries();
                dataSet.addSeries(data);
                
                // update axis
                timelineAxis.setRange(dataSet.getDomainBounds(false));
                metricAxis.setTickUnit(new NumberTickUnit(ymax));
            }
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    axisoffset = chartPanel.getWidth() - (int) chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea().getWidth();
                }
            });
            System.out.println(axisoffset);
            Progress.finish(progressTicket);
        }
    };

    public GraphMetric getMetric() {
        return currentMetric;
    }

    public void setMetric(GraphMetric metric) {
        this.currentMetric = metric;
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
    
    private void resizeTimeline(int width, int height) {
        chartPanel.setBounds(0, 2, width-2, height-6);
        timelinePanel.setBounds(axisoffset-2, 0, width-axisoffset, height); 
        drawerPanel.setBounds(0, 0, width-axisoffset-2, height);
        // timelinePanel.setSize(new Dimension(width, height));
        // drawerPanel.setBounds(2,0,width-4, height);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        menuMetrics = new javax.swing.JPopupMenu();
        mitemTitle = new javax.swing.JMenuItem();
        msepTitle = new javax.swing.JPopupMenu.Separator();
        bgroupMetrics = new javax.swing.ButtonGroup();
        playButton = new javax.swing.JToggleButton();
        tlcontainer = new javax.swing.JLayeredPane();
        timelinePanel = new javax.swing.JPanel();
        closeButton = new CloseButton();
        refreshButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(mitemTitle, org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.mitemTitle.text")); // NOI18N
        mitemTitle.setEnabled(false);
        mitemTitle.setOpaque(true);
        menuMetrics.add(mitemTitle);

        msepTitle.setMinimumSize(new java.awt.Dimension(3, 0));
        msepTitle.setPreferredSize(new java.awt.Dimension(3, 0));
        menuMetrics.add(msepTitle);

        setMaximumSize(new java.awt.Dimension(32767, 58));
        setMinimumSize(new java.awt.Dimension(414, 58));
        setPreferredSize(new java.awt.Dimension(424, 50));
        setLayout(new java.awt.GridBagLayout());

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/enabled.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(playButton, org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.playButton.text")); // NOI18N
        playButton.setToolTipText(org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.playButton.toolTipText")); // NOI18N
        playButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/disabled.png"))); // NOI18N
        playButton.setEnabled(false);
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setRequestFocusEnabled(false);
        playButton.setRolloverEnabled(false);
        playButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/playback_pause3.png"))); // NOI18N
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

        tlcontainer.setComponentPopupMenu(menuMetrics);
        tlcontainer.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        tlcontainer.setMinimumSize(new java.awt.Dimension(300, 28));
        tlcontainer.setPreferredSize(new java.awt.Dimension(300, 31));
        tlcontainer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                tlcontainerResize(evt);
            }
        });

        timelinePanel.setBorder(null);
        timelinePanel.setEnabled(false);
        timelinePanel.setInheritsPopupMenu(true);
        timelinePanel.setMinimumSize(new java.awt.Dimension(300, 28));
        timelinePanel.setOpaque(false);
        timelinePanel.setPreferredSize(new java.awt.Dimension(300, 30));
        timelinePanel.setLayout(new java.awt.GridBagLayout());
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

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/gephi/desktop/timeline/resources/reset_icon.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.refreshButton.text")); // NOI18N
        refreshButton.setToolTipText(org.openide.util.NbBundle.getMessage(TimelineTopComponent.class, "TimelineTopComponent.refreshButton.toolTipText")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setMaximumSize(new java.awt.Dimension(44, 44));
        refreshButton.setMinimumSize(new java.awt.Dimension(44, 44));
        refreshButton.setPreferredSize(new java.awt.Dimension(44, 44));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 3);
        add(refreshButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if (playButton.isSelected()) {
            animator.play(model.getFromFloat(), model.getToFloat());
            playButton.setToolTipText("Pause");
        } else {
            animator.stop();
            playButton.setToolTipText("Play");
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void tlcontainerResize(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tlcontainerResize
        resizeTimeline(evt.getComponent().getWidth(), evt.getComponent().getHeight());
    }//GEN-LAST:event_tlcontainerResize

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        animator.stop();
        if (model != null) {
            model.setEnabled(true);
            model.setRangeFromFloat(0.0, 1.0);
            TimelineTopComponent.this.setEnabled(true);
            setupSparkline(true);
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    resizeTimeline(tlcontainer.getWidth(), tlcontainer.getHeight());

                }
            });
        }
        drawerPanel.refreshBounds();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        TimelineTopComponent.this.close();
    }//GEN-LAST:event_closeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgroupMetrics;
    private javax.swing.JButton closeButton;
    private javax.swing.JPopupMenu menuMetrics;
    private javax.swing.JMenuItem mitemTitle;
    private javax.swing.JPopupMenu.Separator msepTitle;
    private javax.swing.JToggleButton playButton;
    private javax.swing.JButton refreshButton;
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
        chartPanel.setVisible(enable);
        drawerPanel.setEnabled(enable);
        timelinePanel.setEnabled(enable);
        playButton.setEnabled(enable);
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
