/*
Copyright 2008-2010 Gephi
Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.plugin.force;

import org.gephi.graph.spi.LayoutData;
import org.gephi.graph.api.Spatial;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class ForceVector implements Spatial, LayoutData {

    protected float x;
    protected float y;

    public ForceVector(ForceVector vector) {
        this.x = vector.x();
        this.y = vector.y();
    }

    public ForceVector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public ForceVector() {
        this.x = 0;
        this.y = 0;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void add(ForceVector f) {
        if (f != null) {
            x += f.x();
            y += f.y();
        }
    }

    public void multiply(float s) {
        x *= s;
        y *= s;
    }

    public void subtract(ForceVector f) {
        if (f != null) {
            x -= f.x();
            y -= f.y();
        }
    }

    public float getEnergy() {
        return x * x + y * y;
    }

    public float getNorm() {
        return (float) Math.sqrt(getEnergy());
    }

    public ForceVector normalize() {
        float norm = getNorm();
        return new ForceVector(x / norm, y / norm);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
