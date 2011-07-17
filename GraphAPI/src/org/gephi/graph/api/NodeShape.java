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
package org.gephi.graph.api;

/**
 * Enumeration class for visual representation of a node.
 * 
 * @author Vojtech Bardiovsky
 */
public enum NodeShape {
    DEFAULT,
    CIRCLE,
    TRIANGLE,
    SQUARE,
    DIAMOND,
    PENTAGON,
    HEXAGON,
    IMAGE1,
    IMAGE2,
    IMAGE3,
    IMAGE4,
    IMAGE5,
    IMAGE6,
    IMAGE7,
    IMAGE8;

    /**
     * Returns array of all non-default values.
     */
    public static NodeShape[] specificValues() {
        return new NodeShape[] {CIRCLE, TRIANGLE, SQUARE, DIAMOND, PENTAGON, HEXAGON, IMAGE1, IMAGE2, IMAGE3, IMAGE4, IMAGE5, IMAGE6, IMAGE7, IMAGE8};
    }
}
