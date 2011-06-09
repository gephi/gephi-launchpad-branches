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

package org.gephi.visualization.api.selection;

import org.gephi.visualization.api.view.ui.UIPrimitive;

public interface Shape {

    public enum Intersection {OUTSIDE, INTERSECT, FULLY_INSIDE};

    /**
     * Returns true if given 3D coordinate point is inside the projection
     * frustum.
     */
    boolean isInside3D(float x, float y, float z, CameraBridge cameraBridge);

    /**
     * Determines intersection of box with the shape.
     */
    Intersection intersectsBox(float x, float y, float z, float size, CameraBridge cameraBridge);

    /**
     * Returns true if given 2D screen coordinate point is inside the shape.
     */
    boolean isPointInside(int x, int y);

    Shape singleUpdate(int x, int y);

    Shape continuousUpdate(int x, int y);

    /**
     * Returns true if this shape is built of many discrete steps and where
     * every single update alters the shapes properties, such as a polygon.
     */
    boolean isDiscretelyUpdated();

    public UIPrimitive getUIPrimitive();
    
}
