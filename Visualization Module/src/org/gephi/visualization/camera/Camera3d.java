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

package org.gephi.visualization.camera;

import java.awt.Dimension;
import java.awt.Point;
import org.gephi.lib.gleem.linalg.Mat3f;
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Rotf;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.api.camera.Camera;

/**
 * Class representing a camera for three dimensions. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public class Camera3d implements Camera {
    
    private Vec3f front, up;
    private Vec3f position;
    private Vec3f orbitCenter;

    private Mat4f projectiveMatrix;
    private Mat4f modelviewMatrix;
    private boolean recomputeMatrix = true;

    private float imageWidth, imageHeight, fovy, near, far;

    private static final float MIN_ORBIT = 200.0f;
    private static final float MAX_ORBIT = 2000.0f;
    private static final float MAX_FOVY = 3.0f;

    public Camera3d(int width, int height, float near, float far) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.fovy = 1.0f;
        this.near = near;
        this.far = far;

        this.position = new Vec3f();
        this.front = new Vec3f(0.0f, 0.0f, -1.0f);
        this.up = new Vec3f(0.0f, 1.0f, 0.0f);
    }

    @Override
    public Camera copy() {
        Camera3d camera = new Camera3d((int) imageWidth, (int) imageHeight, near, far);
        camera.fovy = this.fovy;
        camera.position = this.position;
        camera.front = this.front;
        camera.up = this.up;
        return camera;
    }

    @Override
    public void setImageSize(Dimension size) {
        this.imageWidth = size.width;
        this.imageHeight = size.height;
        requireRecomputeMatrix();
    }

    @Override
    public void moveTo(Vec3f newPos) {
        this.position = newPos.copy();
        requireRecomputeMatrix();
    }

    @Override
    public void translate(Vec3f v) {
        this.position.add(v);
        requireRecomputeMatrix();
    }

    @Override
    public void rotate(Vec3f axis, float angle) {
        Rotf rot = new Rotf(axis, angle);
        this.front = rot.rotateVector(this.front);
        this.up = rot.rotateVector(up);
        requireRecomputeMatrix();
    }

    @Override
    public void rotate(Vec3f origin, Vec3f axis, float angle) {
        Rotf rot = new Rotf(axis, angle);
        this.front = rot.rotateVector(this.front);
        this.up = rot.rotateVector(up);

        Vec3f diff = this.position.minus(origin);
        this.position.add(origin, rot.rotateVector(diff));
        requireRecomputeMatrix();
    }

    @Override
    public void lookAt(Vec3f center, Vec3f up) {
        this.front.sub(center, this.position);
        this.front.normalize();
        this.up = up.copy();
        this.up.normalize();
        requireRecomputeMatrix();
    }

    @Override
    public void lookAt(Vec3f position, Vec3f center, Vec3f up) {
        this.position = position.copy();
        lookAt(center, up);
    }

    @Override
    public void setFov(float fov) {
        this.fovy = fov;
        requireRecomputeMatrix();
    }

    @Override
    public void setClipPlanes(float near, float far) {
        this.near = near;
        this.far = far;
        requireRecomputeMatrix();
    }

    @Override
    public Vec3f frontVector() {
        return this.front.copy();
    }

    @Override
    public Vec3f upVector() {
        return this.up.copy();
    }

    @Override
    public Vec3f rightVector() {
        return this.front.cross(this.up);
    }

    @Override
    public Vec3f position() {
        return this.position.copy();
    }

    @Override
    public Vec3f lookAtPoint() {
        Vec3f point = new Vec3f();
        point.add(position, front);
        return point;
    }

    @Override
    public float imageWidth() {
        return this.imageWidth;
    }

    @Override
    public float imageHeight() {
        return this.imageHeight;
    }

    @Override
    public float near() {
        return this.near;
    }

    @Override
    public float far() {
        return this.far;
    }

    @Override
    public float fov() {
        return this.fovy;
    }

    @Override
    public float projectedDistanceFrom(Vec3f point) {
        this.front.normalize();
        Vec3f pnt = point.copy();
        pnt.sub(this.position);
        return pnt.dot(this.front);
    }

    /**
     * Returns the model-view matrix.
     */
    @Override
    public Mat4f viewMatrix() {
        if (recomputeMatrix) {
            Vec3f right = rightVector();
            Mat4f mat = new Mat4f();
            mat.setRotation(right, this.up, this.front.times(-1.0f));
            mat.transpose();
            mat.setTranslation(this.position.times(-1.0f));
            mat.set(3, 3, 1.0f);
            modelviewMatrix = mat;
        }
        return modelviewMatrix;
    }

    /**
     * Returns the projective matrix.
     */
    @Override
    public Mat4f projectiveMatrix() {
        if (recomputeMatrix) {
            Mat4f mat = new Mat4f();
            float aspect = imageWidth/imageHeight;
            float f = (float) (1.0 / Math.tan(this.fovy / 2.0));
            mat.set(0, 0, f/aspect);
            mat.set(1, 1, f);
            mat.set(2, 2, (this.far + this.near)/(this.near - this.far));
            mat.set(2, 3, (2.0f * this.far * this.near)/(this.near - this.far));
            mat.set(3, 2, -1.0f);
            projectiveMatrix = mat;
        }
        return projectiveMatrix;
    }

    /**
     * Returns the given point as it will appear on the screen.
     */
    @Override
    public Point projectPoint(float x, float y, float z) {
        Vec4f point = new Vec4f(x, y, z, 1.0f);
        Vec4f screenPoint = new Vec4f();
        Mat4f viewProjMatrix = projectiveMatrix().mul(viewMatrix());
        // multiply by modelview and projection matrices
        viewProjMatrix.xformVec(point, screenPoint);
        screenPoint.scale(1.0f/screenPoint.w());
        // to NDC
        // point.scale(1 / point.w());
        int px = (int) ((screenPoint.x() + 1.0f) * imageWidth / 2.0f);
        int py = (int) ((1.0f - screenPoint.y()) * imageHeight / 2.0f);
        return new Point(px, py);
    }

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen.
     */
    @Override
    public Vec3f projectPointInverse(float x, float y) {
        // FIXME
        Vec3f point = this.position.addScaled(getCameraDistance(), this.front);
        return point.addScaled(y - imageHeight / 2, up).addScaled(x - imageWidth / 2, rightVector());
    }

    private float getCameraDistance() {
        //TODO implement
        Vec3f distance = new Vec3f();
        distance.sub(new Vec3f(0, 0, 0), position);
        return distance.length();
    }

    /**
     * Returns a vector from camera viewing plane corresponding to the 2D vector
     * on screen.
     */
    @Override
    public Vec3f projectVectorInverse(float x, float y) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3f rightVector = rightVector();
        Vec3f horizontalTranslation = rightVector.times(x * ratio);
        Vec3f verticalTranslation = this.up.times(y * ratio);
        Vec3f translation = new Vec3f();
        translation.add(horizontalTranslation, verticalTranslation);
        Vec3f result = new Vec3f();
        Mat3f rotationMatrix = new Mat3f();
        rotationMatrix.setCol(0, rightVector);
        rotationMatrix.setCol(1, this.up);
        rotationMatrix.setCol(2, this.front);
        rotationMatrix.invert();
        rotationMatrix.xformVec(translation, result);
        return result;
    }

    @Override
    public int projectNodeRadius(float x, float y, float z, float size) {
        // FIXME
        return 5;
    }

    @Override
    public int getPlanarDistance(float x, float y, float z, int a, int b) {
        Point point = projectPoint(x, y, z);
        return (int) Math.sqrt((point.x - a) * (point.x - a) + (point.y - b) * (point.y - b));
    }

    @Override
    public void startTranslation() {}

    @Override
    public void updateTranslation(float horizontal, float vertical) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3f rightVector = rightVector();
        Vec3f horizontalTranslation = rightVector.times(horizontal * ratio);
        Vec3f verticalTranslation = this.up.times(vertical * ratio);
        Vec3f translation = new Vec3f();
        translation.add(horizontalTranslation, verticalTranslation);
        //Vec3f result = new Vec3f();
        //Mat3f rotationMatrix = new Mat3f();
        //rotationMatrix.setCol(0, rightVector);
        //rotationMatrix.setCol(1, this.up);
        //rotationMatrix.setCol(2, this.front);
        //rotationMatrix.invert();
        //rotationMatrix.xformVec(translation, result);
        //System.out.println("Position: " + position);
        translate(translation);
    }

    private float orbitRadius(float modifier) {
        return (MIN_ORBIT + (MAX_ORBIT - MIN_ORBIT) * modifier) * fovy;
    }

    /**
     * Initialize orbiting around a center point.
     * @param orbitModifier must be a value between 0.0 and 1.0.
     */
    @Override
    public void startOrbit(float orbitModifier) {
        orbitCenter = new Vec3f(position).addScaled(orbitRadius(orbitModifier), front);
    }

    @Override
    public void updateOrbit(float x, float y) {
        Vec3f fromCenter = new Vec3f();
        fromCenter.sub(position, orbitCenter);

        Mat3f rotationMatrix = new Mat3f();
        float sx = (float) Math.sin(y);
        float sy = (float) Math.sin(x);
        float cx = (float) Math.cos(y);
        float cy = (float) Math.cos(x);
        rotationMatrix.setCol(0, new Vec3f(cy, sx * sy, -cx * sy));
        rotationMatrix.setCol(1, new Vec3f(0, cx, sx));
        rotationMatrix.setCol(2, new Vec3f(sy, -sx * cy, cx * cy));
        Vec3f v = new Vec3f();
        Vec3f u = new Vec3f(up);

        u.add(fromCenter);
        Vec3f ur = new Vec3f();
        rotationMatrix.xformVec(fromCenter, v);
        rotationMatrix.xformVec(u, ur);

        ur.sub(v);
        v.add(orbitCenter);
        lookAt(v, orbitCenter, ur);
        
        //System.out.println("Front: " + this.front);
        //System.out.println("Up: " + this.up);
        //System.out.println("Right: " + rightVector());
        //System.out.println("Position: " + position);
        //System.out.println("--------------------------");
    }

    @Override
    public void zoom(float by) {
        setFov((float) Math.min(fovy * Math.exp(by), MAX_FOVY));
    }

    private void requireRecomputeMatrix() {
        recomputeMatrix = true;
    }
}
