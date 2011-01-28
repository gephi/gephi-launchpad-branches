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

import java.util.concurrent.locks.Lock;
import org.gephi.graph.api.Edge;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.iterators.AbstractEdgeIterator;
import org.gephi.graph.dhns.predicate.Predicate;

/**
 *
 * @author Mathieu Bastian
 */
public class FilteredEdgeIteratorImpl extends EdgeIteratorImpl {

    protected Predicate<AbstractEdge> predicate;
    protected AbstractEdge pointer;

    public FilteredEdgeIteratorImpl(AbstractEdgeIterator iterator, Lock lock, Predicate<AbstractEdge> predicate) {
        super(iterator, lock);
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        while (iterator.hasNext()) {
            pointer = iterator.next();
            if (predicate.evaluate(pointer)) {
                return true;
            }
        }
        if (lock != null) {
            lock.unlock();
        }
        return false;
    }

    @Override
    public Edge next() {
        return pointer;
    }
}
