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
import java.util.Collection;
import org.gephi.graph.api.Node;

/**
 * Interface for data structures containing nodes and allowing effective 
 * selection algorithms.
 *
 * @author Vojtech Bardiovsky
 */
public interface NodeSpatialStructure {

    /**
     * Select first node found among all nodes within radius.
     */
    public static final int SINGLE_NODE_FIRST = 0;

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
     * Add or remove nodes from given shape to selection.
     * @return nodes added to selection that were not selected before.
     */
    public Collection<Node> applySelection(Shape shape);

    /**
     * Adds nodes from inside the shape to a temporary selection.
     * @see #cancelContinuousSelection()
     */
    public void applyContinuousSelection(Shape shape);

    /**
     * Cancels temporary selection.
     */
    public void clearContinuousSelection();
    
    /**
     * Returns every selected node.
     */
    public Collection<Node> getSelectedNodes();

    /**
     * Returns <code>true</code> if any node is selected.
     */
    public boolean isNodeSelected();
    
    /**
     * Adds or removes single node from a permanent selection.
     * @param shape the shape to determine the selected node.
     * @param point the position of the mouse to determine the closest node in
     * case of the CLOSEST policy.
     * @param select true to select, false to deselect.
     * @return nodes added to selection that were not selected before (there may
     * be cases when more than one node is selected, such as with auto-select
     * neighbors function on).
     */
    public Collection<Node> selectSingle(Shape shape, Point point, final boolean select, final int policy);

    /**
     * Adds or removes single node from a temporary selection.
     * @param shape the shape to determine the selected node.
     * @param point the position of the mouse to determine the closest node in
     * case of the CLOSEST policy.
     * @param select true to select, false to deselect.
     * @return true if node has been selected.
     */
    public boolean selectContinuousSingle(Shape shape, Point point, final boolean select, final int policy);

    public void clearSelection();

    public void addNode(Node node);

    public void removeNode(Node node);

    /**
     * Called when selection has changed outside the spatial structure to clear
     * cached results.
     */
    public void clearCache();

    /**
     * Informs the data structure about a running layout for optimization.
     */
    public void setLayoutRunning(boolean running);

}
