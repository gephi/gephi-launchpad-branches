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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.gephi.graph.api.Node;
import org.gephi.visualization.data.graph.NodeStyler;
import org.gephi.visualization.data.graph.VizNode2D;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Node2DCommandListBuilder implements CommandListBuilder<Node> {
    private final CommandListBuilder<VizNode2D> builder;
    private final AtomicReference<NodeStyler> styler;
    private NodeStyler currentStyler;

    public Node2DCommandListBuilder(CommandListBuilder<VizNode2D> builder, NodeStyler styler) {
        this.builder = builder;
        this.styler = new AtomicReference<NodeStyler>(styler);
        this.currentStyler = null;
    }
    
    public void setStyler(NodeStyler styler) {
        this.styler.set(styler);
    }

    @Override
    public void begin() {
        if (this.currentStyler != null) return;
        
        this.currentStyler = this.styler.get();
        this.builder.begin();
    }

    @Override
    public void add(Node e) {
        if (this.currentStyler == null) return;
        
        this.builder.add(this.currentStyler.toVisual2D(e));
    }

    @Override
    public void add(Collection<? extends Node> c) {
        for (Node n : c) {
            this.add(n);
        }
    }

    @Override
    public void add(Node[] es) {
        for (Node n : es) {
            this.add(n);
        }
    }

    @Override
    public List<Command> create() {
        if (this.currentStyler == null) return null;
        
        List<Command> result = this.builder.create();
        this.currentStyler = null;
        return result;
    }
    
}
