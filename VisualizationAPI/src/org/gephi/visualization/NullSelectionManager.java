/*
Copyright 2008-2010 Gephi
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

package org.gephi.visualization;

import javax.swing.event.ChangeListener;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.selection.SelectionManager;

/**
 * Null implementation of SelectionManager used to provide a selection manager
 * which does nothing when there aren't other selection manager available.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class NullSelectionManager implements SelectionManager {

    public NullSelectionManager() {
    }

    public void addChangeListener(ChangeListener changeListener) {
    }

    public void blockSelection(boolean block) {
    }

    public void centerOnNode(Node node) {
    }

    public void disableSelection() {
    }

    public int getMouseSelectionDiameter() {
        return 0;
    }

    public boolean isBlocked() {
        return false;
    }

    public boolean isCustomSelection() {
        return false;
    }

    public boolean isDirectMouseSelection() {
        return false;
    }

    public boolean isDraggingEnabled() {
        return false;
    }

    public boolean isMouseSelectionZoomProportionnal() {
        return false;
    }

    public boolean isRectangleSelection() {
        return false;
    }

    public boolean isSelectionEnabled() {
        return false;
    }

    public boolean isSelectionUpdateWhileDragging() {
        return false;
    }

    public void removeChangeListener(ChangeListener changeListener) {
    }

    public void resetSelection() {
    }

    public void selectEdge(Edge edge) {
    }

    public void selectEdges(Edge[] edges) {
    }

    public void selectNode(Node node) {
    }

    public void selectNodes(Node[] nodes) {
    }

    public void setCustomSelection() {
    }

    public void setDirectMouseSelection() {
    }

    public void setDraggingEnable(boolean dragging) {
    }

    public void setDraggingMouseSelection() {
    }

    public void setMouseSelectionDiameter(int mouseSelectionDiameter) {
    }

    public void setMouseSelectionZoomProportionnal(boolean mouseSelectionZoomProportionnal) {
    }

    public void setRectangleSelection() {
    }

    public void setSelectionUpdateWhileDragging(boolean selectionUpdateWhileDragging) {
    }

}
