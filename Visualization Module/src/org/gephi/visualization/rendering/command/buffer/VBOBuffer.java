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

import javax.media.opengl.GL;

/**
 * VBO based buffer implementation.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class VBOBuffer<E> extends BufferImpl<E> {

    public VBOBuffer(Buffer<E> buffer) {
        super(buffer);
    }
    
    @Override
    public void drawBuffer(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
