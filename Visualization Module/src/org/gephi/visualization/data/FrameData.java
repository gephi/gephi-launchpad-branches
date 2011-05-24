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

import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.data.buffer.VizEdgeBuffer;
import org.gephi.visualization.data.buffer.VizNodeBuffer;
import org.gephi.visualization.data.layout.VizEdgeLayout;
import org.gephi.visualization.data.layout.VizNodeLayout;

/**
 * Class used to get the current graph data in View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameData {

    private final Camera camera;
    private final VizNodeBuffer nodeBuffer;
    private final VizEdgeBuffer edgeBuffer;

    FrameData(Camera camera, VizNodeBuffer nodeBuffer, VizEdgeBuffer edgeBuffer) {
        this.camera = camera;
        this.nodeBuffer = nodeBuffer;
        this.edgeBuffer = edgeBuffer;
    }

    public Camera camera() {
        return this.camera;
    }

    public VizNodeBuffer nodeBuffer() {
        return this.nodeBuffer;
    }

    public VizNodeLayout nodeLayout() {
        return (VizNodeLayout) this.nodeBuffer.layout();
    }

    public VizEdgeBuffer edgeBuffer() {
        return this.edgeBuffer;
    }

    public VizEdgeLayout edgeLayout() {
        return (VizEdgeLayout) this.edgeBuffer.layout();
    }
}
