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
package org.gephi.visualization.data.camera;

import org.gephi.math.Mat4;
import org.gephi.math.Vec2;

/**
 * Orthographic camera used for 2D rendering.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class OrthoCamera extends Camera {
    public final Vec2 center;
    public final float height;

    public OrthoCamera(Vec2 center, float height, float near, float far) {
        super(near, far);
        
        this.center = center;
        this.height = height;
    }
    
    @Override
    protected void recomputeMatrices() {
        this.viewMatrix = Mat4.IDENTITY;
        
        final Rectangle rect = this.area.renderRect;
        final float width = this.height * this.area.aspectRatio;
        final float left = this.center.x() + (rect.x - 0.5f) * width;
        final float right = left + rect.width * width;
        final float bottom = this.center.y() + (rect.y - 0.5f) * this.height;
        final float top = bottom + rect.height * this.height;
        this.projMatrix = Mat4.ortho(left, right, bottom, top, this.near, this.far);
    }
        
}
