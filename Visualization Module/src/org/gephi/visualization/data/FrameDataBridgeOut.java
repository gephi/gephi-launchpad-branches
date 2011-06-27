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

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.data.graph.VizEdge;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.data.layout.Layout;

/**
 * Interface used by View to retrieve frame data from Model.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface FrameDataBridgeOut {

    public FrameData updateCurrent();

    public void setNodeLayout(Layout<Node, VizNode> layout);
    public void setEdgeLayout(Layout<Edge, VizEdge> layout);
    public void setUILayout(Layout<UIShape, UIShape> layout);
    
}
