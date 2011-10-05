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

package org.gephi.visualization.api;

import org.gephi.math.linalg.Vec3;
import org.gephi.math.linalg.Vec3M;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class AABB {

    final private Vec3M minVec;
    final private Vec3M maxVec;

    public AABB(Vec3 center) {
        this.minVec = center.copyM();
        this.maxVec = center.copyM();
    }

    public AABB(Vec3 center, Vec3 scale) {
        this.minVec = new Vec3M(center.x() - scale.x(), center.y() - scale.y(), center.z() - scale.z());
        this.maxVec = new Vec3M(center.x() + scale.x(), center.y() + scale.y(), center.z() + scale.z());
    }

    public AABB(AABB box) {
        this.minVec = box.minVec.copyM();
        this.maxVec = box.maxVec.copyM();
    }

    public AABB(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minVec = new Vec3M(minX, minY, minZ);
        this.maxVec = new Vec3M(maxX, maxY, maxZ);
    }

    public Vec3 center() {
        return this.minVec.plusM(this.maxVec).timesEq(0.5f);
    }

    public Vec3 scale() {
        return this.maxVec().minus(this.minVec);
    }

    public Vec3 minVec() {
        return this.minVec;
    }

    public Vec3 maxVec() {
        return this.maxVec;
    }

    public void addPoint(Vec3 point, Vec3 scale) {
        this.minVec.x(Math.min(this.minVec.x(), point.x() - scale.x()));
        this.minVec.y(Math.min(this.minVec.y(), point.y() - scale.y()));
        this.minVec.z(Math.min(this.minVec.z(), point.z() - scale.z()));
        
        this.maxVec.x(Math.max(this.maxVec.x(), point.x() + scale.x()));
        this.maxVec.y(Math.max(this.maxVec.y(), point.y() + scale.y()));
        this.maxVec.z(Math.max(this.maxVec.z(), point.z() + scale.z()));
    }
}
