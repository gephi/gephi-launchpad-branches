/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.graph.dhns.graph;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.HierarchicalMixedGraph;
import org.gephi.graph.api.MetaEdge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphViewImpl;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.BiEdgeIterator;
import org.gephi.graph.dhns.edge.iterators.EdgeAndMetaEdgeIterator;
import org.gephi.graph.dhns.edge.iterators.EdgeIterator;
import org.gephi.graph.dhns.edge.iterators.EdgeNodeIterator;
import org.gephi.graph.dhns.edge.iterators.MetaEdgeIterator;
import org.gephi.graph.dhns.edge.iterators.MetaEdgeNodeIterator;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.NeighborIterator;
import org.gephi.graph.dhns.node.iterators.TreeIterator;
import org.gephi.graph.dhns.predicate.Predicate;
import org.gephi.graph.dhns.predicate.Tautology;

public class HierarchicalMixedGraphImpl extends HierarchicalGraphImpl implements HierarchicalMixedGraph {

    private Predicate<AbstractEdge> undirectedPredicate;
    private Predicate<AbstractEdge> directedPredicate;

    public HierarchicalMixedGraphImpl(Dhns dhns, GraphViewImpl view) {
        super(dhns, view);
        directedPredicate = new Predicate<AbstractEdge>() {

            public boolean evaluate(AbstractEdge t) {
                return t.isDirected();
            }
        };
        undirectedPredicate = new Predicate<AbstractEdge>() {

            public boolean evaluate(AbstractEdge t) {
                return !t.isDirected();
            }
        };
    }

    public boolean addEdge(Edge edge) {
        AbstractEdge absEdge = checkEdge(edge);
        AbstractNode source = checkNode(edge.getSource());
        AbstractNode target = checkNode(edge.getTarget());
        if (checkEdgeExist(source, target)) {
            //Edge already exist
            return false;
        }
        AbstractEdge symmetricEdge = getSymmetricEdge(absEdge);
        if (symmetricEdge != null && (!symmetricEdge.isDirected() || !absEdge.isDirected())) {
            //The symmetric edge exist and is undirected
            return false;
        }
        if (!absEdge.hasAttributes()) {
            absEdge.setAttributes(dhns.factory().newEdgeAttributes(edge.getEdgeData()));
        }
        view.getStructureModifier().addEdge(absEdge);
        if (absEdge.isDirected()) {
            dhns.touchDirected();
        } else {
            dhns.touchUndirected();
        }
        return true;
    }

    public boolean addEdge(Node source, Node target, boolean directed) {
        AbstractNode absSource = checkNode(source);
        AbstractNode absTarget = checkNode(target);
        if (directed && checkEdgeExist(absSource, absTarget)) {
            //Edge already exist
            return false;
        }
        AbstractEdge symmetricEdge = absSource.getEdgesInTree().getItem(absTarget.getNumber());
        if (symmetricEdge != null && (!symmetricEdge.isDirected() || !directed)) {
            //The symmetric edge exist and is undirected
            return false;
        }

        AbstractEdge edge = dhns.factory().newEdge(null, source, target, 1.0f, directed);
        view.getStructureModifier().addEdge(edge);
        if (directed) {
            dhns.touchDirected();
        } else {
            dhns.touchUndirected();
        }
        return true;
    }

    public boolean removeEdge(Edge edge) {
        AbstractEdge absEdge = checkEdge(edge);
        AbstractEdge undirected = absEdge.getUndirected(view.getViewId());      //Ensure that the edge with the min id is removed before his mutual with a greater id
        return view.getStructureModifier().deleteEdge(undirected);
    }

    public EdgeIterable getDirectedEdges() {
        readLock();
        return dhns.newEdgeIterable(new EdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false, Tautology.instance, Tautology.instance), directedPredicate);
    }

    public EdgeIterable getUndirectedEdges() {
        readLock();
        return dhns.newEdgeIterable(new EdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false, Tautology.instance, Tautology.instance), undirectedPredicate);
    }

    public boolean isDirected(Edge edge) {
        AbstractEdge absEdge = checkEdgeOrMetaEdge(edge);
        return absEdge.isDirected();
    }

    public boolean contains(Edge edge) {
        if (edge == null) {
            throw new NullPointerException();
        }
        AbstractEdge absEdge = (AbstractEdge) edge;
        return getEdge(absEdge.getSource(view.getViewId()), absEdge.getTarget(view.getViewId())) != null;
    }

    public Edge getEdge(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return null;
        }
        readLock();
        AbstractNode sourceNode = checkNode(node1);
        AbstractNode targetNode = checkNode(node2);
        AbstractEdge res = sourceNode.getEdgesOutTree().getItem(targetNode.getNumber());
        if (res == null) {
            res = sourceNode.getEdgesInTree().getItem(targetNode.getNumber());
        }
        readUnlock();
        return res;
    }

    public EdgeIterable getEdges() {
        readLock();
        return dhns.newEdgeIterable(new EdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false, Tautology.instance, Tautology.instance));
    }

    public EdgeIterable getEdgesTree() {
        readLock();
        return dhns.newEdgeIterable(new EdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false, Tautology.instance, Tautology.instance));
    }

    public NodeIterable getNeighbors(Node node) {
        readLock();
        AbstractNode absNode = checkNode(node);
        return dhns.newNodeIterable(new NeighborIterator(new EdgeNodeIterator(absNode, EdgeNodeIterator.EdgeNodeIteratorMode.BOTH, true, Tautology.instance, Tautology.instance), absNode, Tautology.instance));
    }

    public EdgeIterable getEdges(Node node) {
        readLock();
        AbstractNode absNode = checkNode(node);
        return dhns.newEdgeIterable(new EdgeNodeIterator(absNode, EdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false, Tautology.instance, Tautology.instance));
    }

    public int getEdgeCount() {
        return view.getEdgesCountEnabled();
    }

    public int getTotalEdgeCount() {
        return view.getEdgesCountEnabled() + view.getMetaEdgesCountTotal();
    }

    public int getDegree(Node node) {
        AbstractNode absNode = checkNode(node);
        int count = absNode.getEdgesInTree().getCount() + absNode.getEdgesOutTree().getCount();
        return count;
    }

    //Directed
    public int getInDegree(Node node) {
        AbstractNode absNode = checkNode(node);
        int count = absNode.getEdgesInTree().getCount();
        return count;
    }

    //Directed
    public int getOutDegree(Node node) {
        AbstractNode absNode = checkNode(node);
        int count = absNode.getEdgesOutTree().getCount();
        return count;
    }

    public boolean isAdjacent(Node node1, Node node2) {
        if (node1 == node2) {
            throw new IllegalArgumentException("Nodes can't be the same");
        }
        return isSuccessor(node1, node2) || isPredecessor(node1, node2);
    }

    //Directed
    public boolean isSuccessor(Node node, Node successor) {
        return getEdge(node, successor) != null;
    }

    //Directed
    public boolean isPredecessor(Node node, Node predecessor) {
        return getEdge(predecessor, node) != null;
    }

    public EdgeIterable getInnerEdges(Node nodeGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getOuterEdges(Node nodeGroup) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public EdgeIterable getMetaEdges() {
        readLock();
        return dhns.newEdgeIterable(new MetaEdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false));
    }

    public EdgeIterable getEdgesAndMetaEdges() {
        readLock();
        return dhns.newEdgeIterable(new EdgeAndMetaEdgeIterator(structure, new TreeIterator(structure, true, Tautology.instance), false, enabledNodePredicate, Tautology.instance));
    }

    public EdgeIterable getMetaEdges(Node node) {
        readLock();
        AbstractNode absNode = checkNode(node);
        return dhns.newEdgeIterable(new MetaEdgeNodeIterator(absNode.getMetaEdgesOutTree(), absNode.getMetaEdgesInTree(), MetaEdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false));
    }

    public EdgeIterable getEdgesAndMetaEdges(Node node) {
        readLock();
        AbstractNode absNode = checkNode(node);
        EdgeNodeIterator std = new EdgeNodeIterator(absNode, EdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false, enabledNodePredicate, Tautology.instance);
        MetaEdgeNodeIterator meta = new MetaEdgeNodeIterator(absNode.getMetaEdgesOutTree(), absNode.getMetaEdgesInTree(), MetaEdgeNodeIterator.EdgeNodeIteratorMode.BOTH, false);
        return dhns.newEdgeIterable(new BiEdgeIterator(std, meta));
    }

    public MetaEdge getMetaEdge(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return null;
        }
        readLock();
        AbstractNode sourceNode = checkNode(node1);
        AbstractNode targetNode = checkNode(node2);
        AbstractEdge res = sourceNode.getMetaEdgesOutTree().getItem(targetNode.getNumber());
        if (res == null) {
            res = sourceNode.getMetaEdgesInTree().getItem(targetNode.getNumber());
        }
        readUnlock();
        return (MetaEdge) res;
    }

    public int getMetaDegree(Node node) {
        AbstractNode absNode = checkNode(node);
        int count = absNode.getMetaEdgesInTree().getCount() + absNode.getMetaEdgesOutTree().getCount();
        return count;
    }

    public int getTotalDegree(Node node) {
        AbstractNode absNode = checkNode(node);
        int count = absNode.getEdgesInTree().getCount()
                + absNode.getEdgesOutTree().getCount()
                + absNode.getMetaEdgesInTree().getCount()
                + absNode.getMetaEdgesOutTree().getCount();
        return count;
    }

    public boolean removeMetaEdge(Edge edge) {
        AbstractEdge absEdge = checkMetaEdge(edge);
        return view.getStructureModifier().deleteMetaEdge(absEdge);
    }

    @Override
    public HierarchicalMixedGraphImpl copy(Dhns dhns, GraphViewImpl view) {
        return new HierarchicalMixedGraphImpl(dhns, view);
    }

    public EdgeIterable getHierarchyEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
