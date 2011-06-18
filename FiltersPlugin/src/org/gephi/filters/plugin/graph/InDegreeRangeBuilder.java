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
package org.gephi.filters.plugin.graph;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.filters.plugin.RangeFilter;
import org.gephi.filters.api.FilterLibrary;
import org.gephi.filters.api.Range;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.HierarchicalDirectedGraph;
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
public class InDegreeRangeBuilder implements FilterBuilder {

    public Category getCategory() {
        return FilterLibrary.TOPOLOGY;
    }

    public String getName() {
        return NbBundle.getMessage(InDegreeRangeBuilder.class, "InDegreeRangeBuilder.name");
    }

    public Icon getIcon() {
        return null;
    }

    public String getDescription() {
        return NbBundle.getMessage(InDegreeRangeBuilder.class, "InDegreeRangeBuilder.description");
    }

    public InDegreeRangeFilter getFilter() {
        return new InDegreeRangeFilter();
    }

    public JPanel getPanel(Filter filter) {
        RangeUI ui = Lookup.getDefault().lookup(RangeUI.class);
        if (ui != null) {
            return ui.getPanel((InDegreeRangeFilter) filter);
        }
        return null;
    }

    public void destroy(Filter filter) {
    }

    public static class InDegreeRangeFilter implements RangeFilter, NodeFilter {

        private Range range;
        //States
        private List<Integer> values;

        public String getName() {
            return NbBundle.getMessage(InDegreeRangeBuilder.class, "InDegreeRangeBuilder.name");
        }

        public boolean init(Graph graph) {
            if (graph.getNodeCount() == 0 || !(graph instanceof DirectedGraph)) {
                return false;
            }
            refreshValues(graph);
            return true;
        }

        public boolean evaluate(Graph graph, Node node) {
            int degree = ((HierarchicalDirectedGraph) graph).getTotalInDegree(node);
            return range.isInRange(degree);
        }

        public void finish() {
        }

        public Object[] getValues() {
            return values.toArray(new Integer[0]);
        }

        private void refreshValues(Graph graph) {
            Integer min = 0;
            Integer max = 0;
            HierarchicalDirectedGraph hgraph = (HierarchicalDirectedGraph) graph;
            values = new ArrayList<Integer>(((HierarchicalGraph) graph).getNodeCount());
            min = Integer.MAX_VALUE;
            max = Integer.MIN_VALUE;
            for (Node n : hgraph.getNodes()) {
                int degree = hgraph.getTotalInDegree(n);
                min = Math.min(min, degree);
                max = Math.max(max, degree);
                values.add(degree);
            }
            if (range == null) {
                range = new Range(min, max, min, max);
            } else {
                range.setMinMax(min, max);
            }
        }

        public FilterProperty[] getProperties() {
            try {
                return new FilterProperty[]{
                            FilterProperty.createProperty(this, Range.class, "range")
                        };
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return new FilterProperty[0];
        }

        public FilterProperty getRangeProperty() {
            return getProperties()[0];
        }

        public Range getRange() {
            return range;
        }

        public void setRange(Range range) {
            if (range.getMinimum() == null && range.getMaximum() == null) {
                //Opening project
                this.range = new Range(range.getLowerBound(), range.getUpperBound(), this.range.getMinimum(), this.range.getMaximum());
            } else {
                this.range = range;
            }
        }
    }
}
