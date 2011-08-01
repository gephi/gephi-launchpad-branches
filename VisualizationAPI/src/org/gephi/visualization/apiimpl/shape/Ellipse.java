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
import org.gephi.math.linalg.Vec2;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.api.view.ui.UIStyle;

/**
 * Class representing an ellipse shape.
 *
 * @author Vojtech Bardiovsky
 */
class Ellipse extends AbstractShape {

    private final Point origin;
    private final int a, b;

    Ellipse(int x, int y, int a, int b) {
        this.origin = new Point(x, y);
        this.a = a;
        this.b = b;
    }

    Ellipse(int x, int y) {
        this(x, y, 0, 0);
    }

    @Override
    protected boolean isPointInside(int x, int y, int radius) {
        if (a == 0 || b == 0) {
            return false;
        }
        int x1 = Math.abs(origin.x + a - x) > radius ? Math.abs(origin.x + a - x) - radius : 0;
        int y1 = Math.abs(origin.y + b - y) > radius ? Math.abs(origin.y + b - y) - radius : 0;
        return x1 * x1 / (float) (a * a) + y1 * y1 / (float) (b * b) <= 1;
    }

    public Shape singleUpdate(int x, int y) {
        return new Ellipse(x, y);
    }

    public Shape continuousUpdate(int x, int y) {
        return new Ellipse(origin.x, origin.y, (x - origin.x) / 2, (y - origin.y) / 2);
    }

    public boolean isDiscretelyUpdated() {
        return false;
    }

    public UIShape getUIPrimitive() {
        return UIShape.orientedEllipse(UIStyle.SELECTION, new Vec2(origin.x + a, origin.y + b), a, b);
    }

    @Override
    protected boolean intersectsCircle(int x, int y, int radius) {
        int centerX = origin.x + a;
        int centerY = origin.y + b;
        if (a == 0 || b == 0) {
            return false;
        }
        if (x == centerX) {
            return radius + b < Math.abs(y - centerY);
        }
        // Tangent
        float k = (float) (y - centerY) / (x - centerX);

        // Boundary ellipse point intersect the difference vector direction
        float a2 = (float) a * a;
        float b2 = (float) b * b;
        float boundaryX = (float) Math.sqrt((a2 * b2) / (b2 + a2 * k * k)) * Math.signum(x - centerX);
        float boundaryY = k * boundaryX;
        float ellipseRadius = (float) Math.sqrt(boundaryX * boundaryX + boundaryY * boundaryY);
        return ellipseRadius + radius > (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
    }

    public SelectionType getSelectionType() {
        return SelectionType.ELLIPSE;
    }

}
