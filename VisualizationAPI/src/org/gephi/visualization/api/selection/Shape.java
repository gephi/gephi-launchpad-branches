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

import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.view.ui.UIShape;

public interface Shape {

    /**
     * Returns true if given 3D coordinate point is inside the projection
     * frustum.
     */
    public boolean isInside3D(float x, float y, float z, float radius, Camera camera);

    /**
     * Determines intersection of box with the shape.
     */
    public Intersection intersectsBox(float x, float y, float z, float size, float maxNodeSize, Camera camera);

    Shape singleUpdate(int x, int y);

    Shape continuousUpdate(int x, int y);

    /**
     * Returns true if this shape is built of many discrete steps and where
     * every single update alters the shapes properties, such as a polygon.
     */
    public boolean isDiscretelyUpdated();

    public UIShape getUIPrimitive();

    public SelectionType getSelectionType();

    public SelectionModifier getSelectionModifier();

    public void setSelectionModifier(SelectionModifier selectionModifier);

    public enum Intersection {OUTSIDE, INTERSECT, FULLY_INSIDE};

    public enum SelectionModifier {
        DEFAULT,
        INCREMENTAL,
        DECREMENTAL;

        public boolean isPositive() {
            return this != DECREMENTAL;
        }

    };

}
