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
package org.gephi.visualization.rendering.command;

import javax.media.opengl.GL;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdge3D;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.data.graph.VizNode3D;

/**
 * Sets of command list builders used to create the rendering commands for a
 * graph.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class CommandListBuilders {
    
    public final CommandListBuilder<VizNode2D> node2DBuilder;
    public final CommandListBuilder<VizEdge2D> edge2DBuilder;
    public final CommandListBuilder<VizNode3D> node3DBuilder;
    public final CommandListBuilder<VizEdge3D> edge3DBuilder;
    // TODO: add labels and group command lists builders..
    public final CommandListBuilder<UIShape> uiShapeBuilder;

    private CommandListBuilders(CommandListBuilder<VizNode2D> node2DBuilder, 
            CommandListBuilder<VizEdge2D> edge2DBuilder, 
            CommandListBuilder<VizNode3D> node3DBuilder, 
            CommandListBuilder<VizEdge3D> edge3DBuilder,
            CommandListBuilder<UIShape> uiShapeBuilder) {
        this.node2DBuilder = node2DBuilder;
        this.edge2DBuilder = edge2DBuilder;
        this.node3DBuilder = node3DBuilder;
        this.edge3DBuilder = edge3DBuilder;
        this.uiShapeBuilder = uiShapeBuilder;
    }
    
    public static CommandListBuilders create(GL gl) {
        // TODO: implement method
        return null;
    }
    
    public void dispose(GL gl) {
        this.node2DBuilder.dispose(gl);
        this.node3DBuilder.dispose(gl);
        this.edge2DBuilder.dispose(gl);
        this.edge3DBuilder.dispose(gl);
        this.uiShapeBuilder.dispose(gl);
    }
}
