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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilder;
import org.gephi.visualization.rendering.command.GenericCommand;

/**
 * Command list builder for buffered techniques.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class BufferedCommandListBuilder<E> implements CommandListBuilder<E> {
    private boolean isBuilding;
    private final BufferedTechnique<E> technique;
    private List<Buffer<E>> buffers;
    private final Buffer.Type bufferType;

    public BufferedCommandListBuilder(BufferedTechnique<E> technique, Buffer.Type bufferType) {
        this.isBuilding = false;
        this.technique = technique;
        this.buffers = null;
        this.bufferType = bufferType;
    }

    @Override
    public void begin() {
        if (this.isBuilding) return;
        
        this.buffers = new ArrayList<Buffer<E>>();
        this.isBuilding = true;
    }

    @Override
    public void add(E e) {
        if (!this.isBuilding) return;
        
        if (this.buffers.isEmpty()) {
            this.buffers.add(new Buffer<E>(this.technique.layout(), this.bufferType));
        }
        
        final Buffer<E> current = this.buffers.get(this.buffers.size() - 1);
        
        if (!current.add(e)) {
            current.makeDrawable();
            final Buffer<E> newBuffer = new Buffer<E>(this.technique.layout(), this.bufferType);
            newBuffer.add(e);
            this.buffers.add(newBuffer);            
        }
    }

    @Override
    public List<Command> create() {
        if (!this.isBuilding) return null;
        
        /* makes the last buffer drawable */
        if (!this.buffers.isEmpty()) {
            this.buffers.get(this.buffers.size() - 1).makeDrawable();
        }
        
        Command command = new GenericCommand<Buffer<E>>(this.buffers, this.technique);
        this.buffers = null;
        this.isBuilding = false;
        return Collections.singletonList(command);
    }

    @Override
    public void dispose(GL gl) {
        /* EMPTY BLOCK */
    }
    
}
