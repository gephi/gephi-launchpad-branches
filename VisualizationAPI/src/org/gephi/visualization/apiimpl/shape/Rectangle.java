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

import java.awt.Dimension;
import java.awt.Point;
import org.gephi.lib.gleem.linalg.Vec2f;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;

/**
 * Class representing a rectangular shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Rectangle extends AbstractShape {

    Point origin, opposite;

    Rectangle(Point origin, Point opposite) {
        this.origin = origin;
        this.opposite = opposite;
    }

    /**
     * @param origin Top left corner of the rectangle.
     * @param dimension Dimensions of the rectangle.
     */
    private Rectangle(int x, int y, int width, int height) {
        this(new Point(x, y), new Point(x + width, y + height));
    }

    Rectangle(int x, int y) {
        this(x, y, 0, 0);
    }

    public boolean isInside2D(int x, int y) {
        return ((x >= origin.x && x <= opposite.x) || (x <= origin.x && x >= opposite.x)) &&
               ((y >= origin.y && y <= opposite.y) || (y <= origin.y && y >= opposite.y));
    }

    public Shape continuousUpdate(int x, int y) {
        this.opposite.x = x;
        this.opposite.y = y;
        return this;
    }

    public Shape singleUpdate(int x, int y) {
        return new Rectangle(x, y);
    }

    public UIPrimitive getUIPrimitive() {
        return UIPrimitive.orientedQuad(new Vec2f((float)origin.x, (float)origin.y), new Vec2f((float)opposite.x, (float)opposite.y));
    }

    public boolean isDiscretelyUpdated() {
        return false;
    }

}
