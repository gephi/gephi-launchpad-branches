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

import org.gephi.math.linalg.Vec2;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.data.graph.VizNode2D;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class OrthoCameraBuilder {
    private final Vec2 center;
    private final float height;
    private float far;

    public OrthoCameraBuilder(Camera2d camera) {
        this.center = camera.center();
        this.height = camera.height();
        this.far = 0.1f;
    }

    public void add(VizNode2D node) {        
        final float distFar = node.size * 1.001f;
        if (distFar > this.far) {
            this.far = distFar;
        }
    }
    
    public OrthoCamera create() {
        return new OrthoCamera(this.center, this.height, 0.0f, this.far);
    }
}
