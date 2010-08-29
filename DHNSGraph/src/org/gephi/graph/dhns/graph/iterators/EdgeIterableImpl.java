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
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.EdgeIterator;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.AbstractEdgeIterator;
import org.gephi.graph.dhns.predicate.Predicate;

/**
 * Implementation of <code>EdgeIterable</code> with automatic lock unlocking when <code>hasNext</code>
 * returns <code>false</code>.
 * <p>
 * The <code>doBreak</code> method has to be called if the loop is interrupted.
 *
 * @author Mathieu Bastian
 */
public class EdgeIterableImpl implements EdgeIterable {

    private EdgeIteratorImpl iterator;

    public EdgeIterableImpl(AbstractEdgeIterator iterator, Lock lock) {
        this.iterator = new EdgeIteratorImpl(iterator, lock);
    }

    public EdgeIterableImpl(AbstractEdgeIterator iterator, Lock lock, Predicate<AbstractEdge> predicate) {
        this.iterator = new FilteredEdgeIteratorImpl(iterator, lock, predicate);
    }

    public EdgeIterableImpl(EdgeIterableImpl iterable, Predicate<AbstractEdge> predicate) {
        this(iterable.getIterator().getIterator(), iterable.getIterator().getLock(), predicate);
    }

    public EdgeIterator iterator() {
        return iterator;
    }

    public void doBreak() {
        if (iterator.lock != null) {
            iterator.lock.unlock();
        }
    }

    public Edge[] toArray() {
        ArrayList<Edge> list = new ArrayList<Edge>();
        for (; iterator.hasNext();) {
            list.add(iterator.next());
        }
        return list.toArray(new Edge[0]);
    }

    public EdgeIteratorImpl getIterator() {
        return iterator;
    }
}
