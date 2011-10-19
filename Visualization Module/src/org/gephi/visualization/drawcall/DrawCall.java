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
package org.gephi.visualization.drawcall;

import java.nio.ByteBuffer;
import org.gephi.visualization.rendering.command.Technique;

/**
 * It represents a draw call using index and vertex buffers.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class DrawCall {
    /**
     * Technique which should be used to draw execute this draw call.
     */
    public final Technique<DrawCall> technique;
    
    /**
     * Buffer containing the vertex data for this draw call.
     */
    public final ByteBuffer vertexBuffer;
    
    /**
     * Buffer containing the indexes of the vertexes to draw in this draw call.
     */
    public final ByteBuffer indexBuffer;
    
    /**
     * Position of the first index to draw.
     */
    public final int start;
    
    /**
     * Number of elements to be rendered.
     */
    public final int count;
    
    /**
     * Drawing mode, i.e. one of GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, 
     * GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES, 
     * GL_QUAD_STRIP, GL_QUADS, or GL_POLYGON.
     */
    public final int mode;
    
    /**
     * Type of the indexes, i.e. one of GL_UNSIGNED_BYTE, GL_UNSIGNED_SHORT, 
     * or GL_UNSIGNED_INT.
     */
    public final int type;

    /**
     * Creates a draw call from its public final fields.
     * 
     * @param technique the technique which should be used to execture this
     * @param vertexBuffer the vertex buffer
     * @param indexBuffer the index buffer
     * @param start the position of the first index
     * @param count the number of elements to render
     * @param mode the drawing mode
     * @param type the type of the indexes
     */
    public DrawCall(Technique<DrawCall> technique, ByteBuffer vertexBuffer, 
            ByteBuffer indexBuffer, int start, int count, int mode, int type) {
        this.technique = technique;
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.start = start;
        this.count = count;
        this.mode = mode;
        this.type = type;
    }
    
}
