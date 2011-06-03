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
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.data.layout.VizNodeLayout;

/**
 * VizNodeLayout used by GL11Pipeline3D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class GL11NodesLayout3D implements VizNodeLayout {

    @Override
    public int suggestedBlockSize() {
        return 4194304;
    }

    static public int numberOfNodes(ByteBuffer buffer) {
        return buffer.remaining()/28;
    }

    @Override
    public boolean add(ByteBuffer buffer, Node node) {
        if (buffer.remaining() < 28) {
            return false;
        } else {
            buffer.putFloat(node.getNodeData().x());
            buffer.putFloat(node.getNodeData().y());
            buffer.putFloat(node.getNodeData().z());
            buffer.putFloat(node.getNodeData().getSize());
            buffer.putFloat(node.getNodeData().r());
            buffer.putFloat(node.getNodeData().g());
            buffer.putFloat(node.getNodeData().b());
            return true;
        }
    }

    @Override
    public Vec3f position(ByteBuffer b) {
        int i = b.position();
        float x = b.getFloat(i);
        float y = b.getFloat(i+4);
        float z = b.getFloat(i+8);
        return new Vec3f(x, y, z);
    }

    @Override
    public float size(ByteBuffer b) {
        return b.getFloat(b.position() + 12);
    }

    @Override
    public Vec4f color(ByteBuffer b) {
        int i = b.position();
        float R = b.getFloat(i+16);
        float G = b.getFloat(i+20);
        float B = b.getFloat(i+24);
        return new Vec4f(R, G, B, 1.0f);
    }

    @Override
    public boolean isSelected(ByteBuffer b) {
        return false;
    }

    @Override
    public boolean advance(ByteBuffer b) {
        if (b.remaining() > 56) {
            b.position(b.position() + 28);
            return true;
        } else {
            return false;
        }
    }
}
