/*
Copyright 2008-2010 Gephi
Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.plugin.rotate;

import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 * Sample layout that simply rotates the graph.
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class RotateLayout extends AbstractLayout implements Layout {

    private double angle;
    private Graph graph;

    public RotateLayout(LayoutBuilder layoutBuilder, double angle) {
        super(layoutBuilder);
        this.angle = angle;
    }

    public void initAlgo() {
        graph = graphModel.getGraphVisible();
        setConverged(false);
    }

    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        double sin = Math.sin(getAngle() * Math.PI / 180);
        double cos = Math.cos(getAngle() * Math.PI / 180);
        double px = 0f;
        double py = 0f;

        for (Node n : graph.getNodes()) {
            double dx = n.getNodeData().x() - px;
            double dy = n.getNodeData().y() - py;

            n.getNodeData().setX((float) (px + dx * cos - dy * sin));
            n.getNodeData().setY((float) (py + dy * cos + dx * sin));
        }
        setConverged(true);
    }

    public void endAlgo() {
    }

    public void resetPropertiesValues() {
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, 
                    NbBundle.getMessage(getClass(), "clockwise.angle.name"),
                    null,
                    "clockwise.angle.name",
                    NbBundle.getMessage(getClass(), "clockwise.angle.desc"),
                    "getAngle", "setAngle"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    /**
     * @return the angle
     */
    public Double getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(Double angle) {
        this.angle = angle;
    }
}
