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
package org.gephi.visualization.renderqueue;

import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.graph.VizEdge2D;
import org.gephi.visualization.data.graph.VizEdge3D;
import org.gephi.visualization.data.graph.VizNode2D;
import org.gephi.visualization.data.graph.VizNode3D;
import org.gephi.visualization.drawcall.MemoryPool;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RenderQueueFactory {
    
    public RenderQueue<VizNode2D> newNode2DRenderQueue(MemoryPool memory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public RenderQueue<VizNode3D> newNode3DRenderQueue(MemoryPool memory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public RenderQueue<VizEdge2D> newEdge2DRenderQueue(MemoryPool memory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public RenderQueue<VizEdge3D> newEdge3DRenderQueue(MemoryPool memory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public RenderQueue<UIShape> newUIRenderQueue(MemoryPool memory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
