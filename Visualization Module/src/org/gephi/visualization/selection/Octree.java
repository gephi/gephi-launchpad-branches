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
import org.gephi.visualization.api.selection.Shape;
import java.util.Iterator;
import java.util.List;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.api.NodeIterator;
import org.gephi.graph.spi.SpatialData;
import org.gephi.visualization.api.selection.CameraBridge;
import org.gephi.visualization.api.selection.NodeContainer;
import org.gephi.visualization.api.selection.Shape.Intersection;
import org.gephi.visualization.apiimpl.shape.ShapeUtils;
import org.gephi.visualization.controller.Controller;

public final class Octree implements NodeContainer {

    private Octant root;

    private final List<Node> unassignedNodes;

    private final Graph graph;

    float xmin, xmax, ymin, ymax, zmin, zmax;

    public Octree(Graph graph) {
        this.graph = graph;
        this.unassignedNodes = new ArrayList<Node>();
        rebuild();
    }


    @Override
    public void rebuild() {
        // TODO make MAKE_NODES changeable and make it smaller when rebuilding,
        // it will be faster
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
        root = new Octant(null, xmin, ymin, zmin, Math.max(Math.max(xmax - xmin, ymax - ymin), zmax - zmin));
        iterator = graph.getNodes().iterator();
        while (iterator.hasNext()) {
            root.addNode(iterator.next());
        }
    }

    @Override
    public List<Node> getSelectedNodes() {
        // TODO optimize
        List<Node> selectedNodes = new ArrayList<Node>();
        recursiveGetSelectedNodes(root, selectedNodes);
        
        for (Node node : unassignedNodes) {
            if (node.getNodeData().isSelected()) {
                selectedNodes.add(node);
            }
        }
        
        return selectedNodes;
    }

    @Override
    public List<Node> applySelection(final Shape shape) {
        final CameraBridge cameraBridge = Controller.getInstance().getCameraBridge();
        final List<Node> nodes = new ArrayList<Node>();

        final boolean select = shape.getSelectionModifier().isPositive();

        recursiveAddNodes(root, shape, new NodeFunction() {
            @Override
            public void apply(Node node) {
                if (shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), cameraBridge)) {
                    
                    if (select != node.getNodeData().isSelected()) {
                        node.getNodeData().setSelected(select);
                        nodes.add(node);
                    }
                }
            }
        });
        return nodes;
    }

    private void recursiveGetSelectedNodes(Octant octant, List<Node> list) {
        /*
        if (!octant.isSelectFlag()) {
            return;
        }*/
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

    private void recursiveAddNodes(Octant octant, Shape shape, NodeFunction nodeFunction) {
        final CameraBridge cameraBridge = Controller.getInstance().getCameraBridge();
        Intersection intersection = shape.intersectsBox(octant.getX(), octant.getY(), octant.getZ(), octant.getSize(), cameraBridge);

        switch (intersection) {
            case OUTSIDE:
                return;
            case INTERSECT:
                if (octant.hasChildren()) {
                    for (Octant child : octant.getChildren()) {
                        if (child != null) {
                            recursiveAddNodes(child, shape, nodeFunction);
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
    
    @Override
    public void selectSingle(Point point, final boolean select, final int selectionRadius, final int policy) {
        final CameraBridge cameraBridge = Controller.getInstance().getCameraBridge();
        Octant octant = root;
        
        final Shape shape = ShapeUtils.createEllipseShape(point.x, point.y, selectionRadius, selectionRadius);
        recursiveAddNodes(octant, shape, new NodeFunction() {
            private boolean active = true;
            @Override
            // TODO implement closest policy
            public void apply(Node node) {
                 if (active && node.getNodeData().isSelected() != select &&
                     shape.isInside3D(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z(), cameraBridge)) {
                    node.getNodeData().setSelected(select);
                    active = false;
                }
            }
        });
    }

    @Override
    public void clearSelection() {
        root.applyFunction(new NodeFunction() {
            @Override
            public void apply(Node node) {
                node.getNodeData().setSelected(false);
            }
        });
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

        private final static int MAX_NODES = 10;

        private List<Node> nodes;
        private Octant[] children;
        private final float x, y, z;
        private final float size;
        private final Octant parent;
        /**
         * Octant may contain selected nodes.
         */
        private boolean selectFlag;

        public Octant(Octant parent, float x, float y, float z, float size) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.size = size;
            this.nodes = new ArrayList<Node>();
            this.parent = parent;
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

        public void nodeUpdated(Node node) {
            // If node still inside do nothing, otherwise remove
            // TODO better implementation, pass to a parent if chance of inclusion
            NodeData nodeData = node.getNodeData();
            if (nodeData.x() < x || nodeData.x() > x ||
                nodeData.y() < y || nodeData.y() > y ||
                nodeData.z() < z || nodeData.z() > z) {
                nodes.remove(node);
                unassignedNodes.add(node);
                nodeData.setSpatialData(null);
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

                if (nodes.size() > MAX_NODES) {
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
                children[childPosition] = new Octant(this, x + dx, y + dy, z + dz, newSize);
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
    static class OctreeData implements SpatialData {

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
