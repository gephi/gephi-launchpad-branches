/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.filters.plugin.operator;

import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.Operator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = FilterBuilder.class)
public class NOTBuilderEdge implements FilterBuilder {

    public Category getCategory() {
        return new Category("Operator");
    }

    public String getName() {
        return NbBundle.getMessage(NOTBuilderEdge.class, "NOTBuilderEdge.name");
    }

    public Icon getIcon() {
        return null;
    }

    public String getDescription() {
        return NbBundle.getMessage(NOTBuilderEdge.class, "NOTBuilderEdge.description");
    }

    public Filter getFilter() {
        return new NotOperatorEdge();
    }

    public JPanel getPanel(Filter filter) {
        return null;
    }

    public void destroy(Filter filter) {
    }

    public static class NotOperatorEdge implements Operator {

        public int getInputCount() {
            return 1;
        }

        public String getName() {
            return NbBundle.getMessage(NOTBuilderEdge.class, "NOTBuilderEdge.name");
        }

        public FilterProperty[] getProperties() {
            return null;
        }

        public Graph filter(Graph[] graphs) {
            if (graphs.length > 1) {
                throw new IllegalArgumentException("Not Filter accepts a single graph in parameter");
            }

            HierarchicalGraph hgraph = (HierarchicalGraph) graphs[0];
            GraphView hgraphView = hgraph.getView();
            HierarchicalGraph mainHGraph = hgraph.getView().getGraphModel().getHierarchicalGraph();
            for (Edge e : mainHGraph.getEdges().toArray()) {
                Node source = e.getSource().getNodeData().getNode(hgraphView.getViewId());
                Node target = e.getTarget().getNodeData().getNode(hgraphView.getViewId());
                if (source != null && target != null) {
                    Edge edgeInGraph = hgraph.getEdge(source, target);
                    if (edgeInGraph == null) {
                        //The edge is not in graph
                        hgraph.addEdge(e);
                    } else {
                        //The edge is in the graph
                        hgraph.removeEdge(edgeInGraph);
                    }
                }
            }
            return hgraph;
        }

        public Graph filter(Graph graph, Filter[] filters) {
            if (filters.length > 1) {
                throw new IllegalArgumentException("Not Filter accepts a single filter in parameter");
            }
            HierarchicalGraph hgraph = (HierarchicalGraph) graph;
            Filter filter = filters[0];
            if (filter instanceof EdgeFilter && ((EdgeFilter) filter).init(hgraph)) {
                EdgeFilter edgeFilter = (EdgeFilter) filter;
                for (Edge e : hgraph.getEdgesAndMetaEdges().toArray()) {
                    if (edgeFilter.evaluate(hgraph, e)) {
                        hgraph.removeEdge(e);
                    }
                }
                edgeFilter.finish();
            }

            return hgraph;
        }
    }
}
