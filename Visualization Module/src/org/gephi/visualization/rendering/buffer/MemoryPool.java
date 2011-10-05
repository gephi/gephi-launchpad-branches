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
package org.gephi.visualization.rendering.buffer;

import com.jogamp.common.nio.Buffers;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * List of buffers used in the rendering pipeline. When a new frame is passed
 * to the rendering engine, the memory linked to the old FrameData commands is
 * recycled. This class is not thread safe. This class do not give direct access
 * to the buffers because the buffers may be reused and the old content deleted.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class MemoryPool {
    
    private static final int BUFFER_SIZE = 1024 * 1024;
    
    /**
     * Lists of used buffers.
     */
    private List<ByteBuffer> usedBuffers;
    
    /**
     * Queue of allocated but unused buffers.
     */
    private Queue<ByteBuffer> unusedBuffers;
    
    /**
     * Creates an empty memory pool.
     */
    public MemoryPool() {
        this.usedBuffers = new ArrayList<ByteBuffer>();
        this.unusedBuffers = new ArrayDeque<ByteBuffer>();
    }
    
    /**
     * Reuses an unused buffer if one is available or creates a completely new 
     * buffer.
     * 
     * @return the new buffer
     */
    public ByteBuffer newBuffer() {
        ByteBuffer result = this.unusedBuffers.poll();
        if (result == null) {
            result = Buffers.newDirectByteBuffer(BUFFER_SIZE);
        }
        return result;
    }
    
    /**
     * Clears all the buffers and mark them as unused. The use of one of these
     * buffers after a call to this method is undefined behaviors.
     */
    public void recycleAll() {        
        for (ByteBuffer b : this.usedBuffers) {
            b.clear();
            this.unusedBuffers.add(b);
        }        
    }
}
