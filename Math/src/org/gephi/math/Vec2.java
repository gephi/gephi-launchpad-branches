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

package org.gephi.math;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Immutable 2D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Vec2 extends Vec2Base {

    /*--------------------- CONSTANTS 2D VECTORS -----------------------------*/

    /**
     * Zero vector.
     */
    public final static Vec2 ZERO = new Vec2(0.0f, 0.0f);

    /**
     * First standard basis vector.
     */
    public final static Vec2 E1 = new Vec2(1.0f, 0.0f);

    /**
     * First standard basis vector negated.
     */
    public final static Vec2 E1_NEG = new Vec2(-1.0f, 0.0f);

    /**
     * Second standard basis vector.
     */
    public final static Vec2 E2 = new Vec2(0.0f, 1.0f);

    /**
     * Second standard basis vector negated.
     */
    public final static Vec2 E2_NEG = new Vec2(0.0f, -1.0f);

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 2D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     */
    public Vec2(float x, float y) {
        super(x, y);
    }

    /**
     * Creates a copy of another 2D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec2(Vec2Base v) {
        super(v);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Creates a new immutable 2D vector from its polar coordinates.
     *
     * @param radius the length of the new vector
     * @param angle the angle between the x-axis and the new vector
     * @return the new vector
     */
    public static Vec2 fromPolarCoordinates(float radius, float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Vec2(radius * c, radius * s);
    }

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new immutable 2D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 8 bytes in the
     *         buffer
     */
    public static Vec2 readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec2(b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new immutable 2D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 8 bytes in the
     *         buffer
     */
    public static Vec2 readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec2 result = new Vec2(b.getFloat(i[0]), b.getFloat(i[0]+4));
        i[0] += 8;
        return result;
    }
    
    /**
     * Expects a string representation of a vector in form: (x,y). This is an
     * inverse function to the {@link Vec2Base#toString()} method.
     * @throws NumberFormatException
     */
    public static Vec2 fromString(String string) {
        try {
            String[] split = string.substring(1, string.length() - 1).split(",");
            float[] floats = new float[split.length];
            for (int i = 0; i < split.length; i++) {
                floats[i] = Float.parseFloat(split[i]);
            }
            return new Vec2(floats[0], floats[1]);
        } catch (Exception e) {
            throw new NumberFormatException(string);
        }
    }
}
