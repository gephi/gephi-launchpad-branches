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
package org.gephi.visualization.rendering.apiimpl.command.edge;

import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilder;
import org.gephi.visualization.rendering.command.buffer.Buffer;
import org.gephi.visualization.rendering.command.buffer.BufferedCommandListBuilder;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class EdgeCommandListBuilderGL12 implements CommandListBuilder<VizEdge2D> {
    private boolean isBuilding;
    
    private final CommandListBuilder<VizEdge2D> edgeListBuilder;

    public EdgeCommandListBuilderGL12(Buffer.Type type) {
        this.isBuilding = false;
        
        this.edgeListBuilder = new BufferedCommandListBuilder<VizEdge2D>(new EdgeBody2DTechniqueGL12(type), type);
    }

    @Override
    public void begin() {
        if (this.isBuilding) return;
        
        this.isBuilding = true;
        
        this.edgeListBuilder.begin();
    }

    @Override
    public void add(MemoryPool memory, VizEdge2D e) {
        if (!this.isBuilding) return;
        
        // TODO: edge loops
        this.edgeListBuilder.add(memory, e);
    }

    @Override
    public List<Command> create() {
        if (!this.isBuilding) return null;
        
        this.isBuilding = false;
        
        List<Command> list = this.edgeListBuilder.create();
        // TODO: edge loops..
        
        return list;
    }

    @Override
    public void dispose(GL gl) {
        this.edgeListBuilder.dispose(gl);
    }
    
}
