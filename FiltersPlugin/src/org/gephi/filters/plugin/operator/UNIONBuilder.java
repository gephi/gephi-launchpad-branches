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
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.filters.spi.Operator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = FilterBuilder.class)
public class UNIONBuilder implements FilterBuilder {

    public Category getCategory() {
        return new Category("Operator");
    }

    public String getName() {
        return NbBundle.getMessage(UNIONBuilder.class, "UNIONBuilder.name");
    }

    public Icon getIcon() {
        return null;
    }

    public String getDescription() {
        return NbBundle.getMessage(UNIONBuilder.class, "UNIONBuilder.description");
    }

    public Filter getFilter() {
        return new UnionOperator();
    }

    public JPanel getPanel(Filter filter) {
        return null;
    }

    public void destroy(Filter filter) {
    }

    public static class UnionOperator implements Operator {

        public int getInputCount() {
            return Integer.MAX_VALUE;
        }

        public String getName() {
            return NbBundle.getMessage(UNIONBuilder.class, "UNIONBuilder.name");
        }

        public FilterProperty[] getProperties() {
            return null;
        }

        public Graph filter(Graph[] graphs) {
            HierarchicalGraph maxHGraph = (HierarchicalGraph) graphs[0];
            int maxElements = 0;
            for (int i = 0; i < graphs.length; i++) {
                int count = ((HierarchicalGraph) graphs[i]).getNodeCount();
                if (count > maxElements) {
                    maxHGraph = (HierarchicalGraph) graphs[i];
                    maxElements = count;
                }
            }
            for (int i = 0; i < graphs.length; i++) {
                if ((HierarchicalGraph) graphs[i] != maxHGraph) {
                    //Merge
                    for (Node n : ((HierarchicalGraph) graphs[i]).getNodes().toArray()) {
                        maxHGraph.addNode(n);
                    }
                    for (Edge e : ((HierarchicalGraph) graphs[i]).getEdgesAndMetaEdges().toArray()) {
                        maxHGraph.addEdge(e);
                    }
                }
            }
            return maxHGraph;
        }

        public Graph filter(Graph graph, Filter[] filters) {
            HierarchicalGraph hgraph = (HierarchicalGraph) graph;
            List<NodeFilter> nodeFilters = new ArrayList<NodeFilter>();
            List<EdgeFilter> edgeFilters = new ArrayList<EdgeFilter>();
            for (Filter f : filters) {
                if (f instanceof NodeFilter) {
                    nodeFilters.add((NodeFilter) f);
                } else if (f instanceof EdgeFilter) {
                    edgeFilters.add((EdgeFilter) f);
                }
            }
            if (nodeFilters.size() > 0) {
                for (Iterator<NodeFilter> itr = nodeFilters.iterator(); itr.hasNext();) {
                    NodeFilter nf = itr.next();
                    if (!nf.init(hgraph)) {
                        itr.remove();
                    }
                }
                List<Node> nodesToRemove = new ArrayList<Node>();
                for (Node n : hgraph.getNodes()) {
                    boolean remove = true;
                    for (NodeFilter nf : nodeFilters) {
                        if (nf.evaluate(hgraph, n)) {
                            remove = false;
                        }
                    }
                    if (remove) {
                        nodesToRemove.add(n);
                    }
                }

                for (Node n : nodesToRemove) {
                    hgraph.removeNode(n);
                }
                for (NodeFilter nf : nodeFilters) {
                    nf.finish();
                }
            }
            if (edgeFilters.size() > 0) {
                for (Iterator<EdgeFilter> itr = edgeFilters.iterator(); itr.hasNext();) {
                    EdgeFilter ef = itr.next();
                    if (!ef.init(hgraph)) {
                        itr.remove();
                    }
                }
                List<Edge> edgesToRemove = new ArrayList<Edge>();
                for (Edge e : hgraph.getEdgesAndMetaEdges()) {
                    boolean remove = true;
                    for (EdgeFilter ef : edgeFilters) {
                        if (ef.evaluate(hgraph, e)) {
                            remove = false;
                        }
                    }
                    if (remove) {
                        edgesToRemove.add(e);
                    }
                }

                for (Edge e : edgesToRemove) {
                    hgraph.removeEdge(e);
                }
                for (EdgeFilter ef : edgeFilters) {
                    ef.finish();
                }
            }
            return hgraph;
        }
    }
}
