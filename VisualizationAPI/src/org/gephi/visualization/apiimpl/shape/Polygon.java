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
import org.gephi.math.linalg.Vec2;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.api.view.ui.UIStyle;

/**
 * Class representing a polygon shape.
 *
 * @author Vojtech Bardiovsky
 */
class Polygon extends AbstractShape {

    private final Set<Vec2> points;
    private final Vec2 tempPoint;

    private final Vec2[] convexHull;
    private final float[][] lineInfo;

    Polygon(float x, float y) {
        Vec2 point = new Vec2(x, y);
        this.points = new HashSet<Vec2>();
        this.points.add(point);
        this.tempPoint = point;
        this.convexHull = new Vec2[] {point};
        this.lineInfo = new float[1][4];
    }

    Polygon(Polygon polygon, Vec2 tempPoint) {
        this.points = polygon.points;
        this.tempPoint = tempPoint;
        this.convexHull = computeConvexHull();
        this.lineInfo = computeLineInfo();
    }

    @Override
    public boolean isPointInside(float x, float y, float radius) {
        if (lineInfo.length <= 2) {
            return false;
        }
        for (int i = 0; i < lineInfo.length; i++) {
            // Difference vector betwen point and line center
            float vecx = x - convexHull[i].x();
            float vecy = y - convexHull[i].y();
            // Compute inner product
            if (vecx * lineInfo[i][0] + vecy * lineInfo[i][1] > radius) {
                return false;
            }
        }
        return true;
    }

    public Shape singleUpdate(float x, float y) {
        Vec2 p = new Vec2(x, y);
        points.add(p);
        return new Polygon(this, p);
    }

    public Shape continuousUpdate(float x, float y) {
        return new Polygon(this, new Vec2(x, y));
    }

    public UIShape getUIPrimitive() {
        return UIShape.polygon(UIStyle.SELECTION, convexHull);
    }

    private float[][] computeLineInfo() {
        // First two elements for line normal, other two for line center
        float[][] lNormals = new float[convexHull.length][2];
        for (int i = 0, j = convexHull.length - 1; i < convexHull.length; j = i++) {
            // Normal
            lNormals[j][0] = -(convexHull[i].y() - convexHull[j].y());
            lNormals[j][1] = convexHull[i].x() - convexHull[j].x();
        }
        return lNormals;
    }

    private Vec2[] computeConvexHull() {
        Vec2[] ps = new Vec2[points.size() + 1];
        int i = 0;
        for (Vec2 point : points) {
            ps[i++] = point;
        }
        ps[i] = tempPoint;

        // If 2 points
        if (ps.length == 2) {
            return ps;
        }
        
        // Compute convex hull
        Arrays.sort(ps, new Comparator<Vec2>() {
            public int compare(Vec2 o1, Vec2 o2) {
                return o1.x() > o2.x() ? 1 :
                       o1.x() < o2.x() ? -1 :
                       o1.y() > o2.y() ? 1 :
                       o1.y() < o2.y() ? -1 : 0;
            }
        });
        List<Vec2> polygon = new ArrayList<Vec2>();
        // ps[0] contains the most bottom left vertex
        polygon.add(ps[0]);
        i = 0;
        while (i < ps.length - 1) {
            Vec2 best = ps[i + 1];
            int b = i + 1;
            for (int j = i + 1; j < ps.length; j++) {
                if ((ps[j].x() == best.x() && ps[j].y() > best.y()) ||
                    (best.x() != ps[i].x() &&
                     ps[j].x() > best.x() && (ps[j].y() - ps[i].y()) / (ps[j].x() - ps[i].x()) >
                                         (best.y() - ps[i].y()) / (best.x() - ps[i].x()))) {
                     best = ps[j];
                     b = j;
                }
            }
            polygon.add(best);
            i = b;
        }
        i = ps.length - 1;
        while (i > 0) {
            Vec2 best = ps[i - 1];
            int b = i - 1;
            for (int j = i - 1; j >= 0; j--) {
                if ((ps[j].x() == best.x() && ps[j].y() < best.y()) ||
                    (best.x() != ps[i].x() &&
                     ps[j].x() < best.x() && (ps[j].y() - ps[i].y()) / (ps[j].x() - ps[i].x()) >
                                         (best.y() - ps[i].y()) / (best.x() - ps[i].x()))) {
                     best = ps[j];
                     b = j;
                }
            }
            if (b != 0) {
                polygon.add(best);
            }
            i = b;
        }
        return polygon.toArray(new Vec2[]{});
    }

    public boolean isDiscretelyUpdated() {
        return true;
    }

    @Override
    protected boolean intersectsCircle(float x, float y, float radius) {
        for (Vec2 point : points) {
            final Vec2 diff = new Vec2(point.x() - x, point.y() - y);
            if (diff.lengthSquared() <= radius * radius) {
                return true;
            }
        }
        return (tempPoint.lengthSquared() <= radius * radius);
    }

    public SelectionType getSelectionType() {
        return SelectionType.POLYGON;
    }

}
