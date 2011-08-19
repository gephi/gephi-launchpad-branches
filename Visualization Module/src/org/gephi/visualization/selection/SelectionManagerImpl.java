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
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.VisualizationController;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.selection.NodeSpatialStructure;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.apiimpl.shape.ShapeUtils;
import org.openide.util.Lookup;

/**
 * Manager for handling selection queries.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public class SelectionManagerImpl implements SelectionManager, GraphListener {

    private NodeSpatialStructure nodeStructure;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    @Override
    public void initialize() {
        nodeStructure = new Octree();
    }

    @Override
    public synchronized Collection<Node> getSelectedNodes() {
        return nodeStructure.getSelectedNodes();
    }

    @Override
    public synchronized boolean isNodeSelected() {
        return nodeStructure.isNodeSelected();
    }
    
    @Override
    public synchronized void applySelection(Shape shape) {
        nodeStructure.applySelection(shape);
    }

    @Override
    public synchronized void applyContinuousSelection(Shape shape) {
        nodeStructure.applyContinuousSelection(shape);
    }

    @Override
    public synchronized void cancelContinuousSelection() {
        nodeStructure.clearContinuousSelection();
    }

    @Override
    public synchronized void clearSelection() {
        nodeStructure.clearSelection();
    }

    @Override
    public synchronized void selectSingle(Point point, boolean select) {
        int mouseSelectionDiameter = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getIntProperty(VizConfig.MOUSE_SELECTION_DIAMETER);
        Shape singleNodeSelectionShape = ShapeUtils.createEllipseShape(point.x - mouseSelectionDiameter, point.y - mouseSelectionDiameter, mouseSelectionDiameter, mouseSelectionDiameter);
        nodeStructure.clearContinuousSelection();
        nodeStructure.selectSingle(singleNodeSelectionShape, point, select, NodeSpatialStructure.SINGLE_NODE_CLOSEST);
    }

    @Override
    public synchronized boolean selectContinuousSingle(Point point, boolean select) {
        int mouseSelectionDiameter = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getIntProperty(VizConfig.MOUSE_SELECTION_DIAMETER);
        Shape singleNodeSelectionShape = ShapeUtils.createEllipseShape(point.x - mouseSelectionDiameter, point.y - mouseSelectionDiameter, mouseSelectionDiameter, mouseSelectionDiameter);
        return nodeStructure.selectContinuousSingle(singleNodeSelectionShape, point, select, NodeSpatialStructure.SINGLE_NODE_CLOSEST);
    }

    private void refreshDataStructure() {
        boolean use3d = Lookup.getDefault().lookup(VisualizationController.class).getVizModel().isUse3d();
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        if (use3d) {
            nodeStructure = new Octree(graphController.getModel().getGraph());
        } else {
            nodeStructure = new Quadtree(graphController.getModel().getGraph());
        }
    }

    @Override
    public synchronized void disableSelection() {
        // move to NodeSpatialStructure and clear marker
        for (Node node : nodeStructure.getSelectedNodes()) {
            node.getNodeData().setSelected(false);
        }
        nodeStructure.clearCache();
        clearState();
        fireChangeEvent();
    }

    @Override
    public Shape getNodePointerShape() {
        int mouseSelectionDiameter = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getIntProperty(VizConfig.MOUSE_SELECTION_DIAMETER);
        MotionManager motionManager = Lookup.getDefault().lookup(VisualizationController.class).getMotionManager();
        Shape singleNodeSelectionShape = ShapeUtils.createEllipseShape(motionManager.getMousePosition()[0] - mouseSelectionDiameter, 
                                                                 motionManager.getMousePosition()[1] - mouseSelectionDiameter,
                                                                 mouseSelectionDiameter, mouseSelectionDiameter);
        return (isDirectMouseSelection() || isDraggingEnabled()) && motionManager.isInside() && !motionManager.isPressing() && mouseSelectionDiameter > 1 ? singleNodeSelectionShape : null;
    }

    @Override
    public boolean isDirectMouseSelection() {
        return Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getBooleanProperty(VizConfig.DIRECT_MOUSE_SELECTION);
    }

    @Override
    public boolean isDraggingEnabled() {
        return Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getBooleanProperty(VizConfig.DRAGGING);
    }

    @Override
    public boolean isSelectionEnabled() {
        return Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getBooleanProperty(VizConfig.SELECTION);
    }

    @Override
    public boolean isNodeDraggingEnabled() {
        return Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getBooleanProperty(VizConfig.NODE_DRAGGING);
    }

    @Override
    public SelectionType getSelectionType() {
        return Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().getProperty(SelectionType.class, VizConfig.SELECTION_TYPE);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }

    @Override
    public synchronized void selectEdge(Edge edge) {
        if (edge == null) {
            return;
        }
        edge.getSource().getNodeData().setSelected(true);
        edge.getTarget().getNodeData().setSelected(true);
        nodeStructure.clearCache();
    }

    @Override
    public synchronized void selectEdges(Edge[] edges) {
        if (edges == null) {
            return;
        }
        for (Edge edge : edges) {
            edge.getSource().getNodeData().setSelected(true);
            edge.getTarget().getNodeData().setSelected(true);
        }
        nodeStructure.clearCache();
    }

    @Override
    public synchronized void selectNode(Node node) {
        if (node == null) {
            return;
        }
        nodeStructure.clearSelection();
        node.getNodeData().setSelected(true);
        nodeStructure.clearCache();
    }

    @Override
    public synchronized void selectNodes(Node[] nodes) {
        if (nodes == null) {
            return;
        }
        nodeStructure.clearSelection();
        for (Node node : nodes) {
            node.getNodeData().setSelected(true);
        }
        nodeStructure.clearCache();
    }

    @Override
    public void setDraggingEnabled(boolean dragging) {
        clearState();
        Lookup.getDefault().lookup(VisualizationController.class).getVizConfig().setProperty(VizConfig.DRAGGING, true);
        fireChangeEvent();
    }

    @Override
    public void setSelectionType(SelectionType selectionType) {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
        vizConfig.setProperty(VizConfig.SELECTION, true);
        vizConfig.setProperty(VizConfig.SELECTION_TYPE, selectionType);
        fireChangeEvent();
    }

    @Override
    public void setDirectMouseSelection() {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
        vizConfig.setProperty(VizConfig.SELECTION, true);
        vizConfig.setProperty(VizConfig.DIRECT_MOUSE_SELECTION, true);
        fireChangeEvent();
    }

    @Override
    public void setNodeDraggingEnabled() {
        clearState();
        VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
        vizConfig.setProperty(VizConfig.SELECTION, true);
        vizConfig.setProperty(VizConfig.NODE_DRAGGING, true);
        fireChangeEvent();
    }

    private void clearState() {
        VizConfig vizConfig = Lookup.getDefault().lookup(VisualizationController.class).getVizConfig();
        vizConfig.setProperty(VizConfig.SELECTION, false);
        vizConfig.setProperty(VizConfig.DRAGGING, false);
        vizConfig.setProperty(VizConfig.NODE_DRAGGING, false);
        vizConfig.setProperty(VizConfig.DIRECT_MOUSE_SELECTION, false);
        vizConfig.setProperty(VizConfig.SELECTION_TYPE, SelectionType.NONE);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
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
        if (nodeStructure == null) {
            refreshDataStructure();
        }
        switch (event.getEventType()) {
            case ADD_NODES:
                for (Node node : event.getData().addedNodes()) {
                    nodeStructure.addNode(node);
                }
                break;
            case REMOVE_NODES:
            case REMOVE_EDGES:
                refreshDataStructure();
                break;
        }
    }

    @Override
    public void refresh() {
        Lookup.getDefault().lookup(GraphController.class).getModel().addGraphListener(this);
        refreshDataStructure();
    }

}
