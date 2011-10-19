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

import org.gephi.math.Vec2;
import org.gephi.math.Vec2M;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.api.view.ui.UIStyle;

/**
 * Class representing a rectangular shape.
 *
 * @author Vojtech Bardiovsky
 */
class Rectangle extends AbstractShape {

    Vec2M origin, opposite;

    Rectangle(Vec2 origin, Vec2 opposite) {
        this.origin = origin.copyM();
        this.opposite = opposite.copyM();
    }

    /**
     * @param origin Top left corner of the rectangle.
     * @param dimension Dimensions of the rectangle.
     */
    private Rectangle(float x, float y, float width, float height) {
        this.origin = new Vec2M(x, y);
        this.opposite = new Vec2M(x + width, y + height);
    }

    Rectangle(float x, float y) {
        this(x, y, 0, 0);
    }

    @Override
    public boolean isPointInside(float x, float y, float radius) {
        return ((x + radius >= origin.x() && x - radius <= opposite.x()) || (x - radius <= origin.x() && x + radius >= opposite.x())) &&
               ((y + radius >= origin.y() && y - radius <= opposite.y()) || (y - radius <= origin.y() && y + radius >= opposite.y()));
    }

    public Shape continuousUpdate(float x, float y) {
        this.opposite.set(x, y);
        return this;
    }

    public Shape singleUpdate(float x, float y) {
        return new Rectangle(x, y);
    }

    public UIShape getUIPrimitive() {
        return UIShape.orientedQuad(UIStyle.SELECTION, this.origin.copy(), this.opposite.copy());
    }

    public boolean isDiscretelyUpdated() {
        return false;
    }

    @Override
    protected boolean intersectsCircle(float x, float y, float radius) {
        return (origin.x() - x) * (origin.x() - x) + (origin.y() - y) * (origin.y() - y) <= radius * radius ||
               (origin.x() - x) * (origin.x() - x) + (opposite.y() - y) * (opposite.y() - y) <= radius * radius ||
               (opposite.x() - x) * (opposite.x() - x) + (origin.y() - y) * (origin.y() - y) <= radius * radius ||
               (opposite.x() - x) * (opposite.x() - x) + (opposite.y() - y) * (opposite.y() - y) <= radius * radius;
    }

    public SelectionType getSelectionType() {
        return SelectionType.RECTANGLE;
    }

}
