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

import org.gephi.lib.gleem.linalg.Mat3f;
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Rotf;
import org.gephi.lib.gleem.linalg.Vec3f;

/**
 * Class representing a camera. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Camera {
    
    private Vec3f front, up;
    private Vec3f position;
    private Vec3f orbitCenter;

    private float imageWidth, imageHeight, fovy, near, far;

    private float scaleFactor;

    private static final float MIN_ORBIT = 200.0f;
    private static final float MAX_ORBIT = 2000.0f;
    private static final float MAX_FOVY = 3.0f;

    public Camera(int width, int height, float near, float far) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.fovy = 1.0f;
        this.near = near;
        this.far = far;
        this.scaleFactor = 1.0f;

        this.position = new Vec3f();
        this.front = new Vec3f(0.0f, 0.0f, -1.0f);
        this.up = new Vec3f(0.0f, 1.0f, 0.0f);
    }

    public Camera(Camera camera) {
        this.imageWidth = camera.imageWidth;
        this.imageHeight = camera.imageHeight;
        this.fovy = camera.fovy;
        this.near = camera.near;
        this.far = camera.far;

        this.position = camera.position.copy();
        this.front = camera.front.copy();
        this.up = camera.up.copy();
    }

    public void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public void moveTo(Vec3f newPos) {
        this.position = newPos.copy();
    }

    public void translate(Vec3f v) {
        this.position.add(v);
    }

    public void rotate(Vec3f axis, float angle) {
        Rotf rot = new Rotf(axis, angle);
        this.front = rot.rotateVector(this.front);
        this.up = rot.rotateVector(up);
    }

    public void rotate(Vec3f origin, Vec3f axis, float angle) {
        Rotf rot = new Rotf(axis, angle);
        this.front = rot.rotateVector(this.front);
        this.up = rot.rotateVector(up);

        Vec3f diff = this.position.minus(origin);
        this.position.add(origin, rot.rotateVector(diff));
    }

    public void lookAt(Vec3f center, Vec3f up) {
        this.front.sub(center, this.position);
        this.front.normalize();
        this.up = up.copy();
        this.up.normalize();
    }

    public void lookAt(Vec3f position, Vec3f center, Vec3f up) {
        this.position = position.copy();
        lookAt(center, up);
    }

    public void setFov(float fov) {
        this.fovy = fov;
    }

    public void setClipPlanes(float near, float far) {
        this.near = near;
        this.far = far;
    }

    public Vec3f frontVector() {
        return this.front.copy();
    }

    public Vec3f upVector() {
        return this.up.copy();
    }

    public Vec3f rightVector() {
        return this.front.cross(this.up);
    }

    public Vec3f position() {
        return this.position.copy();
    }

    public float imageWidth() {
        return this.imageWidth;
    }

    public float imageHeight() {
        return this.imageHeight;
    }

    public float near() {
        return this.near;
    }

    public float far() {
        return this.far;
    }

    public float fov() {
        return this.fovy;
    }

    public float projectedDistanceFrom(Vec3f point) {
        this.front.normalize();
        Vec3f pnt = point.copy();
        pnt.sub(this.position);
        return pnt.dot(this.front);
    }

    public Mat4f viewMatrix() {
        Vec3f right = rightVector();
        Mat4f mat = new Mat4f();
        for (int i = 0; i < 3; ++i) {
            mat.set(i, 0, right.get(i));
            mat.set(i, 1, this.up.get(i));
            mat.set(i, 2, this.front.get(i));
            mat.set(i, 3, this.position.get(i));
        }
        mat.set(3, 3, 1.0f);
        return mat;
    }

    public Mat4f projectiveMatrix() {
        Mat4f mat = new Mat4f();
            float aspect = imageWidth/imageHeight;
            float f = (float) (1.0 / Math.tan(this.fovy / 2.0));
            mat.set(0, 0, f/aspect);
            mat.set(1, 1, f);
            mat.set(2, 2, (this.far + this.near)/(this.near - this.far));
            mat.set(2, 3, (2.0f * this.far * this.near)/(this.near - this.far));
            mat.set(3, 2, -1.0f);
        return mat;
    }

    public void startTranslation() {}

    public void updateTranslation(float horizontal, float vertical) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3f horizontalTranslation = rightVector().times(horizontal * ratio);
        Vec3f verticalTranslation = this.up.times(vertical * ratio);
        Vec3f translation = new Vec3f();
        translation.add(horizontalTranslation, verticalTranslation);
        Vec3f result = new Vec3f();
        Mat3f rotationMatrix = new Mat3f();
        rotationMatrix.setCol(0, this.rightVector());
        rotationMatrix.setCol(1, this.up);
        rotationMatrix.setCol(2, this.front);
        rotationMatrix.invert();
        rotationMatrix.xformVec(translation, result);
        translate(result);
    }

    public void finishTranslation() {}

    private float orbitRadius(float modifier) {
        return (MIN_ORBIT + (MAX_ORBIT - MIN_ORBIT) * modifier) * fovy;
    }

    /**
     * Initialize orbiting around a center point.
     * @param orbitModifier must be a value between 0.0 and 1.0.
     */
    public void startOrbit(float orbitModifier) {
        orbitCenter = new Vec3f(position).addScaled(orbitRadius(orbitModifier), front);
    }

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
        position = v;
        lookAt(orbitCenter, ur);
    }

    public void finishOrbit() {}

    public void zoom(float by) {
        fovy = (float) Math.min(fovy * Math.exp(by), MAX_FOVY);
    }
}
