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
import org.gephi.graph.api.Edge;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.visualization.api.color.Color;
import org.gephi.visualization.data.layout.VizEdgeLayout;

/**
 * Specialization of VizBuffer which can be used to retrieve edges information.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class VizEdgeBuffer extends VizBuffer<Edge> {

    private Vec3f startNodePosition;
    private float startNodeSize;
    private Color startColor;

    private Vec3f endNodePosition;
    private float endNodeSize;
    private Color endColor;

    private float thickness;

    private boolean isBidirectional;
    private boolean isSelected;

    VizEdgeBuffer(VizEdgeLayout layout, List<ByteBuffer> buffers) {
        super(layout, buffers);
    }

    public static VizEdgeBuffer wrap(VizBuffer<Edge> vb) {
        VizEdgeLayout edgeLayout = (VizEdgeLayout) vb.layout;
        VizEdgeBuffer w = new VizEdgeBuffer(edgeLayout, vb.buffers);
        w.currentBuffer = vb.currentBuffer;

        if (w.isEndOfBuffer()) return w;

        ByteBuffer b = w.buffers.get(w.currentBuffer);

        w.startNodePosition = edgeLayout.startNodePosition(b);
        w.startNodeSize = edgeLayout.startNodeSize(b);
        w.startColor = edgeLayout.startColor(b);

        w.endNodePosition = edgeLayout.endNodePosition(b);
        w.endNodeSize = edgeLayout.endNodeSize(b);
        w.endColor = edgeLayout.endColor(b);

        w.thickness = edgeLayout.thickness(b);

        w.isBidirectional = edgeLayout.isBidirectional(b);
        w.isSelected = edgeLayout.isSelected(b);

        return w;
    }

    @Override
    public void advance() {
        super.advance();

        if (super.isEndOfBuffer()) return;

        ByteBuffer b = this.buffers.get(this.currentBuffer);
        VizEdgeLayout edgeLayout = (VizEdgeLayout) this.layout;

        this.startNodePosition = edgeLayout.startNodePosition(b);
        this.startNodeSize = edgeLayout.startNodeSize(b);
        this.startColor = edgeLayout.startColor(b);

        this.endNodePosition = edgeLayout.endNodePosition(b);
        this.endNodeSize = edgeLayout.endNodeSize(b);
        this.endColor = edgeLayout.endColor(b);

        this.thickness = edgeLayout.thickness(b);

        this.isBidirectional = edgeLayout.isBidirectional(b);
        this.isSelected = edgeLayout.isSelected(b);
    }

    public Vec3f startNodePosition() {
        return this.startNodePosition;
    }

    public float startNodeSize() {
        return this.startNodeSize;
    }

    public Color startColor() {
        return this.startColor;
    }

    public Vec3f endNodePosition() {
        return this.endNodePosition;
    }

    public float endNodeSize() {
        return this.endNodeSize;
    }

    public Color endColor() {
        return this.endColor;
    }

    public float thickness() {
        return this.thickness;
    }

    public boolean isBidirectional() {
        return this.isBidirectional;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

}
