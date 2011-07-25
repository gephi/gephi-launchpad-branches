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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.media.opengl.GL;
import org.gephi.visualization.data.layout.Layout;

/**
 * Class used to store the current frame data.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Buffer<I, O> implements Iterable<O> {
    final Layout<I, O> layout;
    final List<ByteBuffer> buffers;

    Buffer(Layout<I, O> layout, List<ByteBuffer> buffers) {
        this.layout = layout;
        this.buffers = buffers;
    }

    public final Layout<I, O> layout() {
        return this.layout;
    }

    public final int buffersNumber() {
        return this.buffers.size();
    }

    public final void bufferSubData(GL gl, int target, int offset, int i) {
        if (i >= this.buffers.size()) return;

        final ByteBuffer b = this.buffers.get(i);
        gl.glBufferSubData(target, 0, b.remaining(), b);
    }

    @Override
    public Iterator<O> iterator() {
        return new BufferIterator(this);
    }

    private final class BufferIterator implements Iterator<O> {
        private final Buffer<I, O> buffer;
        private final int[] position;

        private int currentBuffer;

        public BufferIterator(Buffer<I, O> buffer) {
            this.buffer = buffer;
            this.position = new int[]{0};
            this.currentBuffer = 0;
        }

        @Override
        public boolean hasNext() {
            final List<ByteBuffer> buffers = this.buffer.buffers;

            if (this.currentBuffer >= buffers.size()) return false;

            final ByteBuffer b = buffers.get(this.currentBuffer);

            return this.currentBuffer < (buffers.size() - 1) || this.buffer.layout.hasNext(b, this.position[0]);
        }

        @Override
        public O next() {
            if (!this.hasNext()) throw new NoSuchElementException("No more elements to get from the Buffer.");

            final List<ByteBuffer> buffers = this.buffer.buffers;
            final Layout<I, O> layout = this.buffer.layout;

            ByteBuffer b = buffers.get(this.currentBuffer);
            O e = layout.get(b, this.position);
            if (e == null) {
                ++this.currentBuffer;
                this.position[0] = 0;

                b = buffers.get(this.currentBuffer);
                e = layout.get(b, this.position);
            }

            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unable to remove an element from an immutable buffer.");
        }

    }
}
