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
package org.gephi.visualization.rendering.apiimpl.command.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import org.gephi.graph.api.NodeShape;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.rendering.command.Command;
import org.gephi.visualization.rendering.command.CommandListBuilder;
import org.gephi.visualization.rendering.command.buffer.Buffer;
import org.gephi.visualization.rendering.command.buffer.BufferedCommandListBuilder;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Node2DCommandListBuilderGL12 implements CommandListBuilder<VizNode2D> {
    private boolean isBuilding;
    
    private final Map<NodeShape.Value, CommandListBuilder<VizNode2D>> builders;

    public Node2DCommandListBuilderGL12(GL gl, Buffer.Type type) {
        this.isBuilding = false;
        
        final Map<NodeShape.Value, CommandListBuilder<VizNode2D>> map = new EnumMap<NodeShape.Value, CommandListBuilder<VizNode2D>>(NodeShape.Value.class);
        
        for (NodeShape shape : NodeShape.specificShapes()) {
            map.put(shape.value, 
                    new BufferedCommandListBuilder<VizNode2D>(new Shape2DTechniqueGL12(gl, type, shape), type));
        }
        builders = Collections.unmodifiableMap(map);        
    }

    @Override
    public void begin() {
        if (this.isBuilding) return;
        
        for (CommandListBuilder<VizNode2D> c : builders.values()) {
            c.begin();
        }
        
        this.isBuilding = true;
    }

    @Override
    public void add(VizNode2D e) {
        CommandListBuilder<VizNode2D>  c = builders.get(e.shape.value);
        if (c != null) {
            c.add(e);
        }
    }

    @Override
    public List<Command> create() {
        if (!this.isBuilding) return null;
        
        List<Command> list = new ArrayList<Command>();
        for (CommandListBuilder<VizNode2D> c : builders.values()) {
            list.addAll(c.create());
        }
        
        this.isBuilding = false;
        
        return list;
    }

    @Override
    public void dispose(GL gl) {
       for (CommandListBuilder<VizNode2D> c : builders.values()) {
           c.dispose(gl);
       }
    }
    
}
