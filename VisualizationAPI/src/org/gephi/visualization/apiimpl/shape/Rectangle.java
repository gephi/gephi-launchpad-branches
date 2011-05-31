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
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.api.selection.CameraBridge;
import org.gephi.visualization.api.selection.Shape;

/**
 * Class representing a rectangular shape.
 *
 * @author Vojtech Bardiovsky
 */
public class Rectangle implements Shape {

    private final Point origin;
    private final Dimension dimension;
    private CameraBridge cameraBridge;

    /**
     * @param origin Top left corner of the rectangle.
     * @param dimension Dimensions of the rectangle.
     */
    public Rectangle(Point origin, Dimension dimension) {
        this.origin = origin;
        this.dimension = dimension;
    }

    public boolean isInside3D(float x, float y, float z) {
        // TODO this is for test only and extremely inefficient
        // TODO after optimization this can be moved to an abstract superclass
        Vec4f point = new Vec4f(x, y, z, 0);
        Vec4f screenPoint = new Vec4f();
        Mat4f projMatrix = cameraBridge.projectiveMatrix();
        Mat4f viewMatrix = cameraBridge.viewMatrix();
        float width = cameraBridge.imageWidth();
        float height = cameraBridge.imageHeight();
        // multiply by modelview and projection matrices
        viewMatrix.xformVec(point, screenPoint);
        projMatrix.xformVec(screenPoint, point);
        // to NDC
        point.scale(1 / point.w());
        // to screen
        int px = (int) ((point.get(1) + 1) * width / 2);
        int py = (int) ((point.get(2) + 1) * height / 2);
        return isInside2D(px, py);
    }

    public boolean isInside2D(int x, int y) {
        return x >= origin.x && x <= origin.x + dimension.width &&
               y >= origin.y && y <= origin.y + dimension.height;
    }

    public void setCameraBridge(CameraBridge cameraBridge) {
        this.cameraBridge = cameraBridge;
    }

}
