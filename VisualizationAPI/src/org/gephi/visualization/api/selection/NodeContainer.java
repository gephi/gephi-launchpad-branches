/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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

package org.gephi.visualization.api.selection;

import java.awt.Point;
import java.util.List;
import org.gephi.graph.api.Node;

/**
 * Interface for data structures containing nodes and allowing effective 
 * selection algorithms.
 *
 * @author Vojtech Bardiovsky
 */
public interface NodeContainer {

    /**
     * Select any node among all nodes within radius.
     */
    public static final int SINGLE_NODE_DEFAULT = 0;

    /**
     * Select the largest node among all nodes within radius.
     */
    public static final int SINGLE_NODE_LARGEST = 1;
    
    /**
     * Select the closes node among all nodes within radius.
     */
    public static final int SINGLE_NODE_CLOSEST = 2;

    public void rebuild();

    /**
     * Add nodes from given shape to selection.
     * @return nodes added to selection that were not selected before.
     */
    public List<Node> addToSelection(Shape shape);

    public void removeFromSelection(Shape shape);

    public List<Node> getSelectedNodes();

    /**
     * Select single node closest to the given point.
     * @param selectionRadius nodes outside the radius will be ignored.
     * @param policy determines how to pick the closest node.
     */
    public void selectSingle(Point point, int selectionRadius, int policy);

    public void removeSingle(Point point);

    public void clearSelection();

    public void addNode(Node node);

    public void removeNode(Node node);

}
