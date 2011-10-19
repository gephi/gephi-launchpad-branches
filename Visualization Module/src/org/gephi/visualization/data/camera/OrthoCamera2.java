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

import org.gephi.math.Vec2;
import org.gephi.math.Vec2M;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class OrthoCamera2 extends Camera2 {
    public final Vec2M center;
    public float height;

    public OrthoCamera2(Vec2 center, float height) {
        this.center = center.copyM();
        this.height = height;
    }
    
    
    public OrthoCamera2 near(float near) {
        this.near = near;
        return this;
    }
    
    public OrthoCamera2 far(float far) {
        this.far = far;
        return this;
    }    

    @Override
    protected void recomputeMatrices() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
