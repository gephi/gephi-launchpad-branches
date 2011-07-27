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
 * 4x4 Matrix class. All the constructors of this class are protected and its
 * subclasses should be used instead. Indexes are zero-based.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Mat4Base {

    /*----------------------- PROTECTED DATA FIELDS --------------------------*/

    /**
     * Entries of the matrix stored column-wise.
     */
    protected final float[] entries;

    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a diagonal matrix with all the entries on the diagonal equal.
     *
     * @param v the value of the entries on the diagonal
     */
    protected Mat4Base(float v) {
        this.entries = new float[]{
            v, 0.0f, 0.0f, 0.0f,
            0.0f, v, 0.0f, 0.0f,
            0.0f, 0.0f, v, 0.0f,
            0.0f, 0.0f, 0.0f, v,
        };
    }

    /**
     * Creates a diagonal matrix from the values on the diagonal.
     *
     * @param v0 the (0,0)-entry
     * @param v1 the (1,1)-entry
     * @param v2 the (2,2)-entry
     * @param v3 the (3,3)-entry
     */
    protected Mat4Base(float v0, float v1, float v2, float v3) {
        this.entries = new float[]{
            v0, 0.0f, 0.0f, 0.0f,
            0.0f, v1, 0.0f, 0.0f,
            0.0f, 0.0f, v2, 0.0f,
            0.0f, 0.0f, 0.0f, v3,
        };
    }
    
    /**
     * Creates a diagonal matrix from the values on the diagonal.
     *
     * @param v the vector containing the values on the diagonal
     */
    protected Mat4Base(Vec4Base v) {
        this.entries = new float[]{
            v.x(), 0.0f, 0.0f, 0.0f,
            0.0f, v.y(), 0.0f, 0.0f,
            0.0f, 0.0f, v.z(), 0.0f,
            0.0f, 0.0f, 0.0f, v.w(),
        };
    }

    /**
     * Creates a matrix from its columns
     *
     * @param c0 the first column
     * @param c1 the second column
     * @param c2 the third column
     * @param c3 the fourth column
     */
    protected Mat4Base(Vec4Base c0, Vec4Base c1, Vec4Base c2, Vec4Base c3) {
        this.entries = new float[]{
            c0.x(), c0.y(), c0.z(), c0.w(),
            c1.x(), c1.y(), c1.z(), c1.w(),
            c2.x(), c2.y(), c2.z(), c2.w(),
            c3.x(), c3.y(), c3.z(), c3.w(),
        };        
    }

    

    /**
     * Creates a matrix from its entries. The entries are listed column-wise.
     *
     * @param e00 the (0,0)-entry
     * @param e10 the (1,0)-entry
     * @param e20 the (2,0)-entry
     * @param e30 the (3,0)-entry
     * @param e01 the (0,1)-entry
     * @param e11 the (1,1)-entry
     * @param e21 the (2,1)-entry
     * @param e31 the (3,1)-entry
     * @param e02 the (0,2)-entry
     * @param e12 the (1,2)-entry
     * @param e22 the (2,2)-entry
     * @param e32 the (3,2)-entry
     * @param e03 the (0,3)-entry
     * @param e13 the (1,3)-entry
     * @param e23 the (2,3)-entry
     * @param e33 the (3,3)-entry
     */
    protected Mat4Base(float e00, float e10, float e20, float e30,
            float e01, float e11, float e21, float e31,
            float e02, float e12, float e22, float e32,
            float e03, float e13, float e23, float e33) {
        this.entries = new float[]{
            e00, e10, e20, e30,
            e01, e11, e21, e31,
            e02, e12, e22, e32,
            e03, e13, e23, e33
        };
    }

    /**
     * Copies an existing matrix.
     *
     * @param m the matrix to copy
     */
    protected Mat4Base(Mat4Base m) {
        this.entries = new float[16];
        System.arraycopy(m.entries, 0, this.entries, 0, 16);
    }

    /**
     * Creates a new matrix from an array. It directly stores the array. It
     * should only be used internally for performance reasons.
     *
     * @param e the entries' array
     */
    protected Mat4Base(float[] e) {
        this.entries = e;
    }

    /*------------------------------ ACCESSORS -------------------------------*/

    /**
     * Gets the (i,j)-entry of the matrix. Indexes are zero-based. It does not
     * check array bounds.
     *
     * @param i the row index
     * @param j the column index
     * @throws IndexOutOfBoundsException if <code>j*4 + i &gt;= 16</code>
     * @return the (i,j) entry
     */
    public final float get(int i, int j) throws IndexOutOfBoundsException {
        return this.entries[j*4 + i];
    }

    /**
     * Gets an immutable copy of the j<sup>th</sup> column of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param j the index of the column
     * @throws IndexOutOfBoundsException if <code>j*4 + 3 &gt;= 16</code>
     * @return the j<sup>th</sup> column of the matrix
     */
    public final Vec4 getColumn(int j) throws IndexOutOfBoundsException {
        final int s = j*4;
        return new Vec4(this.entries[s], this.entries[s+1], this.entries[s+2], this.entries[s+3]);
    }

    /**
     * Gets an mutable copy of the j<sup>th</sup> column of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param j the index of the column
     * @throws IndexOutOfBoundsException if <code>j*4 + 3 &gt;= 16</code>
     * @return the j<sup>th</sup> column of the matrix
     */
    public final Vec4M getColumnM(int j) throws IndexOutOfBoundsException {
        final int s = j*4;
        return new Vec4M(this.entries[s], this.entries[s+1], this.entries[s+2], this.entries[s+3]);
    }

    /**
     * Gets an immutable copy of the i<sup>th</sup> row of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param i the index of the row
     * @throws IndexOutOfBoundsException if <code>i &gt;= 4</code>
     * @return the i<sup>th</sup> row of the matrix
     */
    public final Vec4 getRow(int i) {
        return new Vec4(this.entries[i], this.entries[4 + i], this.entries[8 + i], this.entries[12 + i]);
    }

    /**
     * Gets an mutable copy of the i<sup>th</sup> row of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param i the index of the row
     * @throws IndexOutOfBoundsException if <code>i &gt;= 4</code>
     * @return the i<sup>th</sup> row of the matrix
     */
    public final Vec4M getRowM(int i) {
        return new Vec4M(this.entries[i], this.entries[4 + i], this.entries[8 + i], this.entries[12 + i]);
    }

    /*---------------------- OVERRIDDEN OBJECT'S METHODS ---------------------*/

    /**
     * Compares two 4x4 matrices for equality. Two matrices are equal if their
     * entries are equal.
     *
     * @param obj the object to compare with
     * @return <code>true</code> if <code>obj</code> is a 4x4 matrix and if
     *         their entries are equal, <code>false</code> otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Mat4Base)) return false;

        final Mat4Base m = (Mat4Base) obj;

        boolean result = true;
        for (int i = 0; i < 16; ++i) {
            result &= this.entries[i] == m.entries[i];
        }
        return result;
    }

    /**
     * Returns an integer hash for this matrix based on the raw bit
     * representation of its entries.
     *
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        int result = Float.floatToRawIntBits(this.entries[0]);;
        for (int i = 1; i < 16; ++i) {
            result ^= Float.floatToRawIntBits(this.entries[i]);
        }
        return result;
    }

    /**
     * Returns a string representing this matrix.
     *
     * @return the string
     */
    @Override
    public final String toString() {
        return "(" + this.entries[0] + ", " + this.entries[4] + ", " + this.entries[8] + ", " + this.entries[12] + "; \n" +
                this.entries[1] + ", " + this.entries[5] + ", " + this.entries[9] + ", " + this.entries[13] + "; \n" +
                this.entries[2] + ", " + this.entries[6] + ", " + this.entries[10] + ", " + this.entries[14] + "; \n" +
                this.entries[3] + ", " + this.entries[7] + ", " + this.entries[11] + ", " + this.entries[15] + ")";
    }

    /*------------------------- MATRIX TO SCALAR MAPS ------------------------*/

    /**
     * Computes the determinant of the matrix in the general case.
     * 
     * @return <code>det(this)</code>
     */
    public final float det() {
        final float d30 = this.entries[4]*this.entries[9]*this.entries[14] + this.entries[6]*this.entries[8]*this.entries[13] +
                this.entries[5]*this.entries[10]*this.entries[12] - this.entries[4]*this.entries[10]*this.entries[13] -
                this.entries[5]*this.entries[8]*this.entries[14] - this.entries[6]*this.entries[9]*this.entries[12];
        final float d31 = this.entries[0]*this.entries[9]*this.entries[14] + this.entries[2]*this.entries[8]*this.entries[13] +
                this.entries[1]*this.entries[10]*this.entries[12] - this.entries[0]*this.entries[10]*this.entries[13] -
                this.entries[1]*this.entries[8]*this.entries[14] - this.entries[2]*this.entries[9]*this.entries[12];
        final float d32 = this.entries[0]*this.entries[5]*this.entries[14] + this.entries[2]*this.entries[4]*this.entries[13] +
                this.entries[1]*this.entries[6]*this.entries[12] - this.entries[0]*this.entries[6]*this.entries[13] -
                this.entries[1]*this.entries[4]*this.entries[14] - this.entries[2]*this.entries[5]*this.entries[12];
        final float d33 = this.entries[0]*this.entries[5]*this.entries[10] + this.entries[2]*this.entries[4]*this.entries[9] +
                this.entries[1]*this.entries[6]*this.entries[8] - this.entries[0]*this.entries[6]*this.entries[9] -
                this.entries[1]*this.entries[4]*this.entries[10] - this.entries[2]*this.entries[5]*this.entries[8];        
        return - this.entries[3]*d30 + this.entries[7]*d31 - this.entries[11]*d32 + this.entries[15]*d33;
    }
    
    /**
     * Computes the determinant of the matrix assuming it is an affine 
     * transformation.
     * 
     * @return <code>det(this)</code>
     */
    public final float detAffine() {
        return this.entries[0]*this.entries[5]*this.entries[10] + this.entries[2]*this.entries[4]*this.entries[9] +
                this.entries[1]*this.entries[6]*this.entries[8] - this.entries[0]*this.entries[6]*this.entries[9] -
                this.entries[1]*this.entries[4]*this.entries[10] - this.entries[2]*this.entries[5]*this.entries[8];
    }

    /*------------------- BINARY OPERATIONS TO NEW IMMUTABLE -----------------*/

    /**
     * Returns the sum of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this + m</code>
     */
    public final Mat4 plus(Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] + m.entries[i];
        }
        return new Mat4(e);
    }

    /**
     * Returns the sum of this matrix and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this + sk*m</code>
     */
    public final Mat4 plus(float s, Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] + s * m.entries[i];
        }
        return new Mat4(e);
    }

    /**
     * Returns the difference of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this - m</code>
     */
    public final Mat4 minus(Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] - m.entries[i];
        }
        return new Mat4(e);
    }

    /**
     * Returns the difference of this matrix and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this - sk*m</code>
     */
    public final Mat4 minus(float s, Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] - s * m.entries[i];
        }
        return new Mat4(e);
    }

    /**
     * Returns the product between this matrix and a scalar.
     *
     * @param s the scalar
     * @return <code>this * sk</code>
     */
    public final Mat4 times(float s) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = s * this.entries[i];
        }
        return new Mat4(e);
    }

    /**
     * Returns the product of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this * m</code>
     */
    public final Mat4 times(Mat4Base m) {
        final float[] e = new float[16];
        for (int j = 0; j < 16; j += 4) {
            for (int k = 0; k < 4; ++k) {
                final int col = 4 * k;
                final float sk = m.entries[j + k];
                
                e[j + 0] += this.entries[col + 0] * sk;
                e[j + 1] += this.entries[col + 1] * sk;
                e[j + 2] += this.entries[col + 2] * sk;
                e[j + 3] += this.entries[col + 3] * sk;                
            }            
        }
        return new Mat4(e);
    }

    /*------------------- BINARY OPERATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns the sum of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this + m</code>
     */
    public final Mat4M plusM(Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] + m.entries[i];
        }
        return new Mat4M(e);
    }

    /**
     * Returns the sum of this matrix and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this + sk*m</code>
     */
    public final Mat4M plusM(float s, Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] + s * m.entries[i];
        }
        return new Mat4M(e);
    }

    /**
     * Returns the difference of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this - m</code>
     */
    public final Mat4M minusM(Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] - m.entries[i];
        }
        return new Mat4M(e);
    }

    /**
     * Returns the difference of this matrix and another one scaled by a scalar.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this - sk*m</code>
     */
    public final Mat4M minusM(float s, Mat4Base m) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = this.entries[i] - s * m.entries[i];
        }
        return new Mat4M(e);
    }

    /**
     * Returns the product between this matrix and a scalar.
     *
     * @param s the scalar
     * @return <code>this * sk</code>
     */
    public final Mat4M timesM(float s) {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = s * this.entries[i];
        }
        return new Mat4M(e);
    }

    /**
     * Returns the product of this matrix and another one.
     *
     * @param m the other matrix
     * @return <code>this * m</code>
     */
    public final Mat4M timesM(Mat4Base m) {
        final float[] e = new float[16];
        for (int j = 0; j < 16; j += 4) {
            for (int k = 0; k < 4; ++k) {
                final int col = 4 * k;
                final float sk = m.entries[j + k];
                
                e[j + 0] += this.entries[col + 0] * sk;
                e[j + 1] += this.entries[col + 1] * sk;
                e[j + 2] += this.entries[col + 2] * sk;
                e[j + 3] += this.entries[col + 3] * sk;                
            }            
        }
        return new Mat4M(e);
    }

    /*------------------- UNARY OPERATIONS TO NEW IMMUTABLE ------------------*/

    /**
     * Returns this matrix negated.
     *
     * @return <code>-this</code>
     */
    public final Mat4 negated() {
        return this.times(-1.0f);
    }

    /**
     * Returns the transpose of this matrix.
     *
     * @return <code>this^t</code>
     */
    public final Mat4 transposed() {
        final float[] e = new float[16];
        for (int j = 0; j < 4; ++j) {
            for (int i = 0; i < 4; ++i) {
                e[4*j + i] = this.entries[4*i + j];
            }
        }
        return new Mat4(e);
    }

    /**
     * Returns the inverse of this matrix.
     *
     * @return <code>this^(-1)</code>
     */
    public final Mat4 inverse() {
        throw new UnsupportedOperationException("General matrix inverse not implemented yet.");
    }
    
    /**
     * Returns the inverse of this matrix assuming it represents an affine
     * transformation.
     *
     * @return <code>this^(-1)</code>
     */
    public final Mat4 inverseAffine() {
        final float a00 = this.entries[5]*this.entries[10] - this.entries[6]*this.entries[9];
        final float a10 = this.entries[4]*this.entries[10] - this.entries[6]*this.entries[8];
        final float a20 = this.entries[4]*this.entries[9] - this.entries[5]*this.entries[8];
        final float a01 = this.entries[1]*this.entries[10] - this.entries[2]*this.entries[9];
        final float a11 = this.entries[0]*this.entries[10] - this.entries[2]*this.entries[8];
        final float a21 = this.entries[0]*this.entries[9] - this.entries[1]*this.entries[8];
        final float a02 = this.entries[1]*this.entries[6] - this.entries[2]*this.entries[5];
        final float a12 = this.entries[0]*this.entries[6] - this.entries[2]*this.entries[4];
        final float a22 = this.entries[0]*this.entries[5] - this.entries[1]*this.entries[4];
        
        final float d = 1.0f / (this.entries[0] * a00 - this.entries[1] * a10 + this.entries[2] * a20);
        
        final float vx = - (a00 * this.entries[12] + a10 * this.entries[13] + a20 * this.entries[14]);
        final float vy = - (a01 * this.entries[12] + a11 * this.entries[13] + a21 * this.entries[14]);
        final float vz = - (a02 * this.entries[12] + a12 * this.entries[13] + a22 * this.entries[14]);
        
        final float[] e = new float[]{
            a00 * d, a01 * d, a02 * d, 0.0f,
            a10 * d, a11 * d, a12 * d, 0.0f,
            a20 * d, a21 * d, a22 * d, 0.0f,
            vx * d, vy * d, vz * d, 1.0f
        };
        return new Mat4(e);
    }
    
    /**
     * Computes the inverse of this matrix assuming it represents an orthogonal
     * transformation (an isometry without translation component).
     * 
     * @return <code>this^(-1)</code>
     */
    public final Mat4 inverseOrthogonal() {
        return this.transposed();
    }
    
    /**
     * Computes the inverse of this matrix assuming it represents an isometry.
     * 
     * @return <code>this^(-1)</code>
     */
    public final Mat4 inverseIsometry() {
        final float vx = - (this.entries[0] * this.entries[12] + this.entries[1] * this.entries[13] + this.entries[2] * this.entries[14]);
        final float vy = - (this.entries[4] * this.entries[12] + this.entries[5] * this.entries[13] + this.entries[6] * this.entries[14]);
        final float vz = - (this.entries[8] * this.entries[12] + this.entries[9] * this.entries[13] + this.entries[10] * this.entries[14]);
        
        final float[] e = new float[]{
            this.entries[0], this.entries[4], this.entries[8], 0.0f,
            this.entries[1], this.entries[5], this.entries[9], 0.0f,
            this.entries[2], this.entries[6], this.entries[10], 0.0f,
            vx, vy, vz, 1.0f            
        };
        return new Mat4(e);
    }

    /*-------------------- UNARY OPERATIONS TO NEW MUTABLE -------------------*/

    /**
     * Returns this matrix negated.
     *
     * @return <code>-this</code>
     */
    public final Mat4M negatedM() {
        return this.timesM(-1.0f);
    }

    /**
     * Returns the transpose of this matrix.
     *
     * @return <code>this^t</code>
     */
    public final Mat4M transposedM() {
        final float[] e = new float[16];
        for (int j = 0; j < 4; ++j) {
            for (int i = 0; i < 4; ++i) {
                e[4*j + i] = this.entries[4*i + j];
            }
        }
        return new Mat4M(e);
    }

    /**
     * Returns the inverse of this matrix.
     *
     * @return <code>this^(-1)</code>
     */
    public final Mat4M inverseM() {
        throw new UnsupportedOperationException("General matrix inverse not implemented yet.");
    }
    
    /**
     * Returns the inverse of this matrix assuming it represents an affine
     * transformation.
     *
     * @return <code>this^(-1)</code>
     */
    public final Mat4M inverseAffineM() {
        final float a00 = this.entries[5]*this.entries[10] - this.entries[6]*this.entries[9];
        final float a10 = this.entries[4]*this.entries[10] - this.entries[6]*this.entries[8];
        final float a20 = this.entries[4]*this.entries[9] - this.entries[5]*this.entries[8];
        final float a01 = this.entries[1]*this.entries[10] - this.entries[2]*this.entries[9];
        final float a11 = this.entries[0]*this.entries[10] - this.entries[2]*this.entries[8];
        final float a21 = this.entries[0]*this.entries[9] - this.entries[1]*this.entries[8];
        final float a02 = this.entries[1]*this.entries[6] - this.entries[2]*this.entries[5];
        final float a12 = this.entries[0]*this.entries[6] - this.entries[2]*this.entries[4];
        final float a22 = this.entries[0]*this.entries[5] - this.entries[1]*this.entries[4];
        
        final float d = 1.0f / (this.entries[0] * a00 - this.entries[1] * a10 + this.entries[2] * a20);
        
        final float vx = - (a00 * this.entries[12] + a10 * this.entries[13] + a20 * this.entries[14]);
        final float vy = - (a01 * this.entries[12] + a11 * this.entries[13] + a21 * this.entries[14]);
        final float vz = - (a02 * this.entries[12] + a12 * this.entries[13] + a22 * this.entries[14]);
        
        final float[] e = new float[]{
            a00 * d, a01 * d, a02 * d, vx * d,
            a10 * d, a11 * d, a12 * d, vy * d,
            a20 * d, a21 * d, a22 * d, vz * d,
            0.0f, 0.0f, 0.0f, 1.0f
        };
        return new Mat4M(e);
    }
    
    /**
     * Computes the inverse of this matrix assuming it represents an orthogonal
     * transformation (an isometry without translation component).
     * 
     * @return <code>this^(-1)</code>
     */
    public final Mat4M inverseOrthogonalM() {
        return this.transposedM();
    }
    
    /**
     * Computes the inverse of this matrix assuming it represents an isometry.
     * 
     * @return <code>this^(-1)</code>
     */
    public final Mat4M inverseIsometryM() {
        final float vx = - (this.entries[0] * this.entries[12] + this.entries[1] * this.entries[13] + this.entries[2] * this.entries[14]);
        final float vy = - (this.entries[4] * this.entries[12] + this.entries[5] * this.entries[13] + this.entries[6] * this.entries[14]);
        final float vz = - (this.entries[8] * this.entries[12] + this.entries[9] * this.entries[13] + this.entries[10] * this.entries[14]);
        
        final float[] e = new float[]{
            this.entries[0], this.entries[4], this.entries[8], 0.0f,
            this.entries[1], this.entries[5], this.entries[9], 0.0f,
            this.entries[2], this.entries[6], this.entries[10], 0.0f,
            vx, vy, vz, 1.0f            
        };
        return new Mat4M(e);
    }
    
    /*------------------------ MATRIX-VECTOR OPERATIONS ----------------------*/

    /**
     * Multiplies this matrix with a 4D vector.
     * 
     * @param v the vector
     * @return <code>this * v</code>
     */
    public final Vec4 times(Vec4Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;
        
        x += this.entries[12] * v.w;
        y += this.entries[13] * v.w;
        z += this.entries[14] * v.w;
        w += this.entries[15] * v.w;
        
        return new Vec4(x, y, z, w);
    }
    
    /**
     * Multiplies this matrix with a 4D vector.
     * 
     * @param v the vector
     * @return <code>this * v</code>
     */
    public final Vec4M timesM(Vec4Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;
        
        x += this.entries[12] * v.w;
        y += this.entries[13] * v.w;
        z += this.entries[14] * v.w;
        w += this.entries[15] * v.w;
        
        return new Vec4M(x, y, z, w);
    }
    
    /**
     * Transforms a vector using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the vector to transform
     * @return <code>this * (v, 0)</code>
     */
    public final Vec4 transformVec(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;

        return new Vec4(x, y, z, w);        
    }
    
    /**
     * Transforms a vector using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the vector to transform
     * @return <code>this * (v, 0)</code>
     */
    public final Vec4M transformVecM(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;

        return new Vec4M(x, y, z, w);        
    }
    
    /**
     * Transforms a point using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the point to transform
     * @return <code>this * (v, 1)</code>
     */
    public final Vec4 transformPoint(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;
        
        x += this.entries[12];
        y += this.entries[13];
        z += this.entries[14];
        w += this.entries[15];
        
        return new Vec4(x, y, z, w);
    }
    
    /**
     * Transforms a point using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the point to transform
     * @return <code>this * (v, 1)</code>
     */
    public final Vec4M transformPointM(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        float w = this.entries[3] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        w += this.entries[7] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        w += this.entries[11] * v.z;
        
        x += this.entries[12];
        y += this.entries[13];
        z += this.entries[14];
        w += this.entries[15];
        
        return new Vec4M(x, y, z, w);
    }
    
    /**
     * Transforms a normal using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the normal to transform
     * @return <code>this^(-t) * (v, 0)</code>
     */
    public final Vec4 transformNormal(Vec3Base v) {
        final Mat4M m = this.inverseM().transpose();
        return m.transformVec(v);
    }
    
    /**
     * Transforms a normal using this matrix. It does NOT assume the matrix
     * to represent an affine transformation.
     * 
     * @param v the normal to transform
     * @return <code>this^(-t) * (v, 0)</code>
     */
    public final Vec4M transformNormalM(Vec3Base v) {
        final Mat4M m = this.inverseM().transpose();
        return m.transformVecM(v);
    }
    
    /**
     * Transforms a vector using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the vector to transform
     * @return <code>this * (v, 0)</code>
     */
    public final Vec3 transformAffineVec(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;

        return new Vec3(x, y, z);        
    }
    
    /**
     * Transforms a vector using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the vector to transform
     * @return <code>this * (v, 0)</code>
     */
    public final Vec3M transformAffineVecM(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;

        return new Vec3M(x, y, z);        
    }
    
    /**
     * Transforms a point using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the point to transform
     * @return <code>this * (v, 1)</code>
     */
    public final Vec3 transformAffinePoint(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        
        x += this.entries[12];
        y += this.entries[13];
        z += this.entries[14];
        
        return new Vec3(x, y, z);
    }
    
    /**
     * Transforms a point using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the point to transform
     * @return <code>this * (v, 1)</code>
     */
    public final Vec3M transformAffinePointM(Vec3Base v) {
        float x = this.entries[0] * v.x;
        float y = this.entries[1] * v.x;
        float z = this.entries[2] * v.x;
        
        x += this.entries[4] * v.y;
        y += this.entries[5] * v.y;
        z += this.entries[6] * v.y;
        
        x += this.entries[8] * v.z;
        y += this.entries[9] * v.z;
        z += this.entries[10] * v.z;
        
        x += this.entries[12];
        y += this.entries[13];
        z += this.entries[14];
        
        return new Vec3M(x, y, z);
    }
    
    /**
     * Transforms a normal using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the normal to transform
     * @return <code>this^(-t) * (v, 0)</code>
     */
    public final Vec3 transformAffineNormal(Vec3Base v) {
        final Mat4M m = this.inverseAffineM().transpose();
        return m.transformAffineVec(v);
    }
    
    /**
     * Transforms a normal using this matrix assuming this matrix
     * represents an affine transformation.
     * 
     * @param v the normal to transform
     * @return <code>this^(-t) * (v, 0)</code>
     */
    public final Vec3M transformAffineNormalM(Vec3Base v) {
        final Mat4M m = this.inverseAffineM().transpose();
        return m.transformAffineVecM(v);
    }
    
    /*-------------------------- CAST AND COPY METHODS -----------------------*/

    /**
     * Returns a new array containing the components of this matrix.
     *
     * @return the new array
     */
    public final float[] toArray() {
        float[] result = new float[16];
        System.arraycopy(this.entries, 0, result, 0, 16);
        return result;
    }

    /**
     * Returns a new immutable copy of this matrix.
     *
     * @return the immutable copy of this vector
     */
    public final Mat4 copy() {
        return new Mat4(this);
    }

    /**
     * Returns a new mutable version of this matrix.
     *
     * @return the mutable copy of this vector
     */
    public final Mat4M copyM() {
        return new Mat4M(this);
    }


    /*-------------------------- OUTPUT TO BYTEBUFFERS -----------------------*/

    /**
     * Writes this matrix to the end of a <code>ByteBuffer</code>.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @throws BufferOverflowException if there are fewer than 64 bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final void writeTo(ByteBuffer b) throws BufferOverflowException,
            ReadOnlyBufferException {
        for (int i = 0; i < 16; ++i) {
            b.putFloat(this.entries[i]);
        }
    }

    /**
     * Writes this matrix in a <code>ByteBuffer</code> at a specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to write the matrix
     * @return the position of the first byte after the matrix
     * @throws BufferOverflowException if there are fewer than 64 bytes
     *                                 remaining in the buffer
     * @throws ReadOnlyBufferException if the buffer is read-only
     */
    public final int writeTo(ByteBuffer b, int i)
                      throws BufferOverflowException, ReadOnlyBufferException  {
        for (int j = 0; j < 16; ++j) {
            b.putFloat(i, this.entries[j]);
            i += 4;
        }
        return i;
    }
}
