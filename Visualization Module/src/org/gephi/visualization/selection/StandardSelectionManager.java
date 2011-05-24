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

package org.gephi.visualization.selection;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.selection.NodeContainer;
import org.gephi.visualization.api.selection.SelectionManager;

public class StandardSelectionManager implements SelectionManager {

    private NodeContainer nodeContainer;
    private Collection<Node> selectedNodes;

    public void setGraph(Graph graph) {
        nodeContainer = new Octree(graph);
        selectedNodes = new ArrayList<Node>();
    }

    @Override
    public Collection<Node> getSelectedNodes() {
        return selectedNodes;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void blockSelection(boolean block) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void centerOnNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void disableSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMouseSelectionDiameter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isBlocked() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCustomSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDirectMouseSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDraggingEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMouseSelectionZoomProportionnal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRectangleSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSelectionEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSelectionUpdateWhileDragging() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectEdge(Edge edge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectEdges(Edge[] edges) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void selectNodes(Node[] nodes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCustomSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDirectMouseSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDraggingEnable(boolean dragging) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDraggingMouseSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMouseSelectionDiameter(int mouseSelectionDiameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMouseSelectionZoomProportionnal(boolean mouseSelectionZoomProportionnal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRectangleSelection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSelectionUpdateWhileDragging(boolean selectionUpdateWhileDragging) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
