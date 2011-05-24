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

package org.gephi.visualization.data.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.data.layout.VizLayout;

/**
 * Class used to store the current frame data.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class VizBuffer<T> {

    protected final VizLayout<T> layout;
    protected final List<ByteBuffer> buffers;
    protected int currentBuffer;

    VizBuffer(VizLayout<T> layout, List<ByteBuffer> buffers) {
        this.layout = layout;
        this.buffers = buffers;
        this.currentBuffer = 0;
    }

    public final boolean isEndOfBuffer() {
        return this.currentBuffer >= this.buffers.size();
    }

    public final VizLayout<T> layout() {
        return this.layout;
    }

    public void advance() {
        if (isEndOfBuffer()) return;

        ByteBuffer b = this.buffers.get(this.currentBuffer);
        if (!this.layout.advance(b)) {
            this.advanceBuffer();
        }
    }

    public void advanceBuffer() {
        ++this.currentBuffer;
    }

    public void loadBuffer(GL gl, int target) {
        if (isEndOfBuffer()) return;

        ByteBuffer b = this.buffers.get(this.currentBuffer);
        gl.glBufferSubData(target, 0, b.remaining(), b);
    }

    public void rewind() {
        for (int i = 0; i <= this.currentBuffer; ++i) {
            this.buffers.get(i).rewind();
        }

        this.currentBuffer = 0;
    }
}
