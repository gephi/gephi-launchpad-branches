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

package org.gephi.visualization.api.view.ui;

import org.gephi.lib.gleem.linalg.Vec2f;

/**
 *
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class UIPrimitive {

    public enum Shape {
        CONVEX_POLYGON,
        ELLIPSES,
    };

    private final Shape shape;
    private final float[] arguments;

    private UIPrimitive(Shape shape, float[] arguments) {
        this.shape = shape;
        this.arguments = arguments;
    }

    public static UIPrimitive triangle(Vec2f a, Vec2f b, Vec2f c) {
        float[] args = new float[]{a.x(), a.y(), b.x(), b.y(), c.x(), c.y()};
        return new UIPrimitive(Shape.CONVEX_POLYGON, args);
    }

    public static UIPrimitive quad(Vec2f a, Vec2f b, Vec2f c, Vec2f d) {
        float[] args = new float[]{a.x(), a.y(), b.x(), b.y(), c.x(), c.y(), d.x(), d.y()};
        return new UIPrimitive(Shape.CONVEX_POLYGON, args);
    }

    public static UIPrimitive orientedQuad(Vec2f topLeft, Vec2f bottomRight) {
        float[] args = new float[]{topLeft.x(), topLeft.y(), topLeft.x(), bottomRight.y(), bottomRight.x(), bottomRight.y(), bottomRight.x(), topLeft.y()};
        return new UIPrimitive(Shape.CONVEX_POLYGON, args);
    }

    public static UIPrimitive polygon(Vec2f[] pnts) {
        float[] args = new float[2 * pnts.length];
        for (int i = 0; i < pnts.length; ++i) {
            args[2*i] = pnts[i].x();
            args[2*i+1] = pnts[i].y();
        }
        return new UIPrimitive(Shape.CONVEX_POLYGON, args);
    }

    public static UIPrimitive circle(Vec2f center, float radius) {
        return ellipses(center, new Vec2f(radius, 0.0f), new Vec2f(0.0f, radius));
    }

    public static UIPrimitive ellipses(Vec2f center, Vec2f a1, Vec2f a2) {
        float[] args = new float[]{center.x(), center.y(), a1.x(), a1.y(), a2.x(), a2.y()};
        return new UIPrimitive(Shape.ELLIPSES, args);
    }

    public static UIPrimitive fromData(Shape shape, float[] arguments) {
        switch (shape) {
            case CONVEX_POLYGON:
                if ((arguments.length & 1) != 0 || arguments.length < 6) return null;
                else return new UIPrimitive(shape, arguments);
            case ELLIPSES:
                if (arguments.length != 6) return null;
                else return new UIPrimitive(shape, arguments);
            default:
                return null;
        }
    }

    public Shape shape() {
        return this.shape;
    }

    public float[] arguments() {
        return this.arguments;
    }
}
