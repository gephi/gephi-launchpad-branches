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
 * FrameBuffer implementation which uses frame buffer objects.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class FBOFrameBuffer extends FrameBuffer {

    private final FrameBufferProperties properties;

    FBOFrameBuffer(FrameBufferProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean init(GL gl) {
        if (!gl.isExtensionAvailable("GL_ARB_framebuffer_object")) {
            return false;
        }



        return true;
    }

    @Override
    public void useAsRenderTarget(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useAsRenderTarget(GL gl, int[] bufs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useAsTexture(GL gl, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void colorMask(boolean r, boolean g, boolean b, boolean a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void colorMask(int i, boolean r, boolean g, boolean b, boolean a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void depthMask(boolean mask) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear(GL gl, int[] bufs, boolean depth) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearColor(float r, float g, float b, float a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearColor(int i, float r, float g, float b, float a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearDepth(float d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyTo(GL gl, FrameBuffer target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
