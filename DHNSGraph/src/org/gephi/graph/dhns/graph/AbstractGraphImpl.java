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

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphViewImpl;
import org.gephi.graph.dhns.core.TreeStructure;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.MetaEdgeImpl;
import org.gephi.graph.dhns.node.AbstractNode;

/**
 * Utilities methods for managing graphs implementation like locking of non-null checking
 *
 * @author Mathieu Bastian
 */
public abstract class AbstractGraphImpl {

    protected final Dhns dhns;
    protected final GraphViewImpl view;
    protected final TreeStructure structure;

    public AbstractGraphImpl(Dhns dhns, GraphViewImpl view) {
        this.dhns = dhns;
        this.view = view;
        this.structure = view.getStructure();
        view.addGraphReference(this);
    }

    public GraphModel getGraphModel() {
        return dhns;
    }

    public GraphView getView() {
        return view;
    }

    public void readLock() {
        dhns.readLock();
    }

    public void readUnlock() {
        dhns.readUnlock();
    }

    public void readUnlockAll() {
        dhns.readUnlockAll();
    }

    public void writeLock() {
        dhns.writeLock();
    }

    public void writeUnlock() {
        dhns.writeUnlock();
    }

    public int getNodeVersion() {
        return dhns.getGraphVersion().getNodeVersion();
    }

    public int getEdgeVersion() {
        return dhns.getGraphVersion().getEdgeVersion();
    }

    protected AbstractNode checkNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node can't be null");
        }
        AbstractNode absNode = (AbstractNode) node;
        if (!absNode.isValid(view.getViewId())) {
            //Try to find the node in the proper view
            absNode = absNode.getInView(view.getViewId());
            if (absNode == null) {
                throw new IllegalArgumentException("Node must be in the graph");
            }
        }
        return absNode;
    }

    protected AbstractEdge checkEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("edge can't be null");
        }
        AbstractEdge abstractEdge = (AbstractEdge) edge;
        if (!abstractEdge.isValid()) {
            throw new IllegalArgumentException("Nodes must be in the graph");

        }
        if (abstractEdge.isMetaEdge()) {
            throw new IllegalArgumentException("Edge can't be a meta edge");
        }
        return abstractEdge;
    }

    protected MetaEdgeImpl checkMetaEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("edge can't be null");
        }
        AbstractEdge absEdge = (AbstractEdge) edge;
        if (!absEdge.isMetaEdge()) {
            throw new IllegalArgumentException("edge must be a meta edge");
        }
        if (!absEdge.isValid()) {
            throw new IllegalArgumentException("Nodes must be in the graph");
        }
        return (MetaEdgeImpl) absEdge;
    }

    protected AbstractEdge checkEdgeOrMetaEdge(Edge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("edge can't be null");
        }
        AbstractEdge absEdge = (AbstractEdge) edge;
        if (!absEdge.isValid()) {
            throw new IllegalArgumentException("Nodes must be in the graph");
        }
        return absEdge;
    }

    protected boolean checkEdgeExist(AbstractNode source, AbstractNode target) {
        if (source == null) {
            throw new IllegalArgumentException("Source node must be in the graph");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target node must be in the graph");
        }
        return source.getEdgesOutTree().hasNeighbour(target);
    }

    protected AbstractEdge getSymmetricEdge(AbstractEdge edge) {
        return edge.getTarget(view.getViewId()).getEdgesOutTree().getItem(edge.getSource().getNumber());
    }

    protected AbstractEdge getSymmetricMetaEdge(AbstractEdge edge) {
        return edge.getTarget(view.getViewId()).getMetaEdgesOutTree().getItem(edge.getSource().getNumber());
    }
}
