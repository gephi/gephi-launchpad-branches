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

import org.gephi.graph.api.NodeShape;
import org.gephi.math.Vec2;
import org.gephi.visualization.api.Color;

/**
 * Immutable 2D node representation used by the rendering engine.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class VizNode2D {
    
    public final Vec2 position;
    
    public final float size;
    
    public final NodeShape shape;
    
    public final Color color;
    public final Color borderColor;

    public VizNode2D(Vec2 position, float size, NodeShape shape, Color color, Color borderColor) {
        this.position = position;
        this.size = size;
        this.shape = shape;
        this.color = color;
        this.borderColor = borderColor;
    }
}
