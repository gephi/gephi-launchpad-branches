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
package org.gephi.layout.plugin.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class RandomLayout extends AbstractLayout implements Layout {

    private Random random;
    private Graph graph;
    private boolean converged;
    private double size;

    public RandomLayout(LayoutBuilder layoutBuilder, double size) {
        super(layoutBuilder);
        this.size = size;
        random = new Random();
    }

    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
    }

    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        for (Node n : graph.getNodes()) {
            n.getNodeData().setX((float) (-size / 2 + size * random.nextDouble()));
            n.getNodeData().setY((float) (-size / 2 + size * random.nextDouble()));
        }
        converged = true;
    }

    @Override
    public boolean canAlgo() {
        return !converged;
    }

    public void endAlgo() {
    }

    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class, 
                    NbBundle.getMessage(getClass(), "Random.spaceSize.name"),
                    null,
                    "Random.spaceSize.name",
                    NbBundle.getMessage(getClass(), "Random.spaceSize.desc"),
                    "getSize", "setSize"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    public void resetPropertiesValues() {
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getSize() {
        return size;
    }
}
