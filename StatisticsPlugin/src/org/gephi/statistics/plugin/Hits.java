/*
Copyright 2008-2011 Gephi
Authors : Patick J. McSweeney <pjmcswee@syr.edu>, Sebastien Heymann <seb@gephi.org>
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
package org.gephi.statistics.plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.HierarchicalUndirectedGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Lookup;

/**
 * Ref: Jon M. Kleinberg, Authoritative Sources in a Hyperlinked Environment,
 * in Journal of the ACM 46 (5): 604–632 (1999)
 *
 * @author pjmcswee
 */
public class Hits implements Statistics, LongTask {

    public static final String AUTHORITY = "authority";
    public static final String HUB = "hub";
    private boolean isCanceled;
    private ProgressTicket progress;
    private double[] authority;
    private double[] hubs;
    private boolean useUndirected;
    private double epsilon = 0.0001;
    private LinkedList<Node> hub_list;
    private LinkedList<Node> auth_list;
    private HashMap<Node, Integer> indicies;

    public Hits() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        if (graphController != null && graphController.getModel() != null) {
            useUndirected = graphController.getModel().isUndirected();
        }
    }

    public void setUndirected(boolean pUndirected) {
        useUndirected = pUndirected;
    }

    /**
     * 
     * @return
     */
    public boolean getUndirected() {
        return useUndirected;
    }

    public void execute(GraphModel graphModel, AttributeModel attributeModel) {
        HierarchicalGraph graph = null;
        if (useUndirected) {
            graph = graphModel.getHierarchicalUndirectedGraphVisible();
        } else {
            graph = graphModel.getHierarchicalDirectedGraphVisible();
        }
        execute(graph, attributeModel);
    }

    public void execute(HierarchicalGraph hgraph, AttributeModel attributeModel) {
        hgraph.readLock();

        int N = hgraph.getNodeCount();
        authority = new double[N];
        hubs = new double[N];
        double[] temp_authority = new double[N];
        double[] temp_hubs = new double[N];

        hub_list = new LinkedList<Node>();
        auth_list = new LinkedList<Node>();

        Progress.start(progress);

        indicies = new HashMap<Node, Integer>();
        int index = 0;
        for (Node node : hgraph.getNodes()) {
            indicies.put(node, new Integer(index));
            index++;

            if (!useUndirected) {
                if (((HierarchicalDirectedGraph) hgraph).getTotalOutDegree(node) > 0) {
                    hub_list.add(node);
                }
                if (((HierarchicalDirectedGraph) hgraph).getTotalInDegree(node) > 0) {
                    auth_list.add(node);
                }
            } else {
                if (((HierarchicalUndirectedGraph) hgraph).getTotalDegree(node) > 0) {
                    hub_list.add(node);
                    auth_list.add(node);
                }
            }
        }


        for (Node node : hub_list) {
            int n_index = indicies.get(node);
            hubs[n_index] = 1.0f;
        }
        for (Node node : auth_list) {
            int n_index = indicies.get(node);
            authority[n_index] = 1.0f;
        }

        while (true) {

            boolean done = true;
            double auth_sum = 0;
            for (Node node : auth_list) {

                int n_index = indicies.get(node);
                temp_authority[n_index] = authority[n_index];
                EdgeIterable edge_iter;
                if (!useUndirected) {
                    edge_iter = ((HierarchicalDirectedGraph) hgraph).getInEdgesAndMetaInEdges(node);
                } else {
                    edge_iter = ((HierarchicalUndirectedGraph) hgraph).getEdgesAndMetaEdges(node);
                }
                for (Edge edge : edge_iter) {
                    Node target = hgraph.getOpposite(node, edge);
                    int target_index = indicies.get(target);
                    temp_authority[n_index] += hubs[target_index];
                }

                auth_sum += temp_authority[n_index];
                if (isCanceled) {
                    break;
                }

            }

            double hub_sum = 0;
            for (Node node : hub_list) {

                int n_index = indicies.get(node);
                temp_hubs[n_index] = hubs[n_index];
                EdgeIterable edge_iter;
                if (!useUndirected) {
                    edge_iter = ((HierarchicalDirectedGraph) hgraph).getInEdgesAndMetaInEdges(node);
                } else {
                    edge_iter = ((HierarchicalUndirectedGraph) hgraph).getEdgesAndMetaEdges(node);
                }
                for (Edge edge : edge_iter) {
                    Node target = hgraph.getOpposite(node, edge);
                    int target_index = indicies.get(target);
                    temp_hubs[n_index] += authority[target_index];
                }
                hub_sum += temp_hubs[n_index];
                if (isCanceled) {
                    break;
                }
            }

            for (Node node : auth_list) {
                int n_index = indicies.get(node);
                temp_authority[n_index] /= auth_sum;
                if (((temp_authority[n_index] - authority[n_index]) / authority[n_index]) >= epsilon) {
                    done = false;
                }
            }
            for (Node node : hub_list) {
                int n_index = indicies.get(node);
                temp_hubs[n_index] /= hub_sum;
                if (((temp_hubs[n_index] - hubs[n_index]) / hubs[n_index]) >= epsilon) {
                    done = false;
                }
            }


            authority = temp_authority;
            hubs = temp_hubs;
            temp_authority = new double[N];
            temp_hubs = new double[N];

            if ((done) || (isCanceled)) {
                break;
            }
        }

        AttributeTable nodeTable = attributeModel.getNodeTable();
        AttributeColumn authorityCol = nodeTable.getColumn(AUTHORITY);
        AttributeColumn hubsCol = nodeTable.getColumn(HUB);
        if (authorityCol == null) {
            authorityCol = nodeTable.addColumn(AUTHORITY, "Authority", AttributeType.FLOAT, AttributeOrigin.COMPUTED, new Float(0));
        }
        if (hubsCol == null) {
            hubsCol = nodeTable.addColumn(HUB, "Hub", AttributeType.FLOAT, AttributeOrigin.COMPUTED, new Float(0));
        }

        for (Node s : hgraph.getNodes()) {
            int s_index = indicies.get(s);
            AttributeRow row = (AttributeRow) s.getNodeData().getAttributes();
            row.setValue(authorityCol, (float) authority[s_index]);
            row.setValue(hubsCol, (float) hubs[s_index]);
        }

        hgraph.readUnlockAll();
    }

    /**
     *
     * @return
     */
    public String getReport() {
        //distribution of hub values
        Map<Double, Integer> distHubs = new HashMap<Double, Integer>();
        for (Node node : hub_list) {
            int n_index = indicies.get(node);
            Double d = hubs[n_index];
            if (distHubs.containsKey(d)) {
                Integer v = distHubs.get(d);
                distHubs.put(d, v + 1);
            } else {
                distHubs.put(d, 1);
            }
        }

        //distribution of authority values
        Map<Double, Integer> distAuthorities = new HashMap<Double, Integer>();
        for (Node node : auth_list) {
            int n_index = indicies.get(node);
            Double d = authority[n_index];
            if (distAuthorities.containsKey(d)) {
                Integer v = distAuthorities.get(d);
                distAuthorities.put(d, v + 1);
            } else {
                distAuthorities.put(d, 1);
            }
        }

        //Distribution of hub series
        XYSeries dHubsSeries = ChartUtils.createXYSeries(distHubs, "Hubs");

        //Distribution of authority series
        XYSeries dAuthsSeries = ChartUtils.createXYSeries(distAuthorities, "Authority");

        XYSeriesCollection datasetHubs = new XYSeriesCollection();
        datasetHubs.addSeries(dHubsSeries);

        XYSeriesCollection datasetAuths = new XYSeriesCollection();
        datasetAuths.addSeries(dAuthsSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Hubs Distribution",
                "Score",
                "Count",
                datasetHubs,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        ChartUtils.decorateChart(chart);
        ChartUtils.scaleChart(chart, dHubsSeries, true);
        String imageFile1 = ChartUtils.renderChart(chart, "hubs.png");

        JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Authority Distribution",
                "Score",
                "Count",
                datasetAuths,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        ChartUtils.decorateChart(chart2);
        ChartUtils.scaleChart(chart, dAuthsSeries, true);
        String imageFile2 = ChartUtils.renderChart(chart, "authorities.png");


        String report = "<HTML> <BODY> <h1> HITS Metric Report </h1>"
                + "<hr>"
                + "<br />"
                + "<h2> Parameters: </h2>  &#917; = " + this.epsilon
                + "<br /> <h2> Results: </h2><br />"
                + imageFile1 + "<br />" + imageFile2
                + "<br /><br />" + "<h2> Algorithm: </h2>"
                + "Jon M. Kleinberg, <i>Authoritative Sources in a Hyperlinked Environment</i>, in Journal of the ACM 46 (5): 604–632 (1999)<br />"
                + "</BODY> </HTML>";

        return report;
    }

    /**
     *
     * @return
     */
    public boolean cancel() {
        isCanceled = true;
        return true;
    }

    /**
     *
     * @param progressTicket
     */
    public void setProgressTicket(ProgressTicket progressTicket) {
        progress = progressTicket;
    }

    /**
     *
     * @param eps
     */
    public void setEpsilon(double eps) {
        epsilon = eps;
    }

    /**
     *
     * @return
     */
    public double getEpsilon() {
        return epsilon;
    }
}
