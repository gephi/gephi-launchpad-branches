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

package org.gephi.visualization.view.pipeline.gl11;

import java.nio.ByteBuffer;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.layout.Layout;

/**
 * VizNodeLayout used by GL11Pipeline3D and GL11Pipeline2D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11UILayout implements Layout<UIShape, UIShape> {

    @Override
    public int suggestedBufferSize() {
        return 4096;
    }

    @Override
    public boolean add(ByteBuffer b, UIShape e) {
        final int totalSize = 4 + e.binarySize();

        if (b.remaining() < totalSize) {
            return false;
        } else {
            b.putInt(totalSize);
            e.writeTo(b);

            return true;
        }
    }

    @Override
    public UIShape get(ByteBuffer b) {
        if (b.remaining() < 4) return null;

        int totalSize = b.getInt() - 4;

        if (b.remaining() < totalSize) return null;

        final UIShape shape = UIShape.readFrom(b);

        return shape;
    }

    @Override
    public UIShape get(ByteBuffer b, int[] i) throws IndexOutOfBoundsException {
        if (!hasNext(b, i[0])) return null;

        i[0] += 4;

        final UIShape shape = UIShape.readFrom(b, i);

        return shape;
    }

    @Override
    public boolean hasNext(ByteBuffer b) {
        int i = b.position();

        return hasNext(b, i);
    }

    @Override
    public boolean hasNext(ByteBuffer b, int i) {
        if ((b.limit() - i) < 4) return false;

        final int totalSize = b.getInt(i);

        if ((b.limit() - i) < totalSize) return false;
        else return true;
    }
}
