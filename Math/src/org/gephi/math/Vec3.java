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
 * Immutable 3D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Vec3 extends Vec3Base {

    /*--------------------- CONSTANTS 2D VECTORS -----------------------------*/

    /**
     * Zero vector.
     */
    public final static Vec3 ZERO = new Vec3(0.0f, 0.0f, 0.0f);

    /**
     * First standard basis vector.
     */
    public final static Vec3 E1 = new Vec3(1.0f, 0.0f, 0.0f);

    /**
     * First standard basis vector negated.
     */
    public final static Vec3 E1_NEG = new Vec3(-1.0f, 0.0f, 0.0f);

    /**
     * Second standard basis vector.
     */
    public final static Vec3 E2 = new Vec3(0.0f, 1.0f, 0.0f);

    /**
     * Second standard basis vector negated.
     */
    public final static Vec3 E2_NEG = new Vec3(0.0f, -1.0f, 0.0f);

    /**
     * Third standard basis vector.
     */
    public final static Vec3 E3 = new Vec3(0.0f, 0.0f, 1.0f);

    /**
     * Third standard basis vector negated.
     */
    public final static Vec3 E3_NEG = new Vec3(0.0f, 0.0f, -1.0f);

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 3D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     */
    public Vec3(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Creates a copy of another 3D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec3(Vec3Base v) {
        super(v);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Creates a new immutable 3D vector from its spherical coordinates using
     * the inclination angle from the zenith and the azimuth angle.
     *
     * @param radius the length of the vector
     * @param inclination the inclination from the zenith
     * @param azimuth the azimuth angle
     * @return the new immutable vector
     */
    public static Vec3 fromSphericalCoordinatesRIA(float radius,
            float inclination, float azimuth) {
        final float rsi = radius * (float)Math.sin(inclination);
        final float rci = radius * (float)Math.cos(inclination);
        final float ca = (float)Math.cos(azimuth);
        final float sa = (float)Math.sin(azimuth);
        return new Vec3(rsi * ca, rsi * sa, rci);
    }

    /**
     * Creates a new immutable 3D vector from its spherical coordinates using
     * the elevation angle from the reference plane and the azimuth angle.
     *
     * @param radius the length of the vector
     * @param elevation the elevation from the reference plane
     * @param azimuth the azimuth angle
     * @return the new immutable vector
     */
    public static Vec3 fromSphericalCoordinatesREA(float radius,
            float elevation, float azimuth) {
        final float rce = radius * (float)Math.cos(elevation);
        final float rse = radius * (float)Math.sin(elevation);
        final float ca = (float)Math.cos(azimuth);
        final float sa = (float)Math.sin(azimuth);
        return new Vec3(rce * ca, rce * sa, rse);
    }

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new immutable 3D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 12 bytes in the
     *         buffer
     */
    public static Vec3 readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec3(b.getFloat(), b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new immutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new immutable 3D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 12 bytes in the
     *         buffer
     */
    public static Vec3 readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec3 result = new Vec3(b.getFloat(i[0]), b.getFloat(i[0]+4),
                                        b.getFloat(i[0]+8));
        i[0] += 12;
        return result;
    }
}
