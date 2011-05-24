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
import org.gephi.graph.api.Node;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.data.layout.VizNodeLayout;

/**
 * Specialization of VizBuffer which can be used to retrieve nodes information.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class VizNodeBuffer extends VizBuffer<Node> {

    private Vec3f position;
    private float size;
    private Vec4f color;
    private boolean isSelected;

    VizNodeBuffer(VizNodeLayout layout, List<ByteBuffer> buffers) {
        super(layout, buffers);
    }

    public static VizNodeBuffer wrap(VizBuffer<Node> vb) {
        VizNodeLayout nodeLayout = (VizNodeLayout) vb.layout;
        VizNodeBuffer w = new VizNodeBuffer(nodeLayout, vb.buffers);
        w.currentBuffer = vb.currentBuffer;

        if (w.isEndOfBuffer()) return w;

        ByteBuffer b = w.buffers.get(w.currentBuffer);
        
        w.position = nodeLayout.position(b);
        w.size = nodeLayout.size(b);
        w.color = nodeLayout.color(b);
        w.isSelected = nodeLayout.isSelected(b);

        return w;
    }

    @Override
    public void advance() {
        super.advance();

        if (isEndOfBuffer()) return;

        ByteBuffer b = this.buffers.get(this.currentBuffer);
        VizNodeLayout nodeLayout = (VizNodeLayout) this.layout;

        this.position = nodeLayout.position(b);
        this.size = nodeLayout.size(b);
        this.color = nodeLayout.color(b);
        this.isSelected = nodeLayout.isSelected(b);
    }

    public Vec3f position() {
        return this.position;
    }

    public float size() {
        return this.size;
    }

    public Vec4f color() {
        return this.color;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
}
