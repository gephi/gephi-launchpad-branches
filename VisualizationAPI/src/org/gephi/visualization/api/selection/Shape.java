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

import java.awt.Point;

public interface Shape {

    /**
     * Returns ellipsoid that is a superset of intersection of the shape
     * projected into space and frustum given by the camera.
     */
    Ellipsoid getBoundingEllipsoid(CameraBridge cameraBridge);

    /**
     * Returns true if given 2D screen coordinate point is inside the shape.
     */
    boolean isInside(Point point);

    public class Ellipsoid {
        private final float center, a, b, c;

        public Ellipsoid(float center, float a, float b, float c) {
            this.center = center;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public float getA() {
            return a;
        }

        public float getB() {
            return b;
        }

        public float getC() {
            return c;
        }

        public float getCenter() {
            return center;
        }

    }

}
