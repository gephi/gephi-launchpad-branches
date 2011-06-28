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
import java.util.Collection;

/**
 * Represents an array of 2D vectors.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Vec2Array {

    /*------------------------ PRIVATE DATA FIELDS ---------------------------*/

    /**
     * Internal array used to store the components of the vectors.
     */
    private final float[] data;

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a new array of 2D vectors.
     *
     * @param n the number of vectors
     */
    public Vec2Array(int n) {
        this.data = new float[2 * n];
    }
    
    /**
     * Creates a new array of 2D vectors and initialize them.
     *
     * @param n the number of vectors
     * @param x the first component of the vectors
     * @param y the second component of the vectors
     */
    public Vec2Array(int n, float x, float y) {
        this(n);

        setAll(x, y);
    }

    /**
     * Creates a new array of 2D vectors and initialize them.
     *
     * @param n the number of vectors
     * @param v the 2D vector to copy
     */
    public Vec2Array(int n, Vec2Base v) {
        this(n, v.x, v.y);
    }

    /**
     * Creates a new array of 2D vectors from an array.
     *
     * @param arr the array containing the components of the vectors
     */
    public Vec2Array(float[] arr) {
        int n = arr.length / 2;

        this.data = new float[2*n];

        set(0, n, arr, 0);
    }

    /**
     * Creates a new array of 2D vectors from an array.
     *
     * @param n the number of vectors
     * @param arr the array containing the components of the vectors
     * @throws IndexOutOfBoundsException if the array contains less than
     *                                   <code>2n</code> elements
     */
    public Vec2Array(int n, float[] arr) {
        this(n);

        set(0, n, arr, 0);
    }

    /**
     * Creates a new array of 2D vectors from an array.
     *
     * @param n the number of vectors
     * @param arr the array containing the components of the vectors
     * @param i index of the first component of the first 2D vector to copy
     * @throws IndexOutOfBoundsException if the array contains less than
     *                                   <code>i+2n</code> elements
     */
    public Vec2Array(int n, float[] arr, int i) throws IndexOutOfBoundsException {
        this(n);

        set(0, n, arr, i);
    }

    /**
     * Creates a new array of 2D vectors from a collection of vectors.
     *
     * @param vs the collection of 2D vectors
     */
    public Vec2Array(Collection<? extends Vec2Base> vs) {
        final int n = vs.size();

        this.data = new float[2*n];

        int i = 0;
        for (Vec2Base v : vs) {
            this.data[i++] = v.x();
            this.data[i++] = v.y();
        }
    }

    /*------------------------------ ACCESSORS -------------------------------*/

    /**
     * Returns the first component of a vector in the standard basis.
     *
     * @param i the index of the vector
     * @return the first component of the vector
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public float x(int i) throws IndexOutOfBoundsException {
        return this.data[2*i];
    }

    /**
     * Returns the second component of a vector in the standard basis.
     *
     * @param i the index of the vector
     * @return the second component of the vector
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public float y(int i) throws IndexOutOfBoundsException {
        return this.data[2*i + 1];
    }

    /**
     * Gets an immutable copy of a vector in the array.
     *
     * @param i the index of the vector
     * @return the immutable copy
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2 get(int i) throws IndexOutOfBoundsException {
        return new Vec2(this.data, i);
    }

    /**
     * Gets an mutable copy of a vector in the array.
     *
     * @param i the index of the vector
     * @return the mutable copy
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2M getM(int i) throws IndexOutOfBoundsException {
        return new Vec2M(this.data, i);
    }

    /*-------------------------------- SETTERS -------------------------------*/

    /**
     * Sets the first component of a vector in the array.
     *
     * @param i the index of the vector
     * @param x the new first component of the vector
     * @return this
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2Array x(int i, float x) throws IndexOutOfBoundsException {
        this.data[2*i] = x;
        return this;
    }

    /**
     * Sets the second component of a vector in the array.
     *
     * @param i the index of the vector
     * @param y the new second component of the vector
     * @return this
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2Array y(int i, float y) throws IndexOutOfBoundsException {
        this.data[2*i + 1] = y;
        return this;
    }

    /**
     * Sets the first component of all vectors in the array.
     *
     * @param x the new first component of the vectors
     * @return this
     */
    public Vec2Array allX(float x) {
        for (int i = 0; i < this.data.length; i+=2) {
            this.data[i] = x;
        }
        return this;
    }

    /**
     * Sets the second component of all vectors in the array.
     *
     * @param y the new second component of the vector
     * @return this
     */
    public Vec2Array allY(float y) {
        for (int i = 1; i < this.data.length; i+=2) {
            this.data[i] = y;
        }
        return this;
    }

    /**
     * Sets the components of a vector in the array.
     *
     * @param i the index of the vector
     * @param x the first component of the vector
     * @param y the second component of the vector
     * @return this
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2Array set(int i, float x, float y) throws IndexOutOfBoundsException {
        i *= 2;
        this.data[i] = x;
        this.data[i+1] = y;
        return this;
    }

    /**
     * Sets the components of a vector in the array.
     *
     * @param i the index of the vector
     * @param v the vector to copy
     * @return this
     * @throws IndexOutOfBoundsException if the index i is negative, zero, or
     *                                   greater than the number of vectors in
     *                                   the array
     */
    public Vec2Array set(int i, Vec2Base v) throws IndexOutOfBoundsException  {
        return set(i, v.x, v.y);
    }

    /**
     * Sets the components of all vectors in the array.
     *
     * @param x the first component of the vectors
     * @param y the second component of the vectors
     * @return this
     */
    public Vec2Array setAll(float x, float y) {
        for (int i = 0; i < this.data.length; i+=2) {
            this.data[i] = x;
            this.data[i+1] = y;
        }
        return this;
    }

    /**
     * Sets the components of all vectors in the array.
     *
     * @param v the vector to copy
     * @return this
     */
    public Vec2Array setAll(Vec2Base v) {
        return setAll(v.x, v.y);
    }

    /**
     * Sets the components of <code>n</code> vectors beginning at index
     * <code>i</code> reading them from an array of floats.
     *
     * @param i the index of the first modified vector
     * @param n the number of vector to modify
     * @param arr the array to read from
     * @param j the index of the first component to copy in <code>arr</code>
     * @return this
     * @throws IndexOutOfBoundsException if the array contains less than
     *                                   <code>i+n</code> vectors or
     *                                   <code>arr</code> contains less than
     *                                   <code>j+2n</code> elements.
     */
    private Vec2Array set(int i, int n, float[] arr, int j) {
        i *= 2;
        n *= 2;
        for (int k = 0; k < n; ++k) {
            this.data[i + k] = arr[j + k];
        }
        return this;
    }

    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Reads an array of 2D vectors from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param n the number of vectors to read
     * @param b the <code>ByteBuffer</code> instance
     * @return the new array of 2D vectors or <code>null</code>
     * @throws BufferUnderflowException if there are less than 8*n bytes in the
     *                                  buffer
     */
    public static Vec2Array readFrom(int n, ByteBuffer b)
            throws BufferUnderflowException {
        Vec2Array result = new Vec2Array(n);

        for (int i = 0; i < n; ++i) {
            result.set(i, b.getFloat(), b.getFloat());
        }

        return result;
    }

    /**
     * Reads an array of 2D vector from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param n the number of vectors to read
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the first vector. It is
     *          updated to point to the next byte after the last vector
     * @return the new array of 2D vectors or <code>null</code>
     * @throws BufferUnderflowException if there are less than 8n bytes in the
     *                                  buffer
     */
    public static Vec2Array readFrom(int n, ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        Vec2Array result = new Vec2Array(n);

        for (int j = 0; j < n; ++j) {
            result.set(j, b.getFloat(i[0]), b.getFloat(i[0]+4));
            i[0] += 8;
        }

        return result;
    }

    // TODO: Implement other static factories and all the operations

}
