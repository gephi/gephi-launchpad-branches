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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.filters.spi.Operator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = FilterBuilder.class)
public class MASKBuilderEdge implements FilterBuilder {

    public Category getCategory() {
        return new Category("Operator");
    }

    public String getName() {
        return NbBundle.getMessage(MASKBuilderEdge.class, "MASKBuilderEdge.name");
    }

    public Icon getIcon() {
        return null;
    }

    public String getDescription() {
        return NbBundle.getMessage(MASKBuilderEdge.class, "MASKBuilderEdge.description");
    }

    public Filter getFilter() {
        return new MaskEdgeOperator();
    }

    public JPanel getPanel(Filter filter) {
        MASKEdgeUI ui = Lookup.getDefault().lookup(MASKEdgeUI.class);
        if (ui != null) {
            return ui.getPanel((MaskEdgeOperator) filter);
        }
        return null;
    }

    public void destroy(Filter filter) {
    }

    public static class MaskEdgeOperator implements Operator {

        public enum EdgesOptions {

            SOURCE, TARGET, ANY, BOTH
        };
        private EdgesOptions option = EdgesOptions.ANY;
        private FilterProperty[] filterProperties;

        public int getInputCount() {
            return 1;
        }

        public String getName() {
            return NbBundle.getMessage(MASKBuilderEdge.class, "MASKBuilderEdge.name");
        }

        public FilterProperty[] getProperties() {
            if (filterProperties == null) {
                filterProperties = new FilterProperty[0];
                try {
                    filterProperties = new FilterProperty[]{
                                FilterProperty.createProperty(this, String.class, "option")
                            };
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return filterProperties;
        }

        public Graph filter(Graph[] graphs) {
            if (graphs.length > 1) {
                throw new IllegalArgumentException("Filter accepts a single graph in parameter");
            }

            HierarchicalGraph hgraph = (HierarchicalGraph) graphs[0];
            GraphView hgraphView = hgraph.getView();
            HierarchicalGraph mainHGraph = hgraph.getView().getGraphModel().getHierarchicalGraph();

            List<Edge> edgesToKeep = new ArrayList<Edge>();
            for (Edge e : mainHGraph.getEdges().toArray()) {
                Node source = e.getSource().getNodeData().getNode(hgraphView.getViewId());
                Node target = e.getTarget().getNodeData().getNode(hgraphView.getViewId());
                boolean keep = false;
                switch (option) {
                    case SOURCE:
                        keep = source != null;
                        break;
                    case TARGET:
                        keep = target != null;
                        break;
                    case BOTH:
                        keep = source != null && target != null;
                        break;
                    case ANY:
                        keep = source != null || target != null;
                        break;
                }
                if (keep) {
                    edgesToKeep.add(e);
                }
            }

            hgraph.clearEdges();

            for (Node n : mainHGraph.getNodes().toArray()) {
                if (n.getNodeData().getNode(hgraphView.getViewId()) == null) {
                    hgraph.addNode(n);
                }
            }

            for (Edge e : edgesToKeep) {
                hgraph.addEdge(e);
            }

            return hgraph;
        }

        public Graph filter(Graph graph, Filter[] filters) {
            if (filters.length > 1) {
                throw new IllegalArgumentException("Filter accepts a single filter in parameter");
            }
            HierarchicalGraph hgraph = (HierarchicalGraph) graph;
            if (filters[0] instanceof NodeFilter && ((NodeFilter) filters[0]).init(hgraph)) {
                NodeFilter filter = (NodeFilter) filters[0];
                GraphView hgraphView = hgraph.getView();
                for (Edge e : hgraph.getEdges().toArray()) {
                    Node source = e.getSource().getNodeData().getNode(hgraphView.getViewId());
                    Node target = e.getTarget().getNodeData().getNode(hgraphView.getViewId());
                    boolean remove = false;
                    switch (option) {
                        case SOURCE:
                            remove = !filter.evaluate(hgraph, source);
                            break;
                        case TARGET:
                            remove = !filter.evaluate(hgraph, target);
                            break;
                        case BOTH:
                            remove = !filter.evaluate(hgraph, source) || !filter.evaluate(hgraph, target);
                            break;
                        case ANY:
                            remove = !filter.evaluate(hgraph, source) && !filter.evaluate(hgraph, target);
                            break;
                    }
                    if (remove) {
                        hgraph.removeEdge(e);
                    }
                }
                filter.finish();
            }
            return hgraph;
        }

        public String getOption() {
            return option.toString();
        }

        public void setOption(String option) {
            this.option = EdgesOptions.valueOf(option);
        }
    }
}
