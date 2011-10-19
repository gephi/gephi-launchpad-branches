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

/**
 * 
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class Camera {
    protected float near;
    protected float far;
    
    protected RenderArea area;
    protected Mat4 viewMatrix;
    protected Mat4 projMatrix;

    public Camera(float near, float far) {
        this.near = near;
        this.far = far;
        
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

    protected abstract void recomputeMatrices();
}
