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

import java.util.List;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.math.VanDerCorputSequence;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdge3D;
import org.gephi.visualization.data.graph.styler.EdgeStyler;
import org.gephi.visualization.data.graph.styler.NodeStyler;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.data.graph.VizNode3D;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.OrthoCameraBuilder;
import org.gephi.visualization.data.camera.PerspCameraBuilder;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilders;

/**
 * Class used to create FrameData objects.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
final class FrameDataBuilder {   
    private final Impl impl;

    public FrameDataBuilder(org.gephi.visualization.api.Camera camera,
            MemoryPool memory, NodeStyler nodeStyler, EdgeStyler edgeStyler,
            CommandListBuilders builders) {
        
        if (camera instanceof Camera2d) {
            this.impl = new Impl2D((Camera2d)camera, memory, nodeStyler, edgeStyler, builders);
        } else if (camera instanceof Camera3d) {
            this.impl = new Impl3D((Camera3d)camera, memory, nodeStyler, edgeStyler, builders);
        } else {
            // It should never execute the following code
            assert false;
            this.impl = null;
        }
    }

    public void add(Node node) {
        this.impl.add(node);
    }

    public void add(Edge edge) {
        this.impl.add(edge);
    }

    public void add(UIShape shape) {
        this.impl.add(shape);        
    }

    public FrameData createFrameData() {
        return this.impl.create();
    }
    
    private abstract class Impl {
        protected final MemoryPool memory;
        
        protected final NodeStyler nodeStyler;
        protected final EdgeStyler edgeStyler;
    
        protected final CommandListBuilders builders;

        public Impl(MemoryPool memory, NodeStyler nodeStyler, EdgeStyler edgeStyler, CommandListBuilders builders) {
            this.memory = memory;
            this.nodeStyler = nodeStyler;
            this.edgeStyler = edgeStyler;
            this.builders = builders;
        }
        
        public abstract void add(Node node);
        public abstract void add(Edge edge);
        
        public final void add(UIShape shape) {
            this.builders.uiShapeBuilder.add(this.memory, shape);
        }
        
        public final FrameData create() {
            final List<Command> edgeCommands = this.getEdgeCommands();
            final List<Command> nodeCommands = this.getNodeCommands();
            final List<Command> uiCommands = this.builders.uiShapeBuilder.create();
            
            final Camera camera = this.getCamera();
            
            return new FrameData(camera, this.memory, edgeCommands, nodeCommands, uiCommands);
        }

        protected abstract List<Command> getEdgeCommands();
        protected abstract List<Command> getNodeCommands();
        protected abstract Camera getCamera();
        
    }
    
    private final class Impl2D extends Impl {
        private final OrthoCameraBuilder cameraBuilder;
        
        private int counter;

        public Impl2D(Camera2d camera, MemoryPool memory, NodeStyler nodeStyler, EdgeStyler edgeStyler, CommandListBuilders builders) {
            super(memory, nodeStyler, edgeStyler, builders);
            
            this.cameraBuilder = new OrthoCameraBuilder(camera);
            
            this.builders.begin2D();
            
            this.counter = 0;
        }
        
        @Override
        public void add(Node node) {
            final VizNode2D n = this.nodeStyler.toVisual2D(node);
            
            this.cameraBuilder.add(n);
            
            final float r = VanDerCorputSequence.get(++this.counter);
            final float z = n.size * (0.999f + r * 0.002f);
            final VizNode2D n2 = new VizNode2D(n.position, z, n.shape, n.color, n.borderColor);
            this.builders.node2DBuilder.add(this.memory, n2);            
        }

        @Override
        public void add(Edge edge) {
            final VizEdge2D e = this.edgeStyler.toVisual2D(edge);
            this.builders.edge2DBuilder.add(this.memory, e);
        }

        @Override
        protected List<Command> getEdgeCommands() {
            return this.builders.edge2DBuilder.create();
        }

        @Override
        protected List<Command> getNodeCommands() {
            return this.builders.node2DBuilder.create();
        }

        @Override
        protected Camera getCamera() {
            return this.cameraBuilder.create();
        }
    }
    
    private final class Impl3D extends Impl {
        private final PerspCameraBuilder cameraBuilder;

        public Impl3D(Camera3d camera, MemoryPool memory, NodeStyler nodeStyler, EdgeStyler edgeStyler, CommandListBuilders builders) {
            super(memory, nodeStyler, edgeStyler, builders);
            
            this.cameraBuilder = new PerspCameraBuilder(camera);
            
            this.builders.begin3D();
        }

        @Override
        public void add(Node node) {
            final VizNode3D n = this.nodeStyler.toVisual3D(node);
            
            this.cameraBuilder.add(n);

            this.builders.node3DBuilder.add(this.memory, n); 
        }

        @Override
        public void add(Edge edge) {
            final VizEdge3D e = this.edgeStyler.toVisual3D(edge);
            this.builders.edge3DBuilder.add(this.memory, e);
        }

        @Override
        protected List<Command> getEdgeCommands() {
            return this.builders.edge3DBuilder.create();
        }

        @Override
        protected List<Command> getNodeCommands() {
            return this.builders.node3DBuilder.create();
        }

        @Override
        protected Camera getCamera() {
            return this.cameraBuilder.create();
        }
    }
}