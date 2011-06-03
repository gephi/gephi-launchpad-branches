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
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.selection.NodeContainer;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.Shape;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = SelectionManager.class)
public class SelectionManagerImpl implements SelectionManager {

    private NodeContainer nodeContainer;
    private Collection<Node> selectedNodes;

    private boolean blocked;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public void setGraph(Graph graph) {
        nodeContainer = new Octree(graph);
        selectedNodes = new ArrayList<Node>();
    }

    @Override
    public Collection<Node> getSelectedNodes() {
        return selectedNodes;
    }

    @Override
    public void addSelection(Shape shape) {
        nodeContainer.addToSelection(shape);
    }

    @Override
    public void removeSelection(Shape shape) {
        nodeContainer.removeFromSelection(shape);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
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
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        return vizConfig.isSelectionEnabled() && !vizConfig.isRectangleSelection() && !vizConfig.isDraggingEnabled();
    }

    @Override
    public boolean isDraggingEnabled() {
        return Lookup.getDefault().lookup(VizConfig.class).isDraggingEnabled();
    }

    @Override
    public boolean isMouseSelectionZoomProportionnal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRectangleSelection() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        return vizConfig.isSelectionEnabled() && vizConfig.isRectangleSelection();
    }

    @Override
    public boolean isSelectionEnabled() {
        return Lookup.getDefault().lookup(VizConfig.class).isSelectionEnabled();
    }

    @Override
    public boolean isSelectionUpdateWhileDragging() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
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
    public void setDraggingEnable(boolean dragging) {
        Lookup.getDefault().lookup(VizConfig.class).setMouseSelectionUpdateWhileDragging(!dragging);
        fireChangeEvent();
    }

    @Override
    public void setRectangleSelection() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setRectangleSelection(true);
        vizConfig.setDraggingEnable(false);
        vizConfig.setCustomSelection(false);
        vizConfig.setSelectionEnable(true);
        this.blocked = false;
        fireChangeEvent();
    }

    @Override
    public void setDirectMouseSelection() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setRectangleSelection(false);
        vizConfig.setSelectionEnable(true);
        vizConfig.setDraggingEnable(false);
        vizConfig.setCustomSelection(false);
        this.blocked = false;
        fireChangeEvent();
    }

    @Override
    public void setDraggingMouseSelection() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setRectangleSelection(false);
        vizConfig.setDraggingEnable(true);
        vizConfig.setMouseSelectionUpdateWhileDragging(false);
        vizConfig.setSelectionEnable(true);
        vizConfig.setCustomSelection(false);
        this.blocked = false;
        fireChangeEvent();
    }

    @Override
    public void setCustomSelection() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setSelectionEnable(false);
        vizConfig.setDraggingEnable(false);
        vizConfig.setCustomSelection(true);
        //this.blocked = true;
        fireChangeEvent();
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
    public void setSelectionUpdateWhileDragging(boolean selectionUpdateWhileDragging) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(evt);
        }
    }

}
