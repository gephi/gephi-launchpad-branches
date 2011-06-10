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
import org.gephi.lib.gleem.linalg.Vec2f;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;

/**
 * Class representing an ellipse shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Ellipse extends AbstractShape {

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

    public boolean isPointInside(int x, int y) {
        return a > 0 && b > 0 &&
               (origin.x - x) * (origin.x - x) / (a * a) +
               (origin.y - y) * (origin.y - y) / (b * b) <= 1;
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

    public UIPrimitive getUIPrimitive() {
        return UIPrimitive.ellipses(new Vec2f(origin.x + a, origin.y + b), new Vec2f(a, 0), new Vec2f(0, b));
    }

    @Override
    protected boolean intersectsCircle(int x, int y, int radius) {
        int centerX = origin.x + a;
        int centerY = origin.y + b;
        if (x == centerX && radius + b < Math.abs(y - centerY)) {
            return false;
        }
        // Tangent
        float k = (float) (y - centerY) / (x - centerX);

        // Boundary ellipse point interface the difference vector direction
        float boundaryX = (float) Math.sqrt((float) (a * a * b * b) / (b * b + a * a * k * k)) * Math.signum(x - centerX);
        float boundaryY = k * boundaryX;
        float ellipseRadius = (float) Math.sqrt(boundaryX * boundaryX + boundaryY * boundaryY);
        
        return ellipseRadius + radius > (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
    }

}
