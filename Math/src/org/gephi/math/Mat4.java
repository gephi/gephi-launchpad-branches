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
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Mat4 extends Mat4Base {
    
    /*--------------------- CONSTANTS 4x4 MATRICES ---------------------------*/
    
    /**
     * Zero matrix.
     */
    public final Mat4 ZERO = new Mat4(0.0f);
    
    /**
     * Identity matrix.
     */
    public final Mat4 IDENTITY = new Mat4(1.0f);
    
    
    /*----------------------------- CONSTRUCTORS -----------------------------*/

    /**
     * Creates a diagonal matrix with all the entries on the diagonal equal.
     *
     * @param v the value of the entries on the diagonal
     */
    public Mat4(float v) {
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
    public Mat4(float v0, float v1, float v2, float v3) {
        super(v0, v1, v2, v3);
    }
    
    /**
     * Creates a diagonal matrix from the values on the diagonal.
     *
     * @param v the vector containing the values on the diagonal
     */
    public Mat4(Vec4Base v) {
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
    public Mat4(Vec4Base c0, Vec4Base c1, Vec4Base c2, Vec4Base c3) {
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
    public Mat4(float e00, float e10, float e20, float e30,
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
    public Mat4(Mat4Base m) {
        super(m);
    }

    /**
     * Creates a new matrix from an array. It directly stores the array. It
     * should only be used internally for performance reasons.
     *
     * @param e the entries' array
     */
    protected Mat4(float[] e) {
        super(e);
    }
    
    /*------------------------ STATIC FACTORY METHODS ------------------------*/

    /**
     * Reads a new immutable matrix from a <code>ByteBuffer</code> at the
     * current position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @return the new immutable matrix or <code>null</code>
     * @throws BufferUnderflowException if there are less than 64 bytes in the
     *         buffer
     */
    public static Mat4 readFrom(ByteBuffer b) throws BufferUnderflowException {
        final float[] e = new float[16];
        for (int i = 0; i < 16; ++i) {
            e[i] = b.getFloat();
        }
        return new Mat4(e);
    }

    /**
     * Reads a new immutable matrix from a <code>ByteBuffer</code> at a
     * specified position.
     *
     * @param b the <code>ByteBuffer</code> instance
     * @param i the starting position from where to read the matrix. It is
     *          updated to point to the next byte after the matrix
     * @return the new immutable matrix or <code>null</code>
     * @throws BufferUnderflowException if there are less than 64 bytes in the
     *         buffer
     */
    public static Mat4 readFrom(ByteBuffer b, int[] i)
                            throws BufferUnderflowException {
        final float[] e = new float[16];
        for (int j = 0; j < 16; ++j) {
            e[j] = b.getFloat(i[0]);
            i[0] += 4;
        }
        return new Mat4(e);
    }
    
    /**
     * Computes the matrix which represents a translation by <code>v</code>
     * 
     * @param v the translation vector
     * @return the translation by <code>v</code>
     */
    public static Mat4 translation(Vec3Base v) {
        return new Mat4(1.0f, 0.0f, 0.0f, 0.0f,
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
    public static Mat4 rotation(Vec3Base v, float angle) {
        final float c = (float)Math.cos(angle);
        final float mc = 1.0f - c;
        final float s = (float)Math.sin(angle);
        return new Mat4(c + v.x * v.x * mc, v.z * s + v.x * v.y * mc, v.x * v.z * mc - v.y * s, 0.0f,
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
    public static Mat4 rotationX(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4(1.0f, 0.0f, 0.0f, 0.0f,
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
    public static Mat4 rotationY(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4(c, 0.0f, -s, 0.0f,
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
    public static Mat4 rotationZ(float angle) {
        final float c = (float)Math.cos(angle);
        final float s = (float)Math.sin(angle);
        return new Mat4(c, s, 0.0f, 0.0f,
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
    public static Mat4 frustum(float left, float right, float bottom, float top, float near, float far) {
        final float oneOnWidth = 1.0f / (right - left);
        final float oneOnHeight = 1.0f / (top - bottom);
        final float oneOnDepth = 1.0f / (far - near);
        return new Mat4(2.0f * near * oneOnWidth, 0.0f, 0.0f, 0.0f,
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
    public static Mat4 ortho(float left, float right, float bottom, float top, float near, float far) {
        final float oneOnWidth = 1.0f / (right - left);
        final float oneOnHeight = 1.0f / (top - bottom);
        final float oneOnDepth = 1.0f / (far - near);
        return new Mat4(2.0f * oneOnWidth, 0.0f, 0.0f, 0.0f,
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
    public static Mat4 lookAt(Vec3Base eye, Vec3Base center, Vec3Base up) {
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
    public static Mat4 perspective(float fovy, float aspect, float near, float far) {
        final float f = (float)(1.0 / Math.tan(fovy));
        final float oneOnDepth = 1.0f / (near - far);
        return new Mat4(
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
    public static Mat4 fromReferenceFrame(Vec3Base origin, Vec3Base b1, Vec3Base b2, Vec3Base b3) {
        return new Mat4(
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
    public static Mat4 toReferenceFrame(Vec3Base origin, Vec3Base b1, Vec3Base b2, Vec3Base b3) {
        return new Mat4(
            b1.x, b2.x, b3.x, 0.0f,
            b1.y, b2.y, b3.y, 0.0f,
            b1.z, b2.z, b3.z, 0.0f,
            - origin.dot(b1), - origin.dot(b2), -origin.dot(b3), 1.0f
        );
    }
}
