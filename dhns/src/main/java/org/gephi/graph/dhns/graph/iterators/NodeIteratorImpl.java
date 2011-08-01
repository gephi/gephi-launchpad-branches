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
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterator;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;

/**
 * Iterator for {@link NodeIterableImpl}.
 *
 * @author Mathieu Bastian
 */
public class NodeIteratorImpl implements NodeIterator {

    protected AbstractNodeIterator iterator;
    protected Lock lock;

    public NodeIteratorImpl(AbstractNodeIterator iterator, Lock lock) {
        this.iterator = iterator;
        this.lock = lock;
    }

    public boolean hasNext() {
        boolean res = iterator.hasNext();
        if (!res && lock != null) {
            //System.out.println(Thread.currentThread()+ " iterator unlock");
            lock.unlock();
        }
        return res;
    }

    public Node next() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    public AbstractNodeIterator getIterator() {
        return iterator;
    }

    public Lock getLock() {
        return lock;
    }
}
