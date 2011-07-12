/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Rotf;
import org.gephi.lib.gleem.linalg.Vec2f;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.visualization.api.camera.Camera;

/**
 * Class representing a camera for two dimensions. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public class Camera2d implements Camera {

    private Vec2f up;
    private Vec2f position;

    private Mat4f projectiveMatrix;
    private Mat4f modelviewMatrix;
    private boolean recomputeMatrix = true;

    private float imageWidth, imageHeight, fovy, near, far;

    private static final float MAX_FOVY = 3.0f;

    public Camera2d(int width, int height, float near, float far) {
        this.imageWidth = width;
        this.imageHeight = height;
        this.fovy = 1.0f;
        this.near = near;
        this.far = far;

        this.position = new Vec2f();
        this.up = new Vec2f(0.0f, 1.0f);
    }

    @Override
    public Camera copy() {
        Camera2d camera = new Camera2d((int) imageWidth, (int) imageHeight, near, far);
        camera.fovy = this.fovy;
        camera.position = this.position;
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
        this.position = convertTo2d(newPos).copy();
        requireRecomputeMatrix();
    }

    @Override
    public void translate(Vec3f v) {
        this.position.add(convertTo2d(v));
        requireRecomputeMatrix();
    }

    /**
     * There is only one rotation axis in 2D and it will be used instead.
     */
    @Override
    public void rotate(Vec3f axis, float angle) {
        Rotf rot = new Rotf(Vec3f.NEG_Z_AXIS, angle);
        this.up = convertTo2d(rot.rotateVector(upVector()));
        requireRecomputeMatrix();
    }

    /**
     * There is only one rotation axis in 2D and it will be used instead.
     */
    @Override
    public void rotate(Vec3f origin, Vec3f axis, float angle) {
        Rotf rot = new Rotf(Vec3f.NEG_Z_AXIS, angle);
        this.up = convertTo2d(rot.rotateVector(upVector()));

        Vec3f diff = new Vec3f(position.x(), position.y(), 0).minus(new Vec3f(origin.x(), origin.y(), origin.z()));
        this.position.add(convertTo2d(origin), convertTo2d(rot.rotateVector(diff)));
        requireRecomputeMatrix();
    }

    /**
     * Moves the camera above the center.
     */
    @Override
    public void lookAt(Vec3f center, Vec3f up) {
        this.up = convertTo2d(up);
        this.up.normalize();
        this.position = convertTo2d(center);
        requireRecomputeMatrix();
    }

    /**
     * One of the center or position vectors are redundant. The position vector
     * will be ignored and camera moved above the center.
     */
    @Override
    public void lookAt(Vec3f position, Vec3f center, Vec3f up) {
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
        return Vec3f.NEG_Z_AXIS;
    }

    @Override
    public Vec3f upVector() {
        return new Vec3f(up.x(), up.y(), 0);
    }

    @Override
    public Vec3f rightVector() {
        return frontVector().cross(upVector());
    }

    @Override
    public Vec3f position() {
        return new Vec3f(position.x(), position.y(), 0);
    }

    @Override
    public Vec3f lookAtPoint() {
        return new Vec3f(position.x(), position.y(), 0);
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
        Vec3f pnt = point.copy();
        pnt.sub(this.position());
        return pnt.dot(this.frontVector());
    }

    /**
     * Returns the model-view matrix.
     */
    @Override
    public Mat4f viewMatrix() {
        if (recomputeMatrix) {
            Mat4f mat = new Mat4f();
            // TODO
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
            // TODO
            projectiveMatrix = mat;
        }
        return projectiveMatrix;
    }

    /**
     * Returns the given point as it will appear on the screen.
     */
    @Override
    public Point projectPoint(float x, float y, float z) {
        // TODO
        /*
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
        */
        return new Point(0, 0);
    }

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen.
     */
    @Override
    public Vec3f projectPointInverse(float x, float y) {
        return new Vec3f(position.x(), position.y(), 0);
    }

    /**
     * Returns a vector from camera viewing plane corresponding to the 2D vector
     * on screen.
     */
    @Override
    public Vec3f projectVectorInverse(float x, float y) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3f horizontalTranslation = this.rightVector().times(x * ratio);
        Vec3f verticalTranslation = this.upVector().times(y * ratio);
        Vec3f translation = new Vec3f();
        translation.add(horizontalTranslation, verticalTranslation);
        return translation;
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
        Vec3f horizontalTranslation = this.rightVector().times(horizontal * ratio);
        Vec3f verticalTranslation = this.upVector().times(vertical * ratio);
        Vec3f translation = new Vec3f();
        translation.add(horizontalTranslation, verticalTranslation);
        translate(translation);
    }

    @Override
    public void startOrbit(float orbitModifier) {}

    @Override
    public void updateOrbit(float x, float y) {
        // TODO
    }

    @Override
    public void zoom(float by) {
        setFov((float) Math.min(fovy * Math.exp(by), MAX_FOVY));
    }

    private void requireRecomputeMatrix() {
        recomputeMatrix = true;
    }

    private Vec2f convertTo2d(Vec3f v) {
        return new Vec2f(v.x(), v.y());
    }
}
