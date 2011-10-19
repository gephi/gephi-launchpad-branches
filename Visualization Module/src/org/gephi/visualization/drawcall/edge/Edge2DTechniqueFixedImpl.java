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
package org.gephi.visualization.drawcall.edge;

import java.nio.ByteBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.RenderArea;
import org.gephi.visualization.drawcall.DrawCall;
import org.gephi.visualization.drawcall.TechniqueImpl;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class Edge2DTechniqueFixedImpl implements TechniqueImpl {

    @Override
    public boolean begin(GL gl, Camera camera, RenderArea renderArea) {
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);
        
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return false;
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        
        gl2.glLoadMatrixf(camera.projMatrix(renderArea).toArray(), 0);
        
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        
        gl2.glLoadMatrixf(camera.viewMatrix(renderArea).toArray(), 0);
        
        return true;
    }

    @Override
    public void enableClientStates(GL gl, int currentPass) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public void initPass(GL gl, int currentPass) {
        assert currentPass == 0 : currentPass;
        
        // Nothing to set..
    }

    @Override
    public void endPass(GL gl, int currentPass) {
        assert currentPass == 0 : currentPass;
        
        // Nothing to reset
    }

    @Override
    public void disableClientStates(GL gl, int currentPass) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public int numberOfPasses() {
        return 1;
    }

    @Override
    public void end(GL gl) {
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(true);
    }

    @Override
    public void dispose(GL gl) {
        // Empty block
    }

    @Override
    public void setPointers(GL gl, DrawCall e, int currentPass) {
        assert currentPass == 0 : currentPass;
        
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return;
        
        final ByteBuffer vert = e.vertexBuffer;
        final int oldPos = vert.position();
        vert.position(0);
        gl2.glColorPointer(4, GL.GL_UNSIGNED_BYTE, Edge2DDrawCallBuilder.VERTEX_SIZE, vert);
        vert.position(4);
        gl2.glVertexPointer(2, GL.GL_FLOAT, Edge2DDrawCallBuilder.VERTEX_SIZE, vert);
        vert.position(oldPos);
    }
    
}
