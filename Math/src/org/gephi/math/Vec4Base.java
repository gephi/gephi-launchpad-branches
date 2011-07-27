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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * 4D vector class. All the constructors of this class are protected and its
 * subclasses should be used instead.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Vec4Base {

    /*----------------------- PROTECTED DATA FIELDS --------------------------*/

    /**
     * Components of the vector in the standard basis.
     */
    protected float x, y, z, w;

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 4D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     * @param w the fourth component of the vector
     */
    protected Vec4Base(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Creates a copy of another 4D vector.
     *
     * @param v the 4D vector to copy
     */
    protected Vec4Base(Vec4Base v) {
        this(v.x, v.y, v.z, v.w);
    }

    /*------------------------------ ACCESSORS -------------------------------*/

    /**
     * Returns the first component of the vector in the standard basis.
     *
     * @return the first component of the vector
     */
    public final float x() {
        return this.x;
    }

    /**
     * Returns the second component of the vector in the standard basis.
     *
     * @return the second component of the vector
     */
    public final float y() {
        return this.y;
    }

    /**
     * Returns the third component of the vector in the standard basis.
     *
     * @return the third component of the vector
     */
    public final float z() {
        return this.z;
    }
    
    /**
     * Returns the fourth component of the vector in the standard basis.
     *
     * @return the fourth component of the vector
     */
    public final float w() {
        return this.w;
    }

    /*---------------------- OVERRIDDEN OBJECT'S METHODS ---------------------*/

    /**
     * Compares two 4D vectors for equality. Two vectors are equal if their
     * components are equal.
     *
     * @param obj the object to compare with
     * @return <code>true</code> if <code>obj</code> is a 4D vector and if
     *         their components are equal, <code>false</code> otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Vec4Base)) return false;

        final Vec4Base v = (Vec4Base) obj;

        return (this.x == v.x) && (this.y == v.y) && (this.z == v.z) && (this.w == v.w);
    }

    /**
     * Returns an integer hash for this 4D vector based on the raw bit
     * representation of its components.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        final int ix = Float.floatToRawIntBits(this.x);
        final int iy = Float.floatToRawIntBits(this.y);
        final int iz = Float.floatToRawIntBits(this.z);
        final int iw = Float.floatToRawIntBits(this.w);

        return (ix & 0xFF000000) | ((iy  & 0xFF000000) >> 8) | ((iz  & 0xFF000000) >> 16) | (iw >> 24);
    }

    /**
     * Returns a string representing this 4D vector.
     *
     * @return the string
     */
    @Override
    public final String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    /*------------------------- VECTOR TO SCALAR MAPS ------------------------*/

    /**
     * Computes the dot product of this vector with another one.
     *
     * @param v the other vector
     * @return the dot product of this vector and <code>v</code>
     */
    public final float dot(Vec4Base v) {
        return this.x * v.x + this.y * v.y + this.z * v.z + this.w * v.w;
    }

    /**
     * Returns the square of the Euclidean norm of this vector.
     *
     * @return the square of the Euclidean norm
     */
    public final float lengthSquared() {
        return this.dot(this);
    }

    /**
     * Returns the Euclidean norm of this vector.
     *
     * @return the Euclidean norm
     */
    public final float length() {
        return (float) Math.sqrt(this.lengthSquared());
    }

    /*------------------- BINARY OPERATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec4 plus(Vec4Base v) {
        return new Vec4(this.x + v.x, this.y + v.y, this.z + v.z, this.w + v.w);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec4 plus(float s, Vec4Base v) {
        return new Vec4(this.x + s * v.x, this.y + s * v.y, this.z + s * v.z, this.w + s * v.w);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec4 minus(Vec4Base v) {
        return new Vec4(this.x - v.x, this.y - v.y, this.z - v.z, this.w - v.w);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec4 minus(float s, Vec4Base v) {
        return new Vec4(this.x - s * v.x, this.y - s * v.y, this.z - s * v.z, this.w - s * v.w);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec4 times(float s) {
        return new Vec4(s * this.x, s * this.y, s * this.z, s * this.w);
    }

    /*------------------- BINARY OPERATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec4M plusM(Vec4Base v) {
        return new Vec4M(this.x + v.x, this.y + v.y, this.z + v.z, this.w + v.w);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec4M plusM(float s, Vec4Base v) {
        return new Vec4M(this.x + s * v.x, this.y + s * v.y, this.z + s * v.z, this.w + s * v.w);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec4M minusM(Vec4Base v) {
        return new Vec4M(this.x - v.x, this.y - v.y, this.z - v.z, this.w - v.w);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec4M minusM(float s, Vec4Base v) {
        return new Vec4M(this.x - s * v.x, this.y - s * v.y, this.z - s * v.z, this.w - s * v.w);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec4M timesM(float s) {
        return new Vec4M(s * this.x, s * this.y, s * this.z, s * this.w);
    }

    /*------------------- UNARY OPERATIONS TO NEW IMMUTABLE ------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec4 negated() {
        return this.times(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec4 normalized() {
        final float oneOnLength = 1.0f / this.length();
        return this.times(oneOnLength);
    }

    /*------------------- UNARY OPERATIONS TO NEW MUTABLE --------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec4M negatedM() {
        return this.timesM(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec4M normalizedM() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesM(oneOnLength);
    }

    /*------------------ 4D TRANSFORMATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @param sw scalar factor along the w-axis
     * @return the scaled vector
     */
    public final Vec4 scaled(float sx, float sy, float sz, float sw) {
        return new Vec4(sx * this.x, sy * this.y, sz * this.z, sw * this.w);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec4 scaled(Vec4Base s) {
        return this.scaled(s.x, s.y, s.z, s.w);
    }

    /*------------------ 4D TRANSFORMATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @param sw scalar factor along the w-axis
     * @return the scaled vector
     */
    public final Vec4M scaledM(float sx, float sy, float sz, float sw) {
        return new Vec4M(sx * this.x, sy * this.y, sz * this.z, sw * this.w);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec4M scaledM(Vec4Base s) {
        return this.scaledM(s.x, s.y, s.z, s.w);
    }

    /*-------------------------- CAST AND COPY METHODS -----------------------*/

    /**
     * Returns a new array containing the components of this vector.
     *
     * @return the new array
     */
    public final float[] toArray() {
        return new float[]{this.x, this.y, this.z, this.w};
    }

    /**
     * Returns a new immutable copy of this vector.
     *
     * @return the immutable copy of this vector
     */
    public final Vec4 copy() {
        return new Vec4(this.x, this.y, this.z, this.w);
    }

    /**
     * Returns a new mutable version of this vector.
     *
     * @return the mutable copy of this vector
     */
    public final Vec4M copyM() {
        return new Vec4M(this.x, this.y, this.z, this.w);
    }


    /*-------------------------- OUTPUT TO BYTEBUFFERS -----------------------*/

    /**
     * Writes this vector to the end of a <code>ByteBuffer</code>.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @throws BufferOverflowException if there are fewer than sixteen bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final void writeTo(ByteBuffer b) throws BufferOverflowException,
            ReadOnlyBufferException {
        b.putFloat(this.x);
        b.putFloat(this.y);
        b.putFloat(this.z);
        b.putFloat(this.w);
    }

    /**
     * Writes this vector in a <code>ByteBuffer</code> at a specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to write the vector
     * @return the position of the first byte after the vector
     * @throws BufferOverflowException if there are fewer than sixteen bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final int writeTo(ByteBuffer b, int i)
                      throws BufferOverflowException, ReadOnlyBufferException  {
        b.putFloat(i, this.x);
        b.putFloat(i+4, this.y);
        b.putFloat(i+8, this.z);
        b.putFloat(i+12, this.w);
        return i + 16;
    }
}
