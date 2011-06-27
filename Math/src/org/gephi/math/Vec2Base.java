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
 * 2D vector class. All the constructors of this class are protected and its
 * subclasses should be used instead.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Vec2Base {

    /*----------------------- PROTECTED DATA FIELDS --------------------------*/

    /**
     * Components of the vector in the standard basis.
     */
    protected float x, y;

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 2D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     */
    protected Vec2Base(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a copy of another 2D vector.
     *
     * @param v the 2D vector to copy
     */
    protected Vec2Base(Vec2Base v) {
        this(v.x, v.y);
    }

    /**
     * Creates a new 2D vector from its components stored in an array.
     *
     * @param arr the array containing the components of the vector. The
     *            components are the first two elements of the array.
     * @throws IndexOutOfBoundsException if the array contains less than two
     *                                   elements
     */
    protected Vec2Base(float[] arr) throws IndexOutOfBoundsException {
        this(arr[0], arr[1]);
    }

    /**
     * Creates a new 2D vector from its components stored in an array.
     *
     * @param arr the array containing the components of the vector
     * @param i index of the first component of the 2D vector
     * @throws IndexOutOfBoundsException if the array contains less than 
     *                                   <code>i+2</code> elements
     */
    protected Vec2Base(float[] arr, int i) throws IndexOutOfBoundsException {
        this(arr[i], arr[i+1]);
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

    /*---------------------- OVERRIDDEN OBJECT'S METHODS ---------------------*/

    /**
     * Compares two 2D vectors for equality. Two vectors are equal if their
     * components are equal.
     *
     * @param obj the object to compare with
     * @return <code>true</code> if <code>obj</code> is a 2D vector and if 
     *         their components are equal, <code>false</code> otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Vec2Base)) return false;
        
        final Vec2Base v = (Vec2Base) obj;

        return (this.x == v.x) && (this.y == v.y);
    }

    /**
     * Returns an integer hash for this 2D vector based on the raw bit
     * representation of its components.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        int ix = Float.floatToRawIntBits(this.x);
        int iy = Float.floatToRawIntBits(this.y);

        return (ix & 0xFFFF0000) | (iy >> 16);
    }

    /**
     * Returns a string representing this 2D vector.
     *
     * @return the string
     */
    @Override
    public final String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    /*------------------------- VECTOR TO SCALAR MAPS ------------------------*/

    /**
     * Computes the dot product of this vector with another one.
     *
     * @param v the other vector
     * @return the dot product of this vector and <code>v</code>
     */
    public final float dot(Vec2Base v) {
        return this.x * v.x + this.y * v.y;
    }

    /**
     * Computes the perpendicular dot product of this vector with another one.
     *
     * @param v the other vector
     * @return the perpendicular dot product of this vector and <code>v</code>
     */
    public final float perpDot(Vec2Base v) {
        return this.x * v.y - this.y * v.x;
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

    /**
     * Returns the angle in radians from the x-axis to this vector.
     *
     * @return the angle from the x-axis to this vector
     */
    public final float angle() {
        return (float) Math.atan2(this.y, this.x);
    }

    /**
     * Returns the angle in radians from this vector to another one.
     *
     * @param v the other vector
     * @return the angle from this vector to <code>v</code>
     */
    public final float angle(Vec2Base v) {
        return (float) Math.atan2(this.perpDot(v), this.dot(v));
    }

    /*------------------- BINARY OPERATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec2 plus(Vec2Base v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec2 plus(float s, Vec2Base v) {
        return new Vec2(this.x + s * v.x, this.y + s * v.y);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec2 minus(Vec2Base v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec2 minus(float s, Vec2Base v) {
        return new Vec2(this.x - s * v.x, this.y - s * v.y);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec2 times(float s) {
        return new Vec2(s * this.x, s * this.y);
    }

    /*------------------- BINARY OPERATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec2M plusM(Vec2Base v) {
        return new Vec2M(this.x + v.x, this.y + v.y);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec2M plusM(float s, Vec2Base v) {
        return new Vec2M(this.x + s * v.x, this.y + s * v.y);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec2M minusM(Vec2Base v) {
        return new Vec2M(this.x - v.x, this.y - v.y);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec2M minusM(float s, Vec2Base v) {
        return new Vec2M(this.x - s * v.x, this.y - s * v.y);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec2M timesM(float s) {
        return new Vec2M(s * this.x, s * this.y);
    }

    /*------------------- UNARY OPERATIONS TO NEW IMMUTABLE ------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec2 negated() {
        return this.times(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec2 normalized() {
        final float oneOnLength = 1.0f / this.length();
        return this.times(oneOnLength);
    }

    /**
     * Returns the vector perpendicular to this vector obtained by a 90 degree
     * clockwise rotation.
     *
     * @return the perpendicular vector
     */
    public final Vec2 perp() {
        return new Vec2(- this.y, this.x);
    }

    /*------------------- UNARY OPERATIONS TO NEW MUTABLE --------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec2M negatedM() {
        return this.timesM(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec2M normalizedM() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesM(oneOnLength);
    }

    /**
     * Returns the vector perpendicular to this vector obtained by a 90 degree
     * counter-clockwise rotation.
     *
     * @return the perpendicular vector
     */
    public final Vec2M perpM() {
        return new Vec2M(- this.y, this.x);
    }

    /*------------------ 2D TRANSFORMATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns this vector rotated by some angle.
     *
     * @param angle the angle of rotation expressed in radians
     * @return the rotated vector
     */
    public final Vec2 rotated(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Vec2(this.x * c - this.y * s, this.x * s + this.y * c);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @return the scaled vector
     */
    public final Vec2 scaled(float sx, float sy) {
        return new Vec2(sx * this.x, sy * this.y);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec2 scaled(Vec2Base s) {
        return this.scaled(s.x, s.y);
    }

    /*------------------ 2D TRANSFORMATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns this vector rotated by some angle.
     *
     * @param angle the angle of rotation expressed in radians
     * @return the rotated vector
     */
    public final Vec2M rotatedM(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Vec2M(this.x * c - this.y * s, this.x * s + this.y * c);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @return the scaled vector
     */
    public final Vec2M scaledM(float sx, float sy) {
        return new Vec2M(sx * this.x, sy * this.y);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec2M scaledM(Vec2Base s) {
        return this.scaledM(s.x, s.y);
    }

    /*-------------------------- CAST AND COPY METHODS -----------------------*/

    /**
     * Returns a new array containing the components of this vector.
     *
     * @return the new array
     */
    public final float[] toArray() {
        return new float[]{this.x, this.y};
    }

    /**
     * Returns a new immutable copy of this vector.
     *
     * @return the immutable copy of this vector
     */
    public final Vec2 copy() {
        return new Vec2(this.x, this.y);
    }

    /**
     * Returns a new mutable version of this vector.
     *
     * @return the mutable copy of this vector
     */
    public final Vec2M copyM() {
        return new Vec2M(this.x, this.y);
    }


    /*-------------------------- OUTPUT TO BYTEBUFFERS -----------------------*/

    /**
     * Writes this vector to the end of a <code>ByteBuffer</code>.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @throws BufferOverflowException if there are fewer than eight bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final void writeTo(ByteBuffer b) throws BufferOverflowException,
            ReadOnlyBufferException {
        b.putFloat(this.x);
        b.putFloat(this.y);
    }

    /**
     * Writes this vector in a <code>ByteBuffer</code> at a specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to write the vector
     * @return the position of the first byte after the vector
     * @throws BufferOverflowException if there are fewer than eight bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final int writeTo(ByteBuffer b, int i)
                      throws BufferOverflowException, ReadOnlyBufferException  {
        b.putFloat(i, this.x);
        b.putFloat(i+4, this.y);
        return i + 8;
    }
}
