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
package org.gephi.visualization.api.selection;

import java.awt.Point;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

/**
 * Manager for handling selection queries.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */

public interface SelectionManager {

    void initialize();

    Collection<Node> getSelectedNodes();

    /**
     * Adds or removes nodes from inside the shape to a permanent selection.
     */
    void applySelection(Shape shape);

    /**
     * Adds nodes from inside the shape to a temporary selection.
     * @see #cancelContinuousSelection()
     */
    void applyContinuousSelection(Shape shape);

    /**
     * Cancels temporary selection.
     */
    void cancelContinuousSelection();

    /**
     * Clears all selected nodes.
     */
    void clearSelection();

    /**
     * Adds single node to a permanent selection.
     * @param point the point to determine the closest node
     * @param incremental true if adding to previous permanent selection
     */
    void selectSingle(Point point, boolean incremental);

    /**
     * Removes single node to a permanent selection.
     * @param point the point to determine the closest node
     */
    void removeSingle(Point point);

    void addChangeListener(ChangeListener changeListener);

    void blockSelection(boolean block);

    void disableSelection();

    int getMouseSelectionDiameter();

    public void centerOnNode(Node node);

    boolean isBlocked();

    boolean isDirectMouseSelection();

    boolean isDraggingEnabled();

    boolean isMovementEnabled();

    boolean isMouseSelectionZoomProportionnal();

    boolean isSelectionEnabled();

    boolean isSelectionUpdateWhileDragging();

    SelectionType getSelectionType();

    void removeChangeListener(ChangeListener changeListener);

    void resetSelection();

    void selectEdge(Edge edge);

    void selectEdges(Edge[] edges);

    void selectNode(Node node);

    void selectNodes(Node[] nodes);

    void setDirectMouseSelection();

    void setDraggingEnable(boolean dragging);

    void setDraggingMouseSelection();

    void setMouseSelectionDiameter(int mouseSelectionDiameter);

    void setMouseSelectionZoomProportionnal(boolean mouseSelectionZoomProportionnal);

    void setMovementEnabled(boolean enabled);

    void setSelectionUpdateWhileDragging(boolean selectionUpdateWhileDragging);

    void setSelectionType(SelectionType selectionType);
}
