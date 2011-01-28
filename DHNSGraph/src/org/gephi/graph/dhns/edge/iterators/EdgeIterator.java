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
package org.gephi.graph.dhns.edge.iterators;

import java.util.Iterator;
import org.gephi.graph.dhns.edge.ProperEdgeImpl;
import org.gephi.graph.dhns.core.TreeStructure;
import org.gephi.utils.collection.avl.ParamAVLIterator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;
import org.gephi.graph.dhns.predicate.Predicate;

/**
 * Iterator for main edges for the visible graph.
 *
 * @author Mathieu Bastian
 * @see EdgeIterator
 */
public class EdgeIterator extends AbstractEdgeIterator implements Iterator<Edge> {

    protected AbstractNodeIterator nodeIterator;
    protected ParamAVLIterator<ProperEdgeImpl> edgeIterator;
    protected AbstractNode currentNode;
    protected AbstractEdge pointer;
    protected boolean undirected;
    protected Predicate<AbstractNode> nodePredicate;
    protected Predicate<AbstractEdge> edgePredicate;

    public EdgeIterator(TreeStructure treeStructure, AbstractNodeIterator nodeIterator, boolean undirected, Predicate<AbstractNode> nodePredicate, Predicate<AbstractEdge> edgePredicate) {
        this.nodeIterator = nodeIterator;
        edgeIterator = new ParamAVLIterator<ProperEdgeImpl>();
        this.undirected = undirected;
        this.nodePredicate = nodePredicate;
        this.edgePredicate = edgePredicate;
    }

    @Override
    public boolean hasNext() {
        while (pointer == null || (undirected && pointer.getUndirected(currentNode.getViewId()) != pointer) || !edgePredicate.evaluate(pointer)) {
            while (!edgeIterator.hasNext()) {
                if (nodeIterator.hasNext()) {
                    currentNode = nodeIterator.next();
                    if (!currentNode.getEdgesOutTree().isEmpty()) {
                        edgeIterator.setNode(currentNode.getEdgesOutTree());
                    }
                } else {
                    return false;
                }
            }

            pointer = edgeIterator.next();
            if (!nodePredicate.evaluate(pointer.getTarget(currentNode.getViewId()))) {
                pointer = null;
            }
        }
        return true;
    }

    @Override
    public AbstractEdge next() {
        AbstractEdge e = pointer;
        pointer = null;
        return e;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
