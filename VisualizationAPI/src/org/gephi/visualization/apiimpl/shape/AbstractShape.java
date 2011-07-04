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
import java.util.HashSet;
import java.util.Set;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.selection.Shape;

/**
 * Abstract class for shapes that can be defined by two points (e.g. rectangle
 * is defined by origin corner and opposite corner).
 *
 * @author Vojtech Bardiovsky
 */
public abstract class AbstractShape implements Shape {

    private final static float SQUARE_ROOT = (float) Math.sqrt(2);
    private final static float CUBE_ROOT = (float) Math.sqrt(3);

    private SelectionModifier selectionModifier;

    // For optimization
    private float maxSize = 0f;
    private int projectedMaxSize = 0;

    public boolean isInside3D(float x, float y, float z, float radius, Camera camera) {
        Point point = camera.projectPoint(x, y, z);
        int size = camera.projectScale(radius);
        return isPointInside(point.x, point.y, size);
    }

    public Intersection intersectsSquare(float x, float y, float size, float maxNodeSize, Camera camera) {
        // Create a sphere around the square and test every corner point for inclusion
        int radius = camera.projectScale(size * SQUARE_ROOT);
        if (maxSize != maxNodeSize) {
            projectedMaxSize = camera.projectScale(maxNodeSize);
        }
        Point center = camera.projectPoint(x + size / 2, y + size / 2, 0);
        // Is shape inside the square's bounding sphere?

        // FIXME after camera is fixed, set radius instead of MAX_VALUE
        if (intersectsCircle(center.x, center.y, Integer.MAX_VALUE)) {
            return Intersection.INTERSECT;
        }
        // Is any square's corner point inside the shape?
        boolean intersect = false;
        boolean inside = true;
        int i = 0;
        while (i < 4 && (!intersect || inside)) {
            if (isInside3D(x + SQUARE_CORNERS[i][0] * size, y + SQUARE_CORNERS[i][1] * size, 0, projectedMaxSize, camera)) {
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

    public Intersection intersectsCube(float x, float y, float z, float size, float maxNodeSize, Camera camera) {
        // Create a sphere around the cube and test every corner point for inclusion
        int radius = camera.projectScale(size * CUBE_ROOT);
        if (maxSize != maxNodeSize) {
            projectedMaxSize = camera.projectScale(maxNodeSize);
        }
        Point center = camera.projectPoint(x + size / 2, y + size / 2, z + size / 2);
        // Is shape inside the cube's bounding sphere?

        // FIXME after camera is fixed, set radius instead of MAX_VALUE
        if (intersectsCircle(center.x, center.y, Integer.MAX_VALUE)) {
            return Intersection.INTERSECT;
        }
        // Is any cube's corner point inside the shape?
        boolean intersect = false;
        boolean inside = true;
        int i = 0;
        while (i < 8 && (!intersect || inside)) {
            if (isInside3D(x + CUBE_CORNERS[i][0] * size, y + CUBE_CORNERS[i][1] * size, z + CUBE_CORNERS[i][2] * size, projectedMaxSize, camera)) {
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

    private static final int[][] CUBE_CORNERS = new int[][]{
            new int[]{0, 0, 0},
            new int[]{0, 0, 1},
            new int[]{0, 1, 0},
            new int[]{0, 1, 1},
            new int[]{1, 0, 0},
            new int[]{1, 0, 1},
            new int[]{1, 1, 0},
            new int[]{1, 1, 1}
    };

    private static final int[][] SQUARE_CORNERS = new int[][]{
            new int[]{0, 0, 0},
            new int[]{0, 1, 0},
            new int[]{1, 0, 0},
            new int[]{1, 1, 0},
    };

    public SelectionModifier getSelectionModifier() {
        return selectionModifier;
    }

    public void setSelectionModifier(SelectionModifier selectionModifier) {
        this.selectionModifier = selectionModifier;
    }

}
