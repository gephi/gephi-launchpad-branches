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

    Polygon(int x, int y) {
        this.points = new HashSet<Point>();
        this.points.add(new Point(x, y));
        this.tempPoint = new Point(x, y);
    }

    Polygon(Polygon polygon, Point tempPoint) {
        this.points = polygon.points;
        this.tempPoint = tempPoint;
    }

    public boolean isPointInside(int x, int y) {
        // TODO implement
        return false;
    }

    public Shape singleUpdate(int x, int y) {
        points.add(new Point(x, y));
        return new Polygon(this, new Point(x, y));
    }

    public Shape continuousUpdate(int x, int y) {
        return new Polygon(this, new Point(x, y));
    }

    public UIPrimitive getUIPrimitive() {
        List<Point> convexHull = computeConvexHull();
        Vec2f[] polygonPoints = new Vec2f[convexHull.size()];
        int i = 0;
        for (Point point : convexHull) {
            polygonPoints[i++] = new Vec2f(point.x, point.y);
        }
        return UIPrimitive.polygon(polygonPoints);
    }

    private List<Point> computeConvexHull() {
        Point[] ps = new Point[points.size() + 1];
        int i = 0;
        for (Point point : points) {
            ps[i++] = point;
        }
        ps[i] = tempPoint;

        // If 2 points or 1 point
        if (ps.length == 2) {
            return Arrays.asList(ps);
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
                     ps[j].x > best.x && (ps[j].y - ps[i].y) / (ps[j].x - ps[i].x) >
                                         (best.y - ps[i].y) / (best.x - ps[i].x))) {
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
                     ps[j].x < best.x && (ps[j].y - ps[i].y) / (ps[j].x - ps[i].x) >
                                         (best.y - ps[i].y) / (best.x - ps[i].x))) {
                     best = ps[j];
                     b = j;
                }
            }
            if (b != 0) {
                polygon.add(best);
            }
            i = b;
        }
        return polygon;
    }

    public boolean isDiscretelyUpdated() {
        return true;
    }

    @Override
    protected boolean intersectsCircle(int x, int y, int radius) {
        return true;
    }

}
