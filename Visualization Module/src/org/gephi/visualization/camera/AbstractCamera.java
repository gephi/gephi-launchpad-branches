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

import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.util.Lookup;

/**
 * Base class for camera implementations.
 *
 * @author Vojtech Bardiovsky
 */
abstract class AbstractCamera implements Camera {

    protected boolean recomputeMatrix = true;

    protected float fovy;
    protected float relativeZoom;

    protected static final float MAX_ZOOM = 1.5f;
    protected static final float MIN_ZOOM = -4.0f;

    @Override
    public void setFov(float fov) {
        this.fovy = fov;
        requireRecomputeMatrix();
    }

    @Override
    public void zoom(float by) {
        setFov((float) Math.max(Math.min(fovy * Math.exp(by), Math.exp(MAX_ZOOM)), Math.exp(MIN_ZOOM)));
        Lookup.getDefault().lookup(VizModel.class).setCameraDistance(getZoom());
    }

    @Override
    public void setZoom(float relativeZoom) {
        setFov((float) Math.exp(MIN_ZOOM + relativeZoom * (MAX_ZOOM - MIN_ZOOM)));
        Lookup.getDefault().lookup(VizModel.class).setCameraDistance(relativeZoom);
    }

    @Override
    public float getZoom() {
        return (float) ((Math.log(fovy) - MIN_ZOOM) / (MAX_ZOOM - MIN_ZOOM));
    }

    protected void requireRecomputeMatrix() {
        recomputeMatrix = true;
    }
}
