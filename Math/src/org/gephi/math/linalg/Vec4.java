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

package org.gephi.math.linalg;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Immutable 4D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Vec4 extends Vec4Base {

    /*--------------------- CONSTANTS 2D VECTORS -----------------------------*/

    /**
     * Zero vector.
     */
    public final static Vec4 ZERO = new Vec4(0.0f, 0.0f, 0.0f, 0.0f);

    /**
     * First standard basis vector.
     */
    public final static Vec4 E1 = new Vec4(1.0f, 0.0f, 0.0f, 0.0f);

    /**
     * First standard basis vector negated.
     */
    public final static Vec4 E1_NEG = new Vec4(-1.0f, 0.0f, 0.0f, 0.0f);

    /**
     * Second standard basis vector.
     */
    public final static Vec4 E2 = new Vec4(0.0f, 1.0f, 0.0f, 0.0f);

    /**
     * Second standard basis vector negated.
     */
    public final static Vec4 E2_NEG = new Vec4(0.0f, -1.0f, 0.0f, 0.0f);

    /**
     * Third standard basis vector.
     */
    public final static Vec4 E3 = new Vec4(0.0f, 0.0f, 1.0f, 0.0f);

    /**
     * Third standard basis vector negated.
     */
    public final static Vec4 E3_NEG = new Vec4(0.0f, 0.0f, -1.0f, 0.0f);
    
    /**
     * Fourth standard basis vector.
     */
    public final static Vec4 E4 = new Vec4(0.0f, 0.0f, 0.0f, 1.0f);
    
    /**
     * Fourth standard basis vector negated.
     */
    public final static Vec4 E4_NEG = new Vec4(0.0f, 0.0f, 0.0f, -1.0f);

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 4D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     * @param w the third component of the vector
     */
    public Vec4(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    /**
     * Creates a copy of another 4D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec4(Vec4Base v) {
        super(v);
    }
    
    /**
     * Creates a 4D vector from a 3D vector and the last component.
     * 
     * @param v the 3D vector
     * @param w the fourth component
     */
    public Vec4(Vec3Base v, float w) {
        super(v.x, v.y, v.z, w);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new immutable 4D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 16 bytes in the
     *         buffer
     */
    public static Vec4 readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec4(b.getFloat(), b.getFloat(), b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new immutable 4D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 16 bytes in the
     *         buffer
     */
    public static Vec4 readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec4 result = new Vec4(b.getFloat(i[0]), b.getFloat(i[0]+4),
                                        b.getFloat(i[0]+8), b.getFloat(i[0]+12));
        i[0] += 16;
        return result;
    }
}
