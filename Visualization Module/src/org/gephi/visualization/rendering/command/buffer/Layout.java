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
     * Queue of old buffer to be reused.
     */
    private static final Queue<ByteBuffer> oldBuffers;
    static {
        oldBuffers = new ConcurrentLinkedQueue<ByteBuffer>();
    }
    
    /**
     * Size of the buffer used by the engine.
     */
    private static final int BUFFER_SIZE = 1048576;
    
    /**
     * Creates a new buffer or reuses an old one. 
     * 
     * @return a ByteBuffer
     */
    public static ByteBuffer newByteBuffer() {
        ByteBuffer result = oldBuffers.poll();
        if (result == null) {
            result = Buffers.newDirectByteBuffer(BUFFER_SIZE);
        }
        return result;
    }
    
    /**
     * Inserts the buffer passed as argument in the queue of old buffers.
     * 
     * @param b the buffer to recycle
     */
    public static void recycle(ByteBuffer b) {
        b.clear();
        oldBuffers.offer(b);        
    }
    
    /**
     * Writes the data to the buffer.
     *
     * @param b the buffer where the new data is written
     * @param e the new data to be written
     * @return <code>true</code> if the data can be written on the buffer, 
     *         <code>false</code> otherwise
     */
    public abstract boolean add(ByteBuffer b, E e);
    
    /**
     * Sets the render states for the 
     * 
     * @param gl
     * @param b 
     */
    public abstract void setRenderStates(GL gl, ByteBuffer b);
}
