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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;

/**
 * Class representing a polygon shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Polygon extends AbstractShape {

    private final List<Point> points;
    private final Point tempPoint;

    private Polygon(Point initalPoint) {
        this.points = new ArrayList<Point>();
        this.points.add(initalPoint);
        this.tempPoint = null;
    }

    private Polygon(Polygon polygon, Point tempPoint) {
        this.points = polygon.points;
        this.tempPoint = tempPoint;
    }

    public boolean isInside2D(int x, int y) {
        // TODO implement
        return false;
    }

    public Shape singleUpdate(int x, int y) {
        points.add(new Point(x, y));
        return new Polygon(this, null);
    }

    public Shape continuousUpdate(int x, int y) {
        return new Polygon(this, new Point(x, y));
    }

    public UIPrimitive getUIPrimitive() {
        // Compute convex hull
        Point[] ps = new Point[points.size() + 1];
        for (int i = 0; i < points.size(); i++) {
            ps[i] = points.get(i);
        }
        ps[ps.length - 1] = tempPoint;
        Arrays.sort(ps, new Comparator<Point>() {
            public int compare(Point o1, Point o2) {
                return o1.x > o2.x ? 1 :
                       o1.x == o2.x ? 0 : -1;
            }
        });
        List<Point> polygon = new ArrayList<Point>();
        Point start = ps[0];
        // TODO implement convex hull

        return UIPrimitive.polygon(null);
    }

    public static Polygon initPolygon(int x, int y) {
        return new Polygon(new Point(x, y));
    }

    public boolean isDiscretelyUpdated() {
        return true;
    }

}
