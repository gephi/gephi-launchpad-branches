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

import org.gephi.graph.api.Node;
import org.gephi.math.linalg.Vec2;
import org.gephi.math.linalg.Vec3;
import org.gephi.visualization.api.color.Color;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.data.graph.styler.NodeStyler;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.data.graph.VizNode3D;

/**
 * @author Vojtech Bardiovsky
 */
public class BasicNodeStyler implements NodeStyler {

    private final VizModel vizModel;
    private final VizConfig vizConfig;
    
    public BasicNodeStyler(VizModel vizModel) {
        this.vizModel = vizModel;
        this.vizConfig = vizModel.getConfig();
    }
    
    @Override
    public VizNode2D toVisual2D(Node node) {
        boolean selected = node.getNodeData().isSelected();
        boolean neighbor = node.getNodeData().isAutoSelected();
        Color color, borderColor;
        
        if (!selected) {
            if (vizConfig.getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED)) {
                float[] lightColor = vizConfig.getColorProperty(VizConfig.HIGHLIGHT_NON_SELECTED_COLOR).getColorComponents(null);
                float lightColorFactor = vizConfig.getFloatProperty(VizConfig.HIGHLIGHT_NON_SELECTED_FACTOR);
                // Node color
                float r = node.getNodeData().r();
                float g = node.getNodeData().g();
                float b = node.getNodeData().b();
                color = new Color(r + (lightColor[0] - r) * lightColorFactor, g + (lightColor[1] - g) * lightColorFactor, b + (lightColor[2] - b) * lightColorFactor);
                // Node border color
                float rborder = 0.498f * r;
                float gborder = 0.498f * g;
                float bborder = 0.498f * b;
                borderColor = new Color(rborder + (lightColor[0] - rborder) * lightColorFactor, gborder + (lightColor[1] - gborder) * lightColorFactor, bborder + (lightColor[2] - bborder) * lightColorFactor);
            } else {
                // Node color
                float r = node.getNodeData().r();
                float g = node.getNodeData().g();
                float b = node.getNodeData().b();
                color = new Color(r, g, b);
                // Node border color
                borderColor = new Color(0.498f * r, 0.498f * g, 0.498f * b);
            }
        } else {
            float[] c;
            float rborder;
            float gborder;
            float bborder;
            if (vizModel.isNodeSelectedUniqueColor()) {
                if (neighbor) {
                    c = vizConfig.getColorProperty(VizConfig.NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR).getColorComponents(null);
                } else {
                    c = vizConfig.getColorProperty(VizConfig.NODE_SELECTED_UNIQUE_COLOR).getColorComponents(null);
                }
                rborder = 0.498f * c[0];
                gborder = 0.498f * c[1];
                bborder = 0.498f * c[2];
            } else {
                rborder = node.getNodeData().r();
                gborder = node.getNodeData().g();
                bborder = node.getNodeData().b();
                c = new float[3];
                c[0] = Math.min(1, 0.5f * rborder + 0.5f);
                c[1] = Math.min(1, 0.5f * gborder + 0.5f);
                c[2] = Math.min(1, 0.5f * bborder + 0.5f);
            }
            color = new Color(c[0], c[1], c[2]);
            borderColor = new Color(rborder, gborder, bborder);
        }
        return new VizNode2D(new Vec2(node.getNodeData().x(), node.getNodeData().y()), node.getNodeData().getSize(), node.getNodeData().getNodeShape(), color, borderColor);
    }

    @Override
    public VizNode3D toVisual3D(Node node) {
        boolean selected = node.getNodeData().isSelected();
        boolean neighbor = node.getNodeData().isAutoSelected();
        Color color, borderColor;
        
        if (!selected) {
            if (vizConfig.getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED)) {
                float[] lightColor = vizConfig.getColorProperty(VizConfig.HIGHLIGHT_NON_SELECTED_COLOR).getColorComponents(null);
                float lightColorFactor = vizConfig.getFloatProperty(VizConfig.HIGHLIGHT_NON_SELECTED_FACTOR);
                // Node color
                float r = node.getNodeData().r();
                float g = node.getNodeData().g();
                float b = node.getNodeData().b();
                color = new Color(r + (lightColor[0] - r) * lightColorFactor, g + (lightColor[1] - g) * lightColorFactor, b + (lightColor[2] - b) * lightColorFactor);
                // Node border color
                float rborder = 0.498f * r;
                float gborder = 0.498f * g;
                float bborder = 0.498f * b;
                borderColor = new Color(rborder + (lightColor[0] - rborder) * lightColorFactor, gborder + (lightColor[1] - gborder) * lightColorFactor, bborder + (lightColor[2] - bborder) * lightColorFactor);
            } else {
                // Node color
                float r = node.getNodeData().r();
                float g = node.getNodeData().g();
                float b = node.getNodeData().b();
                color = new Color(r, g, b);
                // Node border color
                borderColor = new Color(0.498f * r, 0.498f * g, 0.498f * b);
            }
        } else {
            float[] c;
            float rborder;
            float gborder;
            float bborder;
            if (vizModel.isNodeSelectedUniqueColor()) {
                if (neighbor) {
                    c = vizConfig.getColorProperty(VizConfig.NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR).getColorComponents(null);
                } else {
                    c = vizConfig.getColorProperty(VizConfig.NODE_SELECTED_UNIQUE_COLOR).getColorComponents(null);
                }
                rborder = 0.498f * c[0];
                gborder = 0.498f * c[1];
                bborder = 0.498f * c[2];
            } else {
                rborder = node.getNodeData().r();
                gborder = node.getNodeData().g();
                bborder = node.getNodeData().b();
                c = new float[3];
                c[0] = Math.min(1, 0.5f * rborder + 0.5f);
                c[1] = Math.min(1, 0.5f * gborder + 0.5f);
                c[2] = Math.min(1, 0.5f * bborder + 0.5f);
            }
            color = new Color(c[0], c[1], c[2]);
            borderColor = new Color(rborder, gborder, bborder);
        }
        // TODO no border color in 3D?
        return new VizNode3D(new Vec3(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z()), node.getNodeData().getSize(), color);
    }
    
}
