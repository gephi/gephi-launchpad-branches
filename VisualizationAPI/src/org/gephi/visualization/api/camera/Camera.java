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
import java.awt.Point;
import org.gephi.lib.gleem.linalg.Mat4f;
import org.gephi.lib.gleem.linalg.Vec3f;

/**
 * Class representing a camera. Enables basic camera movement.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface Camera {

    public Camera copy();

    public void setImageSize(Dimension size);

    public void rotate(Vec3f axis, float angle);

    public void rotate(Vec3f origin, Vec3f axis, float angle);

    public void lookAt(Vec3f center, Vec3f up);

    public void lookAt(Vec3f position, Vec3f center, Vec3f up);

    public void setFov(float fov);

    public void setClipPlanes(float near, float far);

    public Vec3f frontVector();

    public Vec3f upVector();

    public Vec3f rightVector();

    public Vec3f position();

    /**
     * Return the look at point of the camera in any distance.
     */
    public Vec3f lookAtPoint();

    public float imageWidth();

    public float imageHeight();

    public float near();

    public float far();

    public float fov();

    public float projectedDistanceFrom(Vec3f point);

    /**
     * Returns the model-view matrix.
     */
    public Mat4f viewMatrix();

    /**
     * Returns the projective matrix.
     */
    public Mat4f projectiveMatrix();

    /**
     * Returns the given point as it will appear on the screen together with its
     * size on screen after transformation have been applied.
     * @return array of integers, where
     * [0,1] -> point coordinates on screen
     * [2]   -> size of the node
     */
    public int[] projectPoint(float x, float y, float z, float size);

    /**
     * Returns a point from camera viewing plane corresponding to the 2D point
     * on screen. // TODO this method will be changed to accept parameters
     * specifying how the point will be projected (e.g intersection with z=0, or
     * some distance from the camera)
     */
    public Vec3f projectPointInverse(float x, float y);

    /**
     * Returns a vector from camera viewing plane corresponding to the 2D vector
     * on screen.
     */
    public Vec3f projectVectorInverse(float x, float y);

    /**
     * Returns the distance of a point [a,b] on the screen to the projection of
     * point [x,y,z].
     */
    public int getPlanarDistance(float x, float y, float z, int a, int b);

    public void startTranslation();

    public void updateTranslation(float horizontal, float vertical);

    /**
     * Initialize orbiting around a center point.
     * @param orbitModifier must be a value between 0.0 and 1.0.
     */
    public void startOrbit(float orbitModifier);

    public void updateOrbit(float x, float y);

    public void zoom(float by);

}
