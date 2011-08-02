/*
Copyright 2008-2010 Gephi
Authors : Jeremy Subtil <jeremy.subtil@gephi.org>
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
package org.gephi.preview;

import java.util.HashMap;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.HierarchicalDirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.HierarchicalMixedGraph;
import org.gephi.graph.api.HierarchicalUndirectedGraph;
import org.gephi.preview.api.PreviewModel;
import org.openide.util.Lookup;

/**
 * Factory creating preview graphs from workspace graphs.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class PreviewGraphFactory {

    private final HashMap<Integer, NodeImpl> nodeMap = new HashMap<Integer, NodeImpl>();
    private TimeInterval timeInterval = new TimeInterval();

    /**
     * Creates a preview graph from the given undirected graph.
     *
     * @param sourceGraph   the undirected graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(PreviewModel model, HierarchicalUndirectedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl(model);

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        calculateMinMaxWeight(sourceGraph, previewGraph);

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdgesAndMetaEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            createPreviewUndirectedEdge(previewGraph, sourceEdge);
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    /**
     * Creates a preview graph from the given directed graph.
     *
     * @param sourceGraph   the directed graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(PreviewModel model, HierarchicalDirectedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl(model);

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        calculateMinMaxWeight(sourceGraph, previewGraph);

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdgesAndMetaEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            if (isBidirectional(sourceGraph, sourceEdge)) {
                createPreviewBidirectionalEdge(previewGraph, sourceEdge);
            } else {
                createPreviewUnidirectionalEdge(previewGraph, sourceEdge);
            }
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    /**
     * Creates a preview graph from the given mixed graph.
     *
     * @param sourceGraph   the mixed graph
     * @return              a generated preview graph
     */
    public GraphImpl createPreviewGraph(PreviewModel model, HierarchicalMixedGraph sourceGraph) {
        // creates graph
        GraphImpl previewGraph = new GraphImpl(model);

        // creates nodes
        for (org.gephi.graph.api.Node sourceNode : sourceGraph.getNodes()) {
            createPreviewNode(previewGraph, sourceNode);
        }

        calculateMinMaxWeight(sourceGraph, previewGraph);

        // creates edges
        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdgesAndMetaEdges()) {

            if (sourceEdge.isSelfLoop()) {
                createPreviewSelfLoop(previewGraph, sourceEdge);
                continue;
            }

            if (sourceEdge.isDirected()) {
                if (isBidirectional(sourceGraph, sourceEdge)) {
                    createPreviewBidirectionalEdge(previewGraph, sourceEdge);
                } else {
                    createPreviewUnidirectionalEdge(previewGraph, sourceEdge);
                }
            } else {
                createPreviewUndirectedEdge(previewGraph, sourceEdge);
            }
        }

        // clears the node map
        nodeMap.clear();

        return previewGraph;
    }

    private void calculateMinMaxWeight(HierarchicalGraph sourceGraph, GraphImpl previewGraph) {
        //Set time interval
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        if (dynamicController != null) {
            timeInterval = DynamicUtilities.getVisibleInterval(dynamicController.getModel(sourceGraph.getGraphModel().getWorkspace()));
        }
        if (timeInterval == null) {
            timeInterval = new TimeInterval();
        }

        //Min/Max weight
        float minWeight = Float.POSITIVE_INFINITY;
        float maxWeight = Float.NEGATIVE_INFINITY;
        float minMetaWeight = Float.POSITIVE_INFINITY;
        float maxMetaWeight = Float.NEGATIVE_INFINITY;

        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getEdges()) {
            minWeight = Math.min(minWeight, sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()));
            maxWeight = Math.max(maxWeight, sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()));
        }

        for (org.gephi.graph.api.Edge sourceEdge : sourceGraph.getMetaEdges()) {
            minMetaWeight = Math.min(minMetaWeight, sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()));
            maxMetaWeight = Math.max(maxMetaWeight, sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()));
        }
        previewGraph.setMinWeight(minWeight);
        previewGraph.setMaxWeight(maxWeight);
        previewGraph.setMinMetaWeight(minMetaWeight);
        previewGraph.setMaxMetaWeight(maxMetaWeight);
    }

    /**
     * Creates a preview node from the given source node.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceNode    the source node
     * @return              the generated preview node
     */
    private NodeImpl createPreviewNode(GraphImpl previewGraph, org.gephi.graph.api.Node sourceNode) {
        org.gephi.graph.api.NodeData sourceNodeData = sourceNode.getNodeData();
        org.gephi.graph.api.TextData sourceNodeTextData = sourceNodeData.getTextData();

        String label = sourceNodeData.getLabel();
        if (sourceNodeTextData != null && sourceNodeTextData.getText() != null && !sourceNodeTextData.getText().isEmpty() && sourceNodeTextData.isVisible()) {
            label = sourceNodeTextData.getText();
        } else if (sourceNodeTextData != null && !sourceNodeTextData.isVisible()) {
            label = null;
        }

        float labelSize = 1f;
        if (sourceNodeTextData != null) {
            labelSize = sourceNodeTextData.getSize();
        }

        if (previewGraph.getModel().getNodeSupervisor().getProportionalLabelSize()) {
            labelSize *= sourceNodeData.getRadius() / 10f;
        }

        NodeImpl previewNode = new NodeImpl(
                previewGraph,
                sourceNodeData.x(),
                -sourceNodeData.y(), // different referential from the workspace one
                sourceNodeData.getRadius(),
                label,
                labelSize,
                sourceNodeData.r(),
                sourceNodeData.g(),
                sourceNodeData.b());

        previewGraph.addNode(previewNode);

        // adds the preview node to the node map
        nodeMap.put(sourceNode.getId(), previewNode);

        return previewNode;
    }

    /**
     * Creates a preview self-loop from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview self-loop
     */
    private SelfLoopImpl createPreviewSelfLoop(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();

        SelfLoopImpl previewSelfLoop = new SelfLoopImpl(
                previewGraph,
                sourceEdge,
                sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()),
                nodeMap.get(sourceEdge.getSource().getId()));

        previewGraph.addSelfLoop(previewSelfLoop);

        return previewSelfLoop;
    }

    /**
     * Creates a preview unidirectional edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview unidirectional edge
     */
    private UnidirectionalEdgeImpl createPreviewUnidirectionalEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        String label = sourceEdgeData.getLabel();
        if (sourceEdgeTextData != null && sourceEdgeTextData.getText() != null && !sourceEdgeTextData.getText().isEmpty() && sourceEdgeTextData.isVisible()) {
            label = sourceEdgeTextData.getText();
        } else if (sourceEdgeTextData != null && !sourceEdgeTextData.isVisible()) {
            label = null;
        }

        float labelSize = 1f;
        if (sourceEdgeTextData != null) {
            labelSize = sourceEdgeTextData.getSize();
        }

        UnidirectionalEdgeImpl previewEdge = new UnidirectionalEdgeImpl(
                previewGraph,
                sourceEdge,
                sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                label,
                labelSize);

        previewGraph.addUnidirectionalEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Creates a preview bidirectional edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview bidirectional edge
     */
    private BidirectionalEdgeImpl createPreviewBidirectionalEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        String label = sourceEdgeData.getLabel();
        if (sourceEdgeTextData != null && sourceEdgeTextData.getText() != null && !sourceEdgeTextData.getText().isEmpty() && sourceEdgeTextData.isVisible()) {
            label = sourceEdgeTextData.getText();
        } else if (sourceEdgeTextData != null && !sourceEdgeTextData.isVisible()) {
            label = null;
        }

        float labelSize = 1f;
        if (sourceEdgeTextData != null) {
            labelSize = sourceEdgeTextData.getSize();
        }

        BidirectionalEdgeImpl previewEdge = new BidirectionalEdgeImpl(
                previewGraph,
                sourceEdge,
                sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                label,
                labelSize);

        previewGraph.addBidirectionalEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Creates a preview undirected edge from the given source edge.
     *
     * @param previewGraph  the parent preview graph
     * @param sourceEdge    the source edge
     * @return              the generated preview undirected edge
     */
    private UndirectedEdgeImpl createPreviewUndirectedEdge(GraphImpl previewGraph, org.gephi.graph.api.Edge sourceEdge) {
        org.gephi.graph.api.EdgeData sourceEdgeData = sourceEdge.getEdgeData();
        org.gephi.graph.api.TextData sourceEdgeTextData = sourceEdgeData.getTextData();

        String label = sourceEdgeData.getLabel();
        if (sourceEdgeTextData != null && sourceEdgeTextData.getText() != null && !sourceEdgeTextData.getText().isEmpty() && sourceEdgeTextData.isVisible()) {
            label = sourceEdgeTextData.getText();
        } else if (sourceEdgeTextData != null && !sourceEdgeTextData.isVisible()) {
            label = null;
        }

        float labelSize = 1f;
        if (sourceEdgeTextData != null) {
            labelSize = sourceEdgeTextData.getSize();
        }

        UndirectedEdgeImpl previewEdge = new UndirectedEdgeImpl(
                previewGraph,
                sourceEdge,
                sourceEdge.getWeight(timeInterval.getLow(), timeInterval.getHigh()),
                nodeMap.get(sourceEdge.getSource().getId()),
                nodeMap.get(sourceEdge.getTarget().getId()),
                label,
                labelSize);

        previewGraph.addUndirectedEdge(previewEdge);

        return previewEdge;
    }

    /**
     * Returns whether the given source edge is bidirectional or not.
     *
     * @param sourceGraph   the directed graph
     * @param sourceEdge    the source edge
     * @return              true if the source edge is bidirectional
     */
    private boolean isBidirectional(org.gephi.graph.api.DirectedGraph sourceGraph, org.gephi.graph.api.Edge sourceEdge) {
        return sourceGraph.getEdge(sourceEdge.getTarget(), sourceEdge.getSource()) != null;
    }

    /**
     * Returns whether the given source edge is bidirectional or not.
     *
     * @param sourceGraph   the mixed graph
     * @param sourceEdge    the source edge
     * @return              true if the source edge is bidirectional
     */
    private boolean isBidirectional(org.gephi.graph.api.MixedGraph sourceGraph, org.gephi.graph.api.Edge sourceEdge) {
        Edge edge=sourceGraph.getEdge(sourceEdge.getTarget(), sourceEdge.getSource());
        return edge!=null && edge.getTarget()==sourceEdge.getSource();
    }
}
