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

import org.gephi.math.Vec3;
import org.gephi.visualization.camera.Camera3d;
import org.gephi.visualization.data.graph.VizNode3D;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class PerspCameraBuilder {
    private final Vec3 position;
    private final Vec3 front;
    private final Vec3 up;
    private final Vec3 right;
    private final float tanHalfFov;
    
    private float near;
    private float far;

    public PerspCameraBuilder(Camera3d camera) {
        this.position = camera.position();
        this.front = camera.front();
        this.up = camera.up();
        this.right = camera.right();
        this.tanHalfFov = (float)Math.tan(camera.fov() * 0.5);
        
        this.near = Float.POSITIVE_INFINITY;
        this.far = 0.0f;
    }
    
    public void add(VizNode3D node) {
        final float distNear = - this.front.dot(node.position.minus(this.position)) - node.size;
        if (distNear >= 0.1f && distNear < this.near) {
            this.near = distNear;
        }
        
        final float distFar = distNear + 2.0f * node.size;
        if (distFar > this.far) {
            this.far = distFar;
        }
    }
    
    public PerspCamera create() {
        return new PerspCamera(this.position, this.front, this.up, this.right, this.tanHalfFov, this.near, this.far);
    }
}
