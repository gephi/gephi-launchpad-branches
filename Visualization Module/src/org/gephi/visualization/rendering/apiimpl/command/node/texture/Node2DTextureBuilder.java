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
package org.gephi.visualization.rendering.apiimpl.command.node.texture;

import com.jogamp.common.nio.Buffers;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.graph.api.NodeShape;

/**
 * Generates textures for node shapes. It assumes the current context support 
 * for OpenGL 1.2 features. 
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Node2DTextureBuilder {
    private Node2DTextureBuilder() {} // Class with only static methods
    
    static final Map<NodeShape, ProceduralTextureGenerator> generatorsMap;
    static {
        Map<NodeShape, ProceduralTextureGenerator> map = new EnumMap<NodeShape, ProceduralTextureGenerator>(NodeShape.class);
        map.put(NodeShape.CIRCLE, new CircleTextureGenerator());
        map.put(NodeShape.DIAMOND, new RegularPolygonTextureGenerator(4));
        map.put(NodeShape.TRIANGLE, new RegularPolygonTextureGenerator(4));
        map.put(NodeShape.PENTAGON, new RegularPolygonTextureGenerator(4));
        map.put(NodeShape.HEXAGON, new RegularPolygonTextureGenerator(4));
        map.put(NodeShape.SQUARE, new SquareTextureGenerator());
        generatorsMap = Collections.unmodifiableMap(map);        
    }
    
    public static int createFillTexture(GL gl, NodeShape shape, int size) {
        int result;
        {
            final IntBuffer handle = Buffers.newDirectIntBuffer(1);
            gl.glGenTextures(1, handle);
            result = handle.get(0);
        }
                
        gl.glBindTexture(GL.GL_TEXTURE_2D, result);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        
        for (int s = size, i = 0; size > 0; size /= 2, ++i) {
            final ByteBuffer buffer = Buffers.newDirectByteBuffer(size * size);
            ProceduralTextureGenerator generator = generatorsMap.get(shape);
            generator.createFillTexture(buffer, s);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, i, GL2.GL_INTENSITY8, s, s, 0, GL2.GL_INTENSITY, GL.GL_UNSIGNED_BYTE, buffer);
        }
        
        return result;
    }
    
    public static int createBolderTexture(GL gl, NodeShape shape, int size, float borderSize) {
        int result;
        {
            final IntBuffer handle = Buffers.newDirectIntBuffer(1);
            gl.glGenTextures(1, handle);
            result = handle.get(0);
        }
                
        gl.glBindTexture(GL.GL_TEXTURE_2D, result);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        
        for (int s = size, i = 0; size > 0; size /= 2, ++i) {
            final ByteBuffer buffer = Buffers.newDirectByteBuffer(size * size);
            ProceduralTextureGenerator generator = generatorsMap.get(shape);
            generator.createBorderTexture(borderSize, buffer, s);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, i, GL2.GL_INTENSITY8, s, s, 0, GL2.GL_INTENSITY, GL.GL_UNSIGNED_BYTE, buffer);
        }
        
        return result;
    }
}
