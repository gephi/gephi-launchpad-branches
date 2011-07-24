/*
Copyright 2008-2011 Gephi
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

import org.gephi.graph.spi.LayoutData;
import org.gephi.graph.spi.SpatialData;

/**
 * Contains all extended data related to a node, including access to its
 * attributes.
 * <p>
 * The node data is unique for a node, accross all views. Nodes can be get
 * from this node data by using <code>getRootNode()</code> or
 * <code>getNode(viewId)</code>.
 *
 * @author Mathieu Bastian
 * @see Node
 */
public interface NodeData extends Renderable, Attributable {

    /**
     * Sets this node position along the x-axis.
     * @param x         x-component of this node position
     */
    public void setX(float x);

    /**
     * Sets this node position along the y-axis.
     * @param y         y-component of this node position
     */
    public void setY(float y);

    /**
     * Sets this node position along the z-axis.
     * @param z         z-component of this node position
     */
    public void setZ(float z);

    /**
     * Sets this node position.
     * @param x         x-component of this node position
     * @param y         y-component of this node position
     * @param z         z-component of this node position
     */
    public void setPosition(float x, float y, float z);

    /**
     * Returns the node this node data belongs in the <b>main</b> view. To get
     * the node in a particular view, see {@link #getNode(int) }.
     * @return              the node this node data belongs in the <b>main</b> view
     * @see GraphView
     */
    public Node getRootNode();

    /**
     * Returns the node this node data belongs in the view that has
     * <code>viewId</code> has identifier or <code>null</code> if the view
     * cannot be found.
     * @param viewId        the view identifier
     * @return              the node this node data belongs in the view
     * @see GraphView
     */
    public Node getNode(int viewId);

    /**
     * Returns the node label, or <code>null</code> if none has been set.
     * @return              the node lable, or <code>null</code>
     */
    public String getLabel();

    /**
     * Sets this node label.
     * @param label         the label that is to be set as this node label
     */
    public void setLabel(String label);

    /**
     * Returns the string identifier of this node. This identifier can be set
     * by users, in contrario of {@link Node#getId()} which is set by the system.
     * <p>
     * Use <code>Graph.getNode(String)</code> to find nodes from this id.
     * <p>
     * If no identifier has been set, returns the system integer identifier.
     * @return              the node identifier
     */
    public String getId();

    /**
     * Returns the layout data object associated to this node. Layout data are
     * temporary data layout algorithms can push to nodes to save states when
     * computing.
     * @param <T>           must inherit from <code>LayoutData</code>
     * @return              the layout data of this node, can be <code>null</code>
     */
    public <T extends LayoutData> T getLayoutData();

    /**
     * Sets the layout data of this node. Layout data are temporary data layout
     * algorithms can push to nodes to save states when computing.
     * @param layoutData    the layout data that is to be set for this node
     */
    public void setLayoutData(LayoutData layoutData);

    /**
     * Returns the spatial data object associated to this node. Spatial data
     * are used to optimize operations on spatial data structures.
     * @param <T>           must inherit from <code>SpatialData</code>
     * @return              the spatial data of this node, can be <code>null</code>
     */
    public <T extends SpatialData> T getSpatialData();

    /**
     * Sets the spatial data of this node. Spatial data are used to optimize
     * operations on spatial data structures.
     * @param spatialData   the spatial data object that is to be set for this node
     */
    public void setSpatialData(SpatialData spatialData);

    /**
     * Returns <code>true</code> if this node is fixed. A node can be fixed
     * to block it's position during layout.
     * @return              <code>true</code> if this node is fixed, <code>false</code>
     * otherwise
     */
    public boolean isFixed();

    /**
     * Sets this node fixed attribute. A node can be fixed
     * to block it's position during layout.
     * @param fixed         the fixed attribute value
     */
    public void setFixed(boolean fixed);

    /**
     * Returns the visual shape of this node.
     */
    public NodeShape getNodeShape();

    /**
     * Sets the visual shape of this node.
     * @param nodeShape     the visual shape to be set.
     */
    public void setNodeShape(NodeShape nodeShape);
}
