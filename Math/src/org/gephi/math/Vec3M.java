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
 * Mutable 3D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Vec3M extends Vec3Base {

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * The default constructor of Vec3M creates a zero vector.
     */
    public Vec3M() {
        super(0.0f, 0.0f, 0.0f);
    }
    
    /**
     * Creates a new 3D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     */
    public Vec3M(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Creates a copy of another 3D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec3M(Vec3Base v) {
        super(v);
    }

    /*-------------------------------- SETTERS -------------------------------*/

    /**
     * Sets the first component of the vector.
     *
     * @param x the new first component of the vector
     * @return this
     */
    public Vec3M x(float x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the second component of the vector.
     *
     * @param y the new second component of the vector
     * @return this
     */
    public Vec3M y(float y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the third component of the vector.
     *
     * @param z the new third component of the vector
     * @return this
     */
    public Vec3M z(float z) {
        this.z = z;
        return this;
    }

    /**
     * Sets the components of the vector.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     * @return this
     */
    public Vec3M set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Sets the components of the vector to be equal to another vector.
     *
     * @param v the other vector
     * @return this
     */
    public Vec3M set(Vec3Base v) {
        return set(v.x, v.y, v.z);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Creates a new mutable 3D vector from its spherical coordinates using
     * the inclination angle from the zenith and the azimuth angle.
     *
     * @param radius the length of the vector
     * @param inclination the inclination from the zenith
     * @param azimuth the azimuth angle
     * @return the new mutable vector
     */
    public static Vec3M fromSphericalCoordinatesRIA(float radius,
            float inclination, float azimuth) {
        final float rsi = radius * (float)Math.sin(inclination);
        final float rci = radius * (float)Math.cos(inclination);
        final float ca = (float)Math.cos(azimuth);
        final float sa = (float)Math.sin(azimuth);
        return new Vec3M(rsi * ca, rsi * sa, rci);
    }

    /**
     * Creates a new mutable 3D vector from its spherical coordinates using
     * the elevation angle from the reference plane and the azimuth angle.
     *
     * @param radius the length of the vector
     * @param elevation the elevation from the reference plane
     * @param azimuth the azimuth angle
     * @return the new mutable vector
     */
    public static Vec3M fromSphericalCoordinatesREA(float radius,
            float elevation, float azimuth) {
        final float rce = radius * (float)Math.cos(elevation);
        final float rse = radius * (float)Math.sin(elevation);
        final float ca = (float)Math.cos(azimuth);
        final float sa = (float)Math.sin(azimuth);
        return new Vec3M(rce * ca, rce * sa, rse);
    }

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new mutable 3D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 12 bytes in the
     *         buffer
     */
    public static Vec3M readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec3M(b.getFloat(), b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new mutable 3D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 12 bytes in the
     *         buffer
     */
    public static Vec3M readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec3M result = new Vec3M(b.getFloat(i[0]), b.getFloat(i[0]+4),
                                        b.getFloat(i[0]+8));
        i[0] += 12;
        return result;
    }

    /*---------------------- BINARY OPERATIONS TO THIS -----------------------*/

    /**
     * Sums this vector to another one and stores the result in this vector.
     *
     * @param v the other vector
     * @return <code>this += v</code>
     */
    public Vec3M plusEq(Vec3Base v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    /**
     * Sums this vector to another one scaled by a scalar and stores the result
     * in this vector.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this += s*v</code>
     */
    public Vec3M plusEq(float s, Vec3Base v) {
        this.x += s*v.x;
        this.y += s*v.y;
        this.z += s*v.z;
        return this;
    }

    /**
     * Subtracts this vector to another one and stores the result in this
     * vector.
     *
     * @param v the other vector
     * @return <code>this -= v</code>
     */
    public Vec3M minusEq(Vec3Base v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    /**
     * Subtracts this vector to another one scaled by a scalar and stores the
     * result in this vector.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this -= s*v</code>
     */
    public Vec3M minusEq(float s, Vec3Base v) {
        this.x -= s*v.x;
        this.y -= s*v.y;
        this.z -= s*v.z;
        return this;
    }

    /**
     * Multiplies this vector and a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @return <code>this *= s</code>
     */
    public Vec3M timesEq(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    /**
     * Computes the cross product between this vector and another one and store
     * the result in this vector.
     *
     * @param v the other vector
     * @return <code>this = cross(this, v)</code>
     */
    public final Vec3M crossEq(Vec3Base v) {
        final float a = this.y * v.z - this.z * v.y;
        final float b = this.z * v.x - this.x * v.z;
        final float c = this.x * v.y - this.y * v.x;
        return this.set(a, b, c);
    }

    /**
     * Adds two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v + w</code>
     */
    public Vec3M add(Vec3Base v, Vec3Base w) {
        this.x = v.x + w.x;
        this.y = v.y + w.y;
        this.z = v.z + w.z;
        return this;
    }

    /**
     * Adds two vectors, the second scaled, and stores the result in this
     * vector.
     *
     * @param v the first vector
     * @param s the scalar to multiply to <code>w</code>
     * @param w the second vector
     * @return <code>this = v + s*w</code>
     */
    public Vec3M add(Vec3Base v, float s, Vec3Base w) {
        this.x = v.x + s*w.x;
        this.y = v.y + s*w.y;
        this.z = v.z + s*w.z;
        return this;
    }

    /**
     * Adds two scaled vectors and stores the result in this vector.
     *
     * @param t the scalar to multiply to <code>v</code>
     * @param v the first vector
     * @param s the scalar to multiply to <code>w</code>
     * @param w the second vector
     * @return <code>this = t*v + s*w</code>
     */
    public Vec3M add(float t, Vec3Base v, float s, Vec3Base w) {
        this.x = t*v.x + s*w.x;
        this.y = t*v.y + s*w.y;
        this.z = t*v.z + s*w.z;
        return this;
    }

    /**
     * Subtracts two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v - w</code>
     */
    public Vec3M sub(Vec3Base v, Vec3Base w) {
        this.x = v.x - w.x;
        this.y = v.y - w.y;
        this.z = v.z - w.z;
        return this;
    }

    /**
     * Subtracts two vectors, the second scaled, and stores the result in this
     * vector.
     *
     * @param v the first vector
     * @param s the scalar to multiply to <code>w</code>
     * @param w the second vector
     * @return <code>this = v - s*w</code>
     */
    public Vec3M sub(Vec3Base v, float s, Vec3Base w) {
        this.x = v.x - s*w.x;
        this.y = v.y - s*w.y;
        this.z = v.z - s*w.z;
        return this;
    }

    /**
     * Subtracts two scaled vectors and stores the result in this vector.
     *
     * @param t the scalar to multiply to <code>v</code>
     * @param v the first vector
     * @param s the scalar to multiply to <code>w</code>
     * @param w the second vector
     * @return <code>this = t*v - s*w</code>
     */
    public Vec3M sub(float t, Vec3Base v, float s, Vec3Base w) {
        this.x = t*v.x - s*w.x;
        this.y = t*v.y - s*w.y;
        this.z = t*v.z - s*w.z;
        return this;
    }

    /**
     * Multiplies a vector by a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @param v the vector
     * @return <code>this = s*v</code>
     */
    public Vec3M mul(float s, Vec3Base v) {
        this.x = s*v.x;
        this.y = s*v.y;
        this.z = s*v.z;
        return this;
    }

    /**
     * Computes the cross product between two vectors and stores the result in
     * this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = cross(v, w)</code>
     */
    public final Vec3M toCross(Vec3Base v, Vec3Base w) {
        final float a = v.y * w.z - v.z * w.y;
        final float b = v.z * w.x - v.x * w.z;
        final float c = v.x * w.y - v.y * w.x;
        return this.set(a, b, c);
    }

    /*----------------------- UNARY OPERATIONS TO THIS -----------------------*/

    /**
     * Negates this vector.
     *
     * @return <code>this *= -1</code>
     */
    public Vec3M negate() {
        return this.timesEq(-1.0f);
    }

    /**
     * Normalizes this vector.
     *
     * @return <code>this /= this.length()</code>
     */
    public Vec3M normalize() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesEq(oneOnLength);
    }

    /*------------------------ 3D TRANSFORMATIONS ----------------------------*/

    /**
     * Scales this vector non-uniformly.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @return this vector scaled
     */
    public Vec3M scale(float sx, float sy, float sz) {
        return this.set(sx * this.x, sy * this.y, sz * this.z);
    }

    /**
     * Scales this vector non-uniformly.
     *
     * @param s scalar factors stored in a vector
     * @return this vector scaled
     */
    public Vec3M scale(Vec3Base s) {
        return this.set(s.x, s.y, s.z);
    }
}
