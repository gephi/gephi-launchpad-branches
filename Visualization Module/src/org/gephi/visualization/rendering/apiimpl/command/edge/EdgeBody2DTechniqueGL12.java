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
package org.gephi.visualization.rendering.apiimpl.command.edge;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.OrthoCamera;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.buffer.Buffer.Type;
import org.gephi.visualization.rendering.command.buffer.BufferedTechnique;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class EdgeBody2DTechniqueGL12 extends BufferedTechnique<VizEdge2D> {

    public EdgeBody2DTechniqueGL12(Type bufferType) {
        super(new EdgeBody2DLayoutGL12(), bufferType);
    }
    
    @Override
    public boolean begin(GL gl, Camera camera, RenderArea renderArea, boolean reuseResources) {        
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        
        return super.begin(gl, camera, renderArea, reuseResources);
    }

    @Override
    protected boolean setCamera(GL gl, Camera camera, RenderArea renderArea) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null || !(camera instanceof OrthoCamera)) return false;
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        
        gl2.glLoadMatrixf(camera.projMatrix(renderArea).toArray(), 0);
        
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        
        gl2.glLoadMatrixf(camera.viewMatrix(renderArea).toArray(), 0);
        
        return true;
    }
    
    @Override
    public boolean advanceToNextPass(GL gl) {
        GL2 gl2 = gl.getGL2();
        if (gl2 == null) return false;
        
        boolean result = super.advanceToNextPass(gl);
        
        if (this.currentPass != 0) return false;
        
        // nothing to set
        
        return result;
    }

    @Override
    public void end(GL gl) {
        super.end(gl);
        
        this.currentPass = -1;
    }

    @Override
    public void dispose(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
