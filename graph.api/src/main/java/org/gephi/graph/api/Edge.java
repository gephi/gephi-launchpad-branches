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
 * Edge interface. An edge connects two nodes and can be <b>directed</b> or
 * not.
 * <p>
 * If working with several {@link GraphView}, note that <code>source</code> and
 * <code>target</code> are always nodes in the <b>main</b> view. To obtain nodes
 * in other views, do:
 * <pre>
 * GraphView view = ...;
 * Node node = node.getNodeData().getNode(view.getViewId());
 * </pre>
 * 
 * @author Mathieu Bastian
 */
public interface Edge {

    /**
     * Returns the unique identifier of the edge.
     * @return          the Id of the edge
     */
    public int getId();

    /**
     * Returns the source of the edge.
     * @return          the source of the edge
     */
    public Node getSource();

    /**
     * Returns the target of the edge.
     * @return          the target of the edge
     */
    public Node getTarget();

    /**
     * Returns the weight of the edge. Default value is 1.0.
     * @return          the weight of the edge
     */
    public float getWeight();

    /**
     * Returns the weight of the edge for the given time interval.
     * If the weight is dynamic, it has several values over time. This method
     * returns the weight for a particular interval.
     * @param low       the lower interval bound, can be
     *                  <code>Double.NEGATIVE_INFINITY</code>
     * @param high      the upper interval bound, can be
     *                  <code>Double.POSITIVE_INFINITY</code>
     * @return          the weight of the edge at this [low,high] interval
     */
    public float getWeight(double low, double high);

    /**
     * Set the weight of the edge.
     * @param weight    the weight of the edge
     */
    public void setWeight(float weight);

    /**
     * Returns <code>true</code> if the edge is directed or <code>false</code> if it's undirected.
     * Default is directed. Note that value is immutable.<p>
     * Special cases:
     * <ul><li>If the edge has been created from an <b>undirected</b> graph, returns false.</li>
     * <li>If the edge has been created from a <b>directed</b> graph, returns true.</li>
     * <li>If the edge has been created from a <b>mixed</b> graph, returns the value set to the
     * <code>addEdge()</code> method.</li></ul>
     * @return           <code>true</code> if the edge is directed, <code>false</code> otherwise
     */
    public boolean isDirected();

    /**
     * Returns <code>true</code> if edge source and target are the same.
     * @return          <code>true</code> if the edge is a self-loop, <code>false</code> otherwise
     */
    public boolean isSelfLoop();

    /**
     * Returns edge data.
     * @return          edge data instance
     */
    public EdgeData getEdgeData();
}
