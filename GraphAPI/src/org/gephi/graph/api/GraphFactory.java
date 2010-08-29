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
 * Graph factory that builds node and edges elements. Belongs to a {@link GraphModel}.
 * 
 * @author Mathieu Bastian
 */
public interface GraphFactory {

    /**
     * Create a new node, with default identifier.
     * @return          a new node instance
     */
    public Node newNode();

    /**
     * Create a new node with an identifier. If <code>id</code> is <code>null</code>
     * a default identifier is used.
     * @param id        a unique identifier, could be <code>null</code>
     * @return          a new node instance
     */
    public Node newNode(String id);

    /**
     * Create a new edge. This method don't force the type of edge (directed or
     * undirected). That means it is directed by default but will be considered
     * as undirected if queried from an <code>UndirectedGraph</code>.
     * <p>
     * Edge's weight is 1.0 by default.
     * @param source    the edge's source
     * @param target    the edge's target
     * @return      a new proper edge instance
     */
    public Edge newEdge(Node source, Node target);

    /**
     * Creates a new edge.
     * @param source    the edge's source
     * @param target    the edge's targer
     * @param weight    the edge's weight
     * @param directed  the edge's type
     * @return      a new mixed edge instance
     */
    public Edge newEdge(Node source, Node target, float weight, boolean directed);

    /**
     * Creates a new edge.
     * @param id        a unique identifier, could be <code>null</code>
     * @param source    the edge's source
     * @param target    the edge's targer
     * @param weight    the edge's weight
     * @param directed  the edge's type
     * @return      a new mixed edge instance
     */
    public Edge newEdge(String id, Node source, Node target, float weight, boolean directed);
}
