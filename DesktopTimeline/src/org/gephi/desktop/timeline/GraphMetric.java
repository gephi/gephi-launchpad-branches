/*
 * 
 * 
 */
package org.gephi.desktop.timeline;

import java.util.EnumMap;
import org.gephi.graph.api.Graph;

/**
 *
 * @author daniel
 */
public class GraphMetric {
    
    public enum SimpleMetric {
        NODES, EDGES
    }
    
    private SimpleMetric metric = SimpleMetric.NODES;
    private EnumMap<SimpleMetric,String> metricNames = new EnumMap<SimpleMetric,String>(SimpleMetric.class);
    
    GraphMetric() {
        setupMetricNames();
    }
    
    GraphMetric(SimpleMetric metric) {
        setupMetricNames();
        this.metric = metric;
    }
    
    private void setupMetricNames() {
        metricNames.put(SimpleMetric.NODES, "Number of nodes");
        metricNames.put(SimpleMetric.EDGES, "Number of edges");                
    }

    public void setMetric(SimpleMetric metric) {
        this.metric = metric;
    }
    
    public SimpleMetric getMetric() {
        return this.metric;
    }
    
    public String getMetricName() {
        return metricNames.get(this.metric);
    }
    
    public int getGraphMetric(Graph graph) {
        return getGraphMetric(graph, metric);
    }

    public int getGraphMetric(Graph graph, SimpleMetric metric) {
        switch (metric) {
            case NODES:
                return graph.getNodeCount();
            case EDGES:
                return graph.getEdgeCount();
            default:
                return -1;
        }
    }
}