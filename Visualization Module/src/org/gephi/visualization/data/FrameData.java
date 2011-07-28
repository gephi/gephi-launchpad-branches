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
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.buffer.Buffer;
import org.gephi.visualization.data.graph.VizEdge;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.data.layout.Layout;

/**
 * Class used to get the current graph data in View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameData {

    private final Camera camera;

    private final boolean somethingIsSelected;

    private final Buffer<Node, VizNode> nodeBuffer;
    private final Buffer<Edge, VizEdge> edgeBuffer;
    private final Buffer<UIShape, UIShape> uiBuffer;
    
    

    FrameData(Camera camera, boolean somethingIsSelected, Buffer<Node, VizNode> nodeBuffer, Buffer<Edge, VizEdge> edgeBuffer, Buffer<UIShape, UIShape> uiBuffer) {
        this.camera = camera;
        this.somethingIsSelected = somethingIsSelected;
        this.nodeBuffer = nodeBuffer;
        this.edgeBuffer = edgeBuffer;
        this.uiBuffer = uiBuffer;
    }

    public Camera camera() {
        return this.camera;
    }

    public boolean somethingIsSelected() {
        return this.somethingIsSelected;
    }

    public Buffer<Node, VizNode> nodeBuffer() {
        return this.nodeBuffer;
    }

    public Layout<Node, VizNode> nodeLayout() {
        return this.nodeBuffer.layout();
    }

    public Buffer<Edge, VizEdge> edgeBuffer() {
        return this.edgeBuffer;
    }

    public Layout<Edge, VizEdge> edgeLayout() {
        return this.edgeBuffer.layout();
    }

    public Buffer<UIShape, UIShape> uiBuffer() {
        return this.uiBuffer;
    }

    public Layout<UIShape, UIShape> uiLayout() {
        return this.uiBuffer.layout();
    }
}
