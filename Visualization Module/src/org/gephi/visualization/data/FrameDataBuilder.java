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
import org.gephi.math.qrand.VanDerCorputSequence;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;
import org.gephi.visualization.data.graph.styler.EdgeStyler;
import org.gephi.visualization.data.graph.styler.NodeStyler;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.data.graph.VizNode3D;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.PerspCamera;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilders;

/**
 * Class used to create FrameData objects.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameDataBuilder {

    private final Camera camera;
    private final boolean is3D;
    
    private int counter;
    
    private float near;
    private float far;
    
    private final NodeStyler nodeStyler;
    private final EdgeStyler edgeStyler;
    
    private final CommandListBuilders builders;

    public FrameDataBuilder(org.gephi.visualization.api.Camera camera,
            NodeStyler nodeStyler, EdgeStyler edgeStyler,
            CommandListBuilders builders) {
        if (camera instanceof Camera2d) {
            this.camera = Camera.from((Camera2d)camera);
            this.is3D = false;
        } else if (camera instanceof Camera3d) {
            this.camera = Camera.from((Camera3d)camera);
            this.is3D = true;
        } else {
            // It should never execute the following code
            assert false;
            this.camera = null;
            this.is3D = false;
        }
        
        this.counter = 0;
        
        this.near = Float.POSITIVE_INFINITY;
        this.far = 0.0f;

        this.nodeStyler = nodeStyler;
        this.edgeStyler = edgeStyler;
        
        this.builders = builders;
        if (this.is3D) {
            this.builders.begin3D();
        } else {
            this.builders.begin2D();
        }
    }

    public void add(Node node) {
        if (this.is3D) {
            final VizNode3D n = this.nodeStyler.toVisual3D(node);
            this.builders.node3DBuilder.add(n);
            final PerspCamera camera3d = (PerspCamera) this.camera;
            final float distNear = camera3d.frontNeg.dot(n.position.minus(camera3d.position)) - n.size;
            if (distNear >= 0.1f && distNear < this.near) {
                this.near = distNear;
            }
            final float distFar = distNear + 2.0f * n.size;
            if (distFar > this.far) {
                this.far = distFar;
            }
        } else {
            final VizNode2D n = this.nodeStyler.toVisual2D(node);
            final float r = VanDerCorputSequence.get(++this.counter);
            final float z = (float) (n.size * (0.009 + r * 0.002));
            final VizNode2D n2 = new VizNode2D(n.position, z, n.shape, n.color, n.borderColor);
            this.builders.node2DBuilder.add(n);
            final float distNear = n.size * 0.009f;
            if (distNear < this.near) {
                this.near = distNear;
            }
            final float distFar = n.size * 1.001f;
            if (distFar > this.far) {
                this.far = distFar;
            }
        }
    }

    public void add(Edge edge) {
        if (this.is3D) {
            this.builders.edge3DBuilder.add(this.edgeStyler.toVisual3D(edge));
        } else {
            this.builders.edge2DBuilder.add(this.edgeStyler.toVisual2D(edge));
        }
    }

    public void add(UIShape shape) {
        this.builders.uiShapeBuilder.add(shape);
    }

    public FrameData createFrameData() {
        List<Command> nodeCommands, edgeCommands;
        if (this.is3D) {
            nodeCommands = this.builders.node3DBuilder.create();
            edgeCommands = this.builders.edge3DBuilder.create();
        } else {
            nodeCommands = this.builders.node2DBuilder.create();
            edgeCommands = this.builders.edge2DBuilder.create();
        }
        final List<Command> uiCommands = this.builders.uiShapeBuilder.create();

        return new FrameData(this.camera, this.near, this.far, nodeCommands, edgeCommands, uiCommands);
    }
}
