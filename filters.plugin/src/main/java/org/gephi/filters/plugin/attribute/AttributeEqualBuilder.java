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
package org.gephi.filters.plugin.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.filters.api.FilterLibrary;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.DynamicAttributesHelper;
import org.gephi.filters.spi.Category;
import org.gephi.filters.spi.CategoryBuilder;
import org.gephi.filters.spi.EdgeFilter;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.filters.spi.NodeFilter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
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
@ServiceProvider(service = CategoryBuilder.class)
public class AttributeEqualBuilder implements CategoryBuilder {

    private final static Category EQUAL = new Category(
            NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.name"),
            null,
            FilterLibrary.ATTRIBUTES);

    public Category getCategory() {
        return EQUAL;
    }

    public FilterBuilder[] getBuilders() {
        List<FilterBuilder> builders = new ArrayList<FilterBuilder>();
        AttributeModel am = Lookup.getDefault().lookup(AttributeController.class).getModel();
        for (AttributeColumn c : am.getNodeTable().getColumns()) {
            if (AttributeUtils.getDefault().isStringColumn(c) || c.getType().equals(AttributeType.DYNAMIC_STRING)) {
                EqualStringFilterBuilder b = new EqualStringFilterBuilder(c);
                builders.add(b);
            } else if (AttributeUtils.getDefault().isNumberColumn(c) || AttributeUtils.getDefault().isDynamicNumberColumn(c)) {
                EqualNumberFilterBuilder b = new EqualNumberFilterBuilder(c);
                builders.add(b);
            } else if (c.getType().equals(AttributeType.BOOLEAN) || c.getType().equals(AttributeType.DYNAMIC_BOOLEAN)) {
                EqualBooleanFilterBuilder b = new EqualBooleanFilterBuilder(c);
                builders.add(b);
            }
        }
        for (AttributeColumn c : am.getEdgeTable().getColumns()) {
            if (AttributeUtils.getDefault().isStringColumn(c) || c.getType().equals(AttributeType.DYNAMIC_STRING)) {
                EqualStringFilterBuilder b = new EqualStringFilterBuilder(c);
                builders.add(b);
            } else if (AttributeUtils.getDefault().isNumberColumn(c) || AttributeUtils.getDefault().isDynamicNumberColumn(c)) {
                EqualNumberFilterBuilder b = new EqualNumberFilterBuilder(c);
                builders.add(b);
            } else if (c.getType().equals(AttributeType.BOOLEAN) || c.getType().equals(AttributeType.DYNAMIC_BOOLEAN)) {
                EqualBooleanFilterBuilder b = new EqualBooleanFilterBuilder(c);
                builders.add(b);
            }
        }
        return builders.toArray(new FilterBuilder[0]);
    }

    private static class EqualStringFilterBuilder implements FilterBuilder {

        private final AttributeColumn column;

        public EqualStringFilterBuilder(AttributeColumn column) {
            this.column = column;
        }

        public Category getCategory() {
            return EQUAL;
        }

        public String getName() {
            return "<font color='#000000'>" + column.getTitle() + "</font> "
                    + "<font color='#999999'><i>" + column.getType().toString() + " "
                    + (AttributeUtils.getDefault().isNodeColumn(column) ? "(Node)" : "(Edge)") + "</i></font>";
        }

        public Icon getIcon() {
            return null;
        }

        public String getDescription() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.description");
        }

        public EqualStringFilter getFilter() {
            if (AttributeUtils.getDefault().isNodeColumn(column)) {
                NodeEqualStringFilter f = new NodeEqualStringFilter();
                f.setColumn(column);
                return f;
            } else {
                EdgeEqualStringFilter f = new EdgeEqualStringFilter();
                f.setColumn(column);
                return f;
            }
        }

        public JPanel getPanel(Filter filter) {
            EqualStringUI ui = Lookup.getDefault().lookup(EqualStringUI.class);
            if (ui != null) {
                return ui.getPanel((EqualStringFilter) filter);
            }
            return null;
        }

        public void destroy(Filter filter) {
        }
    }

    public static class NodeEqualStringFilter extends EqualStringFilter implements NodeFilter {
    }

    public static class EdgeEqualStringFilter extends EqualStringFilter implements EdgeFilter {
    }

    public static class EqualStringFilter implements Filter {

        private FilterProperty[] filterProperties;
        private String pattern;
        private boolean useRegex;
        private AttributeColumn column;
        private Pattern regex;
        private DynamicAttributesHelper dynamicHelper = new DynamicAttributesHelper(this, null);

        public String getName() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.name");
        }

        public boolean init(Graph graph) {
            HierarchicalGraph hg = (HierarchicalGraph) graph;
            dynamicHelper = new DynamicAttributesHelper(this, hg);
            return true;
        }

        public boolean evaluate(Graph graph, Node node) {
            if (pattern == null) {
                return true;
            }
            Object val = node.getNodeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null && useRegex) {
                return regex.matcher((String) val).matches();
            } else if (val != null) {
                return pattern.equals((String) val);
            }
            return false;
        }

        public boolean evaluate(Graph graph, Edge edge) {
            Object val = edge.getEdgeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null && useRegex) {
                return regex.matcher((String) val).matches();
            } else if (val != null) {
                return pattern.contains((String) val);
            }
            return false;
        }

        public void finish() {
        }

        public FilterProperty[] getProperties() {
            if (filterProperties == null) {
                filterProperties = new FilterProperty[0];
                try {
                    filterProperties = new FilterProperty[]{
                                FilterProperty.createProperty(this, AttributeColumn.class, "column"),
                                FilterProperty.createProperty(this, String.class, "pattern"),
                                FilterProperty.createProperty(this, Boolean.class, "useRegex")
                            };
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return filterProperties;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
            this.regex = Pattern.compile(pattern);
        }

        public boolean isUseRegex() {
            return useRegex;
        }

        public void setUseRegex(boolean useRegex) {
            this.useRegex = useRegex;
        }

        public AttributeColumn getColumn() {
            return column;
        }

        public void setColumn(AttributeColumn column) {
            this.column = column;
        }
    }

    private static class EqualNumberFilterBuilder implements FilterBuilder {

        private final AttributeColumn column;

        public EqualNumberFilterBuilder(AttributeColumn column) {
            this.column = column;
        }

        public Category getCategory() {
            return EQUAL;
        }

        public String getName() {
            return "<font color='#000000'>" + column.getTitle() + "</font> "
                    + "<font color='#999999'><i>" + column.getType().toString() + " "
                    + (AttributeUtils.getDefault().isNodeColumn(column) ? "(Node)" : "(Edge)") + "</i></font>";
        }

        public Icon getIcon() {
            return null;
        }

        public String getDescription() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.description");
        }

        public EqualNumberFilter getFilter() {
            if (AttributeUtils.getDefault().isNodeColumn(column)) {
                return new NodeEqualNumberFilter(column);
            } else {
                return new EdgeEqualNumberFilter(column);
            }
        }

        public JPanel getPanel(Filter filter) {
            EqualNumberUI ui = Lookup.getDefault().lookup(EqualNumberUI.class);
            if (ui != null) {
                return ui.getPanel((EqualNumberFilter) filter);
            }
            return null;
        }

        public void destroy(Filter filter) {
        }
    }

    public static class NodeEqualNumberFilter extends EqualNumberFilter implements NodeFilter {

        public NodeEqualNumberFilter(AttributeColumn column) {
            super(column);
        }
    }

    public static class EdgeEqualNumberFilter extends EqualNumberFilter implements EdgeFilter {

        public EdgeEqualNumberFilter(AttributeColumn column) {
            super(column);
        }
    }

    public static class EqualNumberFilter implements Filter {

        private FilterProperty[] filterProperties;
        private Number match;
        private AttributeColumn column;
        private Number min;
        private Number max;
        //State
        private Comparable[] values;
        private DynamicAttributesHelper dynamicHelper = new DynamicAttributesHelper(this, null);

        public EqualNumberFilter(AttributeColumn column) {
            this.column = column;
            this.dynamicHelper = new DynamicAttributesHelper(this, null);
        }

        public String getName() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.name");
        }

        public boolean init(Graph graph) {
            HierarchicalGraph hg = (HierarchicalGraph) graph;
            if (AttributeUtils.getDefault().isNodeColumn(column)) {
                if (graph.getNodeCount() == 0) {
                    return false;
                }
            } else if (AttributeUtils.getDefault().isEdgeColumn(column)) {
                if (hg.getTotalEdgeCount() == 0) {
                    return false;
                }
            }
            dynamicHelper = new DynamicAttributesHelper(this, hg);
            refreshValues(hg);
            return true;
        }

        public boolean evaluate(Graph graph, Node node) {
            Object val = node.getNodeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null) {
                return val.equals(match);
            }
            return false;
        }

        public boolean evaluate(Graph graph, Edge edge) {
            Object val = edge.getEdgeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null) {
                return val.equals(match);
            }
            return false;
        }

        public void finish() {
        }

        public void refreshValues(HierarchicalGraph graph) {
            List<Object> vals = new ArrayList<Object>();
            if (AttributeUtils.getDefault().isNodeColumn(column)) {
                for (Node n : graph.getNodes()) {
                    Object val = n.getNodeData().getAttributes().getValue(column.getIndex());
                    val = dynamicHelper.getDynamicValue(val);
                    if (val != null) {
                        vals.add(val);
                    }
                }
            } else {
                for (Edge e : graph.getEdgesAndMetaEdges()) {
                    Object val = e.getEdgeData().getAttributes().getValue(column.getIndex());
                    val = dynamicHelper.getDynamicValue(val);
                    if (val != null) {
                        vals.add(val);
                    }
                }
            }

            if (vals.isEmpty()) {
                vals.add(0);
            }

            values = ComparableArrayConverter.convert(vals);

            min = (Number) AttributeUtils.getDefault().getMin(column, values /*valuesArray*/);
            max = (Number) AttributeUtils.getDefault().getMax(column, values /*valuesArray*/);
            match = Range.tribToBounds(min, max, match);
        }

        public FilterProperty[] getProperties() {
            if (filterProperties == null) {
                filterProperties = new FilterProperty[0];
                try {
                    filterProperties = new FilterProperty[]{
                                FilterProperty.createProperty(this, AttributeColumn.class, "column"),
                                FilterProperty.createProperty(this, Number.class, "match"),};
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return filterProperties;
        }

        public Number getMatch() {
            return match;
        }

        public void setMatch(Number match) {
            this.match = match;
        }

        public Object getMinimun() {
            return min;
        }

        public Object getMaximum() {
            return max;
        }

        public AttributeColumn getColumn() {
            return column;
        }

        public void setColumn(AttributeColumn column) {
            this.column = column;
        }
    }

    private static class EqualBooleanFilterBuilder implements FilterBuilder {

        private final AttributeColumn column;

        public EqualBooleanFilterBuilder(AttributeColumn column) {
            this.column = column;
        }

        public Category getCategory() {
            return EQUAL;
        }

        public String getName() {
            return "<font color='#000000'>" + column.getTitle() + "</font> "
                    + "<font color='#999999'><i>" + column.getType().toString() + " "
                    + (AttributeUtils.getDefault().isNodeColumn(column) ? "(Node)" : "(Edge)") + "</i></font>";
        }

        public Icon getIcon() {
            return null;
        }

        public String getDescription() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.description");
        }

        public EqualBooleanFilter getFilter() {
            if (AttributeUtils.getDefault().isNodeColumn(column)) {
                NodeEqualBooleanFilter f = new NodeEqualBooleanFilter();
                f.setColumn(column);
                return f;
            } else {
                EdgeEqualBooleanFilter f = new EdgeEqualBooleanFilter();
                f.setColumn(column);
                return f;
            }
        }

        public JPanel getPanel(Filter filter) {
            EqualBooleanUI ui = Lookup.getDefault().lookup(EqualBooleanUI.class);
            if (ui != null) {
                return ui.getPanel((EqualBooleanFilter) filter);
            }
            return null;
        }

        public void destroy(Filter filter) {
        }
    }

    public static class NodeEqualBooleanFilter extends EqualBooleanFilter implements NodeFilter {
    }

    public static class EdgeEqualBooleanFilter extends EqualBooleanFilter implements EdgeFilter {
    }

    public static class EqualBooleanFilter implements Filter {

        private FilterProperty[] filterProperties;
        private boolean match = false;
        private AttributeColumn column;
        private DynamicAttributesHelper dynamicHelper = new DynamicAttributesHelper(this, null);

        public String getName() {
            return NbBundle.getMessage(AttributeEqualBuilder.class, "AttributeEqualBuilder.name");
        }

        public FilterProperty[] getProperties() {
            if (filterProperties == null) {
                filterProperties = new FilterProperty[0];
                try {
                    filterProperties = new FilterProperty[]{
                                FilterProperty.createProperty(this, AttributeColumn.class, "column"),
                                FilterProperty.createProperty(this, Boolean.class, "match")
                            };
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return filterProperties;
        }

        public boolean init(Graph graph) {
            HierarchicalGraph hg = (HierarchicalGraph) graph;
            dynamicHelper = new DynamicAttributesHelper(this, hg);
            return true;
        }

        public boolean evaluate(Graph graph, Node node) {
            Object val = node.getNodeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null) {
                return val.equals(match);
            }
            return false;
        }

        public boolean evaluate(Graph graph, Edge edge) {
            Object val = edge.getEdgeData().getAttributes().getValue(column.getIndex());
            val = dynamicHelper.getDynamicValue(val);
            if (val != null) {
                return val.equals(match);
            }
            return false;
        }

        public void finish() {
        }

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public AttributeColumn getColumn() {
            return column;
        }

        public void setColumn(AttributeColumn column) {
            this.column = column;
        }
    }
}
