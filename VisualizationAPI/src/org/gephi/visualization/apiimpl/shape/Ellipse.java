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
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;

/**
 * Class representing an ellipse shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Ellipse extends AbstractShape {

    private final Point center;
    private final float a, b;

    Ellipse(int x, int y, float a, float b) {
        this.center = new Point(x, y);
        this.a = a;
        this.b = b;
    }

    Ellipse(int x, int y) {
        this(x, y, 0, 0);
    }

    public boolean isInside2D(int x, int y) {
        return (center.x - x) * (center.x - x) / (a * a) +
               (center.y - y) * (center.y - y) / (b * b) <= 1;
    }

    public Shape singleUpdate(int x, int y) {
        return new Ellipse(x, y);
    }

    public Shape continuousUpdate(int x, int y) {
        return new Ellipse(x, y, Math.abs(x - center.x), Math.abs(y - center.y));
    }

    public boolean isDiscretelyUpdated() {
        return false;
    }

    public UIPrimitive getUIPrimitive() {
        //return UIPrimitive.ellipses(new Vec2f(center.x, center.y), a, b);
        return null;
    }

}
