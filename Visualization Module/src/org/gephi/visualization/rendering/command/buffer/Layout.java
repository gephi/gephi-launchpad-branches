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
package org.gephi.visualization.rendering.command.buffer;

import com.jogamp.common.nio.Buffers;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.media.opengl.GL;

/**
 * Layout objects controls how the graph elements are stored in a buffer.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class Layout<E> {    
    /**
     * Queries if the technique owned by this layout use a static index buffer
     * (an index buffer which do not depends on input).
     * 
     * @return <code>true</code> if the technique use a static index buffer,
     *         <code>false</code> otherwise.
     */
    public abstract boolean useStaticBuffer();
    
    /**
     * Gets the OpenGL drawing mode used by the technique owning this technique.
     * 
     * @return the OpenGL drawing mode
     */
    public abstract int glDrawMode();
    
    public abstract int glDrawType();
    
    public int glDrawTypeSize() {
        switch (this.glDrawType()) {
            case GL.GL_UNSIGNED_BYTE:
                return 1;
            case GL.GL_UNSIGNED_SHORT:
                return 2;
            case GL.GL_UNSIGNED_INT:
                return 4;
            default:
                // The following code should never be reached
                assert false;
                return 0;
        }
    }
    
    /**
     * Gets the static index buffer used by the technique owning this layout.
     * This buffer SHOULD NOT be recycled!
     * 
     * @return the startic index buffer
     */
    public abstract ByteBuffer getStaticIndexBuffer();    
    
    /**
     * Writes the data to the buffer.
     *
     * @param b the buffer where the new data is written
     * @param e the new data to be written
     * @return the number of elements written to the buffer or -1 if the
     *         the data can not be written on the buffer
     */
    public abstract int add(ByteBuffer b, E e);
    
    /**
     * Writes the data to the vertex and index buffers.
     * 
     * @param b the vertex buffer where the data is written
     * @param ind the index buffer
     * @param e the new data to be written
     * @return <code>true</code> if the data can be written on the buffer, 
     *         <code>false</code> otherwise 
     */
    public abstract boolean add(ByteBuffer b, ByteBuffer ind, E e);
    
    /**
     * Enables the client states for the vertex arrays.
     * 
     * @param gl the GL class
     */
    public abstract void enableClientStates(GL gl);
    
    /**
     * Disables the client states for the vertex arrays.
     * 
     * @param gl the GL class
     */
    public abstract void disableClientStates(GL gl);
    
    /**
     * Sets the pointers for vertex arrays for the i<sup>th</sup> pass.
     * 
     * @param gl the GL class
     * @param b the buffer used to store the data
     * @param i the current rendering pass
     */
    public abstract void setPointers(GL gl, ByteBuffer b, int i);
    
    /**
     * Sets the offsets when using VBOs for the i<sup>th</sup> pass.
     * 
     * @param gl the GL class
     * @param i the current rendering pass
     */
    public abstract void setOffsets(GL gl, int i);
}
