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
 * Mutable 4D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Vec4M extends Vec4 {

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * The default constructor of Vec4M creates a zero vector.
     */
    public Vec4M() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    /**
     * Creates a new 4D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     * @param w the third component of the vector
     */
    public Vec4M(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    /**
     * Creates a copy of another 4D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec4M(Vec4 v) {
        super(v);
    }
    
    /**
     * Creates a 4D vector from a 3D vector and the last component.
     * 
     * @param v the 3D vector
     * @param w the fourth component
     */
    public Vec4M(Vec3 v, float w) {
        super(v.x, v.y, v.z, w);
    }

    /*-------------------------------- SETTERS -------------------------------*/

    /**
     * Sets the first component of the vector.
     *
     * @param x the new first component of the vector
     * @return this
     */
    public Vec4M x(float x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the second component of the vector.
     *
     * @param y the new second component of the vector
     * @return this
     */
    public Vec4M y(float y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the third component of the vector.
     *
     * @param z the new third component of the vector
     * @return this
     */
    public Vec4M z(float z) {
        this.z = z;
        return this;
    }
    
    /**
     * Sets the fourth component of the vector.
     *
     * @param w the new fourth component of the vector
     * @return this
     */
    public Vec4M w(float w) {
        this.w = w;
        return this;
    }

    /**
     * Sets the components of the vector.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     * @param w the new fourth component of the vector
     * @return this
     */
    public Vec4M set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Sets the components of the vector to be equal to another vector.
     *
     * @param v the other vector
     * @return this
     */
    public Vec4M set(Vec4 v) {
        return set(v.x, v.y, v.z, v.w);
    }
    
    /**
     * Sets the components of the vector from a 3D vector and the fourth 
     * component.
     *
     * @param v the 3D vector
     * @param w the new fourth component of the vector
     * @return this
     */
    public Vec4M set(Vec3 v, float w) {
        return set(v.x, v.y, v.z, w);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new mutable 4D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 16 bytes in the
     *         buffer
     */
    public static Vec4M readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec4M(b.getFloat(), b.getFloat(), b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new mutable 4D vector or <code></code>
     * @throws BufferUnderflowException if there are less than 16 bytes in the
     *         buffer
     */
    public static Vec4M readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec4M result = new Vec4M(b.getFloat(i[0]), b.getFloat(i[0]+4),
                                        b.getFloat(i[0]+8), b.getFloat(i[0]+12));
        i[0] += 16;
        return result;
    }

    /*---------------------- BINARY OPERATIONS TO THIS -----------------------*/

    /**
     * Sums this vector to another one and stores the result in this vector.
     *
     * @param v the other vector
     * @return <code>this += v</code>
     */
    public Vec4M plusEq(Vec4 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        this.w += v.w;
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
    public Vec4M plusEq(float s, Vec4 v) {
        this.x += s*v.x;
        this.y += s*v.y;
        this.z += s*v.z;
        this.w += s*v.w;
        return this;
    }

    /**
     * Subtracts this vector to another one and stores the result in this
     * vector.
     *
     * @param v the other vector
     * @return <code>this -= v</code>
     */
    public Vec4M minusEq(Vec4 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        this.w -= v.w;
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
    public Vec4M minusEq(float s, Vec4 v) {
        this.x -= s*v.x;
        this.y -= s*v.y;
        this.z -= s*v.z;
        this.w -= s*v.w;
        return this;
    }

    /**
     * Multiplies this vector and a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @return <code>this *= s</code>
     */
    public Vec4M timesEq(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= s;
        return this;
    }

    /**
     * Adds two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v + w</code>
     */
    public Vec4M add(Vec4 v, Vec4 w) {
        this.x = v.x + w.x;
        this.y = v.y + w.y;
        this.z = v.z + w.z;
        this.w = v.w + w.w;
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
    public Vec4M add(Vec4 v, float s, Vec4 w) {
        this.x = v.x + s*w.x;
        this.y = v.y + s*w.y;
        this.z = v.z + s*w.z;
        this.w = v.w + s*w.w;
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
    public Vec4M add(float t, Vec4 v, float s, Vec4 w) {
        this.x = t*v.x + s*w.x;
        this.y = t*v.y + s*w.y;
        this.z = t*v.z + s*w.z;
        this.w = t*v.w + s*w.w;
        return this;
    }

    /**
     * Subtracts two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v - w</code>
     */
    public Vec4M sub(Vec4 v, Vec4 w) {
        this.x = v.x - w.x;
        this.y = v.y - w.y;
        this.z = v.z - w.z;
        this.w = v.w - w.w;
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
    public Vec4M sub(Vec4 v, float s, Vec4 w) {
        this.x = v.x - s*w.x;
        this.y = v.y - s*w.y;
        this.z = v.z - s*w.z;
        this.w = v.w - s*w.w;
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
    public Vec4M sub(float t, Vec4 v, float s, Vec4 w) {
        this.x = t*v.x - s*w.x;
        this.y = t*v.y - s*w.y;
        this.z = t*v.z - s*w.z;
        this.w = t*v.w - s*w.w;
        return this;
    }

    /**
     * Multiplies a vector by a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @param v the vector
     * @return <code>this = s*v</code>
     */
    public Vec4M mul(float s, Vec4 v) {
        this.x = s*v.x;
        this.y = s*v.y;
        this.z = s*v.z;
        this.w = s*v.w;
        return this;
    }

    /*----------------------- UNARY OPERATIONS TO THIS -----------------------*/

    /**
     * Negates this vector.
     *
     * @return <code>this *= -1</code>
     */
    public Vec4M negate() {
        return this.timesEq(-1.0f);
    }

    /**
     * Normalizes this vector.
     *
     * @return <code>this /= this.length()</code>
     */
    public Vec4M normalize() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesEq(oneOnLength);
    }

    /*------------------------ 4D TRANSFORMATIONS ----------------------------*/

    /**
     * Scales this vector non-uniformly.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @param sw scalar factor along the w-axis
     * @return this vector scaled
     */
    public Vec4M scale(float sx, float sy, float sz, float sw) {
        return this.set(sx * this.x, sy * this.y, sz * this.z, sw * this.w);
    }

    /**
     * Scales this vector non-uniformly.
     *
     * @param s scalar factors stored in a vector
     * @return this vector scaled
     */
    public Vec4M scale(Vec4 s) {
        return this.set(s.x, s.y, s.z, s.w);
    }
}
