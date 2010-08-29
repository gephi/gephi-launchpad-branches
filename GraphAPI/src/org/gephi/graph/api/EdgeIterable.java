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
package org.gephi.graph.api;

/**
 * Proxy iterable for iterating edges, users can either get <code>EdgeIterator</code>
 * or directly getting an <code>Edge</code> array.
 * <p>
 * This iterable has an additionnal feature, it automatically <b>lock</b> the graph
 * when the iterator is called and <b>unlock</b> it when iterator terminates. That
 * means calling <code>break</code> before the iterator terminates won't unlock
 * the graph. The <code>doBreak()</code> method will properly unlock the graph.
 * Note that calling <code>toArray()</code> avoid this issue.
 *<p>
 * If you're not sure if the graph is properly unlocked, for instance when
 * exception are thrown within the loop, use {@link Graph#readUnlockAll()}.
 * 
 * @author Mathieu Bastian
 * @see Graph#readLock() 
 */
public interface EdgeIterable extends Iterable<Edge> {

    /**
     * Returns the edge iterator.
     * @return  the edge iterator
     */
    public EdgeIterator iterator();

    /**
     * Perform <b>unlocking</b> in case the iterator was terminated before normal
     * end.
     */
    public void doBreak();

    /**
     * Returns the content of the iterable directly as an array. It reads the
     * iterable until its end and returns the array. <b>Use this method to avoid
     * the automatic lock when using the iterator</b>.
     * @return the iterator content as an array
     */
    public Edge[] toArray();
}
