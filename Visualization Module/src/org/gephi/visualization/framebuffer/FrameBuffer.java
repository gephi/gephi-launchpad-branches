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

package org.gephi.visualization.framebuffer;

import javax.media.opengl.GL;

/**
 * A frame buffer is a collection of 2D images which can be used as render
 * targets.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class FrameBuffer {

    public abstract boolean init(GL gl);

    public abstract void useAsRenderTarget(GL gl);
    public abstract void useAsRenderTarget(GL gl, int[] bufs);

    public abstract void useAsTexture(GL gl, int i);

    public abstract void colorMask(boolean r, boolean g, boolean b, boolean a);
    public abstract void colorMask(int i, boolean r, boolean g, boolean b, boolean a);
    public abstract void depthMask(boolean mask);

    public abstract void clear(GL gl);
    public abstract void clear(GL gl, int[] bufs, boolean depth);
    public abstract void clearColor(float r, float g, float b, float a);
    public abstract void clearColor(int i, float r, float g, float b, float a);
    public abstract void clearDepth(float d);

    public abstract void copyTo(GL gl, FrameBuffer target);
    
    
    public static FrameBuffer create(FrameBufferProperties properties) {
        return new FBOFrameBuffer(properties);
    }


    private static final SystemFrameBuffer defaultFrameBuffer = new SystemFrameBuffer();

    public static FrameBuffer getDefault() {
        return defaultFrameBuffer;
    }
}
