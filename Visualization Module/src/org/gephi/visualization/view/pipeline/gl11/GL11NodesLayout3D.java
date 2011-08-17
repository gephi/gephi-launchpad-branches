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
import org.gephi.graph.api.Node;
import org.gephi.math.linalg.Vec3;
import org.gephi.visualization.api.Color;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.data.layout.Layout;

/**
 * NodeLayout used by GL11Pipeline3D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class GL11NodesLayout3D implements Layout<Node, VizNode> {

    private final static int nodeSize = 29;

    @Override
    public int suggestedBufferSize() {
        return 4 * 1024 * 1024;
    }

    @Override
    public boolean add(ByteBuffer buffer, Node node) {
        if (buffer.remaining() < nodeSize) {
            return false;
        } else {
            buffer.putFloat(node.getNodeData().x());
            buffer.putFloat(node.getNodeData().y());
            buffer.putFloat(node.getNodeData().z());
            buffer.putFloat(node.getNodeData().getSize());
            buffer.putFloat(node.getNodeData().r());
            buffer.putFloat(node.getNodeData().g());
            buffer.putFloat(node.getNodeData().b());
            buffer.put(node.getNodeData().isSelected() ? (byte) 1 : 0);
            return true;
        }
    }

    @Override
    public VizNode get(ByteBuffer b) {
        if (hasNext(b)) {
            final Vec3 position = Vec3.readFrom(b);
            final float size = b.getFloat();
            final Color color = Color.readNoAlphaFrom(b);
            final boolean selected = b.get() != 0;
            return new VizNode(position, size, color, selected);
        } else {
            return null;
        }
    }

    @Override
    public VizNode get(ByteBuffer b, int[] i) throws IndexOutOfBoundsException {
        if (hasNext(b, i[0])) {
            final Vec3 position = Vec3.readFrom(b, i);
            final float size = b.getFloat(i[0]); i[0] += 4;
            final Color color = Color.readNoAlphaFrom(b, i);
            final boolean selected = b.get(i[0]) != 0; ++i[0];
            return new VizNode(position, size, color, selected);
        } else {
            return null;
        }
    }

    @Override
    public boolean hasNext(ByteBuffer b) {
        return b.remaining() >= nodeSize;
    }

    @Override
    public boolean hasNext(ByteBuffer b, int i) {
        return (b.limit() - i) >= nodeSize;
    }
}
