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
package org.gephi.filters.plugin.dynamic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.dynamic.api.DynamicModelEvent;
import org.gephi.dynamic.api.DynamicModelListener;
import org.gephi.filters.api.Range;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.CategoryBuilder;
import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.timeline.api.TimelineController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = CategoryBuilder.class)
public class DynamicRangeBuilder implements CategoryBuilder {

    private final static Category DYNAMIC = new Category(
            NbBundle.getMessage(DynamicRangeBuilder.class, "DynamicRangeBuilder.category"),
            null,
            null);

    public Category getCategory() {
        return DYNAMIC;
    }

    public FilterBuilder[] getBuilders() {
        List<FilterBuilder> builders = new ArrayList<FilterBuilder>();
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();
        AttributeColumn nodeColumn = am.getNodeTable().getColumn(DynamicModel.TIMEINTERVAL_COLUMN);
        AttributeColumn edgeColumn = am.getEdgeTable().getColumn(DynamicModel.TIMEINTERVAL_COLUMN);
        if (nodeColumn != null || edgeColumn != null) {
            builders.add(new DynamicRangeFilterBuilder(nodeColumn, edgeColumn));
        }
        return builders.toArray(new FilterBuilder[0]);
    }

    private static class DynamicRangeFilterBuilder implements FilterBuilder {

        private final AttributeColumn nodeColumn;
        private final AttributeColumn edgeColumn;

        public DynamicRangeFilterBuilder(AttributeColumn nodeColumn, AttributeColumn edgeColumn) {
            this.nodeColumn = nodeColumn;
            this.edgeColumn = edgeColumn;
        }

        public Category getCategory() {
            return DYNAMIC;
        }

        public String getName() {
            return "Time Interval";
        }

        public Icon getIcon() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public DynamicRangeFilter getFilter() {
            TimelineController timelineController = Lookup.getDefault().lookup(TimelineController.class);
            DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
            return new DynamicRangeFilter(timelineController, dynamicController, nodeColumn, edgeColumn);
        }

        public JPanel getPanel(Filter filter) {
            final DynamicRangeFilter dynamicRangeFilter = (DynamicRangeFilter) filter;
            DynamicRangeUI ui = Lookup.getDefault().lookup(DynamicRangeUI.class);
            if (ui != null) {
                return ui.getPanel(dynamicRangeFilter);
            }
            return null;
        }

        public void destroy(Filter filter) {
            ((DynamicRangeFilter) filter).destroy();
        }
    }

    public static class DynamicRangeFilter implements NodeFilter, EdgeFilter, DynamicModelListener {

        private AttributeColumn nodeColumn;
        private AttributeColumn edgeColumn;
        private DynamicController dynamicController;
        private DynamicModel dynamicModel;
        private TimelineController timelineController;
        private TimeInterval visibleInterval;
        private FilterProperty[] filterProperties;
        private Double min;
        private Double max;
        private Range range;
        private boolean keepNull = true;

        public DynamicRangeFilter(TimelineController timelineController, DynamicController dynamicController, AttributeColumn nodeColumn, AttributeColumn edgeColumn) {
            this.nodeColumn = nodeColumn;
            this.edgeColumn = edgeColumn;
            this.dynamicController = dynamicController;
            this.dynamicModel = dynamicController.getModel();
            this.timelineController = timelineController;
//            min = dynamicModel.getMin();
//            max = dynamicModel.getMax();
//            if (timelineController != null) {
//                timelineController.setMin(min);
//                timelineController.setMax(max);
//            }
        }

        public boolean init(Graph graph) {
            dynamicController.addModelListener(this);
            visibleInterval = dynamicModel.getVisibleInterval();
            //Refresh minmax
            refreshValues((HierarchicalGraph) graph);
            return true;
        }

        public boolean evaluate(Graph graph, Node node) {
            if (nodeColumn != null) {
                Object obj = node.getNodeData().getAttributes().getValue(nodeColumn.getIndex());
                if (obj != null) {
                    TimeInterval timeInterval = (TimeInterval) obj;
                    return timeInterval.isInRange(visibleInterval.getLow(), visibleInterval.getHigh());
                }
                return keepNull;
            }
            return true;
        }

        public boolean evaluate(Graph graph, Edge edge) {
            if (edgeColumn != null) {
                Object obj = edge.getEdgeData().getAttributes().getValue(edgeColumn.getIndex());
                if (obj != null) {
                    TimeInterval timeInterval = (TimeInterval) obj;
                    return timeInterval.isInRange(visibleInterval.getLow(), visibleInterval.getHigh());
                }
                return keepNull;
            }
            return true;
        }

        public void finish() {
        }

        public void refreshValues(HierarchicalGraph graph) {
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;

            if (nodeColumn != null) {
                for (Node n : graph.getNodes()) {
                    Object obj = n.getNodeData().getAttributes().getValue(nodeColumn.getIndex());
                    if (obj != null) {
                        TimeInterval timeInterval = (TimeInterval) obj;
                        min = Math.min(min, Double.isInfinite(timeInterval.getLow()) ? min : timeInterval.getLow());
                        max = Math.max(max, Double.isInfinite(timeInterval.getHigh()) ? max : timeInterval.getHigh());
                    }
                }
            }
            if (edgeColumn != null) {
                for (Edge e : graph.getEdgesAndMetaEdges()) {
                    Object obj = e.getEdgeData().getAttributes().getValue(edgeColumn.getIndex());
                    if (obj != null) {
                        TimeInterval timeInterval = (TimeInterval) obj;
                        min = Math.min(min, Double.isInfinite(timeInterval.getLow()) ? min : timeInterval.getLow());
                        max = Math.max(max, Double.isInfinite(timeInterval.getHigh()) ? max : timeInterval.getHigh());
                    }
                }
            }
            if (range == null) {
                range = new Range(visibleInterval.getLow(), visibleInterval.getHigh(), min, max);
            } else {
                range.setMinMax(min, max);
            }
            if (!Double.isInfinite(min) && timelineController != null) {
                timelineController.setMin(min);
            }
            if (!Double.isInfinite(max) && timelineController != null) {
                timelineController.setMax(max);
            }
        }

        public String getName() {
            return NbBundle.getMessage(DynamicRangeBuilder.class, "DynamicRangeBuilder.name");
        }

        public FilterProperty[] getProperties() {
            if (filterProperties == null) {
                filterProperties = new FilterProperty[0];
                try {
                    filterProperties = new FilterProperty[]{
                                FilterProperty.createProperty(this, Range.class, "range"),
                                FilterProperty.createProperty(this, Boolean.class, "keepNull")};
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return filterProperties;
        }

        public void dynamicModelChanged(DynamicModelEvent event) {
            switch (event.getEventType()) {
                case VISIBLE_INTERVAL:
                    TimeInterval interval = (TimeInterval) event.getData();
                    getProperties()[0].setValue(new Range(interval.getLow(), interval.getHigh(), min, max));
                    break;
            }
        }

        public FilterProperty getRangeProperty() {
            return getProperties()[0];
        }

        public Double getMinimum() {
            return min;
        }

        public Double getMaximum() {
            return max;
        }

        public boolean isKeepNull() {
            return keepNull;
        }

        public void setKeepNull(boolean keepNull) {
            this.keepNull = keepNull;
        }

        public Range getRange() {
            return new Range(visibleInterval.getLow(), visibleInterval.getHigh(), min, max);
        }

        public void setRange(Range range) {
            dynamicController.setVisibleInterval(range.getLowerDouble(), range.getUpperDouble());
        }

        public void destroy() {
            dynamicController.removeModelListener(this);
        }
    }
}
