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
package org.gephi.visualization.rendering.apiimpl.command.node;

import com.jogamp.common.nio.Buffers;
import java.nio.IntBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.graph.api.NodeShape;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.rendering.apiimpl.command.node.texture.Node2DTextureBuilder;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.command.Technique;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Shape2DTechniqueGL12 implements Technique<VizNode2D> {
    private final int fillTex;
    private final int borderTex;
    private final float texBorderSize;
    private boolean initialized;
    private int currentPass;
    
    public Shape2DTechniqueGL12(GL gl, NodeShape shape) {
        int size;
        {
            final IntBuffer buffer = Buffers.newDirectIntBuffer(1); 
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, buffer);
            size = buffer.get(0) > 512 ? 512 : buffer.get(0);
        }
        this.texBorderSize = 2.0f / size;
        
        this.fillTex = Node2DTextureBuilder.createFillTexture(gl, shape, size);
        this.borderTex = Node2DTextureBuilder.createBolderTexture(gl, shape, size, 0.2f);
        
        this.initialized = false;
        this.currentPass = -1;
    }

    @Override
    public void begin(GL gl, Camera camera) {
        /*
        GL2 gl2 = gl.getGL2();
        if (gl2 == null || !(camera instanceof Camera2d)) return;
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        
        float[] matrix = new float[16];
        camera.projectiveMatrix().getColumnMajorData(matrix);
        gl2.glLoadMatrixf(matrix, 0);
        
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        
        camera.viewMatrix().getColumnMajorData(matrix);
        gl2.glLoadMatrixf(matrix, 0);
        
        this.initialized = true;
         * */
         
    }

    @Override
    public void setCurrentPass(GL gl, int i) {
        if (!this.initialized) return;
        
        GL2 gl2 = gl.getGL2();
        if (gl2 == null || this.currentPass != (i-1)) return;
        
        switch(i) {
            case 0:
                setPass0(gl2);
                break;
            case 1:
                setPass1(gl2);
                break;
            default:
                return;
        }        
    }

    @Override
    public int numberOfPasses() {
        return 2;
    }

    @Override
    public void draw(GL gl, VizNode2D e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void end(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose(GL gl) {
        IntBuffer textures = Buffers.newDirectIntBuffer(2);
        textures.put(this.fillTex);
        textures.put(this.borderTex);
        textures.rewind();
        gl.glDeleteTextures(2, textures);
    }

    private void setPass0(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.fillTex);
        
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        
        gl.glEnable(GL2.GL_ALPHA_TEST);
        
        gl.glAlphaFunc(GL2.GL_GREATER, 0.5f);
    }

    private void setPass1(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.borderTex);
        
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        
        gl.glDisable(GL2.GL_ALPHA_TEST);
        gl.glEnable(GL2.GL_BLEND);
        
        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL2.GL_FUNC_ADD);
    }
    
}
