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
import org.gephi.visualization.data.graph.EdgeStyler;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdge3D;

/**
 * @author Vojtech Bardiovsky
 */
public class BasicEdgeStyler implements EdgeStyler {

    @Override
    public VizEdge2D toVisual2D(Edge node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VizEdge3D toVisual3D(Edge node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
