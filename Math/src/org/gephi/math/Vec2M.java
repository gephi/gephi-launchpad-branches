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
 * Mutable 2D vector class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Vec2M extends Vec2 {

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Defaults constructor of Vec2M which creates a zero vector.
     */
    public Vec2M() {
        super(0.0f, 0.0f);
    }

    /**
     * Creates a new 2D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     */
    public Vec2M(float x, float y) {
        super(x, y);
    }

    /**
     * Creates a copy of another 2D vector.
     *
     * @param v the 2D vector to copy
     */
    public Vec2M(Vec2 v) {
        super(v);
    }

    /*-------------------------------- SETTERS -------------------------------*/

    /**
     * Sets the first component of the vector.
     *
     * @param x the new first component of the vector
     * @return this
     */
    public Vec2M x(float x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the second component of the vector.
     *
     * @param y the new second component of the vector
     * @return this
     */
    public Vec2M y(float y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the components of the vector.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @return this
     */
    public Vec2M set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets the components of the vector to be equal to another vector.
     *
     * @param v the other vector
     * @return this
     */
    public Vec2M set(Vec2 v) {
        return set(v.x, v.y);
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Creates a new mutable 2D vector from its polar coordinates.
     *
     * @param radius the length of the new vector
     * @param angle the angle between the x-axis and the new vector
     * @return the new vector
     */
    public static Vec2M fromPolarCoordinates(float radius, float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Vec2M(radius * c, radius * s);
    }

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new mutable 2D vector or <code>null</code>
     * @throws BufferUnderflowException if there are less than 8 bytes in the
     *         buffer
     */
    public static Vec2M readFrom(ByteBuffer b) throws BufferUnderflowException {
        return new Vec2M(b.getFloat(), b.getFloat());
    }

    /**
     * Reads a new mutable vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the vector. It is
     *          updated to point to the next byte after the vector
     * @return the new mutable 2D vector or <code>null</code>
     * @throws BufferUnderflowException if there are less than 8 bytes in the
     *         buffer
     */
    public static Vec2M readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final Vec2M result = new Vec2M(b.getFloat(i[0]), b.getFloat(i[0]+4));
        i[0] += 8;
        return result;
    }

    /*---------------------- BINARY OPERATIONS TO THIS -----------------------*/

    /**
     * Sums this vector to another one and stores the result in this vector.
     *
     * @param v the other vector
     * @return <code>this += v</code>
     */
    public Vec2M plusEq(Vec2 v) {
        this.x += v.x;
        this.y += v.y;
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
    public Vec2M plusEq(float s, Vec2 v) {
        this.x += s*v.x;
        this.y += s*v.y;
        return this;
    }

    /**
     * Subtracts this vector to another one and stores the result in this
     * vector.
     *
     * @param v the other vector
     * @return <code>this -= v</code>
     */
    public Vec2M minusEq(Vec2 v) {
        this.x -= v.x;
        this.y -= v.y;
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
    public Vec2M minusEq(float s, Vec2 v) {
        this.x -= s*v.x;
        this.y -= s*v.y;
        return this;
    }

    /**
     * Multiplies this vector and a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @return <code>this *= s</code>
     */
    public Vec2M timesEq(float s) {
        this.x *= s;
        this.y *= s;
        return this;
    }

    /**
     * Adds two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v + w</code>
     */
    public Vec2M add(Vec2 v, Vec2 w) {
        this.x = v.x + w.x;
        this.y = v.y + w.y;
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
    public Vec2M add(Vec2 v, float s, Vec2 w) {
        this.x = v.x + s*w.x;
        this.y = v.y + s*w.y;
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
    public Vec2M add(float t, Vec2 v, float s, Vec2 w) {
        this.x = t*v.x + s*w.x;
        this.y = t*v.y + s*w.y;
        return this;
    }

    /**
     * Subtracts two vectors and stores the result in this vector.
     *
     * @param v the first vector
     * @param w the second vector
     * @return <code>this = v - w</code>
     */
    public Vec2M sub(Vec2 v, Vec2 w) {
        this.x = v.x - w.x;
        this.y = v.y - w.y;
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
    public Vec2M sub(Vec2 v, float s, Vec2 w) {
        this.x = v.x - s*w.x;
        this.y = v.y - s*w.y;
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
    public Vec2M sub(float t, Vec2 v, float s, Vec2 w) {
        this.x = t*v.x - s*w.x;
        this.y = t*v.y - s*w.y;
        return this;
    }

    /**
     * Multiplies a vector by a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @param v the vector
     * @return <code>this = s*v</code>
     */
    public Vec2M mul(float s, Vec2 v) {
        this.x = s*v.x;
        this.y = s*v.y;
        return this;
    }

    /*----------------------- UNARY OPERATIONS TO THIS -----------------------*/

    /**
     * Negates this vector.
     *
     * @return <code>this *= -1</code>
     */
    public Vec2M negate() {
        return this.timesEq(-1.0f);
    }

    /**
     * Normalizes this vector.
     *
     * @return <code>this /= this.length()</code>
     */
    public Vec2M normalize() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesEq(oneOnLength);
    }

    /**
     * Rotates counter-clockwise this vector by 90 degree.
     *
     * @return this vector rotated
     */
    public Vec2M toPerp() {
        return this.set(- this.y, this.x);
    }

    /*------------------------ 2D TRANSFORMATIONS ----------------------------*/

    /**
     * Rotates this vector counter-clockwise by some angle.
     *
     * @param angle the angle of rotation expressed in radians
     * @return this vector rotated
     */
    public Vec2M rotate(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return this.set(this.x * c - this.y * s, this.x * s + this.y * c);
    }

    /**
     * Scales this vector non-uniformly.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @return this vector scaled
     */
    public Vec2M scale(float sx, float sy) {
        return this.set(sx * this.x, sy * this.y);
    }

    /**
     * Scales this vector non-uniformly.
     *
     * @param s scalar factors stored in a vector
     * @return this vector scaled
     */
    public Vec2M scale(Vec2 s) {
        return this.set(s.x, s.y);
    }
}
