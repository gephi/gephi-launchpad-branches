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

package org.gephi.visualization.apiimpl.shape;

import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.selection.Shape.SelectionModifier;

/**
 * Utility class for {@link Shape}
 *
 * @author Vojtech Bardiovsky
 */
public class ShapeUtils {

    public static Shape initShape(SelectionType selectionType, SelectionModifier selectionModifier, int x, int y) {
        AbstractShape shape = null;
        switch (selectionType) {
            case POLYGON:
                shape = new Polygon(x, y);
                break;
            case ELLIPSE:
                shape = new Ellipse(x, y);
                break;
            case RECTANGLE:
                shape = new Rectangle(x, y);
                break;
        }
        shape.setSelectionModifier(selectionModifier);
        return shape;
    }

    public static Shape createEllipseShape(int x, int y, int a, int b) {
        return new Ellipse(x, y, a, b);
    }

    public static Shape singleUpdate(Shape shape, int x, int y) {
        Shape newShape = shape.singleUpdate(x, y);
        newShape.setSelectionModifier(shape.getSelectionModifier());
        return newShape;
    }

    public static Shape continuousUpdate(Shape shape, int x, int y) {
        Shape newShape = shape.continuousUpdate(x, y);
        newShape.setSelectionModifier(shape.getSelectionModifier());
        return newShape;
    }

}
