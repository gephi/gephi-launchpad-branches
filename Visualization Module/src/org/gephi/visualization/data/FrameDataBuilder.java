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
import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.data.buffer.VizBufferBuilder;
import org.gephi.visualization.data.buffer.VizEdgeBuffer;
import org.gephi.visualization.data.buffer.VizNodeBuffer;
import org.gephi.visualization.data.layout.VizEdgeLayout;
import org.gephi.visualization.data.layout.VizNodeLayout;

/**
 * Class used to create FrameData objects.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBuilder {

    private Camera camera;
    private final VizBufferBuilder<Node> nodeBufferBuilder;
    private final VizBufferBuilder<Edge> edgeBufferBuilder;

    public FrameDataBuilder(VizNodeLayout nodeLayout, VizEdgeLayout edgeLayout) {
        this.camera = null;
        this.nodeBufferBuilder = new VizBufferBuilder<Node>(nodeLayout);
        this.edgeBufferBuilder = new VizBufferBuilder<Edge>(edgeLayout);
    }

    public FrameDataBuilder(VizNodeBuffer nodeBuffer, VizEdgeBuffer edgeBuffer) {
        this.camera = null;
        this.nodeBufferBuilder = new VizBufferBuilder<Node>(nodeBuffer);
        this.edgeBufferBuilder = new VizBufferBuilder<Edge>(edgeBuffer);
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

    public FrameData createFrameData() {
        VizNodeBuffer nodeBuffer = VizNodeBuffer.wrap(this.nodeBufferBuilder.createVizBuffer());
        VizEdgeBuffer edgeBuffer = VizEdgeBuffer.wrap(this.edgeBufferBuilder.createVizBuffer());

        return new FrameData(camera, nodeBuffer, edgeBuffer);
    }
}
