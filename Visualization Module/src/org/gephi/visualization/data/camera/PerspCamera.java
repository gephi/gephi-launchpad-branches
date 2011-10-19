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
import org.gephi.math.Vec3;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class PerspCamera extends Camera {
    public final Vec3 position;
    public final Vec3 frontNeg;
    public final Vec3 up;
    public final Vec3 right;
    public final float tanHalfFov;

    public PerspCamera(Vec3 position, Vec3 front, Vec3 up, Vec3 right, float tanHalfFov, float near, float far) {
        super(near, far);
        
        this.position = position;
        this.frontNeg = front.negated();
        this.up = up;
        this.right = right;
        this.tanHalfFov = tanHalfFov;
    }
    
    @Override
    protected void recomputeMatrices() {
        this.viewMatrix = Mat4.toReferenceFrame(this.position, this.right, this.up, this.frontNeg);
        
        Rectangle rect = this.area.renderRect;
        final float h = 2.0f * this.tanHalfFov * this.near;
        final float w = h * this.area.aspectRatio;
        final float l = (rect.x - 0.5f)*w;
        final float r = l + rect.width * w;
        final float b = (rect.y - 0.5f)*h;
        final float t = b + rect.height * h;
        this.projMatrix = Mat4.frustum(l, r, b, t, this.near, this.far);
    }
    
}
