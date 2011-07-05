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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphEvent;
import org.gephi.graph.api.GraphListener;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.selection.NodeContainer;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.controller.Controller;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = SelectionManager.class)
public class SelectionManagerImpl implements SelectionManager, WorkspaceListener, GraphListener {

    private NodeContainer nodeContainer;

    private boolean blocked;

    private int mouseSelectionDiameter;
    private boolean mouseSelectionZoomProportional;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    @Override
    public void initialize() {
        Lookup.getDefault().lookup(GraphController.class).getModel().addGraphListener(this);
        Lookup.getDefault().lookup(ProjectController.class).addWorkspaceListener(this);
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        mouseSelectionDiameter = vizConfig.getMouseSelectionDiameter();
        mouseSelectionZoomProportional = vizConfig.isMouseSelectionUpdateWhileDragging();
    }

    @Override
    public Collection<Node> getSelectedNodes() {
        return nodeContainer.getSelectedNodes();
    }

    @Override
    public void applySelection(Shape shape) {
        nodeContainer.applySelection(shape);
    }

    @Override
    public void applyContinuousSelection(Shape shape) {
        nodeContainer.applyContinuousSelection(shape);
    }

    @Override
    public void cancelContinuousSelection() {
        nodeContainer.cancelContinuousSelection();
    }


    @Override
    public void clearSelection() {
        nodeContainer.clearSelection();
    }

    @Override
    public void selectSingle(Point point, boolean select) {
        nodeContainer.selectSingle(point, select, (getMouseSelectionDiameter() + 1) / 2, NodeContainer.SINGLE_NODE_DEFAULT);
    }

    @Override
    public boolean selectContinuousSingle(Point point, boolean select) {
        return nodeContainer.selectContinuousSingle(point, select, (getMouseSelectionDiameter() + 1) / 2, NodeContainer.SINGLE_NODE_DEFAULT);
    }

    @Override
    public void deselectSingle() {
        nodeContainer.deselectSingle();
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    @Override
    public void centerOnNode(Node node) {
        Controller.getDefault().centerOnNode(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z());
    }

    @Override
    public void blockSelection(boolean block) {
        // TODO find a better name for blocking selection and leaving direct selection
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        if (vizConfig.getSelectionType() != SelectionType.NONE) {
            this.blocked = block;
            vizConfig.setSelectionEnable(!block);
            fireChangeEvent();
        } else {
            setDirectMouseSelection();
        }
    }

    @Override
    public void disableSelection() {
        Lookup.getDefault().lookup(VizConfig.class).setSelectionEnable(false);
    }

    @Override
    public int getMouseSelectionDiameter() {
        return mouseSelectionDiameter;
    }

    @Override
    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public boolean isDirectMouseSelection() {
        return Lookup.getDefault().lookup(VizConfig.class).isDirectMouseSelection();
    }

    @Override
    public boolean isDraggingEnabled() {
        return Lookup.getDefault().lookup(VizConfig.class).isDraggingEnabled();
    }

    @Override
    public boolean isMouseSelectionZoomProportional() {
        return mouseSelectionZoomProportional;
    }

    @Override
    public boolean isSelectionEnabled() {
        return Lookup.getDefault().lookup(VizConfig.class).isSelectionEnabled();
    }

    @Override
    public SelectionType getSelectionType() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        return vizConfig.getSelectionType();
    }

    @Override
    public boolean isSelectionUpdateWhileDragging() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        return vizConfig.isMouseSelectionUpdateWhileDragging();
    }

    @Override
    public boolean isMovementEnabled() {
        return Lookup.getDefault().lookup(VizConfig.class).isMovementEnabled();
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
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
        node.getNodeData().setSelected(true);
    }

    @Override
    public void selectNodes(Node[] nodes) {
        for (Node node : nodes) {
            node.getNodeData().setSelected(true);
        }
    }

    @Override
    public void setDraggingEnable(boolean dragging) {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setDraggingEnable(true);
        fireChangeEvent();
    }

    @Override
    public void setSelectionType(SelectionType selectionType) {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setSelectionEnable(true);
        vizConfig.setSelectionType(selectionType);
        fireChangeEvent();
    }

    @Override
    public void setDirectMouseSelection() {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setSelectionEnable(true);
        vizConfig.setDirectMouseSelection(true);
        fireChangeEvent();
    }

    @Override
    public void setDraggingMouseSelection() {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setDraggingEnable(true);
        fireChangeEvent();
    }

    @Override
    public void setMovementEnabled(boolean enabled) {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setMovementEnabled(true);
        fireChangeEvent();
    }

    private void clearState() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setDraggingEnable(false);
        vizConfig.setMouseSelectionUpdateWhileDragging(false);
        vizConfig.setSelectionEnable(false);
        vizConfig.setSelectionType(SelectionType.NONE);
        vizConfig.setMovementEnabled(false);
        vizConfig.setDirectMouseSelection(false);
        this.blocked = false;
    }

    @Override
    public void setMouseSelectionDiameter(int mouseSelectionDiameter) {
        this.mouseSelectionDiameter = mouseSelectionDiameter;
    }

    @Override
    public void setMouseSelectionZoomProportionnal(boolean mouseSelectionZoomProportionnal) {
        this.mouseSelectionZoomProportional = mouseSelectionZoomProportionnal;
    }

    @Override
    public void setSelectionUpdateWhileDragging(boolean selectionUpdateWhileDragging) {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        vizConfig.setMouseSelectionUpdateWhileDragging(selectionUpdateWhileDragging);
    }

    private void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(evt);
        }
    }

    // Graph event
    @Override
    public void graphChanged(GraphEvent event) {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        if (nodeContainer == null) {
            nodeContainer = new Octree(graphController.getModel().getGraph());
        }
        switch (event.getEventType()) {
            case ADD_NODES:
                for (Node node : event.getData().addedNodes()) {
                    nodeContainer.addNode(node);
                }
                break;
            default:
                nodeContainer = new Octree(graphController.getModel().getGraph());
                break;
        }
    }

    // Workspace event
    @Override
    public void initialize(Workspace workspace) {
    }

    @Override
    public void select(Workspace workspace) {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        nodeContainer = workspace.getLookup().lookup(NodeContainer.class);
        if (nodeContainer == null) {
            nodeContainer = new Octree(graphController.getModel().getGraph());
        }
    }

    @Override
    public void unselect(Workspace workspace) {
    }

    @Override
    public void close(Workspace workspace) {
    }

    @Override
    public void disable() {
    }

}
