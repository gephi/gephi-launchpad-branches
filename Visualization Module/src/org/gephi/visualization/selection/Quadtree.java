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
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.NodeIterator;
import org.gephi.graph.spi.SpatialData;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.selection.NodeSpatialStructure;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.selection.Shape.Intersection;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.util.Lookup;

/**
 * @author Vojtech Bardiovsky
 */
public final class Quadtree implements NodeSpatialStructure {

    private Quadrant root;

    private final Collection<Node> unassignedNodes;

    private boolean singleFound;

    private Collection<Node> selectedNodes;

    private Collection<Node> temporarySelectedNodes;

    private boolean temporarySelectionMod;

    private boolean changeMarker;

    private float maxNodeSize;

    private final Graph graph;

    float xmin, xmax, ymin, ymax;

    private final static int MAX_DEPTH = 15;

    public Quadtree(Graph graph) {
        this.graph = graph;
        this.unassignedNodes = new ArrayList<Node>();
        this.selectedNodes = new ArrayList<Node>();
        rebuild();
    }

    @Override
    public void rebuild() {
        // TODO make MAKE_NODES changeable and make it smaller when rebuilding,
        // it will be faster
        xmin = ymin = Float.MAX_VALUE;
        xmax = ymax = Float.MIN_VALUE;
        NodeIterator iterator = graph.getNodes().iterator();
        while (iterator.hasNext()) {
            NodeData nd = iterator.next().getNodeData();
            if (nd.x() < xmin) {
                xmin = nd.x();
            }
            if (nd.y() < ymin) {
                ymin = nd.y();
            }
            if (nd.x() > xmax) {
                xmax = nd.x();
            }
            if (nd.y() > ymax) {
                ymax = nd.y();
            }
            if (nd.getSize() > maxNodeSize) {
                maxNodeSize = nd.getSize();
            }
        }
        root = new Quadrant(null, xmin, ymin, Math.max(xmax - xmin, ymax - ymin), 0);
        iterator = graph.getNodes().iterator();
        while (iterator.hasNext()) {
            root.addNode(iterator.next());
        }
    }

    @Override
    public Collection<Node> getSelectedNodes() {
        // Return last selected nodes collection
        if (!changeMarker) {
            return selectedNodes;
        }

        selectedNodes = new ArrayList<Node>();
        recursiveGetSelectedNodes(root, selectedNodes);

        for (Node node : unassignedNodes) {
            if (node.getNodeData().isSelected()) {
                selectedNodes.add(node);
            }
        }
        changeMarker = false;

        return selectedNodes;
    }

    @Override
    public void applyContinuousSelection(Shape shape) {
        temporarySelectedNodes = applySelection(shape);
        temporarySelectionMod = shape.getSelectionModifier().isPositive();
    }

    @Override
    public void clearContinuousSelection() {
        if (temporarySelectedNodes == null) {
            return;
        }
        for (Node node : temporarySelectedNodes) {
            node.getNodeData().setSelected(!temporarySelectionMod);
        }
        temporarySelectedNodes = null;
        changeMarker = true;
    }

    @Override
    public List<Node> applySelection(final Shape shape) {
        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        final List<Node> nodes = new ArrayList<Node>();
        final boolean autoSelectNeighbor = Lookup.getDefault().lookup(VizModel.class).isAutoSelectNeighbor();

        final boolean select = shape.getSelectionModifier().isPositive();

        NodeFunction nodeFunction = new NodeFunction() {
            @Override
            public void apply(Node node) {
                if (shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), node.getNodeData().getSize(), camera)) {
                    if (select != node.getNodeData().isSelected()) {
                        node.getNodeData().setSelected(select);
                        nodes.add(node);
                        changeMarker = true;
                    }
                    // If auto-selection is on and the selection is positive (adding nodes)
                    if (select && autoSelectNeighbor) {
                        for (Node neighbor : graph.getNeighbors(node)) {
                            if (!neighbor.getNodeData().isSelected()) {
                                neighbor.getNodeData().setSelected(true);
                                nodes.add(neighbor);
                                changeMarker = true;
                            }
                        }
                    }
                }
            }
        };
        recursiveAddNodes(root, shape, false, nodeFunction);
        recursiveAddUnassignedNodes(nodeFunction);
        return nodes;
    }

    private void recursiveGetSelectedNodes(Quadrant quadrant, Collection<Node> list) {
        if (quadrant.hasChildren()) {
            for (Quadrant child : quadrant.getChildren()) {
                if (child != null) {
                    recursiveGetSelectedNodes(child, list);
                }
            }
        } else {
            for (Node node : quadrant.getNodes()) {
                if (node.getNodeData().isSelected()) {
                    list.add(node);
                }
            }
        }
    }

    private void recursiveAddNodes(Quadrant quadrant, Shape shape, boolean singleOnly, NodeFunction nodeFunction) {
        if (singleOnly && singleFound) {
            return;
        }

        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        Intersection intersection = shape.intersectsCube(quadrant.getX(), quadrant.getY(), 0, quadrant.getSize(), maxNodeSize, camera);

        switch (intersection) {
            case OUTSIDE:
                return;
            case INTERSECT:
                if (quadrant.hasChildren()) {
                    for (Quadrant child : quadrant.getChildren()) {
                        if (child != null) {
                            recursiveAddNodes(child, shape, singleOnly, nodeFunction);
                        }
                    }
                } else {
                    quadrant.applyFunction(nodeFunction);
                }
                break;
            case FULLY_INSIDE:
                quadrant.applyFunction(nodeFunction);
                break;
        }
    }

    private void recursiveAddUnassignedNodes(NodeFunction nodeFunction) {
        for (Node node : unassignedNodes) {
            nodeFunction.apply(node);
        }
    }

    @Override
    public Collection<Node> selectSingle(final Shape shape, final Point point, final boolean select, final int policy) {
        Quadrant quadrant = root;
        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        final Node[] nodes = new Node[1];
        boolean autoSelectNeighbor = Lookup.getDefault().lookup(VizModel.class).isAutoSelectNeighbor();

        singleFound = false;

        NodeFunction nodeFunction = null;
        switch (policy) {
            case SINGLE_NODE_FIRST:
                nodeFunction = new NodeFunction() {
                                    @Override
                                    public void apply(Node node) {
                                        if (node.getNodeData().isSelected() != select &&
                                             shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), node.getNodeData().getSize(), camera)) {
                                            singleFound = true;
                                            nodes[0] = node;
                                        }
                                    }
                                };
                break;
            case SINGLE_NODE_CLOSEST:
                nodeFunction = new NodeFunction() {
                                    private int minDistance = Integer.MAX_VALUE;
                                    @Override
                                    public void apply(Node node) {
                                        int distance = camera.getPlanarDistance(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), point.x, point.y);
                                        if (node.getNodeData().isSelected() != select &&
                                            distance < minDistance &&
                                            shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), node.getNodeData().getSize(), camera)) {
                                            nodes[0] = node;
                                            minDistance = distance;
                                        }
                                    }
                                };
                break;
            case SINGLE_NODE_LARGEST:
                nodeFunction = new NodeFunction() {
                                    private float maxSize = 0;
                                    @Override
                                    public void apply(Node node) {
                                        if (node.getNodeData().isSelected() != select &&
                                            node.getNodeData().getSize() > maxSize &&
                                            shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), node.getNodeData().getSize(), camera)) {
                                            nodes[0] = node;
                                            maxSize = node.getNodeData().getSize();
                                        }
                                    }
                                };
                break;
        }
        recursiveAddNodes(quadrant, shape, true, nodeFunction);
        if (nodes[0] == null || policy != NodeSpatialStructure.SINGLE_NODE_FIRST) {
            recursiveAddUnassignedNodes(nodeFunction);
        }

        List<Node> selNodes = new ArrayList<Node>();
        if (nodes[0] != null) {
            if (select != nodes[0].getNodeData().isSelected()) {
                nodes[0].getNodeData().setSelected(select);
                selNodes.add(nodes[0]);
                changeMarker = true;
            }
        }
        // If auto-selection is on and the selection is positive (adding nodes)
        if (nodes[0] != null && select && autoSelectNeighbor) {
            for (Node neighbor : graph.getNeighbors(nodes[0])) {
                if (!neighbor.getNodeData().isSelected()) {
                    neighbor.getNodeData().setSelected(true);
                    selNodes.add(neighbor);
                    changeMarker = true;
                }
            }
        }
        
        return selNodes;
    }

    @Override
    public boolean selectContinuousSingle(final Shape shape, final Point point, final boolean select, final int policy) {
        temporarySelectionMod = select;
        temporarySelectedNodes = selectSingle(shape, point, select, policy);
        return !temporarySelectedNodes.isEmpty();
    }

    @Override
    public void clearSelection() {
        root.applyFunction(new NodeFunction() {
            @Override
            public void apply(Node node) {
                node.getNodeData().setSelected(false);
            }
        });
        changeMarker = true;
    }

    @Override
    public void addNode(Node node) {
        NodeData nodeData = node.getNodeData();
        if (nodeData.x() < xmin || nodeData.x() > xmax ||
            nodeData.y() < ymin || nodeData.y() > ymax) {
            unassignedNodes.add(node);
        } else {
            root.addNode(node);
        }
    }

    @Override
    public void removeNode(Node node) {
        if (node.getNodeData().getSpatialData() instanceof QuadtreeData) {
            ((QuadtreeData) node.getNodeData().getSpatialData()).getQuadrant().removeNode(node);
        }
    }

    /**
     * Update max node size if changed.
     */
    private void nodeSizeUpdated(float size) {
        if (size > maxNodeSize) {
            maxNodeSize = size;
        }
        // TODO implement occasional maximum checks for optimization
    }

    @Override
    public float getMaxNodeSize() {
        return maxNodeSize;
    }

    @Override
    public void clearCache() {
        changeMarker = true;
    }

    /**
     * Interface representing a conditioned function on a node.
     */
    interface NodeFunction {

        public void apply(Node node);

    }

    /**
     * Class representing a single quadrant. Quadrants have either a list of internal
     * nodes or references to four children quadrants.
     */
    class Quadrant {

        private final static int MAX_NODES = 10;

        private List<Node> nodes;
        private Quadrant[] children;
        private final float x, y;
        private final float size;
        private final Quadrant parent;
        private final int depth;
        /**
         * Octant may contain selected nodes.
         */
        private boolean selectFlag;

        public Quadrant(Quadrant parent, float x, float y, float size, int depth) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.nodes = new ArrayList<Node>();
            this.parent = parent;
            this.depth = depth;
        }

        public float getSize() {
            return size;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getDepth() {
            return depth;
        }

        public void nodeUpdated(Node node) {
            // If node still inside do nothing, otherwise remove
            NodeData nodeData = node.getNodeData();
            if (nodeData.x() < x || nodeData.x() > x + size ||
                nodeData.y() < y || nodeData.y() > y + size) {
                nodes.remove(node);
                // Test parent, there is a large probability that node can be assigned fast
                if (parent != null && nodeData.x() >= parent.x && nodeData.x() <= parent.x + parent.size &&
                                      nodeData.y() >= parent.y && nodeData.y() <= parent.y + parent.size) {
                    parent.addNode(node);
                } else {
                    unassignedNodes.add(node);
                    nodeData.setSpatialData(null);
                }
            }
        }

        public boolean isSelectFlag() {
            return selectFlag;
        }

        public void setSelectFlag(boolean selectFlag) {
            this.selectFlag = selectFlag;
        }

        public void addNode(Node node) {
            if (nodes != null) {
                nodes.add(node);
                node.getNodeData().setSpatialData(new QuadtreeData(this, node));
                // Node overflow, create new level - if depth less than maximal
                if (nodes.size() > MAX_NODES && depth < MAX_DEPTH) {
                    children = new Quadrant[4];
                    for (Node n : nodes) {
                        int quadrantPosition = getChildPosition(n.getNodeData().x(), n.getNodeData().y());
                        addToChild(n, quadrantPosition);
                    }
                    nodes = null;
                }
            } else {
                int quadrantPosition = getChildPosition(node.getNodeData().x(), node.getNodeData().y());
                addToChild(node, quadrantPosition);
            }
        }

        /**
         * Adds node to octant's child. Children array must be initialized.
         */
        private void addToChild(Node node, int childPosition) {
            if (children[childPosition] == null) {
                float newSize = size / 2;
                float dx = (childPosition & 1) == 1 ? 0 : newSize;
                float dy = (childPosition & 2) == 2 ? 0 : newSize;
                children[childPosition] = new Quadrant(this, x + dx, y + dy, newSize, depth + 1);
            }
            children[childPosition].addNode(node);
        }

        private int getChildPosition(float nodeX, float nodeY) {
            int quadrantPosition = 0;
            float halfSize = size / 2;
            if (nodeX <= x + halfSize) {
                quadrantPosition |= 1;
            }
            if (nodeY <= y + halfSize) {
                quadrantPosition |= 2;
            }
            return quadrantPosition;
        }

        /**
         * Returns the coordinates of corner vertices.
         */
        public float[][] getCornerCoordinates() {
            float[][] coordinates = new float[8][3];
            for (int i = 0; i < 8; i++) {
                coordinates[i][0] = x + (i & 1) * size;
                coordinates[i][1] = y + (i & 2) * size;
            }
            return coordinates;
        }

        public boolean hasChildren() {
            return children != null;
        }

        public Quadrant[] getChildren() {
            return children;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        /**
         * Removes a node, must have internal nodes (no children).
         */
        public void removeNode(Node node) {
            nodes.remove(node);
            node.getNodeData().setSpatialData(null);
            if (parent != null) {
                // TODO Chain delete of unused octants
            }
        }

        public void applyFunction(NodeFunction nodeFunction) {
            if (children == null) {
                for (Node node : nodes) {
                    nodeFunction.apply(node);
                }
            } else {
                for (Quadrant child : children) {
                    if (child != null) {
                        child.applyFunction(nodeFunction);
                    }
                }
            }
        }

    }

    /**
     * Class containing reference to {@link Quadtree} internal structure information.
     *
     * @author Vojtech Bardiovsky
     */
    class QuadtreeData implements SpatialData {

        private final Quadrant quadrant;
        private final Node node;

        public QuadtreeData(Quadrant quadrant, Node node) {
            this.quadrant = quadrant;
            this.node = node;
        }

        public Quadrant getQuadrant() {
            return quadrant;
        }

        @Override
        public void positionUpdated() {
            quadrant.nodeUpdated(node);
        }

        @Override
        public void sizeUpdated() {
            nodeSizeUpdated(node.getNodeData().getSize());
        }

    }

}
