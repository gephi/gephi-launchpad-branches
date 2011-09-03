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

import java.nio.ByteBuffer;
import javax.media.opengl.GL;

/**
 * Generic implementation of a Buffer. The behavior is controlled by the Layout
 * and BufferImpl instances.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Buffer<E> {
    final ByteBuffer data;
    final ByteBuffer indexBuffer;
    final Layout<E> layout;
    final BufferImpl<E> bufferImpl;
    
    int numberOfElements;
    
    boolean isLoaded;
    boolean isDrawable;
    
    public Buffer(Layout<E> layout, Type type) {
        this.data = Layout.newByteBuffer();
        this.layout = layout;
        if (this.layout.useStaticBuffer()) {
            this.indexBuffer = this.layout.getStaticIndexBuffer();
        } else {
            this.indexBuffer = Layout.newByteBuffer();
        }
        this.bufferImpl = this.createBufferImpl(type);
        
        this.numberOfElements = 0;
        
        this.isLoaded = false;
        this.isDrawable = false;
    }
    
    public final boolean add(E e) {
        if (this.layout.useStaticBuffer()) {
           final int ret = this.layout.add(this.data, e);
           if (ret < 0) {
               return false;
           } else {
               this.numberOfElements += ret;
               return true;
           }           
        } else {
            return this.layout.add(this.data, this.indexBuffer, e);
        }
    }
    
    public final void makeDrawable() {
        if (this.isDrawable) return;
        
        this.data.flip();
        if (!this.layout.useStaticBuffer()) {
            this.indexBuffer.flip();
            this.numberOfElements = this.indexBuffer.remaining() / this.layout.glDrawTypeSize();
        }
        this.isDrawable = true;
    }
    
    public void draw(GL gl, int i) {
        if (!this.isDrawable) return;
        
        this.bufferImpl.drawBuffer(gl, i);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            Layout.recycle(this.data);
            if (!this.layout.useStaticBuffer()) {
                Layout.recycle(this.indexBuffer);
            }
        } finally {
            super.finalize();
        }
    }

    BufferImpl<E> createBufferImpl(Type type) {
        switch (type) {
            case VERTEX_ARRAY:
                return new VertexArrayBuffer<E>(this);
            case VBO:
                return new VBOBuffer<E>(this);
            case VBO_VAO:
                return new VAOBuffer<E>(this);
            default:
                // the following code shouldn't be executed
                assert false;
                return null;
        }
    }
    
    public enum Type {
        VERTEX_ARRAY,
        VBO,
        VBO_VAO,
    }
}
