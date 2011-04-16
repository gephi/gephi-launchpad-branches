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

package org.gephi.visualization.geometry;

import org.gephi.lib.gleem.linalg.Vec3f;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class AABB {

    final private Vec3f minVec;
    final private Vec3f maxVec;

    public AABB(Vec3f center) {
        this.minVec = center.copy();
        this.maxVec = center.copy();
    }

    public AABB(Vec3f center, Vec3f scale) {
        this.minVec = new Vec3f(center.x() - scale.x(), center.y() - scale.y(), center.z() - scale.z());
        this.maxVec = new Vec3f(center.x() + scale.x(), center.y() + scale.y(), center.z() + scale.z());
    }

    public AABB(AABB box) {
        this.minVec = box.minVec();
        this.maxVec = box.maxVec();
    }

    public AABB(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minVec = new Vec3f(minX, minY, minZ);
        this.maxVec = new Vec3f(maxX, maxY, maxZ);
    }

    public Vec3f center() {
        Vec3f ret = this.minVec();
        ret.add(this.maxVec);
        ret.scale(0.5f);
        return ret;
    }

    public Vec3f scale() {
        Vec3f ret = this.maxVec();
        ret.sub(this.minVec);
        ret.scale(0.5f);
        return ret;
    }

    public Vec3f minVec() {
        return this.minVec.copy();
    }

    public Vec3f maxVec() {
        return this.maxVec.copy();
    }

    public void addPoint(Vec3f point, Vec3f scale) {
        this.minVec.setX(Math.min(this.minVec.x(), point.x() - scale.x()));
        this.minVec.setY(Math.min(this.minVec.y(), point.y() - scale.y()));
        this.minVec.setZ(Math.min(this.minVec.z(), point.z() - scale.z()));
        
        this.maxVec.setX(Math.max(this.maxVec.x(), point.x() + scale.x()));
        this.maxVec.setY(Math.max(this.maxVec.y(), point.y() + scale.y()));
        this.maxVec.setZ(Math.max(this.maxVec.z(), point.z() + scale.z()));
    }
}
