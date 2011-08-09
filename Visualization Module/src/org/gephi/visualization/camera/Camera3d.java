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
import org.gephi.math.linalg.Mat4M;
import org.gephi.math.linalg.Vec3;
import org.gephi.math.linalg.Vec3Base;
import org.gephi.math.linalg.Vec3M;
import org.gephi.math.linalg.Vec4;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.geometry.AABB;
import org.openide.util.Lookup;

/**
 * Class representing a camera for three dimensions. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public final class Camera3d implements Camera {
    
    private Vec3M front, up, right;
    private Vec3M position;
    private Vec3M orbitCenter;
    
    private float fovy;
    private float relativeZoom;

    private float screenWidth, screenHeight;

    private static final float MIN_ORBIT = 200.0f;
    private static final float MAX_ORBIT = 2000.0f;

    private static final float MAX_ZOOM = 1.0f;
    private static final float MIN_ZOOM = -4.0f;

    public Camera3d() {
        this.screenWidth = 1;
        this.screenHeight = 1;
        
        this.position = Vec3.E3.copyM();
        this.front = Vec3.E3_NEG.copyM();
        this.up = Vec3.E2.copyM();
        this.right = Vec3.E1.copyM();
        this.orbitCenter = new Vec3M();
        
        this.fovy = 1.0f;
        this.relativeZoom = 1.0f;
    }
    
    public Camera3d(int width, int height) {
        this();
        
        this.screenWidth = width;
        this.screenHeight = height;
    }
    
    public Camera3d(Camera3d camera) {
        this.screenWidth = camera.screenWidth;
        this.screenHeight = camera.screenHeight;
        
        this.position = camera.position.copyM();
        this.front = camera.front.copyM();
        this.up = camera.up.copyM();
        this.right = camera.right.copyM();
        this.orbitCenter = camera.orbitCenter.copyM();
        
        this.fovy = camera.fovy;
        this.relativeZoom = camera.relativeZoom;
    }

    public Camera3d(Camera2d camera) {
        this((int)camera.screenWidth(), (int)camera.screenHeight());
        // TODO: study a better way to create a 3D camera from a 2D one
    }
    
    @Override
    public Camera copy() {
        return new Camera3d(this);
    }

    @Override
    public void screenSize(Dimension size) {
        this.screenWidth = size.width;
        this.screenHeight = size.height;
    }

    @Override
    public void lookAt(Vec3Base center, Vec3Base up) {
        this.front.sub(center, this.position);
        this.front.normalize();
        this.right.toCross(this.front, up);
        this.right.normalize();
        this.up.toCross(this.right, this.front);
        this.up.normalize(); // it should be unnecessary
    }

    public void lookAt(Vec3Base position, Vec3Base center, Vec3Base up) {
        this.position.set(position);
        lookAt(center, up);
    }

    @Override
    public float screenWidth() {
        return this.screenWidth;
    }

    @Override
    public float screenHeight() {
        return this.screenHeight;
    }

    /**
     * Returns the given point as it will appear on the screen together with its
     * size on screen after transformation have been applied.
     * @return array of floats, where
     * [0,1] -> point coordinates on screen
     * [2]   -> size of the node on screen
     */
    @Override
    public float[] projectPoint(float x, float y, float z, float size) {
        return new float[]{0.0f, 0.0f, 0.0f}; // TODO: implement it
    }

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen.
     */
    @Override
    public Vec3 projectPointInverse(float x, float y) {
        // FIXME
        Vec3M point = this.position.plusM(this.position.length(), this.front);
        return point.plusEq(y - screenHeight / 2, up).plus(x - screenWidth / 2, this.right);
    }

    /**
     * Returns the translation vector which corresponds to the (x,y) screen
     * vector.
     */
    @Override
    public Vec3 projectVectorInverse(float x, float y) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3 horizontalTranslation = this.right.times(x * ratio);
        Vec3 verticalTranslation = this.up.times(y * ratio);
        Vec3 translation = horizontalTranslation.plus(verticalTranslation);
        Mat4M rotationMatrix = new Mat4M(1.0f);
        rotationMatrix.setColumn(0, this.right.x(), this.right.y(), this.right.z(), 0.0f);
        rotationMatrix.setColumn(1, this.up.x(), this.up.y(), this.up.z(), 0.0f);
        rotationMatrix.setColumn(2, this.front.x(), this.front.y(), this.front.z(), 0.0f);
        rotationMatrix.invertOrthogonal();
        return rotationMatrix.transformAffineVec(translation);
    }

    @Override
    public float getPlanarDistance(float x, float y, float z, int a, int b) {
        float[] point = projectPoint(x, y, z, 0);
        return (float) Math.sqrt((point[0] - a) * (point[0] - a) + (point[1] - b) * (point[1] - b));
    }

    @Override
    public void translate(float horizontal, float vertical) {
        float ratio = (float) Math.sqrt((1 - Math.cos(fovy)) / (1 - Math.cos(1.0)));
        Vec3 horizontalTranslation = this.right.times(horizontal * ratio);
        Vec3 verticalTranslation = this.up.times(vertical * ratio);
        Vec3 translation = horizontalTranslation.plus(verticalTranslation);
        //Vec3f result = new Vec3f();
        //Mat3f rotationMatrix = new Mat3f();
        //rotationMatrix.setCol(0, rightVector);
        //rotationMatrix.setCol(1, this.up);
        //rotationMatrix.setCol(2, this.front);
        //rotationMatrix.invert();
        //rotationMatrix.xformVec(translation, result);
        //System.out.println("Position: " + positionVector);
        this.position.plusEq(translation);
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
        orbitCenter.add(position, orbitRadius(orbitModifier), front);
    }

    @Override
    public void updateOrbit(float x, float y) {
        Vec3 fromCenter = this.position.minus(this.orbitCenter);

        Mat4M rotationMatrix = new Mat4M(1.0f);
        float sx = (float) Math.sin(y);
        float sy = (float) Math.sin(x);
        float cx = (float) Math.cos(y);
        float cy = (float) Math.cos(x);
        rotationMatrix.setColumn(0, new Vec4(cy, sx * sy, -cx * sy, 0.0f));
        rotationMatrix.setColumn(1, new Vec4(0, cx, sx, 0.0f));
        rotationMatrix.setColumn(2, new Vec4(sy, -sx * cy, cx * cy, 0.0f));
        Vec3 u = this.up.plus(fromCenter);
        
        Vec3M v = rotationMatrix.transformAffineVecM(fromCenter);
        Vec3M ur = rotationMatrix.transformAffineVecM(u);

        ur.minusEq(v);
        v.plusEq(orbitCenter);
        lookAt(v, orbitCenter, ur);
    }
    
    public Vec3 position() {
        return new Vec3(this.position.x(), this.position.y(), this.position.z());
    }
    
    public Vec3M positionM() {
        return new Vec3M(this.position.x(), this.position.y(), this.position.z());
    }
    
    public Vec3 up() {
        return new Vec3(this.up.x(), this.up.y(), this.up.z());
    }
    
    public Vec3M upM() {
        return new Vec3M(this.up.x(), this.up.y(), this.up.z());
    }
    
    public Vec3 front() {
        return new Vec3(this.front.x(), this.front.y(), this.front.z());
    }
    
    public Vec3M frontM() {
        return new Vec3M(this.front.x(), this.front.y(), this.front.z());
    }
    
    public Vec3 right() {
        return this.front().cross(this.up());
    }
    
    public Vec3M rightM() {
        return this.frontM().crossEq(this.up());
    }

    @Override
    public void centerBox(AABB box) {
        final Vec3 center = box.center();
        final Vec3 scale = box.scale();
        final Vec3 maxVec = box.maxVec();

        final float d = scale.y() / (float)Math.tan(0.5 * this.fovy);

        final Vec3 origin = new Vec3(center.x(), center.y(), maxVec.z() + d*1.1f);
        this.lookAt(origin, center, Vec3.E2);
    }

    public float fov() {
        return this.fovy;
    }
    
    public void setFov(float fov) {
        this.fovy = fov;
    }

    @Override
    public void zoom(float x, float y, float by) {
        setFov((float) Math.max(Math.min(fovy * Math.exp(by), Math.exp(MAX_ZOOM)), Math.exp(MIN_ZOOM)));
        Lookup.getDefault().lookup(VisualizationController.class).getVizModel().setZoomFactor(getZoom());
    }

    @Override
    public void setZoom(float relativeZoom) {
        setFov((float) Math.exp(MIN_ZOOM + relativeZoom * (MAX_ZOOM - MIN_ZOOM)));
        Lookup.getDefault().lookup(VisualizationController.class).getVizModel().setZoomFactor(relativeZoom);
    }

    @Override
    public float getZoom() {
        return (float) ((Math.log(fovy) - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM));
    }
    
}
