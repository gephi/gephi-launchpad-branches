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

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.graph.VizEdge;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.data.layout.Layout;

/**
 * Class used to exchange frame data information between Model and View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBridge implements FrameDataBridgeIn, FrameDataBridgeOut {

    private Layout<Node, VizNode> nodeLayout;
    private Layout<Edge, VizEdge> edgeLayout;
    private Layout<UIShape, UIShape> uiLayout;

    private final Queue<FrameDataBuilder> waitingQueue;
    private FrameDataBuilder currentBuilder;
    private FrameData newFrameData;
    private FrameData oldFrameData;

    private final Object lock;

    private boolean isUpdating;
    
    public FrameDataBridge() {
        this.lock = new Object();

        this.nodeLayout = null;
        this.edgeLayout = null;

        this.waitingQueue = new ArrayBlockingQueue<FrameDataBuilder>(5);

        this.currentBuilder = null;
        this.newFrameData = null;
        this.oldFrameData = null;

        this.isUpdating = false;
    }

    @Override
    public void beginFrame(Camera camera) {
        if (this.isUpdating) return;

        this.isUpdating = true;

        this.currentBuilder = this.waitingQueue.poll();

        if (this.currentBuilder == null) {
            this.currentBuilder = new FrameDataBuilder(this.nodeLayout, this.edgeLayout, this.uiLayout);
        }

        this.currentBuilder.setCamera(camera);
    }

    @Override
    public void add(Node node) {
        if (this.isUpdating) {
            this.currentBuilder.add(node);
        }
    }

    @Override
    public void add(Edge edge) {
        if (this.isUpdating) {
            this.currentBuilder.add(edge);
        }
    }

    @Override
    public void add(UIShape shape) {
        if (this.isUpdating) {
            this.currentBuilder.add(shape);
        }
    }

    @Override
    public void endFrame() {
        if (!this.isUpdating) return;

        synchronized(lock) {
            if (this.newFrameData != null &&
                    this.newFrameData.nodeLayout() == this.nodeLayout &&
                    this.newFrameData.edgeLayout() == this.edgeLayout) {
                FrameDataBuilder newBuilder = new FrameDataBuilder(this.newFrameData.nodeBuffer(), this.newFrameData.edgeBuffer(), this.newFrameData.uiBuffer());
                this.waitingQueue.offer(newBuilder);
            }
            this.newFrameData = this.currentBuilder.createFrameData();
            this.currentBuilder = null;
        }

        this.isUpdating = false;
    }

    @Override
    public void setNodeLayout(Layout<Node, VizNode> layout) {
        synchronized(this.lock) {
            this.nodeLayout = layout;
        }
    }

    @Override
    public void setEdgeLayout(Layout<Edge, VizEdge> layout) {
        synchronized(this.lock) {
            this.edgeLayout = layout;
        }
    }

    @Override
    public void setUILayout(Layout<UIShape, UIShape> layout) {
        synchronized(this.lock) {
            this.uiLayout = layout;
        }
    }

    @Override
    public FrameData updateCurrent() {
        synchronized(this.lock) {
            if (this.newFrameData == null) {
                if (this.oldFrameData != null &&
                    this.oldFrameData.nodeLayout() == this.nodeLayout &&
                    this.oldFrameData.edgeLayout() == this.edgeLayout)
                    return this.oldFrameData;
                else
                    return null;
            } else if (this.newFrameData.nodeLayout() == this.nodeLayout &&
                    this.newFrameData.edgeLayout() == this.edgeLayout) {
                if (this.oldFrameData != null &&
                    this.oldFrameData.nodeLayout() == this.nodeLayout &&
                    this.oldFrameData.edgeLayout() == this.edgeLayout) {
                    FrameDataBuilder newBuilder = new FrameDataBuilder(this.oldFrameData.nodeBuffer(), this.oldFrameData.edgeBuffer(), this.oldFrameData.uiBuffer());
                    this.waitingQueue.offer(newBuilder);
                }

                this.oldFrameData = this.newFrameData;
                this.newFrameData = null;

                return this.oldFrameData;
            } else {
                return null;
            }
        }
    }

}
