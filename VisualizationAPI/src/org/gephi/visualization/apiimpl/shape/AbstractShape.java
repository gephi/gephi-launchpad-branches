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

package org.gephi.visualization.apiimpl.shape;

import java.awt.Point;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.selection.Shape;

/**
 * Abstract class for shapes that can be defined by two points (e.g. rectangle
 * is defined by origin corner and opposite corner).
 *
 * @author Vojtech Bardiovsky
 */
public abstract class AbstractShape implements Shape {

    private final static float THIRD_ROOT = (float) Math.sqrt(3);

    private SelectionModifier selectionModifier;

    // For optimization
    private float maxSize = 0f;
    private int projectedMaxSize = 0;

    public boolean isInside3D(float x, float y, float z, float radius, Camera camera) {
        Point point = camera.projectPoint(x, y, z);
        int size = camera.projectScale(radius);
        return isPointInside(point.x, point.y, size);
    }

    public Intersection intersectsBox(float x, float y, float z, float size, float maxNodeSize, Camera camera) {
        // Create a sphere around the box and test every corner point for inclusion
        int radius = camera.projectScale(size * THIRD_ROOT);
        if (maxSize != maxNodeSize) {
            projectedMaxSize = camera.projectScale(maxNodeSize);
        }
        Point center = camera.projectPoint(x + size / 2, y + size / 2, z + size / 2);
        // Is shape inside the boxes bounding sphere?
        if (intersectsCircle(center.x, center.y, Integer.MAX_VALUE)) {
            return Intersection.INTERSECT;
        }
        // Is any box corner point inside the shape?
        boolean intersect = false;
        boolean inside = true;
        int i = 0;
        while (i < 8 && (!intersect || inside)) {
            if (isInside3D(x + BOX_CORNERS[i][0] * size, y + BOX_CORNERS[i][1] * size, z + BOX_CORNERS[i][2] * size, projectedMaxSize, camera)) {
                intersect = true;
            } else {
                inside = false;
            }
            i++;
        }
        if (intersect) {
            return inside ? Intersection.FULLY_INSIDE : Intersection.INTERSECT;
        } else {
            return Intersection.OUTSIDE;
        }
    }

    /**
     * Returns true if shape intersects a given circle.
     */
    protected abstract boolean intersectsCircle(int x, int y, int radius);

    /**
     * Returns true if given 2D screen coordinate point is inside the shape.
     */
    protected abstract boolean isPointInside(int x, int y, int radius);

    private static final int[][] BOX_CORNERS = new int[][]{
            new int[]{0, 0, 0},
            new int[]{0, 0, 1},
            new int[]{0, 1, 0},
            new int[]{0, 1, 1},
            new int[]{1, 0, 0},
            new int[]{1, 0, 1},
            new int[]{1, 1, 0},
            new int[]{1, 1, 1}
    };

    public SelectionModifier getSelectionModifier() {
        return selectionModifier;
    }

    public void setSelectionModifier(SelectionModifier selectionModifier) {
        this.selectionModifier = selectionModifier;
    }

}
