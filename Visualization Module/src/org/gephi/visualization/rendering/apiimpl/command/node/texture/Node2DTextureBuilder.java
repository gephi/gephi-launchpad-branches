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
    
    static final Map<NodeShape.Value, ProceduralTextureGenerator> generatorsMap;
    static {
        Map<NodeShape.Value, ProceduralTextureGenerator> map = new EnumMap<NodeShape.Value, ProceduralTextureGenerator>(NodeShape.Value.class);
        map.put(NodeShape.Value.CIRCLE, new CircleTextureGenerator());
        map.put(NodeShape.Value.DIAMOND, new RegularPolygonTextureGenerator(4));
        map.put(NodeShape.Value.TRIANGLE, new RegularPolygonTextureGenerator(3));
        map.put(NodeShape.Value.PENTAGON, new RegularPolygonTextureGenerator(5));
        map.put(NodeShape.Value.HEXAGON, new RegularPolygonTextureGenerator(6));
        map.put(NodeShape.Value.SQUARE, new SquareTextureGenerator());
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
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 0);
        
        ProceduralTextureGenerator generator = generatorsMap.get(shape.value);
        if (generator == null) generator = new CircleTextureGenerator();
        final ByteBuffer buffer = Buffers.newDirectByteBuffer(4 * size * size);
        generator.createFillTexture(buffer, size);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, size, size, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        
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
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 0);
        
        final ProceduralTextureGenerator generator = generatorsMap.get(shape.value);
        final ByteBuffer buffer = Buffers.newDirectByteBuffer(4 * size * size);
        generator.createBorderTexture(borderSize, buffer, size);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, size, size, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        
        return result;
    }
}
