
/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.spi.nodes;

import org.gephi.datalab.spi.ContextMenuItemManipulator;
import org.gephi.datalab.spi.Manipulator;
import org.gephi.graph.api.Node;

/**
 * <p><b>Please note that the methods offered in this service are the same as Visualization API GraphContextMenuItem.
 * It is possible to reuse actions implementations by adding both <code>ServiceProvider</code> annotations.</b></p>
 * Manipulator for nodes.
 * @see Manipulator
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public interface NodesManipulator extends ContextMenuItemManipulator {
    /**
     * Prepare nodes for this action.
     * @param nodes All selected nodes to operate
     * @param clickedNode The right clicked node of all nodes
     */
    void setup(Node[] nodes, Node clickedNode);
}
