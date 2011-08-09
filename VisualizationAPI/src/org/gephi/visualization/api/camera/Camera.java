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
    
    /**
     * Creates a new copy of this camera.
     * 
     * @return the copy
     */
    public Camera copy();

    /**
     * Sets the screen size. It updates the scale factor to show the same
     * vertical region of the graph.
     * 
     * @param size the screen size
     */
    public void screenSize(Dimension size);
    
    /**
     * Returns the screen width.
     * 
     * @return the screen width
     */
    public float screenWidth();

    /**
     * Returns the screen height.
     * 
     * @return the screen height
     */
    public float screenHeight();

    /**
     * Moves the center of the screen to <code>center</code>. It ignores the
     * 3<sup>rd</sup> components of the <code>center</code> vector and the
     * <code>up</code> completely.
     * 
     * @param center the new center of the screen. The third component is not
     *               used
     * @param up it is ignored by this method
     */
    public void lookAt(Vec3Base center, Vec3Base up);

    /**
     * Returns the given point as it will appear on the screen together with its
     * size on screen after transformation have been applied.
     * 
     * @param x first component of the point in world coordinates
     * @param y second component of the point in world coordinates
     * @param z ignored by this method
     * @param size the size of the object on screen
     * @return array of floats, where <br />
     * [0,1] -> point coordinates on screen <br />
     * [2]   -> size of the node on screen
     */
    public float[] projectPoint(float x, float y, float z, float size);

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen.
     */
    public Vec3 projectPointInverse(float x, float y);

    /**
     * Returns the 3D vector which corresponds to the translation on the screen
     * by <code>(x,y)</code>. In this case the 
     * 
     * @param x the first component of the vector in screen coordinates
     * @param y the second component of the vector in screen coordinates
     * @return the translation vector in world coordinate
     */
    public Vec3 projectVectorInverse(float x, float y);

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
    public float getPlanarDistance(float x, float y, float z, int a, int b);

    /**
     * Moves the camera by the screen vector <code>(dx, dy)</code>
     * 
     * @param dx the first component of the translation vector on the screen
     * @param dy the second component of the translation vector on the screen
     */
    public void translate(float dx, float dy);

    /**
     * Starts the orbiting function.
     * 
     * @param orbitModifier ignored by this method
     */
    public void startOrbit(float orbitModifier);

    /**
     * Updates the camera during orbiting by horizontal and vertical difference.
     * 
     * @param x horizontal difference
     * @param y vertical difference
     */
    public void updateOrbit(float x, float y);

    /**
     * Centers the box on the screen and updates the scale factor to see the
     * entire box.
     * 
     * @param box the box
     */
    public void centerBox(AABB box);

    /**
     * Increases the scale factor by some amount.
     * 
     * @param by the increased amount
     */
    public void zoom(float x, float y, float by);

    /**
     * Gets the current scale factor.
     * 
     * @return the scale factor
     */
    public float getZoom();

    /**
     * Sets the current scale factor.
     * 
     * @param relativeZoom the scale factor
     */
    public void setZoom(float relativeZoom);
            
}
