/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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

package org.gephi.visualization.model.styler;

import org.gephi.graph.api.Edge;
import org.gephi.visualization.data.graph.styler.EdgeStyler;
import org.gephi.graph.api.MetaEdge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.math.linalg.Vec2;
import org.gephi.visualization.api.Color;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.api.vizmodel.GraphLimits;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdge3D;
import org.gephi.visualization.data.graph.VizEdgeShape;

/**
 * @author Vojtech Bardiovsky
 */
public class BasicEdgeStyler implements EdgeStyler {

    protected static final float WEIGHT_MINIMUM = 0.4f;
    protected static final float WEIGHT_MAXIMUM = 8f;
    
    private final VizModel vizModel;
    private final VizConfig vizConfig;
    
    public BasicEdgeStyler(VizModel vizModel) {
        this.vizModel = vizModel;
        this.vizConfig = vizModel.getConfig();
    }
    
    @Override
    public VizEdge2D toVisual2D(Edge edge) {
        boolean selected = edge.getSource().getNodeData().isSelected() || edge.getTarget().getNodeData().isSelected();
        if (!selected && vizModel.isHideNonSelectedEdges()) {
            return null;
        }
        Color color1, color2;
        // Edge weight
        GraphLimits limits = vizModel.getGraphLimits();
        float weight = edge.getWeight();
        float w;
        if (edge instanceof MetaEdge) {
            float weightRatio;
            if (limits.getMinMetaWeight() == limits.getMaxMetaWeight()) {
                weightRatio = WEIGHT_MINIMUM / limits.getMinMetaWeight();
            } else {
                weightRatio = Math.abs((WEIGHT_MAXIMUM - WEIGHT_MINIMUM) / (limits.getMaxMetaWeight() - limits.getMinMetaWeight()));
            }
            float edgeScale = vizModel.getEdgeScale() * vizModel.getMetaEdgeScale();
            w = weight;
            w = ((w - limits.getMinMetaWeight()) * weightRatio + WEIGHT_MINIMUM) * edgeScale;
        } else {
            float weightRatio;
            if (limits.getMinWeight() == limits.getMaxWeight()) {
                weightRatio = WEIGHT_MINIMUM / limits.getMinWeight();
            } else {
                weightRatio = Math.abs((WEIGHT_MAXIMUM - WEIGHT_MINIMUM) / (limits.getMaxWeight() - limits.getMinWeight()));
            }
            float edgeScale = vizModel.getEdgeScale();
            w = weight;
            w = ((w - limits.getMinWeight()) * weightRatio + WEIGHT_MINIMUM) * edgeScale;
        }

        if (!selected) {
            float r1, r2;
            float g1, g2;
            float b1, b2;
            float a;
            r1 = r2 = edge.getEdgeData().r();
            if (r1 == -1f) {
                if (vizModel.isEdgeHasUniColor()) {
                    Color uni = new Color(vizModel.getEdgeUniColor());
                    r1 = r2 = uni.r;
                    g1 = g2 = uni.g;
                    b1 = b2 = uni.b;
                    a = uni.a;
                } else {
                    NodeData nodeData = edge.getSource().getNodeData();
                    r1 = 0.498f * nodeData.r();
                    g1 = 0.498f * nodeData.g();
                    b1 = 0.498f * nodeData.b();
                    a = nodeData.alpha();
                    nodeData = edge.getTarget().getNodeData();
                    r2 = 0.498f * nodeData.r();
                    g2 = 0.498f * nodeData.g();
                    b2 = 0.498f * nodeData.b();
                }
            } else {
                g1 = g2 = 0.498f * edge.getEdgeData().g();
                b1 = b2 = 0.498f * edge.getEdgeData().b();
                r1 *= 0.498f;
                r2 *= 0.498f;
                a = edge.getEdgeData().alpha();
            }
            if (vizConfig.getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED)) {
                float lightColorFactor = vizModel.getConfig().getFloatProperty(VizConfig.HIGHLIGHT_NON_SELECTED_FACTOR);
                a = a - (a - 0.01f) * lightColorFactor;
            }
            color1 = new Color(r1, g1, b1, a);
            color2 = new Color(r2, g2, b2, a);
        } else {
            if (vizModel.isEdgeSelectionColor()) {
                float r = 0f;
                float g = 0f;
                float b = 0f;
                Node node1 = edge.getSource();
                Node node2 = edge.getTarget();
                if (node1.getNodeData().isSelected() && node2.getNodeData().isSelected()) {
                    Color both = new Color(vizModel.getEdgeBothSelectionColor());
                    r = both.r;
                    g = both.g;
                    b = both.b;
                } else if (node1.getNodeData().isSelected()) {
                    Color out = new Color(vizModel.getEdgeOutSelectionColor());
                    r = out.r;
                    g = out.g;
                    b = out.b;
                } else if (node2.getNodeData().isSelected()) {
                    Color in = new Color(vizModel.getEdgeInSelectionColor());
                    r = in.r;
                    g = in.g;
                    b = in.b;
                }
                color1 = color2 = new Color(r, g, b, 1f);
            } else {
                float r1, r2;
                float g1, g2;
                float b1, b2;
                r1 = r2 = edge.getEdgeData().r();
                if (r1 == -1f) {
                    NodeData nodeData = edge.getSource().getNodeData();
                    r1 = 0.498f * nodeData.r();
                    g1 = 0.498f * nodeData.g();
                    b1 = 0.498f * nodeData.b();
                    nodeData = edge.getTarget().getNodeData();
                    r2 = 0.498f * nodeData.r();
                    g2 = 0.498f * nodeData.g();
                    b2 = 0.498f * nodeData.b();
                } else {
                    g1 = g2 = edge.getEdgeData().g();
                    b1 = b2 = edge.getEdgeData().b();
                }
                color1 = new Color(r1, g1, b1, 1f); 
                color2 = new Color(r2, g2, b2, 1f);
            }
        }
        // bidirectional?
        VizEdgeShape shape = edge.isSelfLoop() ? VizEdgeShape.EDGE_LOOP : 
                            (!edge.isDirected() ? VizEdgeShape.STRAIGHT_EDGE_NO_DIRECTION :
                            VizEdgeShape.STRAIGHT_EDGE_DIRECTIONAL);
        return new VizEdge2D(new Vec2(edge.getSource().getNodeData().x(), edge.getSource().getNodeData().y()),
                             edge.getSource().getNodeData().getSize(),
                             edge.getSource().getNodeData().getNodeShape(),
                             new Vec2(edge.getTarget().getNodeData().x(), edge.getTarget().getNodeData().y()),
                             edge.getTarget().getNodeData().getSize(),
                             edge.getTarget().getNodeData().getNodeShape(),
                             w,
                             color1,
                             color2,
                             shape);
    }

    @Override
    public VizEdge3D toVisual3D(Edge edge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
