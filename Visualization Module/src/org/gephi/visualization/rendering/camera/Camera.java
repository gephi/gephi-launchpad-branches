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
package org.gephi.visualization.rendering.camera;

import org.gephi.math.linalg.Mat4;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;

/**
 * 
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class Camera {
    protected RenderArea area;
    protected Mat4 viewMatrix;
    protected Mat4 projMatrix;

    public Camera() {
        this.area = null;
        this.viewMatrix = Mat4.IDENTITY;
        this.projMatrix = Mat4.IDENTITY;
    }
    
    public final Mat4 viewMatrix(RenderArea area) {
        if (this.area != area) {
            this.area = area;
            recomputeMatrices();
        }
        return this.viewMatrix;
    }
    
    public final Mat4 projMatrix(RenderArea area) {
        if (this.area != area) {
            this.area = area;
            recomputeMatrices();
        }
        return this.projMatrix;
    }
    
    public final Mat4 viewProjMatrix(RenderArea area) {
        if (this.area != area) {
            this.area = area;
            recomputeMatrices();
        }
        return this.projMatrix.times(this.viewMatrix);
    }
    
    public static Camera from(Camera2d camera) {
        return new OrthoCamera(camera.center(), camera.height());
    }
    
    public static Camera from(Camera3d camera) {
        return new PerspCamera(camera.position(), camera.front(), camera.up(), camera.right(), (float)Math.tan(camera.fov()*0.5f));
    }

    protected abstract void recomputeMatrices();
}
