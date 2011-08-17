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
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;
import org.gephi.visualization.data.buffer.BufferBuilder;
import org.gephi.visualization.data.buffer.Buffer;
import org.gephi.visualization.data.graph.VizEdge;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.data.layout.Layout;
import org.gephi.visualization.rendering.camera.Camera;

/**
 * Class used to create FrameData objects.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBuilder {

    private Camera camera;
    private boolean is3D;

    private boolean somethingIsSelected;

    private final BufferBuilder<Node, VizNode> nodeBufferBuilder;
    private final BufferBuilder<Edge, VizEdge> edgeBufferBuilder;
    private final BufferBuilder<UIShape, UIShape> uiBufferBuilder;

    public FrameDataBuilder(Layout<Node, VizNode> nodeLayout, Layout<Edge, VizEdge> edgeLayout, Layout<UIShape, UIShape> uiLayout) {
        this.camera = null;
        this.is3D = false;

        this.somethingIsSelected = false;

        this.nodeBufferBuilder = new BufferBuilder<Node, VizNode>(nodeLayout);
        this.edgeBufferBuilder = new BufferBuilder<Edge, VizEdge>(edgeLayout);
        this.uiBufferBuilder = new BufferBuilder<UIShape, UIShape>(uiLayout);
    }

    public FrameDataBuilder(Buffer<Node, VizNode> nodeBuffer, Buffer<Edge, VizEdge> edgeBuffer, Buffer<UIShape, UIShape> uiBuffer) {
        this.camera = null;

        this.somethingIsSelected = false;

        this.nodeBufferBuilder = new BufferBuilder<Node, VizNode>(nodeBuffer);
        this.edgeBufferBuilder = new BufferBuilder<Edge, VizEdge>(edgeBuffer);
        this.uiBufferBuilder = new BufferBuilder<UIShape, UIShape>(uiBuffer);
    }

    public void setCamera(org.gephi.visualization.api.Camera camera) {
        if (camera instanceof Camera2d) {
            this.camera = Camera.from((Camera2d)camera);
            this.is3D = false;
        } else if (camera instanceof Camera3d) {
            this.camera = Camera.from((Camera3d)camera);
            this.is3D = false;
        }
    }

    public void add(Node node) {
        if (node.getNodeData().isSelected()) this.somethingIsSelected = true;
        this.nodeBufferBuilder.add(node);
    }

    public void add(Edge edge) {
        this.edgeBufferBuilder.add(edge);
    }

    public void add(UIShape shape) {
        this.uiBufferBuilder.add(shape);
    }

    public FrameData createFrameData() {
        Buffer<Node, VizNode> nodeBuffer = this.nodeBufferBuilder.create();
        Buffer<Edge, VizEdge> edgeBuffer = this.edgeBufferBuilder.create();
        Buffer<UIShape, UIShape> uiBuffer = this.uiBufferBuilder.create();

        return new FrameData(camera, somethingIsSelected, nodeBuffer, edgeBuffer, uiBuffer);
    }
}
