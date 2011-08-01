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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * 3D vector class. All the constructors of this class are protected and its
 * subclasses should be used instead.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Vec3Base {

    /*----------------------- PROTECTED DATA FIELDS --------------------------*/

    /**
     * Components of the vector in the standard basis.
     */
    protected float x, y, z;

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new 3D vector from its components.
     *
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @param z the third component of the vector
     */
    protected Vec3Base(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a copy of another 3D vector.
     *
     * @param v the 3D vector to copy
     */
    protected Vec3Base(Vec3Base v) {
        this(v.x, v.y, v.z);
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

    /*---------------------- OVERRIDDEN OBJECT'S METHODS ---------------------*/

    /**
     * Compares two 3D vectors for equality. Two vectors are equal if their
     * components are equal.
     *
     * @param obj the object to compare with
     * @return <code>true</code> if <code>obj</code> is a 3D vector and if
     *         their components are equal, <code>false</code> otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Vec3Base)) return false;

        final Vec3Base v = (Vec3Base) obj;

        return (this.x == v.x) && (this.y == v.y) && (this.z == v.z);
    }

    /**
     * Returns an integer hash for this 3D vector based on the raw bit
     * representation of its components.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        final int ix = Float.floatToRawIntBits(this.x);
        final int iy = Float.floatToRawIntBits(this.y);
        final int iz = Float.floatToRawIntBits(this.z);

        return (ix & 0xFFe00000) | ((iy  & 0xFFe00000) >> 11) | (iz >> 22);
    }

    /**
     * Returns a string representing this 3D vector.
     *
     * @return the string
     */
    @Override
    public final String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    /*------------------------- VECTOR TO SCALAR MAPS ------------------------*/

    /**
     * Computes the dot product of this vector with another one.
     *
     * @param v the other vector
     * @return the dot product of this vector and <code>v</code>
     */
    public final float dot(Vec3Base v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    /**
     * Computes the scalar triple product of this vector and other two, ie.
     * it computes the volume of the parallelepiped spanned by the three
     * vectors.
     *
     * @param v the second vector
     * @param w the third vector
     * @return dot(cross(this, v), w)
     */
    public final float triple(Vec3Base v, Vec3Base w) {
        float a = this.y * v.z - this.z * v.y;
        float b = this.z * v.x - this.x * v.z;
        float c = this.x * v.y - this.y * v.x;
        return a * w.x + b * w.y + c * w.z;
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
     * Returns the inclination angle from the zenith in spherical coordinates.
     *
     * @return the inclination angle from the zenith
     */
    public final float inclination() {
        return (float) Math.acos(this.z / this.length());
    }

    /**
     * Returns the elevation angle from the reference plane in spherical
     * coordinates.
     *
     * @return the elevation angle from the reference plane
     */
    public final float elevation() {
        return (float) Math.asin(this.z / this.length());
    }

    /**
     * Returns the azimuth angle in spherical coordinates.
     *
     * @return the azimuth angle
     */
    public final float azimuth() {
        return (float) Math.atan2(this.y, this.x);
    }

    /**
     * Returns the angle in radians from this vector to another one.
     *
     * @param v the other vector
     * @return the angle from this vector to <code>v</code>
     */
    public final float angle(Vec3Base v) {
        return (float) Math.atan2(this.cross(v).length(), this.dot(v));
    }

    /*------------------- BINARY OPERATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec3 plus(Vec3Base v) {
        return new Vec3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec3 plus(float s, Vec3Base v) {
        return new Vec3(this.x + s * v.x, this.y + s * v.y, this.z + s * v.z);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec3 minus(Vec3Base v) {
        return new Vec3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec3 minus(float s, Vec3Base v) {
        return new Vec3(this.x - s * v.x, this.y - s * v.y, this.z - s * v.z);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec3 times(float s) {
        return new Vec3(s * this.x, s * this.y, s * this.z);
    }

    /**
     * Returns the cross product between this vector and another one.
     *
     * @param v the other vector
     * @return <code>cross(this, v)</code>
     */
    public final Vec3 cross(Vec3Base v) {
        final float a = this.y * v.z - this.z * v.y;
        final float b = this.z * v.x - this.x * v.z;
        final float c = this.x * v.y - this.y * v.x;
        return new Vec3(a, b, c);
    }

    /*------------------- BINARY OPERATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns the sum of this vector and another one.
     *
     * @param v the other vector
     * @return <code>this + v</code>
     */
    public final Vec3M plusM(Vec3Base v) {
        return new Vec3M(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Returns the sum of this vector and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this + s*v</code>
     */
    public final Vec3M plusM(float s, Vec3Base v) {
        return new Vec3M(this.x + s * v.x, this.y + s * v.y, this.z + s * v.z);
    }

    /**
     * Returns the difference between this vector and another one.
     *
     * @param v the other vector
     * @return <code>this - v</code>
     */
    public final Vec3M minusM(Vec3Base v) {
        return new Vec3M(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    /**
     * Returns the difference between this vector and another one scaled by a
     * scalar.
     *
     * @param s the scalar to multiply to <code>v</code>
     * @param v the other vector
     * @return <code>this - s*v</code>
     */
    public final Vec3M minusM(float s, Vec3Base v) {
        return new Vec3M(this.x - s * v.x, this.y - s * v.y, this.z - s * v.z);
    }

    /**
     * Returns the product between this vector and a scalar.
     *
     * @param s the scalar
     * @return <code>this * s</code>
     */
    public final Vec3M timesM(float s) {
        return new Vec3M(s * this.x, s * this.y, s * this.z);
    }

    /**
     * Returns the cross product between this vector and another one.
     *
     * @param v the other vector
     * @return <code>cross(this, v)</code>
     */
    public final Vec3M crossM(Vec3Base v) {
        final float a = this.y * v.z - this.z * v.y;
        final float b = this.z * v.x - this.x * v.z;
        final float c = this.x * v.y - this.y * v.x;
        return new Vec3M(a, b, c);
    }

    /*------------------- UNARY OPERATIONS TO NEW IMMUTABLE ------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec3 negated() {
        return this.times(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec3 normalized() {
        final float oneOnLength = 1.0f / this.length();
        return this.times(oneOnLength);
    }

    /*------------------- UNARY OPERATIONS TO NEW MUTABLE --------------------*/

    /**
     * Returns this vector negated.
     *
     * @return <code>-this</code>
     */
    public final Vec3M negatedM() {
        return this.timesM(-1.0f);
    }

    /**
     * Returns this vector normalized.
     *
     * @return <code>this / this.length()</code>
     */
    public final Vec3M normalizedM() {
        final float oneOnLength = 1.0f / this.length();
        return this.timesM(oneOnLength);
    }

    /*------------------ 3D TRANSFORMATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @return the scaled vector
     */
    public final Vec3 scaled(float sx, float sy, float sz) {
        return new Vec3(sx * this.x, sy * this.y, sz * this.z);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec3 scaled(Vec3Base s) {
        return this.scaled(s.x, s.y, s.z);
    }

    /*------------------ 3D TRANSFORMATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param sx scalar factor along the x-axis
     * @param sy scalar factor along the y-axis
     * @param sz scalar factor along the z-axis
     * @return the scaled vector
     */
    public final Vec3M scaledM(float sx, float sy, float sz) {
        return new Vec3M(sx * this.x, sy * this.y, sz * this.z);
    }

    /**
     * Returns this vector non-uniformly scaled.
     *
     * @param s scalar factors stored in a vector
     * @return the scaled vector
     */
    public final Vec3M scaledM(Vec3Base s) {
        return this.scaledM(s.x, s.y, s.z);
    }
    
    /*-------------------------- CAST AND COPY METHODS -----------------------*/

    /**
     * Returns a new array containing the components of this vector.
     *
     * @return the new array
     */
    public final float[] toArray() {
        return new float[]{this.x, this.y, this.z};
    }

    /**
     * Returns a new immutable copy of this vector.
     *
     * @return the immutable copy of this vector
     */
    public final Vec3 copy() {
        return new Vec3(this.x, this.y, this.z);
    }

    /**
     * Returns a new mutable version of this vector.
     *
     * @return the mutable copy of this vector
     */
    public final Vec3M copyM() {
        return new Vec3M(this.x, this.y, this.z);
    }


    /*-------------------------- OUTPUT TO BYTEBUFFERS -----------------------*/

    /**
     * Writes this vector to the end of a <code>ByteBuffer</code>.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @throws BufferOverflowException if there are fewer than twelve bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final void writeTo(ByteBuffer b) throws BufferOverflowException,
            ReadOnlyBufferException {
        b.putFloat(this.x);
        b.putFloat(this.y);
        b.putFloat(this.z);
    }

    /**
     * Writes this vector in a <code>ByteBuffer</code> at a specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to write the vector
     * @return the position of the first byte after the vector
     * @throws BufferOverflowException if there are fewer than twelve bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final int writeTo(ByteBuffer b, int i)
                      throws BufferOverflowException, ReadOnlyBufferException  {
        b.putFloat(i, this.x);
        b.putFloat(i+4, this.y);
        b.putFloat(i+8, this.z);
        return i + 12;
    }
}
