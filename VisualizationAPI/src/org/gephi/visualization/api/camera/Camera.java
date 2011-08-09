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

package org.gephi.visualization.api.camera;

import java.awt.Dimension;
import org.gephi.math.linalg.Vec3;
import org.gephi.math.linalg.Vec3Base;
import org.gephi.visualization.api.geometry.AABB;

/**
 * Class representing a camera. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface Camera {

    public Camera copy();
    
    
    public void screenSize(Dimension size);
    
    public float screenWidth();

    public float screenHeight();


    public void lookAt(Vec3Base center, Vec3Base up);

    /**
     * Returns the given point as it will appear on the screen together with its
     * size on screen after transformation have been applied.
     * @return array of floats, where
     * [0,1] -> point coordinates on screen
     * [2]   -> size of the node on screen
     */
    public float[] projectPoint(float x, float y, float z, float size);

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen. // TODO this method will be changed to accept parameters
     * specifying how the point will be projected (e.g intersection with z=0, or
     * some distance from the camera)
     */
    public Vec3 projectPointInverse(float x, float y);

    /**
     * Returns the translation vector which corresponds to the (x,y) screen
     * vector.
     */
    public Vec3 projectVectorInverse(float x, float y);

    /**
     * Returns the distance of a point [a,b] on the screen to the projection of
     * point [x,y,z].
     */
    public float getPlanarDistance(float x, float y, float z, int a, int b);

    /**
     * 
     * @param dx
     * @param dy 
     */
    public void translate(float dx, float dy);

    /**
     * Initialize orbiting around a center point.
     * @param orbitModifier must be a value between 0.0 and 1.0.
     */
    public void startOrbit(float orbitModifier);

    public void updateOrbit(float x, float y);

    public void zoom(float x, float y, float by);

    /**
     * Returns the relative camera zoom.
     */
    public float getZoom();

    /**
     * Sets the relative camera zoom.
     * @param relativeZoom float from interval [0.0, 1.0]
     */
    public void setZoom(float relativeZoom);
    
    /**
     * Centers the box on the screen.
     * 
     * @param box the box to center
     */
    public void centerBox(AABB box);
}
