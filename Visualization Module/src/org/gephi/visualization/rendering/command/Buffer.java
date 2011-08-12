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
package org.gephi.visualization.rendering.command;

import java.nio.ByteBuffer;
import javax.media.opengl.GL;

/**
 * Generic implementation of a Buffer, the behaviour of the class (the use of 
 * VBOs or vertex arrays or VAOs or the layout of the data in the buffer) is 
 * controlled by the Layout class.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Buffer<T> {
    private int resourceID;
    private final ByteBuffer data;
    private final Layout<T> layout;

    public Buffer(ByteBuffer data, Layout<T> layout) {
        this.resourceID = 0;
        this.data = data;
        this.layout = layout;
    }
    
    public void draw(GL gl) {
        // TODO: implement it
    }
}
