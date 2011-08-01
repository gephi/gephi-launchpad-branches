/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
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

package org.gephi.visualization.data.graph;

import org.gephi.math.linalg.Vec3;
import org.gephi.visualization.api.color.Color;

/**
 * Immutable 3D edge representation used by the rendering engine.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class VizEdge3D {

    public final Vec3 sourcePosition;
    public final float sourceSize;
    
    public final Vec3 destinationPosition;
    public final float destinationSize;
    
    public final float thickness;
    
    public final Color gradientStart;
    public final Color gradientEnd;
    
    public final VizEdgeShape shape;

    public VizEdge3D(Vec3 sourcePosition, float sourceSize, Vec3 destinationPosition, float destinationSize, float thickness, Color gradientStart, Color gradientEnd, VizEdgeShape shape) {
        this.sourcePosition = sourcePosition;
        this.sourceSize = sourceSize;
        this.destinationPosition = destinationPosition;
        this.destinationSize = destinationSize;
        this.thickness = thickness;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.shape = shape;
    }
}
