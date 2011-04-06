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
import javax.media.opengl.GL2GL3;

/**
 * FrameBuffer implementation which uses the default frame buffer.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class SystemFrameBuffer extends FrameBuffer {

    private boolean updateMasks;
    private final boolean[] colorMask;
    private boolean depthMask;

    private boolean updateClearValues;
    private final float[] clearColor;
    private float clearDepth;

    // TODO: set default values
    public SystemFrameBuffer() {
        this.updateMasks = true;
        this.colorMask = new boolean[] { true, true, true, true };
        this.depthMask = true;

        this.updateClearValues = true;
        this.clearColor = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
        this.clearDepth = 1.0f;
    }

    @Override
    public boolean init(GL gl) {
        gl.glEnable(GL.GL_DEPTH_TEST);

        return true;
    }

    @Override
    public void useAsRenderTarget(GL gl) {
        if (gl.isExtensionAvailable("GL_ARB_framebuffer_object")) { // FBOs are available
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
        }

        if (this.updateClearValues) {
            gl.glClearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
            gl.glClearDepthf(this.clearDepth);
        }

        if (this.updateMasks) {
            gl.glColorMask(this.colorMask[0], this.colorMask[1], this.colorMask[2], this.colorMask[3]);
            gl.glDepthMask(this.depthMask);
        }
    }

    @Override
    public void useAsRenderTarget(GL gl, int[] bufs) {
        if (bufs == null) return;

        if (bufs.length != 1 || bufs[0] != 0) {
            throw new IllegalArgumentException("The default frame buffer has one color buffer.");
        } else {
            useAsRenderTarget(gl);
        }
    }

    @Override
    public void useAsTexture(GL gl, int i) {
        throw new UnsupportedOperationException("The default frame buffer can't be used as a texture.");
    }

    @Override
    public void colorMask(boolean r, boolean g, boolean b, boolean a) {
        this.updateMasks = true;
        this.colorMask[0] = r;
        this.colorMask[0] = g;
        this.colorMask[0] = b;
        this.colorMask[0] = a;
    }

    @Override
    public void colorMask(int i, boolean r, boolean g, boolean b, boolean a) {
        if (i == 0) colorMask(r, g, b, a);
        else throw new IllegalArgumentException("The default frame buffer has one color buffer.");
    }

    @Override
    public void depthMask(boolean mask) {
        this.updateMasks = true;
        this.depthMask = mask;
    }

    @Override
    public void clear(GL gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void clear(GL gl, int[] bufs, boolean depth) {
        if (bufs == null || bufs.length == 0) {
            if (depth) gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        } else if (bufs.length != 1 || bufs[0] != 0) {
            throw new IllegalArgumentException("The default frame buffer has one color buffer.");
        } else {
            int mask = GL.GL_COLOR_BUFFER_BIT | (depth ? GL.GL_DEPTH_BUFFER_BIT : 0);
            gl.glClear(mask);
        }
    }

    @Override
    public void clearColor(float r, float g, float b, float a) {
        this.updateClearValues = true;
        this.clearColor[0] = r;
        this.clearColor[0] = g;
        this.clearColor[0] = b;
        this.clearColor[0] = a;
    }

    @Override
    public void clearColor(int i, float r, float g, float b, float a) {
        if (i == 0) clearColor(r, g, b, a);
    }

    @Override
    public void clearDepth(float d) {
        this.updateClearValues = true;
        this.clearDepth = d;
    }

    @Override
    public void copyTo(GL gl, FrameBuffer target) {
        if (gl.isExtensionAvailable("GL_ARB_framebuffer_object")) {
            GL2GL3 gl2gl3 = gl.getGL2GL3();
            gl2gl3.glBindFramebuffer(GL2GL3.GL_READ_FRAMEBUFFER, 0);
            gl2gl3.glBindFramebuffer(GL2GL3.GL_DRAW_FRAMEBUFFER, 0);
        }
    }
}
