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
import java.util.HashSet;
import org.gephi.visualization.api.selection.Shape;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.NodeIterator;
import org.gephi.graph.spi.SpatialData;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.selection.NodeSpatialStructure;
import org.gephi.visualization.api.selection.Shape.Intersection;
import org.openide.util.Lookup;

/**
 * @author Vojtech Bardiovsky
 */
public final class Octree extends QuadrantTree {

    private Octant root;

    private final Collection<Node> unassignedNodes;

    private boolean singleFound;

    private Collection<Node> selectedNodes;

    private Collection<Node> temporarySelectedNodes;

    private boolean temporarySelectionMod;

    private final Graph graph;

    float xmin, xmax, ymin, ymax, zmin, zmax;

    // Required for the case when more nodes have same coordinates.
    private final static int MAX_DEPTH = 15;
    private final static int MAX_NODES = 10;

    public Octree(Graph graph) {
        this.graph = graph;
        this.unassignedNodes = new ArrayList<Node>();
        this.selectedNodes = new ArrayList<Node>();
        rebuild();
    }

    /**
     * Creates an empty octree structure with no underlying graph.
     */
    public Octree() {
        this.graph = null;
        this.unassignedNodes = new ArrayList<Node>();
        this.selectedNodes = new ArrayList<Node>();
        this.root = new Octant(null, 0, 0, 0, 1f, 0);
    }

    @Override
    public void rebuild() {
        xmin = ymin = zmin = Float.MAX_VALUE;
        xmax = ymax = zmax = Float.MIN_VALUE;
        NodeIterator iterator = graph.getNodes().iterator();
        while (iterator.hasNext()) {
            NodeData nd = iterator.next().getNodeData();
            if (nd.x() < xmin) {
                xmin = nd.x();
            }
            if (nd.y() < ymin) {
                ymin = nd.y();
            }
            if (nd.z() < zmin) {
                zmin = nd.z();
            }
            if (nd.x() > xmax) {
                xmax = nd.x();
            }
            if (nd.y() > ymax) {
                ymax = nd.y();
            }
            if (nd.z() > zmax) {
                zmax = nd.z();
            }
        }
        root = new Octant(null, xmin, ymin, zmin, Math.max(Math.max(xmax - xmin, ymax - ymin), zmax - zmin), 0);
        iterator = graph.getNodes().iterator();
        while (iterator.hasNext()) {
            root.addNode(iterator.next());
        }
    }

    @Override
    public synchronized Collection<Node> getSelectedNodes() {
        // Return last selected nodes collection
        if (!changeMarker) {
            return selectedNodes;
        }
        // Reassign unassigned nodes
        if (reassignNodes) {
            Iterator<Node> iterator = unassignedNodes.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (isInsideRoot(node)) {
                    addNode(node);
                    iterator.remove();
                }
            }
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
    }

    @Override
    public List<Node> applySelection(final Shape shape) {
        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        final List<Node> nodes = new ArrayList<Node>();
        final Set<Node> neighbors = new HashSet<Node>();
        final boolean autoSelectNeighbor = Lookup.getDefault().lookup(VisualizationController.class).getVizModel().isAutoSelectNeighbor();

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
                    if (select && autoSelectNeighbor && graph != null) {
                        for (Node neighbor : graph.getNeighbors(node)) {
                            neighbors.add(neighbor);
                        }
                    }
                }
            }
        };
        recursiveAddNodes(root, shape, false, nodeFunction);
        recursiveAddUnassignedNodes(nodeFunction);
        // If auto-selection is on and the selection is positive (adding nodes)
        if (select && autoSelectNeighbor) {
            for (Node neighbor : neighbors) {
                if (!neighbor.getNodeData().isSelected()) {
                    neighbor.getNodeData().setSelected(true);
                    neighbor.getNodeData().setAutoSelected(true);
                    nodes.add(neighbor);
                    changeMarker = true;
                }
            }
        }        
        return nodes;
    }

    private void recursiveGetSelectedNodes(Octant octant, Collection<Node> list) {
        if (octant.hasChildren()) {
            for (Octant child : octant.getChildren()) {
                if (child != null) {
                    recursiveGetSelectedNodes(child, list);
                }
            }
        } else {
            for (Node node : octant.getNodes()) {
                if (node.getNodeData().isSelected()) {
                    list.add(node);
                }
            }
        }
    }

    private void recursiveAddNodes(Octant octant, Shape shape, boolean singleOnly, NodeFunction nodeFunction) {
        if (singleOnly && singleFound) {
            return;
        }
        
        final float maxNodeSize = Lookup.getDefault().lookup(VisualizationController.class).getVizModel().getGraphLimits().getMaxNodeSize();
        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        Intersection intersection = shape.intersectsCube(octant.getX(), octant.getY(), octant.getZ(), octant.getSize(), maxNodeSize, camera);

        switch (intersection) {
            case OUTSIDE:
                return;
            case INTERSECT:
                if (octant.hasChildren()) {
                    for (Octant child : octant.getChildren()) {
                        if (child != null) {
                            recursiveAddNodes(child, shape, singleOnly, nodeFunction);
                        }
                    }
                } else {
                    octant.applyFunction(nodeFunction);
                }
                break;
            case FULLY_INSIDE:
                octant.applyFunction(nodeFunction);
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
        Octant octant = root;
        final Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        final Node[] nodes = new Node[1];
        boolean autoSelectNeighbor = Lookup.getDefault().lookup(VisualizationController.class).getVizModel().isAutoSelectNeighbor();

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
                                    private float minDistance = Integer.MAX_VALUE;
                                    @Override
                                    public void apply(Node node) {
                                        float distance = camera.getPlanarDistance(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), point.x, point.y);
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
        recursiveAddNodes(octant, shape, true, nodeFunction);
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
        if (nodes[0] != null && select && autoSelectNeighbor && graph != null) {
            for (Node neighbor : graph.getNeighbors(nodes[0])) {
                if (!neighbor.getNodeData().isSelected()) {
                    neighbor.getNodeData().setSelected(true);
                    neighbor.getNodeData().setAutoSelected(true);
                    selNodes.add(neighbor);
                    changeMarker = true;
                }
            }
        }

        return selNodes;
    }

    @Override
    public boolean selectContinuousSingle(final Shape shape, Point point, final boolean select, final int policy) {
        temporarySelectionMod = select;
        temporarySelectedNodes = selectSingle(shape, point, select, policy);
        return !temporarySelectedNodes.isEmpty();
    }

    @Override
    public void addNode(Node node) {
        NodeData nodeData = node.getNodeData();
        if (nodeData.x() < xmin || nodeData.x() > xmax ||
            nodeData.y() < ymin || nodeData.y() > ymax ||
            nodeData.z() < zmin || nodeData.z() > zmax) {
            unassignedNodes.add(node);
        } else {
            root.addNode(node);
        }
    }

    private boolean isInsideRoot(Node node) {
        NodeData nodeData = node.getNodeData();
        return !(nodeData.x() < xmin || nodeData.x() > xmax ||
                 nodeData.y() < ymin || nodeData.y() > ymax ||
                 nodeData.z() < zmin || nodeData.z() > zmax);
    }
    
    @Override
    public void removeNode(Node node) {
        if (node.getNodeData().getSpatialData() instanceof OctreeData) {
            ((OctreeData) node.getNodeData().getSpatialData()).getOctant().removeNode(node);
        }
    }

    /**
     * Interface representing a conditioned function on a node.
     */
    interface NodeFunction {

        public void apply(Node node);

    }

    /**
     * Class representing a single octant. Octants have either a list of internal
     * nodes or references to eight children octants.
     */
    class Octant {

        private List<Node> nodes;
        private Octant[] children;
        private final float x, y, z;
        private final float size;
        private final Octant parent;
        private final int depth;
        /**
         * Octant may contain selected nodes.
         */
        private boolean selectFlag;

        public Octant(Octant parent, float x, float y, float z, float size, int depth) {
            this.x = x;
            this.y = y;
            this.z = z;
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

        public float getZ() {
            return z;
        }

        public int getDepth() {
            return depth;
        }

        public void nodeUpdated(Node node) {
            // If node still inside do nothing, otherwise remove
            NodeData nodeData = node.getNodeData();
            if (nodeData.x() < x || nodeData.x() > x + size ||
                nodeData.y() < y || nodeData.y() > y + size ||
                nodeData.z() < z || nodeData.z() > z + size) {
                nodes.remove(node);
                // Test parent, there is a large probability that node can be assigned fast
                if (parent != null && nodeData.x() >= parent.x && nodeData.x() <= parent.x + parent.size &&
                                      nodeData.y() >= parent.y && nodeData.y() <= parent.y + parent.size &&
                                      nodeData.z() >= parent.z && nodeData.z() <= parent.z + parent.size) {
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
                node.getNodeData().setSpatialData(new OctreeData(this, node));
                // Node overflow, create new level - if depth less than maximal
                if (nodes.size() > MAX_NODES && depth < MAX_DEPTH) {
                    children = new Octant[8];
                    for (Node n : nodes) {
                        int octantPosition = getChildPosition(n.getNodeData().x(), n.getNodeData().y(), n.getNodeData().z());
                        addToChild(n, octantPosition);
                    }
                    nodes = null;
                }
            } else {
                int octantPosition = getChildPosition(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z());
                addToChild(node, octantPosition);
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
                float dz = (childPosition & 4) == 4 ? 0 : newSize;
                children[childPosition] = new Octant(this, x + dx, y + dy, z + dz, newSize, depth + 1);
            }
            children[childPosition].addNode(node);
        }

        private int getChildPosition(float nodeX, float nodeY, float nodeZ) {
            int octantPosition = 0;
            float halfSize = size / 2;
            if (nodeX <= x + halfSize) {
                octantPosition |= 1;
            }
            if (nodeY <= y + halfSize) {
                octantPosition |= 2;
            }
            if (nodeZ <= z + halfSize) {
                octantPosition |= 4;
            }
            return octantPosition;
        }

        /**
         * Returns the coordinates of corner vertices.
         */
        public float[][] getCornerCoordinates() {
            float[][] coordinates = new float[8][3];
            for (int i = 0; i < 8; i++) {
                coordinates[i][0] = x + (i & 1) * size;
                coordinates[i][1] = y + (i & 2) * size;
                coordinates[i][2] = z + (i & 4) * size;
            }
            return coordinates;
        }

        public boolean hasChildren() {
            return children != null;
        }

        public Octant[] getChildren() {
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
                for (Octant child : children) {
                    if (child != null) {
                        child.applyFunction(nodeFunction);
                    }
                }
            }
        }

        public Iterator<Node> getAllNodes() {
            if (children != null) {
                return new OctantNodeIterator(this);
            } else {
                return nodes.iterator();
            }
        }

        /**
         * Iterator for all underlying octant nodes.
         */
        private class OctantNodeIterator implements Iterator<Node> {

            private final Octant octant;
            private int currentChild = 0;
            private Iterator<Node> currentIterator;
            private Iterator<Node> lastUsedIterator;

            public OctantNodeIterator(Octant octant) {
                this.octant = octant;
                moveToNext();
            }

            private boolean moveToNext() {
                while (octant.children[currentChild] == null) {
                    currentChild++;
                }
                if (currentChild == 8) {
                    return false;
                }
                currentIterator = octant.children[currentChild].getAllNodes();
                return true;
            }

            @Override
            public boolean hasNext() {
                if (!currentIterator.hasNext()) {
                    if (currentChild == 7) {
                        return false;
                    }
                    currentChild++;
                    currentIterator = octant.children[currentChild].getAllNodes();
                    return hasNext();
                }
                return currentIterator.hasNext();
            }

            @Override
            public Node next() {
                moveToNext();
                lastUsedIterator = this;
                return currentIterator.next();
            }

            @Override
            public void remove() {
                if (lastUsedIterator == null) {
                    throw new IllegalStateException();
                }
                lastUsedIterator.remove();
            }

        }

    }

    /**
     * Class containing reference to {@link Octree} internal structure information.
     *
     * @author Vojtech Bardiovsky
     */
    class OctreeData implements SpatialData {

        private final Octant octant;
        private final Node node;

        public OctreeData(Octant octant, Node node) {
            this.octant = octant;
            this.node = node;
        }

        public Octant getOctant() {
            return octant;
        }

        @Override
        public void positionUpdated() {
            octant.nodeUpdated(node);
        }

    }

}
