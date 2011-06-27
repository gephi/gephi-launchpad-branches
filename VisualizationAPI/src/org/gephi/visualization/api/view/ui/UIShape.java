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

import java.nio.ByteBuffer;
import org.gephi.math.Vec2;

/**
 * 2D shape which can be drawn on screen for UI purposes.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class UIShape {
    private UIShape(UIStyle style) { this.style = style; }

    private final UIStyle style;

    public enum Type {
        CONVEX_POLYGON,
        ELLIPSE;
    };

    public final UIStyle style() {
        return this.style;
    }

    public final int binarySize() {
        return UIStyle.binarySize + 4 + this.binarySizeData();
    }

    public abstract Type type();
    protected abstract int binarySizeData();
    protected abstract void writeDataTo(ByteBuffer b);
    protected abstract int writeDataTo(ByteBuffer b, int i);

    public final void writeTo(ByteBuffer b) {
        this.style.writeTo(b);
        b.putInt(this.type().ordinal());
        this.writeDataTo(b);
    }

    public final int writeTo(ByteBuffer b, int i) {
        i = this.style.writeTo(b, i);
        b.putInt(i, this.type().ordinal());
        return this.writeDataTo(b, i+4);
    }

    public static final class UIConvexPolygon extends UIShape {
        private final Vec2[] points;

        private UIConvexPolygon(UIStyle style, Vec2[] points) {
            super(style);
            this.points = points;
        }

        public Vec2 point(int i) {
            return this.points[i];
        }

        public int numPoints() {
            return this.points.length;
        }

        @Override
        public Type type() {
            return Type.CONVEX_POLYGON;
        }

        @Override
        protected void writeDataTo(ByteBuffer b) {
            b.putInt(this.points.length);
            for (Vec2 pnt : this.points) {
                pnt.writeTo(b);
            }
        }

        @Override
        protected int writeDataTo(ByteBuffer b, int i) {
            b.putInt(i, this.points.length);
            i += 4;
            for (Vec2 pnt : this.points) {
                i = pnt.writeTo(b, i);
            }
            return i;
        }

        private static UIShape readDataFrom(UIStyle style, ByteBuffer b) {
            int len = b.getInt();
            Vec2[] points = new Vec2[len];
            for (int i = 0; i < len; ++i) {
                points[i] = Vec2.readFrom(b);
            }
            return new UIConvexPolygon(style, points);
        }

        private static UIShape readDataFrom(UIStyle style, ByteBuffer b, int[] i) {
            int len = b.getInt(i[0]); i[0] += 4;
            Vec2[] points = new Vec2[len];
            for (int j = 0; j < len; ++j) {
                points[j] = Vec2.readFrom(b, i);
            }
            return new UIConvexPolygon(style, points);
        }

        @Override
        protected int binarySizeData() {
            return this.points.length * 8 + 4;
        }
    }

    public static final class UIEllipse extends UIShape {
        public final Vec2 center;
        public final Vec2 axis1;
        public final Vec2 axis2;

        private UIEllipse(UIStyle style, Vec2 center, Vec2 axis1, Vec2 axis2) {
            super(style);
            this.center = center;
            this.axis1 = axis1;
            this.axis2 = axis2;
        }

        @Override
        public Type type() {
            return Type.ELLIPSE;
        }

        @Override
        protected void writeDataTo(ByteBuffer b) {
            this.center.writeTo(b);
            this.axis1.writeTo(b);
            this.axis2.writeTo(b);
        }

        @Override
        protected int writeDataTo(ByteBuffer b, int i) {
            i = this.center.writeTo(b, i);
            i = this.axis1.writeTo(b, i);
            return this.axis2.writeTo(b, i);
        }

        private static UIShape readDataFrom(UIStyle style, ByteBuffer b) {
            Vec2 center = Vec2.readFrom(b);
            Vec2 axis1 = Vec2.readFrom(b);
            Vec2 axis2 = Vec2.readFrom(b);
            return new UIEllipse(style, center, axis1, axis2);
        }

        private static UIShape readDataFrom(UIStyle style, ByteBuffer b, int[] i) {
            Vec2 center = Vec2.readFrom(b, i);
            Vec2 axis1 = Vec2.readFrom(b, i);
            Vec2 axis2 = Vec2.readFrom(b, i);
            return new UIEllipse(style, center, axis1, axis2);
        }

        @Override
        protected int binarySizeData() {
            return 3 * 8;
        }
    }

    public static UIShape triangle(UIStyle style, Vec2 a, Vec2 b, Vec2 c) {
        Vec2[] points = new Vec2[]{a, b, c};
        return new UIConvexPolygon(style, points);
    }

    public static UIShape quad(UIStyle style, Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
        Vec2[] points = new Vec2[]{a, b, c, d};
        return new UIConvexPolygon(style, points);
    }

    public static UIShape orientedQuad(UIStyle style, Vec2 topLeft, Vec2 bottomRight) {
        return quad(style, topLeft, new Vec2(topLeft.x(), bottomRight.y()), bottomRight, new Vec2(bottomRight.x(), topLeft.y()));
    }

    public static UIShape polygon(UIStyle style, Vec2[] pnts) {
        Vec2[] points = new Vec2[pnts.length];
        System.arraycopy(pnts, 0, points, 0, pnts.length);
        return new UIConvexPolygon(style, points);
    }

    public static UIShape circle(UIStyle style, Vec2 center, float radius) {
        return ellipse(style, center, Vec2.E1.times(radius), Vec2.E2.times(radius));
    }

    public static UIShape ellipse(UIStyle style, Vec2 center, Vec2 a1, Vec2 a2) {
        return new UIEllipse(style, center, a1, a2);
    }

    public static UIShape orientedEllipse(UIStyle style, Vec2 center, float a, float b) {
        return ellipse(style, center, Vec2.E1.times(a), Vec2.E2.times(b));
    }

    public static UIShape readFrom(ByteBuffer b) {
        UIStyle style = UIStyle.readFrom(b);
        Type shape = Type.values()[b.getInt()];
        switch (shape) {
            case CONVEX_POLYGON:
                return UIConvexPolygon.readDataFrom(style, b);
            case ELLIPSE:
                return UIEllipse.readDataFrom(style, b);
            default:
                return null;
        }
    }

    public static UIShape readFrom(ByteBuffer b, int[] i) {
        UIStyle style = UIStyle.readFrom(b, i);
        Type shape = Type.values()[b.getInt(i[0])]; i[0] += 4;
        switch (shape) {
            case CONVEX_POLYGON:
                return UIConvexPolygon.readDataFrom(style, b, i);
            case ELLIPSE:
                return UIEllipse.readDataFrom(style, b, i);
            default:
                return null;
        }
    }
}
