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
package org.gephi.graph.dhns.graph.iterators;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.NodeIterator;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;
import org.gephi.graph.dhns.predicate.Predicate;

/**
 * Implementation of <code>NodeIterable</code> with automatic lock unlocking when <code>hasNext</code>
 * returns <code>false</code>.
 * <p>
 * The <code>doBreak</code> method has to be called if the loop is interrupted.
 *
 * @author Mathieu Bastian
 */
public class NodeIterableImpl implements NodeIterable {

    private NodeIteratorImpl iterator;

    public NodeIterableImpl(AbstractNodeIterator iterator, Lock lock) {
        this.iterator = new NodeIteratorImpl(iterator, lock);
    }

    public NodeIterableImpl(AbstractNodeIterator iterator, Lock lock, Predicate<Node> predicate) {
        this.iterator = new FilteredNodeIteratorImpl(iterator, lock, predicate);
    }

    public NodeIterableImpl(NodeIterableImpl iterable, Predicate<Node> predicate) {
        this(iterable.getIterator().getIterator(), iterable.getIterator().getLock(), predicate);
    }

    public NodeIterator iterator() {
        return iterator;
    }

    public void doBreak() {
        if (iterator.lock != null) {
            iterator.lock.unlock();
        }
    }

    public Node[] toArray() {
        ArrayList<Node> list = new ArrayList<Node>();
        for (; iterator.hasNext();) {
            list.add(iterator.next());
        }
        return list.toArray(new Node[0]);
    }

    public NodeIteratorImpl getIterator() {
        return iterator;
    }
}
