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
package org.gephi.graph.dhns.edge;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.MetaEdge;
import org.gephi.graph.api.Node;
import org.gephi.graph.spi.MetaEdgeBuilder;

/**
 *
 * @author Mathieu Bastian
 */
public class SumMetaEdgeBuilder implements MetaEdgeBuilder {

    //If the edge pushed to the metaEdge has a common adjacent node with the metaEdge, it is called
    //a non-deep edge and should be considered less important and thus less weighted
    private float nonDeepDivisor = 1f;

    public SumMetaEdgeBuilder() {
    }

    public SumMetaEdgeBuilder(float divisor) {
        this.nonDeepDivisor = divisor;
    }

    public void pushEdge(Edge edge, Node source, Node target, MetaEdge metaEdge) {
        float edgeWeight = edge.getWeight();
        float metaWeight = metaEdge.getWeight();
        float div = 1f;
        if (edge.getSource() == metaEdge.getSource()
                || edge.getSource() == metaEdge.getTarget()
                || edge.getTarget() == metaEdge.getTarget()
                || edge.getTarget() == metaEdge.getSource()) {
            div = nonDeepDivisor;
        }
        metaWeight += edgeWeight / div;
        metaEdge.setWeight(metaWeight);
    }

    public void pullEdge(Edge edge, Node source, Node target, MetaEdge metaEdge) {
        float edgeWeight = edge.getWeight();
        float metaWeight = metaEdge.getWeight();
        float div = 1f;
        if (source == metaEdge.getSource()
                || source == metaEdge.getTarget()
                || target == metaEdge.getTarget()
                || target == metaEdge.getSource()) {
            div = nonDeepDivisor;
        }
        metaWeight -= edgeWeight / div;
        metaEdge.setWeight(metaWeight);
    }
}
