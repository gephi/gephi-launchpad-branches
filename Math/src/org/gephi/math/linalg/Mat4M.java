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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Mat4M extends Mat4Base {
    
    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Default constructor.
     */
    public Mat4M() {
        super(0.0f);
    }
    
    /**
     * Creates a diagonal matrix with all the entries on the diagonal equal.
     *
     * @param v the value of the entries on the diagonal
     */
    public Mat4M(float v) {
        super(v);
    }

    /**
     * Creates a diagonal matrix from the values on the diagonal.
     *
     * @param v0 the (0,0)-entry
     * @param v1 the (1,1)-entry
     * @param v2 the (2,2)-entry
     * @param v3 the (3,3)-entry
     */
    public Mat4M(float v0, float v1, float v2, float v3) {
        super(v0, v1, v2, v3);
    }
    
    /**
     * Creates a diagonal matrix from the values on the diagonal.
     *
     * @param v the vector containing the values on the diagonal
     */
    public Mat4M(Vec4Base v) {
        super(v);
    }

    /**
     * Creates a matrix from its columns
     *
     * @param c0 the first column
     * @param c1 the second column
     * @param c2 the third column
     * @param c3 the fourth column
     */
    public Mat4M(Vec4Base c0, Vec4Base c1, Vec4Base c2, Vec4Base c3) {
        super(c0, c1, c2, c3);
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
    public Mat4M(float e00, float e10, float e20, float e30,
            float e01, float e11, float e21, float e31,
            float e02, float e12, float e22, float e32,
            float e03, float e13, float e23, float e33) {
        super(
            e00, e10, e20, e30,
            e01, e11, e21, e31,
            e02, e12, e22, e32,
            e03, e13, e23, e33
        );
    }

    /**
     * Copies an existing matrix.
     *
     * @param m the matrix to copy
     */
    public Mat4M(Mat4Base m) {
        super(m);
    }

    /**
     * Creates a new matrix from an array. It directly stores the array. It
     * should only be used internally for performance reasons.
     *
     * @param e the entries' array
     */
    protected Mat4M(float[] e) {
        super(e);
    }

    /*-------------------------------- SETTERS -------------------------------*/
    
    /**
     * Sets the (i,j)-entry of the matrix. Indexes are zero-based. It does not
     * check array bounds.
     *
     * @param i the row index
     * @param j the column index
     * @param v the value
     * @throws IndexOutOfBoundsException if <code>j*4 + i &gt;= 16</code>
     * @return <code>this</code>
     */
    public Mat4M set(int i, int j, float v) throws IndexOutOfBoundsException {
        this.entries[j*4 + i] = v;
        return this;
    }
    
    /**
     * Sets an the j<sup>th</sup> column of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param j the index of the column
     * @param v the new column
     * @throws IndexOutOfBoundsException if <code>j*4 + 3 &gt;= 16</code>
     * @return <code>this</code>
     */
    public Mat4M setColumn(int j, Vec4Base v) throws IndexOutOfBoundsException {
        final int s = j*4;
        this.entries[s] = v.x;
        this.entries[s+1] = v.y;
        this.entries[s+2] = v.z;
        this.entries[s+3] = v.w;
        return this;
    }
    
    /**
     * Sets an the j<sup>th</sup> column of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param j the index of the column
     * @param v0 the new (0,j)-entry
     * @param v1 the new (1,j)-entry
     * @param v2 the new (2,j)-entry
     * @param v3 the new (3,j)-entry
     * @throws IndexOutOfBoundsException if <code>j*4 + 3 &gt;= 16</code>
     * @return <code>this</code>
     */
    public Mat4M setColumn(int j, float v0, float v1, float v2, float v3) throws IndexOutOfBoundsException {
        final int s = j*4;
        this.entries[s] = v0;
        this.entries[s+1] = v1;
        this.entries[s+2] = v2;
        this.entries[s+3] = v3;
        return this;
    }
    
    /**
     * Sets an the i<sup>th</sup> row of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param i the index of the row
     * @param v the new row
     * @throws IndexOutOfBoundsException if <code>i &gt;= 4</code>
     * @return <code>this</code>
     */
    public Mat4M setRow(int i, Vec4Base v) throws IndexOutOfBoundsException {
        this.entries[i] = v.x;
        this.entries[4+i] = v.y;
        this.entries[8+i] = v.z;
        this.entries[12+i] = v.w;
        return this;
    }
    
    /**
     * Sets an the i<sup>th</sup> row of the matrix. 
     * Indexes are zero-based. It does not check array bounds.
     *
     * @param i the index of the row
     * @param v0 the new (i,0)-entry
     * @param v1 the new (i,1)-entry
     * @param v2 the new (i,2)-entry
     * @param v3 the new (i,3)-entry
     * @throws IndexOutOfBoundsException if <code>i &gt;= 4</code>
     * @return <code>this</code>
     */
    public Mat4M setRow(int i, float v0, float v1, float v2, float v3) throws IndexOutOfBoundsException {
        this.entries[i] = v0;
        this.entries[4+i] = v1;
        this.entries[8+i] = v2;
        this.entries[12+i] = v3;
        return this;
    }
    
    /**
     * Sets a diagonal matrix with all the entries on the diagonal equal.
     *
     * @param v the value of the entries on the diagonal
     * @return <code>this</code>
     */
    public Mat4M set(float v) {
        this.entries[0] = v;
        this.entries[1] = 0.0f;
        this.entries[2] = 0.0f;
        this.entries[3] = 0.0f;
        this.entries[4] = 0.0f;
        this.entries[5] = v;
        this.entries[6] = 0.0f;
        this.entries[7] = 0.0f;
        this.entries[8] = 0.0f;
        this.entries[9] = 0.0f;
        this.entries[10] = v;
        this.entries[11] = 0.0f;
        this.entries[12] = 0.0f;
        this.entries[13] = 0.0f;
        this.entries[14] = 0.0f;
        this.entries[15] = v;
        return this;
    }

    /**
     * Sets a diagonal matrix from the values on the diagonal.
     *
     * @param v0 the (0,0)-entry
     * @param v1 the (1,1)-entry
     * @param v2 the (2,2)-entry
     * @param v3 the (3,3)-entry
     * @return <code>this</code>
     */
    public Mat4M set(float v0, float v1, float v2, float v3) {
        this.entries[0] = v0;
        this.entries[1] = 0.0f;
        this.entries[2] = 0.0f;
        this.entries[3] = 0.0f;
        this.entries[4] = 0.0f;
        this.entries[5] = v1;
        this.entries[6] = 0.0f;
        this.entries[7] = 0.0f;
        this.entries[8] = 0.0f;
        this.entries[9] = 0.0f;
        this.entries[10] = v2;
        this.entries[11] = 0.0f;
        this.entries[12] = 0.0f;
        this.entries[13] = 0.0f;
        this.entries[14] = 0.0f;
        this.entries[15] = v3;
        return this;
    }
    
    /**
     * Sets a diagonal matrix from the values on the diagonal.
     *
     * @param v the vector containing the values on the diagonal
     * @return <code>this</code>
     */
    public Mat4M set(Vec4Base v) {
        return this.set(v.x, v.y, v.z, v.w);        
    }

    /**
     * Sets the columns of the matrix
     *
     * @param c0 the first column
     * @param c1 the second column
     * @param c2 the third column
     * @param c3 the fourth column
     * @return <code>this</code>
     */
    public Mat4M set(Vec4Base c0, Vec4Base c1, Vec4Base c2, Vec4Base c3) {
        return this.setColumn(0, c0).setColumn(1, c1).setColumn(2, c2).setColumn(3, c3);
    }

    /**
     * Sets the entries of the matrix. The entries are listed column-wise.
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
     * @return <code>this</code>
     */
    public Mat4M set(float e00, float e10, float e20, float e30,
            float e01, float e11, float e21, float e31,
            float e02, float e12, float e22, float e32,
            float e03, float e13, float e23, float e33) {
        this.entries[0] = e00;
        this.entries[1] = e10;
        this.entries[2] = e20;
        this.entries[3] = e30;
        this.entries[4] = e01;
        this.entries[5] = e11;
        this.entries[6] = e21;
        this.entries[7] = e31;
        this.entries[8] = e02;
        this.entries[9] = e12;
        this.entries[10] = e22;
        this.entries[11] = e32;
        this.entries[12] = e03;
        this.entries[13] = e13;
        this.entries[14] = e23;
        this.entries[15] = e33;
        return this;
    }

    /**
     * Copies the content of an existing matrix.
     *
     * @param m the matrix to copy
     * @return <code>this</code>
     */
    public Mat4M set(Mat4Base m) {
        System.arraycopy(m.entries, 0, this.entries, 0, 16);
        return this;
    }
    
    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Reads a new mutable matrix from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new mutable matrix or <code>null</code>
     * @throws BufferUnderflowException if there are less than 64 bytes in the
     *         buffer
     */
    public static Mat4M readFrom(ByteBuffer b) throws BufferUnderflowException {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = b.getFloat();
        }
        return new Mat4M(e);
    }

    /**
     * Reads a new mutable matrix from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the matrix. It is
     *          updated to point to the next byte after the matrix
     * @return the new mutable matrix or <code>null</code>
     * @throws BufferUnderflowException if there are less than 64 bytes in the
     *         buffer
     */
    public static Mat4M readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final float[] e = new float[16];
        for (int j = 0; j < 16; ++j) {
            e[j] = b.getFloat(i[0]);
            i[0] += 4;
        }
        return new Mat4M(e);
    }
    
    /**
     * Computes the matrix which represents a translation by <code>v</code>
     * 
     * @param v the translation vector
     * @return the translation by <code>v</code>
     */
    public static Mat4M translation(Vec3Base v) {
        return new Mat4M(1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        v.x, v.y, v.z, 1.0f);
    }

    /**
     * Computes the rotation matrix representing a rotation by some angle about
     * a fixed axis.
     * 
     * @param v the axis of rotation
     * @param angle the angle of rotation
     * @return the rotation by <code>angle</code> radiants about the axis 
     *         <code>v</code>
     */
    public static Mat4M rotation(Vec3Base v, float angle) {
        final float c = (float)Math.cos(angle);
        final float mc = 1.0f - c;
        final float s = (float)Math.sin(angle);
        return new Mat4M(c + v.x * v.x * mc, v.z * s + v.x * v.y * mc, v.x * v.z * mc - v.y * s, 0.0f,
                        v.x * v.y * mc - v.z * s, c + v.y * v.y * mc, v.x * s + v.y * v.z * mc, 0.0f,
                        v.y * s + v.x * v.z * mc, v.y * v.z * mc - v.x * s, c + v.z * v.z * mc, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    /**
     * Computes the rotation matrix representing a rotation about the x-axis.
     * 
     * @param angle the angle of rotation
     * @return rotation about the x-axis
     */
    public static Mat4M rotationX(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4M(1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, c, s, 0.0f,
                        0.0f, -s, c, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    /**
     * Computes the rotation matrix representing a rotation about the y-axis.
     * 
     * @param angle the angle of rotation
     * @return rotation about the y-axis
     */
    public static Mat4M rotationY(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4M(c, 0.0f, -s, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f,
                        s, 0.0f, c, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f);
        
    }
    
    /**
     * Computes the rotation matrix representing a rotation about the z-axis.
     * 
     * @param angle the angle of rotation
     * @return rotation about the z-axis
     */
    public static Mat4M rotationZ(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4M(c, s, 0.0f, 0.0f,
                        -s, c, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    /**
     * Computes the perspective transformation given a frustum.
     * 
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return the perspective transform
     */
    public static Mat4M frustum(float left, float right, float bottom, float top, float near, float far) {
        final float oneOnWidth = 1.0f / (right - left);
        final float oneOnHeight = 1.0f / (top - bottom);
        final float oneOnDepth = 1.0f / (far - near);
        return new Mat4M(2.0f * near * oneOnWidth, 0.0f, 0.0f, 0.0f,
                        0.0f, 2.0f * near * oneOnHeight, 0.0f, 0.0f,
                        (right + left) * oneOnWidth, (top + bottom) * oneOnHeight, - (far + near) * oneOnDepth, -1.0f,
                        0.0f, 0.0f, -2.0f * far * near * oneOnDepth, 0.0f
                );        
    }
    
    /**
     * Computes the matrix representing an ortogonal projection.
     * 
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return the projection matrix
     */
    public static Mat4M ortho(float left, float right, float bottom, float top, float near, float far) {
        final float oneOnWidth = 1.0f / (right - left);
        final float oneOnHeight = 1.0f / (top - bottom);
        final float oneOnDepth = 1.0f / (far - near);
        return new Mat4M(2.0f * oneOnWidth, 0.0f, 0.0f, 0.0f,
                        0.0f, 2.0f * oneOnHeight, 0.0f, 0.0f,
                        0.0f, 0.0f, - 2.0f * oneOnDepth, 0.0f,
                        - (right + left) * oneOnWidth, - (top + bottom) * oneOnHeight, - (far + near) * oneOnDepth, 1.0f
                );
    }
    
    /**
     * Computes the lookAt matrix.
     * 
     * @param eye
     * @param center
     * @param up
     * @return the lookAt matrix.
     */
    public static Mat4M lookAt(Vec3Base eye, Vec3Base center, Vec3Base up) {
        Vec3M front = center.minusM(eye).normalize();
        Vec3M right = front.crossM(up).normalize();
        Vec3M newUp = right.crossM(front);
        return toReferenceFrame(eye, right, newUp, front.negate());      
    }
    
    /**
     * Computes the matrix representing a perspective transformation.
     * 
     * @param fovy
     * @param aspect
     * @param near
     * @param far
     * @return the projection matrix
     */
    public static Mat4M perspective(float fovy, float aspect, float near, float far) {
        final float f = (float)(1.0 / Math.tan(fovy));
        final float oneOnDepth = 1.0f / (near - far);
        return new Mat4M(
                f/aspect, 0.0f, 0.0f, 0.0f,
                0.0f, f, 0.0f, 0.0f,
                0.0f, 0.0f, (far + near) * oneOnDepth, -1.0f,
                0.0f, 0.0f, 2.0f * far * near * oneOnDepth, 0.0f);
    }
    
    /**
     * Computes the matrix which transform a vector from a given reference frame
     * to the standard reference frame. The basis should be orthonormal.
     * 
     * @param origin the origin of the reference frame
     * @param b1 the first vector of the basis of the reference frame
     * @param b2 the second vector of the basis of the reference frame
     * @param b3 the third vector of the basis of the reference frame
     * @return the matrix which represents the change of reference frame
     */
    public static Mat4M fromReferenceFrame(Vec3Base origin, Vec3Base b1, Vec3Base b2, Vec3Base b3) {
        return new Mat4M(
            b1.x, b1.y, b1.z, 0.0f,
            b2.x, b2.y, b2.z, 0.0f,
            b3.x, b3.y, b3.z, 0.0f,
            origin.x, origin.y, origin.z, 1.0f
        );    
    }
    
    /**
     * Computes the matrix which transform a vector from the standard reference
     * frame to a given one. The basis should be orthonormal.
     * 
     * @param origin the origin of the reference frame
     * @param b1 the first vector of the basis of the reference frame
     * @param b2 the second vector of the basis of the reference frame
     * @param b3 the third vector of the basis of the reference frame
     * @return the matrix which represents the change of reference frame
     */
    public static Mat4M toReferenceFrame(Vec3Base origin, Vec3Base b1, Vec3Base b2, Vec3Base b3) {
        return new Mat4M(
            b1.x, b2.x, b3.x, 0.0f,
            b1.y, b2.y, b3.y, 0.0f,
            b1.z, b2.z, b3.z, 0.0f,
            - origin.dot(b1), - origin.dot(b2), -origin.dot(b3), 1.0f
        );
    }
    
    /*---------------------- BINARY OPERATIONS TO THIS -----------------------*/
    
    /**
     * Sums this matrix and another one and stores the result in this matrix.
     *
     * @param m the other matrix
     * @return <code>this += m</code>
     */
    public Mat4M plusEq(Mat4Base m) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] += m.entries[i];
        }
        return this;
    }

    /**
     * Sums this matrix and another one scaled by a scalar and stores the result
     * in this matrix.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this += s*m</code>
     */
    public Mat4M plusEq(float s, Mat4Base m) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] += s * m.entries[i];
        }
        return this;
    }

    /**
     * Subtracts this matrix and another one and stores the result in this matrix.
     *
     * @param m the other matrix
     * @return <code>this -= m</code>
     */
    public Mat4M minusEq(Mat4Base m) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] -= m.entries[i];
        }
        return this;
    }

    /**
     * Subtracts this matrix and another one scaled by a scalar and stores the 
     * result in this matrix.
     *
     * @param s the scalar to multiply to <code>m</code>
     * @param m the other matrix
     * @return <code>this -= s*m</code>
     */
    public Mat4M minusEq(float s, Mat4Base m) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] -= s * m.entries[i];
        }
        return this;
    }

    /**
     * Multiplies this matrix and a scalar and stores the result in this matrix.
     *
     * @param s the scalar
     * @return <code>this *= s</code>
     */
    public Mat4M timesEq(float s) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] *= s;
        }
        return this;
    }

    /**
     * Multiplies this matrix and another one and stores the result in this matrix.
     *
     * @param m the other matrix
     * @return <code>this *= m</code>
     */
    public Mat4M timesEq(Mat4Base m) {
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
        System.arraycopy(e, 0, this.entries, 0, 16);
        return this;
    }
    
    /**
     * Adds two matrices and stores the result in this vector.
     *
     * @param m the first matrix
     * @param n the second matrix
     * @return <code>this = m + n</code>
     */
    public Mat4M add(Mat4Base m, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = m.entries[i] + n.entries[i];
        }
        return this;
    }
    
    /**
     * Adds two matrices, the second scaled, and stores the result in this vector.
     *
     * @param m the first matrix
     * @param s the scale factor
     * @param n the second matrix
     * @return <code>this = m + s*n</code>
     */
    public Mat4M add(Mat4Base m, float s, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = m.entries[i] + s * n.entries[i];
        }
        return this;
    }
    
    /**
     * Adds two scaled matrices and stores the result in this vector.
     *
     * @param t the scale factor for m
     * @param m the first matrix
     * @param s the scale factor for n
     * @param n the second matrix
     * @return <code>this = t*m + s*n</code>
     */
    public Mat4M add(float t, Mat4Base m, float s, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = t * m.entries[i] + s * n.entries[i];
        }
        return this;
    }
    
    /**
     * Subtracts two matrices and stores the result in this vector.
     *
     * @param m the first matrix
     * @param n the second matrix
     * @return <code>this = m - n</code>
     */
    public Mat4M sub(Mat4Base m, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = m.entries[i] - n.entries[i];
        }
        return this;
    }
    
    /**
     * Subtracts two matrices, the second scaled, and stores the result in this vector.
     *
     * @param m the first matrix
     * @param s the scale factor
     * @param n the second matrix
     * @return <code>this = m - s*n</code>
     */
    public Mat4M sub(Mat4Base m, float s, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = m.entries[i] - s * n.entries[i];
        }
        return this;
    }
    
    /**
     * Subtracts two scaled matrices and stores the result in this vector.
     *
     * @param t the scale factor for m
     * @param m the first matrix
     * @param s the scale factor for n
     * @param n the second matrix
     * @return <code>this = t*m + s*n</code>
     */
    public Mat4M sub(float t, Mat4Base m, float s, Mat4Base n) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = t * m.entries[i] - s * n.entries[i];
        }
        return this;
    }
    
    /**
     * Multiplies a matrix by a scalar and stores the result in this vector.
     *
     * @param s the scalar
     * @param m the matrix
     * @return <code>this = s*m</code>
     */
    public Mat4M mul(float s, Mat4Base m) {
        for (int i = 0; i < 16; ++i) {
            this.entries[i] = s * m.entries[i];
        }
        return this;
    }
    
    /**
     * Multiplies two matrices and stores the result in this vector.
     *
     * @param m the first matrix
     * @param n the second matrix
     * @return <code>this = m * n</code>
     */
    public Mat4M mul(Mat4Base m, Mat4Base n) {
        final float[] e = new float[16];
        for (int j = 0; j < 16; j += 4) {
            for (int k = 0; k < 4; ++k) {
                final int col = 4 * k;
                final float sk = n.entries[j + k];
                
                e[j + 0] += m.entries[col + 0] * sk;
                e[j + 1] += m.entries[col + 1] * sk;
                e[j + 2] += m.entries[col + 2] * sk;
                e[j + 3] += m.entries[col + 3] * sk;                
            }            
        }
        System.arraycopy(e, 0, this.entries, 0, 16);
        return this;
    }
    
    /*----------------------- UNARY OPERATIONS TO THIS -----------------------*/
    
    /**
     * Returns this matrix negated.
     *
     * @return <code>this = -this</code>
     */
    public Mat4M negate() {
        return this.timesEq(-1.0f);
    }

    /**
     * Transposes this matrix.
     *
     * @return <code>this = this^t</code>
     */
    public Mat4M transpose() {
        float t = this.entries[1];
        this.entries[1] = this.entries[4];
        this.entries[4] = t;
        
        t = this.entries[2];
        this.entries[2] = this.entries[8];
        this.entries[8] = t;
        
        t = this.entries[3];
        this.entries[3] = this.entries[12];
        this.entries[12] = t;
        
        t = this.entries[6];
        this.entries[6] = this.entries[9];
        this.entries[9] = t;
        
        t = this.entries[7];
        this.entries[7] = this.entries[13];
        this.entries[13] = t;
        
        t = this.entries[11];
        this.entries[11] = this.entries[14];
        this.entries[14] = t;
        
        return this;
    }

    /**
     * Inverts this matrix.
     *
     * @return <code>this = this^(-1)</code>
     */
    public Mat4M invert() {
        throw new UnsupportedOperationException("General matrix inverse not implemented yet.");
    }
    
    /**
     * Returns the inverse of this matrix assuming it represents an affine
     * transformation.
     *
     * @return <code>this = this^(-1)</code>
     */
    public Mat4M invertAffine() {
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
        
        this.entries[0] = a00 * d;
        this.entries[1] = a01 * d;
        this.entries[2] = a02 * d;
        this.entries[3] = 0.0f;
        
        this.entries[4] = a10 * d;
        this.entries[5] = a11 * d;
        this.entries[6] = a12 * d;
        this.entries[7] = 0.0f;
        
        this.entries[8] = a20 * d;
        this.entries[9] = a21 * d;
        this.entries[10] = a22 * d;
        this.entries[11] = 0.0f;
        
        this.entries[12] = vx * d;
        this.entries[13] = vy * d;
        this.entries[14] = vz * d;
        this.entries[15] = 1.0f;
        
        return this;
    }
    
    /**
     * Invert this matrix assuming it represents an orthogonal
     * transformation (an isometry without translation component).
     * 
     * @return <code>this = this^(-1)</code>
     */
    public Mat4M invertOrthogonal() {
        return this.transpose();
    }
    
    /**
     * Invert this matrix assuming it represents an isometry.
     * 
     * @return <code>this = this^(-1)</code>
     */
    public final Mat4M invertIsometry() {
        final float vx = - (this.entries[0] * this.entries[12] + this.entries[1] * this.entries[13] + this.entries[2] * this.entries[14]);
        final float vy = - (this.entries[4] * this.entries[12] + this.entries[5] * this.entries[13] + this.entries[6] * this.entries[14]);
        final float vz = - (this.entries[8] * this.entries[12] + this.entries[9] * this.entries[13] + this.entries[10] * this.entries[14]);
        
        float t = this.entries[1];
        this.entries[1] = this.entries[4];
        this.entries[4] = t;
        
        t = this.entries[2];
        this.entries[2] = this.entries[8];
        this.entries[8] = t;
        
        t = this.entries[6];
        this.entries[6] = this.entries[9];
        this.entries[9] = t;

        this.entries[12] = vx;
        this.entries[13] = vy;
        this.entries[14] = vz;
        
        return this;
    }
}
