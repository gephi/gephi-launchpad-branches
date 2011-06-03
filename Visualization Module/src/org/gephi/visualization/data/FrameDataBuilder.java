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

package org.gephi.visualization.data;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.data.buffer.VizBufferBuilder;
import org.gephi.visualization.data.buffer.VizEdgeBuffer;
import org.gephi.visualization.data.buffer.VizNodeBuffer;
import org.gephi.visualization.data.buffer.VizUIBuffer;
import org.gephi.visualization.data.layout.VizEdgeLayout;
import org.gephi.visualization.data.layout.VizNodeLayout;
import org.gephi.visualization.data.layout.VizUILayout;
import org.gephi.visualization.utils.Pair;
import org.gephi.visualization.view.ui.UIStyle;

/**
 * Class used to create FrameData objects.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBuilder {

    private Camera camera;
    private final VizBufferBuilder<Node> nodeBufferBuilder;
    private final VizBufferBuilder<Edge> edgeBufferBuilder;
    private final VizBufferBuilder<Pair<Shape, UIStyle>> uiBufferBuilder;

    public FrameDataBuilder(VizNodeLayout nodeLayout, VizEdgeLayout edgeLayout, VizUILayout uiLayout) {
        this.camera = null;
        this.nodeBufferBuilder = new VizBufferBuilder<Node>(nodeLayout);
        this.edgeBufferBuilder = new VizBufferBuilder<Edge>(edgeLayout);
        this.uiBufferBuilder = new VizBufferBuilder<Pair<Shape, UIStyle>>(uiLayout);
    }

    public FrameDataBuilder(VizNodeBuffer nodeBuffer, VizEdgeBuffer edgeBuffer, VizUIBuffer uiBuffer) {
        this.camera = null;
        this.nodeBufferBuilder = new VizBufferBuilder<Node>(nodeBuffer);
        this.edgeBufferBuilder = new VizBufferBuilder<Edge>(edgeBuffer);
        this.uiBufferBuilder = new VizBufferBuilder<Pair<Shape, UIStyle>>(uiBuffer);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void add(Node node) {
        this.nodeBufferBuilder.add(node);
    }

    public void add(Edge edge) {
        this.edgeBufferBuilder.add(edge);
    }

    public void add(Shape shape, UIStyle style) {
        this.uiBufferBuilder.add(Pair.of(shape, style));
    }

    public FrameData createFrameData() {
        VizNodeBuffer nodeBuffer = VizNodeBuffer.wrap(this.nodeBufferBuilder.createVizBuffer());
        VizEdgeBuffer edgeBuffer = VizEdgeBuffer.wrap(this.edgeBufferBuilder.createVizBuffer());
        VizUIBuffer uiBuffer = VizUIBuffer.wrap(this.uiBufferBuilder.createVizBuffer());

        return new FrameData(camera, nodeBuffer, edgeBuffer, uiBuffer);
    }
}
