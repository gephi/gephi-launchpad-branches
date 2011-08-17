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
import org.gephi.math.linalg.Vec2;
import org.gephi.math.linalg.Vec2Base;
import org.gephi.math.linalg.Vec2M;
import org.gephi.math.linalg.Vec3;
import org.gephi.math.linalg.Vec3Base;
import org.gephi.visualization.api.Camera;
import org.gephi.visualization.api.vizmodel.GraphLimits;

/**
 * Camera for 2D mode. It only supports translation and zoom.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public final class Camera2d implements Camera {
    
    protected final static float MAX_SCALE = 5.0f;
    protected final static float SCALE_FACTOR = 1.01f;
    
    /**
     * Camera position in 2D. It corresponds to the center of the screen.
     */
    private final Vec2M center;
    
    /**
     * Camera scale factor. It is the scalar which should be multiplied to a
     * vector in world coordinates to get the screen coordinates.
     */
    private float scale;

    /**
     * Width of the screen in pixels.
     */
    private float screenWidth;
            
    /**
     * Height of the screen in pixels.
     */
    private float screenHeight;

    /**
     * Creates a camera centered at the origin with a 1x1 screen.
     */
    public Camera2d() {
        this(1, 1);
    }
    
    /**
     * Creates a camera centered at the origin with a specific screen dimension
     * and scale factor equal to one.
     * 
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public Camera2d(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        this.center = new Vec2M();
        this.scale = 1.0f;
    }
    
    /**
     * Creates a camera centered at <code>center</code> and with scale factor
     * <code>scale</code> with a screen dimension of <code>width * height</code>
     * 
     * @param width the width of the screen
     * @param height the height of the screen
     * @param center the center of the screen in world coordinates
     * @param scale the scale factor
     */
    public Camera2d(int width, int height, Vec2Base center, float scale) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        this.center = center.copyM();
        this.scale = scale;
    }
    
    /**
     * Creates a new camera from another 2D camera.
     * 
     * @param camera the other camera
     */
    public Camera2d(Camera2d camera) {
        this((int)camera.screenWidth, (int)camera.screenHeight, camera.center, camera.scale);
    }
    
    /**
     * Creates a 2D camera from a 3D one.
     * 
     * @param camera the 3D camera
     */
    public Camera2d(Camera3d camera) {
        this.screenWidth = camera.screenWidth();
        this.screenHeight = camera.screenHeight();
        
        // TODO: find better values
        this.center = new Vec2M();
        this.scale = 1.0f;
    }
    
    /**
     * Creates a new copy of this camera.
     * 
     * @return the copy
     */
    @Override
    public Camera copy() {
        return new Camera2d(this);
    }

    /**
     * Sets the screen size. It updates the scale factor to show the same
     * vertical region of the graph.
     * 
     * @param size the screen size
     */
    @Override
    public void screenSize(Dimension size) {
        this.screenWidth = size.width;
        this.scale = ((float)size.height * this.scale) / this.screenHeight;
        this.screenHeight = size.height;
    }
    
    /**
     * Returns the screen width.
     * 
     * @return the screen width
     */
    @Override
    public float screenWidth() {
        return this.screenWidth;
    }

    /**
     * Returns the screen height.
     * 
     * @return the screen height
     */
    @Override
    public float screenHeight() {
        return this.screenHeight;
    }

    /**
     * Moves the center of the screen to <code>center</code>. It ignores the
     * 3<sup>rd</sup> components of the <code>center</code> vector and the
     * <code>up</code> completely.
     * 
     * @param center the new center of the screen. The third component is not
     *               used
     * @param up it is ignored by this method
     */
    @Override
    public void lookAt(Vec3Base center, Vec3Base up) {
        this.center.set(center.x(), center.y());        
    }
    
    /**
     * Returns the position of the camera.
     */
    @Override
    public float[] getPosition() {
        return new float[] {center.x(), center.y(), 0};
    }

    /**
     * NOT SUPPORTED
     * 
     * Returns a point from the camera look-at line at a normalized distance 
     * from camera position.
     * 
     * @return zero vector as the look at vector is defined from camera position.
     */
    @Override
    public float[] getLookAt() {
        return new float[] {0f, 0f, 0f};
    }
    
    /**
     * Returns the given point as it will appear on the screen together with its
     * size on screen after transformation have been applied.
     * 
     * @param x first component of the point in world coordinates
     * @param y second component of the point in world coordinates
     * @param z ignored by this method
     * @param size the size of the object in world coordinates
     * @return array of floats, where <br />
     * [0,1] -> point coordinates on screen <br />
     * [2]   -> size of the node on screen
     */
    @Override
    public float[] projectPoint(float x, float y, float z, float size) {        
        final float xs = 0.5f * this.screenWidth + (x - this.center.x()) * scale;
        final float ys = 0.5f * this.screenHeight - (y - this.center.y()) * scale;
        
        return new float[]{xs, ys, size*scale};
    }

    /**
     * Returns a 3D point corresponding to a 2D point on the screen. In this 
     * case the third component of the 3D point is simply zero and the other
     * are the 2D world coordinates of the point.
     * 
     * @param x the first component of the point on screen
     * @param y the second component of the point on the screen
     * @return the 3D point
     */
    @Override
    public Vec3 projectPointInverse(float x, float y) {
        final float invScale = 1.0f / scale;
        return new Vec3(this.center.x() + (x - this.screenWidth * 0.5f) * invScale, 
                this.center.y() - (y - this.screenHeight * 0.5f) * invScale, 0.0f);
    }

    /**
     * Returns the 3D vector which correponds to the translation on the screen
     * by <code>(x,y)</code>. In this case the 
     * 
     * @param x the first component of the vector in screen coordinates
     * @param y the second component of the vector in screen coordinates
     * @return the translation vector in world coordinate
     */
    @Override
    public Vec3 projectVectorInverse(float x, float y) {
        final float invScale = 1.0f / scale;
        return new Vec3(x * invScale, - y * invScale, 0.0f);
    }

    /**
     * Gets the distance on the screen between a point in world coordinates
     * and a point in screen coordinates.
     * 
     * @param x the first component of the point in world coordinates
     * @param y the second component of the point in world coordinates
     * @param z ignored by this method
     * @param a the first component of the point in screen coordinates
     * @param b the second component of the point in screen coordinates
     * @return the distance between the two points on the screen
     */
    @Override
    public float getPlanarDistance(float x, float y, float z, int a, int b) {
        final float[] pr = this.projectPoint(x, y, z, 0.0f);
        
        return (float)Math.sqrt(pr[0]*pr[0] + pr[1]*pr[1]);
    }

    /**
     * Moves the camera by the screen vector <code>(dx, dy)</code>
     * 
     * @param dx the first component of the translation vector on the screen
     * @param dy the second component of the translation vector on the screen
     */
    @Override
    public void translate(float dx, float dy) {
        final float invScale = 1.0f / this.scale;
        final Vec2 v = new Vec2(- dx * invScale, dy * invScale);
        this.center.plusEq(v);
    }

    /**
     * NOT SUPPORTED
     * 
     * @param orbitModifier ignored by this method
     */
    @Override
    public void startOrbit(float orbitModifier) { /* NOT SUPPORTED */ }

    /**
     * NOT SUPPORTED
     * 
     * @param x ignored by this method
     * @param y ignored by this method
     */
    @Override
    public void updateOrbit(float x, float y) { /* NOT SUPPORTED */ }

    /**
     * Returns the center of the camera in world coordinates.
     * 
     * @return the center of the screen
     */
    public Vec2 center() {
        return this.center.copy();        
    }
    
    /**
     * The height of the rendered area in world coordinates.
     * 
     * @return the height of the rendered area
     */
    public float height() {
        return this.screenHeight / this.scale;
    }

    /**
     * Centers the graph on the screen and updates the scale factor or the 
     * position of the camera to see the entire graph.
     * 
     * @param graphLimits the graph limits
     */
    @Override
    public void centerGraph(GraphLimits graphLimits) {
        this.center.set((graphLimits.getMaxX() + graphLimits.getMinX()) / 2, (graphLimits.getMaxY() + graphLimits.getMinY()) / 2);
        this.scale = Math.min(this.screenWidth / (graphLimits.getMaxX() - graphLimits.getMinX()), this.screenHeight / (graphLimits.getMaxY() - graphLimits.getMinY()));
    }

    /**
     * Zooms toward a point on the screen.
     * 
     * @param x the first component of the point to zoom to
     * @param y the second component of the point to zoom to
     * @param by the zoom amount
     */
    @Override
    public void zoom(float x, float y, float by) {
        final float newScale = Math.min(MAX_SCALE, this.scale / (float) Math.pow(SCALE_FACTOR, by));
        final Vec2M p = new Vec2M(x - this.screenWidth * 0.5f, this.screenHeight * 0.5f - y);
        final float s = 0.008f / scale * newScale;
        p.timesEq(-by * s);
        this.center.plusEq(p);
        this.scale = newScale;
    }

    /**
     * Zooms toward the center of screen.
     * @param by the zoom amount
     */
    @Override
    public void zoom(float by) {
        zoom(screenWidth * 0.5f, screenHeight * 0.5f, by);
    }
    
}
