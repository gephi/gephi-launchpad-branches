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

package org.gephi.visualization.apiimpl.shape;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gephi.lib.gleem.linalg.Vec2f;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;

/**
 * Class representing a polygon shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Polygon extends AbstractShape {

    private final Set<Point> points;
    private final Point tempPoint;

    private final Point[] convexHull;
    private final float[][] lineInfo;

    Polygon(int x, int y) {
        Point point = new Point(x, y);
        this.points = new HashSet<Point>();
        this.points.add(point);
        this.tempPoint = point;
        this.convexHull = new Point[] {point};
        this.lineInfo = new float[1][4];
    }

    Polygon(Polygon polygon, Point tempPoint) {
        this.points = polygon.points;
        this.tempPoint = tempPoint;
        this.convexHull = computeConvexHull();
        this.lineInfo = computeLineInfo();
    }

    public boolean isPointInside(int x, int y) {
        if (lineInfo.length <= 2) {
            return false;
        }
        for (int i = 0; i < lineInfo.length; i++) {
            // Difference vector betwen point and line center
            float vecx = x - convexHull[i].x;
            float vecy = y - convexHull[i].y;
            // Compute inner product
            if (vecx * lineInfo[i][0] + vecy * lineInfo[i][1] > 0) {
                return false;
            }
        }
        return true;
    }

    public Shape singleUpdate(int x, int y) {
        points.add(new Point(x, y));
        return new Polygon(this, new Point(x, y));
    }

    public Shape continuousUpdate(int x, int y) {
        return new Polygon(this, new Point(x, y));
    }

    public UIPrimitive getUIPrimitive() {
        Vec2f[] polygonPoints = new Vec2f[convexHull.length];
        for (int i = 0; i < convexHull.length; i++) {
            polygonPoints[i] = new Vec2f(convexHull[i].x, convexHull[i].y);
        }
        return UIPrimitive.polygon(polygonPoints);
    }

    private float[][] computeLineInfo() {
        // First two elements for line normal, other two for line center
        float[][] lNormals = new float[convexHull.length][2];
        for (int i = 0; i < convexHull.length; i++) {
            int j = i < convexHull.length - 1 ? i + 1 : 0;
            // Normal
            lNormals[i][0] = -(convexHull[j].y - convexHull[i].y);
            lNormals[i][1] = convexHull[j].x - convexHull[i].x;
        }
        return lNormals;
    }

    private Point[] computeConvexHull() {
        Point[] ps = new Point[points.size() + 1];
        int i = 0;
        for (Point point : points) {
            ps[i++] = point;
        }
        ps[i] = tempPoint;

        // If 2 points
        if (ps.length == 2) {
            return ps;
        }
        
        // Compute convex hull
        Arrays.sort(ps, new Comparator<Point>() {
            public int compare(Point o1, Point o2) {
                return o1.x > o2.x ? 1 :
                       o1.x < o2.x ? -1 :
                       o1.y > o2.y ? 1 :
                       o1.y < o2.y ? -1 : 0;
            }
        });
        List<Point> polygon = new ArrayList<Point>();
        // ps[0] contains the most bottom left vertex
        polygon.add(ps[0]);
        i = 0;
        while (i < ps.length - 1) {
            Point best = ps[i + 1];
            int b = i + 1;
            for (int j = i + 1; j < ps.length; j++) {
                if ((ps[j].x == best.x && ps[j].y > best.y) ||
                    (best.x != ps[i].x &&
                     ps[j].x > best.x && (ps[j].y - ps[i].y) / (float) (ps[j].x - ps[i].x) >
                                         (best.y - ps[i].y) / (float) (best.x - ps[i].x))) {
                     best = ps[j];
                     b = j;
                }
            }
            polygon.add(best);
            i = b;
        }
        i = ps.length - 1;
        while (i > 0) {
            Point best = ps[i - 1];
            int b = i - 1;
            for (int j = i - 1; j >= 0; j--) {
                if ((ps[j].x == best.x && ps[j].y < best.y) ||
                    (best.x != ps[i].x &&
                     ps[j].x < best.x && (ps[j].y - ps[i].y) / (float) (ps[j].x - ps[i].x) >
                                         (best.y - ps[i].y) / (float) (best.x - ps[i].x))) {
                     best = ps[j];
                     b = j;
                }
            }
            if (b != 0) {
                polygon.add(best);
            }
            i = b;
        }
        return polygon.toArray(new Point[]{});
    }

    public boolean isDiscretelyUpdated() {
        return true;
    }

    @Override
    protected boolean intersectsCircle(int x, int y, int radius) {
        for (Point point : points) {
            if ((point.x - x) * (point.x - x) + (point.y - y) * (point.y - y) <= radius * radius) {
                return true;
            }
        }
        return ((tempPoint.x - x) * (tempPoint.x - x) + (tempPoint.y - y) * (tempPoint.y - y) <= radius * radius);
    }

    public SelectionType getSelectionType() {
        return SelectionType.POLYGON;
    }

}
