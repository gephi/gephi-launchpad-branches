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
import java.util.concurrent.ConcurrentLinkedQueue;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.Camera;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.graph.styler.EdgeStyler;
import org.gephi.visualization.data.graph.styler.NodeStyler;
import org.gephi.visualization.rendering.buffer.MemoryPool;
import org.gephi.visualization.rendering.command.CommandListBuilders;

/**
 * Class used to exchange frame data information between Model and View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBridge implements FrameDataBridgeIn, FrameDataBridgeOut {    
    private CommandListBuilders commandListBuilders;
    
    private Queue<MemoryPool> memoryPools;
    
    private NodeStyler nodeStyler;
    private EdgeStyler edgeStyler;

    private FrameDataBuilder currentBuilder;
    private FrameData newFrameData;
    private FrameData oldFrameData;

    private boolean isInizialized;
    
    public FrameDataBridge() {
        this.commandListBuilders = null;
        
        this.memoryPools = new ArrayBlockingQueue<MemoryPool>(5);
        
        this.nodeStyler = null;
        this.edgeStyler = null;
        
        this.currentBuilder = null;
        this.newFrameData = null;
        this.oldFrameData = null;

        this.isInizialized = false;
    }

    @Override
    public synchronized void beginFrame(Camera camera) {
        if (!this.isInizialized || this.currentBuilder != null) return;

        MemoryPool memory = this.memoryPools.poll();
        if (memory == null) {
            memory = new MemoryPool();
        }
        
        this.currentBuilder = new FrameDataBuilder(camera, memory, this.nodeStyler,
                this.edgeStyler, this.commandListBuilders);
    }

    @Override
    public synchronized void add(Node node) {
        if (this.currentBuilder != null) {
            this.currentBuilder.add(node);
        }
    }

    @Override
    public synchronized void add(Edge edge) {
        if (this.currentBuilder != null) {
            this.currentBuilder.add(edge);
        }
    }

    @Override
    public synchronized void add(UIShape shape) {
        if (this.currentBuilder != null) {
            this.currentBuilder.add(shape);
        }
    }

    @Override
    public synchronized void endFrame() {
        if (this.currentBuilder == null) return;

        this.newFrameData = this.currentBuilder.createFrameData();
        this.currentBuilder = null;
    }

    @Override
    public synchronized FrameData updateCurrent() {
        if (this.newFrameData == null) {
            return this.oldFrameData;
        } else {
            if (this.oldFrameData != null) {
                MemoryPool memory = this.oldFrameData.memory();
                memory.recycleAll();
                this.memoryPools.offer(memory);
            }
            
            this.oldFrameData = this.newFrameData;
            this.newFrameData = null;
            return this.oldFrameData;
        }
    }

    @Override
    public void setCommandListBuilders(CommandListBuilders builders) {
        this.commandListBuilders = builders;
        this.isInizialized = this.commandListBuilders != null && 
                this.nodeStyler != null && this.edgeStyler != null;
    }

    @Override
    public void setStylers(NodeStyler nodeStyler, EdgeStyler edgeStyler) {
        this.nodeStyler = nodeStyler;
        this.edgeStyler = edgeStyler;
        this.isInizialized = this.commandListBuilders != null && 
                this.nodeStyler != null && this.edgeStyler != null;
    }

    @Override
    public synchronized void reset() {
        this.memoryPools = new ArrayBlockingQueue<MemoryPool>(5);
        this.newFrameData = null;
        this.oldFrameData = null;
    }

}
