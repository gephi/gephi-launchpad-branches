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
package org.gephi.visualization.rendering.apiimpl.command.node.texture;

import org.gephi.math.Vec3;

/**
 * Creates a regular polygon texture.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RegularPolygonTextureGenerator extends ProceduralTextureGenerator {
    private final int n;
    private final Vec3[] lines;

    public RegularPolygonTextureGenerator(int n) {
        this.n = n;
        
        final double step = 2.0 * Math.PI / n;
        
        final double sideLength = 2.0 * Math.sin(0.5 * step);
        final double s = 1.0 / sideLength;
        
        Vec3[] points = new Vec3[n];
        for (int i = 0; i < n; ++i) {
            double angle = step * i;
            
            points[i] = new Vec3((float)(Math.cos(angle) * s), (float)(Math.sin(angle) * s), (float)s);
        }        
        
        this.lines = new Vec3[n];
        for (int i = n-1, j = 0; j < n; i = j++) {
            final Vec3 oldP = points[i];
            final Vec3 newP = points[j];
            
            this.lines[i] = oldP.cross(newP);
        }
    }
    
    @Override
    protected float calculateDistance(float x, float y, float radius) {
        float max = Float.NEGATIVE_INFINITY;
        final Vec3 p = new Vec3(x / radius, y / radius, 1.0f);
        for (int i = 0; i < this.n; ++i) {
            final float distance = -this.lines[i].dot(p);
            max = distance > max ? distance : max;
        }
        return max * radius;
    }
}
