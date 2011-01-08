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
package org.gephi.graph.dhns.core;

import java.util.ArrayList;
import java.util.List;
import org.gephi.graph.api.GraphEvent.EventType;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.MetaEdgeImpl;
import org.gephi.graph.dhns.event.EdgeEvent;
import org.gephi.graph.dhns.event.GeneralEvent;
import org.gephi.graph.dhns.event.NodeEvent;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.AbstractNodeIterator;
import org.gephi.graph.dhns.node.iterators.ChildrenIterator;
import org.gephi.graph.dhns.node.iterators.DescendantAndSelfIterator;
import org.gephi.graph.dhns.node.iterators.DescendantIterator;
import org.gephi.graph.dhns.node.iterators.TreeIterator;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;
import org.gephi.graph.dhns.predicate.Tautology;

/**
 * Business class for external operations on the data structure. Propose blocking mechanism.
 *
 * @author Mathieu Bastian
 */
public class StructureModifier {

    private final Dhns dhns;
    private final GraphVersion graphVersion;
    private final TreeStructure treeStructure;
    private final GraphViewImpl view;
    private final EdgeProcessor edgeProcessor;
    //Business
    private final Business business;

    public StructureModifier(Dhns dhns, GraphViewImpl view) {
        this.dhns = dhns;
        this.view = view;
        this.treeStructure = view.getStructure();
        this.graphVersion = dhns.getGraphVersion();
        edgeProcessor = new EdgeProcessor(dhns, view);
        business = new Business();
    }

    public EdgeProcessor getEdgeProcessor() {
        return edgeProcessor;
    }

    public void expand(AbstractNode node) {
        dhns.writeLock();
        if (node.level < treeStructure.getTreeHeight()) {
            business.expand(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.EXPAND, node, view));
    }

    public void retract(AbstractNode node) {
        dhns.writeLock();
        if (node.level < treeStructure.getTreeHeight()) {
            business.retract(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.RETRACT, node, view));
    }

    public void addNode(AbstractNode node, AbstractNode parent) {
        dhns.writeLock();
        AbstractNode parentNode;
        if (parent == null) {
            parentNode = treeStructure.getRoot();
        } else {
            parentNode = (parent);
        }
        node.parent = parentNode;
        business.addNode(node);
        dhns.getGraphStructure().addToDictionnary(node);
        graphVersion.incNodeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.ADD_NODES, node, view));
    }

    public void deleteNode(AbstractNode node) {
        if (view.isMainView() && node.getNodeData().getNodes().getCount() > 1) {
            dhns.writeLock();
            for (AbstractNodeIterator itr = node.getNodeData().getNodes().iterator(); itr.hasNext();) {
                AbstractNode nodeInOtherView = itr.next();
                if (nodeInOtherView.getViewId() != view.getViewId()) {
                    GraphViewImpl otherView = nodeInOtherView.avlNode.getList().getView();
                    business.deleteNode(nodeInOtherView, otherView);
                }
            }
            AbstractNode[] deletesNodes = business.deleteNode(node, view);
            graphVersion.incNodeAndEdgeVersion();
            dhns.writeUnlock();
            for (int i = 0; i < deletesNodes.length; i++) {
                dhns.getEventManager().fireEvent(new NodeEvent(EventType.REMOVE_NODES, deletesNodes[i], view));
            }

        } else {
            dhns.writeLock();
            AbstractNode[] deletesNodes = business.deleteNode(node, view);
            graphVersion.incNodeAndEdgeVersion();
            dhns.writeUnlock();
            for (int i = 0; i < deletesNodes.length; i++) {
                dhns.getEventManager().fireEvent(new NodeEvent(EventType.REMOVE_NODES, deletesNodes[i], view));
            }
        }
    }

    public void addEdge(AbstractEdge edge) {
        dhns.writeLock();
        business.addEdge(edge);
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new EdgeEvent(EventType.ADD_EDGES, edge, view));
    }

    public boolean deleteEdge(AbstractEdge edge) {
        dhns.writeLock();
        boolean res = business.delEdge(edge);
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        if (res) {
            dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, edge, view));
        }
        return res;
    }

    public boolean deleteMetaEdge(AbstractEdge edge) {
        dhns.writeLock();
        boolean res = business.delMetaEdge((MetaEdgeImpl) edge);
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        return res;
    }

    public void clear() {
        dhns.writeLock();
        AbstractEdge[] clearedEdges = business.clearAllEdges();
        AbstractNode[] clearedNodes = business.clearAllNodes();
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        if (clearedEdges != null) {
            for (int i = 0; i < clearedEdges.length; i++) {
                if (clearedEdges[i] != null) {
                    dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, clearedEdges[i], view));
                }
            }
        }
        if (clearedNodes != null) {
            for (int i = 0; i < clearedNodes.length; i++) {
                if (clearedNodes[i] != null) {
                    dhns.getEventManager().fireEvent(new NodeEvent(EventType.REMOVE_NODES, clearedNodes[i], view));
                }
            }
        }
    }

    public void clearEdges() {
        dhns.writeLock();
        AbstractEdge[] clearedEdges = business.clearAllEdges();
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        if (clearedEdges != null) {
            for (int i = 0; i < clearedEdges.length; i++) {
                if (clearedEdges[i] != null) {
                    dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, clearedEdges[i], view));
                }
            }
        }
    }

    public void clearEdges(AbstractNode node) {
        dhns.writeLock();
        AbstractEdge[] clearedEdges = business.clearEdges(node);
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        if (clearedEdges != null) {
            for (int i = 0; i < clearedEdges.length; i++) {
                if (clearedEdges[i] != null) {
                    dhns.getGraphStructure().removeFromDictionnary(clearedEdges[i]);
                    dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, clearedEdges[i], view));
                }
            }
        }
    }

    public void clearMetaEdges(AbstractNode node) {
        dhns.writeLock();
        business.clearMetaEdges(node);
        graphVersion.incEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new GeneralEvent(EventType.META_EDGES_UPDATE, view));
    }

    public void resetViewToLeaves() {
        dhns.writeLock();
        edgeProcessor.clearAllMetaEdges();
        view.setNodesEnabled(0);
        for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
            AbstractNode node = itr.next();
            node.setEnabled(node.size == 0);
            if (node.isEnabled()) {
                view.incNodesEnabled(1);
            }
            edgeProcessor.resetEdgesCounting(node);
        }
        view.setEdgesCountEnabled(0);
        view.setMutualEdgesEnabled(0);
        for (TreeIterator itr = new TreeIterator(treeStructure, true, Tautology.instance); itr.hasNext();) {
            AbstractNode node = itr.next();
            edgeProcessor.computeMetaEdges(node, node);
            edgeProcessor.computeEdgesCounting(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new GeneralEvent(EventType.META_EDGES_UPDATE, view));
    }

    public void resetViewToTopNodes() {
        dhns.writeLock();
        edgeProcessor.clearAllMetaEdges();
        view.setNodesEnabled(0);
        for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
            AbstractNode node = itr.next();
            node.setEnabled(node.parent == treeStructure.root);
            if (node.isEnabled()) {
                view.incNodesEnabled(1);
            }
            edgeProcessor.resetEdgesCounting(node);
        }
        view.setEdgesCountEnabled(0);
        view.setMutualEdgesEnabled(0);
        for (TreeIterator itr = new TreeIterator(treeStructure, true, Tautology.instance); itr.hasNext();) {
            AbstractNode node = itr.next();
            edgeProcessor.computeMetaEdges(node, node);
            edgeProcessor.computeEdgesCounting(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new GeneralEvent(EventType.META_EDGES_UPDATE, view));
    }

    public void resetViewToLevel(int level) {
        dhns.writeLock();
        edgeProcessor.clearAllMetaEdges();
        view.setNodesEnabled(0);
        for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
            AbstractNode node = itr.next();
            node.setEnabled(node.level == level);
            if (node.isEnabled()) {
                view.incNodesEnabled(1);
            }
            edgeProcessor.resetEdgesCounting(node);
        }
        view.setEdgesCountEnabled(0);
        view.setMutualEdgesEnabled(0);
        for (TreeIterator itr = new TreeIterator(treeStructure, true, Tautology.instance); itr.hasNext();) {
            AbstractNode node = itr.next();
            edgeProcessor.computeMetaEdges(node, node);
            edgeProcessor.computeEdgesCounting(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new GeneralEvent(EventType.META_EDGES_UPDATE, view));
    }

    public void moveToGroup(AbstractNode node, AbstractNode nodeGroup) {
        dhns.writeLock();
        business.moveToGroup(node, nodeGroup);
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.MOVE_NODES, node, view));
    }

    public Node group(AbstractNode[] nodes) {
        dhns.writeLock();
        AbstractNode group = dhns.factory().newNode(view.getViewId());
        business.group(group, nodes);
        graphVersion.incNodeAndEdgeVersion();
        dhns.getGraphStructure().addToDictionnary(group);
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.ADD_NODES, group, view));
        for (int i = 0; i < nodes.length; i++) {
            dhns.getEventManager().fireEvent(new NodeEvent(EventType.MOVE_NODES, nodes[i], view));
        }
        return group;
    }

    public void ungroup(AbstractNode nodeGroup) {
        dhns.writeLock();
        AbstractNode[] ungroupedNodes = business.ungroup(nodeGroup);
        graphVersion.incNodeAndEdgeVersion();
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new NodeEvent(EventType.REMOVE_NODES, nodeGroup, view));
        for (int i = 0; i < ungroupedNodes.length; i++) {
            dhns.getEventManager().fireEvent(new NodeEvent(EventType.MOVE_NODES, ungroupedNodes[i], view));
        }
    }

    public void flatten() {
        dhns.writeLock();
        if (treeStructure.getTreeHeight() > 1) {
            TreeIterator nodesIterator = new TreeIterator(treeStructure, true, Tautology.instance);
            for (; nodesIterator.hasNext();) {
                AbstractNode node = nodesIterator.next();
                AbstractEdge[] newEdges = edgeProcessor.flattenNode(node);
                if (newEdges != null) {
                    for (int i = 0; i < newEdges.length; i++) {
                        AbstractEdge e = newEdges[i];
                        if (e != null) {
                            dhns.getGraphStructure().addToDictionnary(e);
                            dhns.getEventManager().fireEvent(new EdgeEvent(EventType.ADD_EDGES, e, view));
                        }
                    }
                }
            }

            List<AbstractNode> nodesToDelete = new ArrayList<AbstractNode>();
            List<AbstractNode> nodesToKeep = new ArrayList<AbstractNode>();

            for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
                AbstractNode node = itr.next();
                if (!node.isEnabled()) {
                    nodesToDelete.add(node);
                } else {
                    nodesToKeep.add(node);
                }
            }

            for (AbstractNode node : nodesToDelete) {
                //Del edges
                AbstractEdge[] deletedEdges = edgeProcessor.clearEdges(node);
                if (deletedEdges != null) {
                    for (int j = 0; j < deletedEdges.length; j++) {
                        if (deletedEdges[j] != null) {
                            dhns.getGraphStructure().removeFromDictionnary(deletedEdges[j]);
                            dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, deletedEdges[j], view));
                        }
                    }
                }

                dhns.getGraphStructure().removeFromDictionnary(node);


                treeStructure.deleteOnlySelf(node);
                dhns.getEventManager().fireEvent(new NodeEvent(EventType.REMOVE_NODES, node, view));
            }

            for (AbstractNode node : nodesToKeep) {
                node.size = 0;
                node.parent = treeStructure.root;
                node.level = 1;
                node.getPost();
            }
            treeStructure.root.size = nodesToKeep.size();
            treeStructure.root.getPost();
            treeStructure.resetLevelSize(nodesToKeep.size());

            graphVersion.incNodeAndEdgeVersion();
        }
        dhns.writeUnlock();
        dhns.getEventManager().fireEvent(new GeneralEvent(EventType.META_EDGES_UPDATE, view));
    }

    //------------------------------------------
    private class Business {

        private void expand(AbstractNode absNode) {

            //Disable parent
            absNode.setEnabled(false);
            view.decNodesEnabled(1);
            edgeProcessor.clearMetaEdges(absNode);

            //Enable children
            for (ChildrenIterator itr = new ChildrenIterator(treeStructure, absNode, Tautology.instance); itr.hasNext();) {
                AbstractNode child = itr.next();
                child.setEnabled(true);
                view.incNodesEnabled(1);
                edgeProcessor.computeMetaEdges(child, child);
            }

            //Update counting
            for (ChildrenIterator itr = new ChildrenIterator(treeStructure, absNode, Tautology.instance); itr.hasNext();) {
                AbstractNode child = itr.next();
                edgeProcessor.incrementEdgesCounting(child, absNode);
            }

            edgeProcessor.decrementEdgesCouting(absNode, null);
        }

        private void retract(AbstractNode parent) {
            //Disable children
            for (ChildrenIterator itr = new ChildrenIterator(treeStructure, parent, Tautology.instance); itr.hasNext();) {
                AbstractNode child = itr.next();
                child.setEnabled(false);
                view.decNodesEnabled(1);
                edgeProcessor.clearMetaEdges(child);
            }

            //Enable node
            parent.setEnabled(true);
            view.incNodesEnabled(1);
            edgeProcessor.computeMetaEdges(parent, parent);

            //Edges counting
            for (ChildrenIterator itr = new ChildrenIterator(treeStructure, parent, Tautology.instance); itr.hasNext();) {
                AbstractNode child = itr.next();
                edgeProcessor.decrementEdgesCouting(child, parent);
            }
            edgeProcessor.incrementEdgesCounting(parent, null);
        }

        private void addNode(AbstractNode node) {
            boolean enabled = (treeStructure.getEnabledAncestor(node) == null);
            node.setEnabled(enabled);

            treeStructure.insertAsChild(node, node.parent);

            if (node.isEnabled()) {
                view.incNodesEnabled(1);
            }
        }

        private void addEdge(AbstractEdge edge) {
            AbstractNode sourceNode = edge.getSource(view.getViewId());
            AbstractNode targetNode = edge.getTarget(view.getViewId());

            boolean enabled = sourceNode.isEnabled() && targetNode.isEnabled();

            //Add Edges
            sourceNode.getEdgesOutTree().add(edge);
            targetNode.getEdgesInTree().add(edge);

            if (!edge.isSelfLoop() && sourceNode.getEdgesInTree().hasNeighbour(targetNode)) {
                //Mututal edge
                view.incMutualEdgesTotal(1);
                if (enabled) {
                    sourceNode.incEnabledMutualDegree();
                    targetNode.incEnabledMutualDegree();
                    view.incMutualEdgesEnabled(1);
                }
            }

            view.incEdgesCountTotal(1);
            if (enabled) {
                view.incEdgesCountEnabled(1);
                sourceNode.incEnabledOutDegree();
                targetNode.incEnabledInDegree();
            }

            dhns.getGraphStructure().addToDictionnary(edge);

            //Add Meta Edge
            if (!edge.isSelfLoop()) {
                edgeProcessor.createMetaEdge(edge);
            }
        }

        private AbstractNode[] deleteNode(AbstractNode node, GraphViewImpl graphView) {
            AbstractNode[] descendants = new AbstractNode[node.size + 1];
            int i = 0;
            for (DescendantAndSelfIterator itr = new DescendantAndSelfIterator(graphView.getStructure(), node, Tautology.instance); itr.hasNext();) {
                AbstractNode descendant = itr.next();
                descendants[i] = descendant;
                if (descendant.isEnabled()) {
                    graphView.getStructureModifier().edgeProcessor.clearMetaEdges(descendant);
                    graphView.decNodesEnabled(1);
                }
                AbstractEdge[] deletedEdges = graphView.getStructureModifier().edgeProcessor.clearEdges(descendant);
                if (deletedEdges != null) {
                    for (int j = 0; j < deletedEdges.length; j++) {
                        if (deletedEdges[j] != null) {
                            dhns.getGraphStructure().removeFromDictionnary(deletedEdges[j]);
                            dhns.getEventManager().fireEvent(new EdgeEvent(EventType.REMOVE_EDGES, deletedEdges[j], graphView));
                        }
                    }
                }
                dhns.getGraphStructure().removeFromDictionnary(descendant);

                i++;
            }

            graphView.getStructure().deleteDescendantAndSelf(node);
            return descendants;
        }

        private boolean delEdge(AbstractEdge edge) {
            AbstractNode source = edge.getSource(view.getViewId());
            AbstractNode target = edge.getTarget(view.getViewId());

            boolean enabled = source.isEnabled() && target.isEnabled();

            if (!edge.isSelfLoop() && source.getEdgesInTree().hasNeighbour(target)) {
                //mutual
                if (enabled) {
                    view.decMutualEdgesEnabled(1);
                    source.decEnabledMutualDegree();
                    target.decEnabledMutualDegree();
                }
                view.decMutualEdgesTotal(1);
            }

            view.decEdgesCountTotal(1);
            if (enabled) {
                view.decEdgesCountEnabled(1);
                source.decEnabledOutDegree();
                target.decEnabledInDegree();
            }

            //Remove edge
            boolean res = source.getEdgesOutTree().remove(edge);
            res = res && target.getEdgesInTree().remove(edge);

            dhns.getGraphStructure().removeFromDictionnary(edge);

            //Remove edge from possible metaEdge
            edgeProcessor.removeEdgeFromMetaEdge(edge);
            return res;
        }

        public boolean delMetaEdge(MetaEdgeImpl edge) {
            AbstractNode source = edge.getSource(view.getViewId());
            AbstractNode target = edge.getTarget(view.getViewId());

            if (!edge.isSelfLoop() && source.getEdgesInTree().hasNeighbour(target)) {
                //mutual
                view.decMutualMetaEdgesTotal(1);
                source.decMutualMetaEdgeDegree();
                target.decMutualMetaEdgeDegree();

            }
            view.decMetaEdgesCount(1);

            //Remove edge
            boolean res = source.getMetaEdgesOutTree().remove(edge);
            res = res && target.getMetaEdgesInTree().remove(edge);

            return res;
        }

        private AbstractEdge[] clearAllEdges() {
            return edgeProcessor.clearAllEdges();
        }

        private AbstractNode[] clearAllNodes() {
            AbstractNode[] deletedNodes = new AbstractNode[treeStructure.getTreeSize() - 1];
            int n = 0;
            for (TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1); itr.hasNext();) {
                AbstractNode node = itr.next();
                node.getNodeData().getNodes().remove(view.getViewId());
                dhns.getGraphStructure().removeFromDictionnary(node);
                deletedNodes[n++] = node;
            }
            treeStructure.clear();
            view.setNodesEnabled(0);
            return deletedNodes;
        }

        private AbstractEdge[] clearEdges(AbstractNode node) {
            return edgeProcessor.clearEdges(node);
        }

        private void clearMetaEdges(AbstractNode node) {
            edgeProcessor.clearMetaEdges(node);
        }

        private void group(AbstractNode group, AbstractNode[] nodes) {
            group.setEnabled(true);
            AbstractNode parent = ((AbstractNode) nodes[0]).parent;
            //parent = parent.getInView(view.getViewId());
            group.parent = parent;
            business.addNode(group);
            for (int i = 0; i < nodes.length; i++) {
                AbstractNode nodeToGroup = nodes[i];
                nodeToGroup = nodeToGroup.getInView(view.getViewId());
                business.moveToGroup(nodeToGroup, group);
            }
        }

        private AbstractNode[] ungroup(AbstractNode nodeGroup) {
            //TODO Better implementation. Just remove nodeGroup from the treelist and lower level of children
            int count = 0;
            for (ChildrenIterator itr = new ChildrenIterator(treeStructure, nodeGroup, Tautology.instance); itr.hasNext();) {
                itr.next();
                count++;
            }

            AbstractNode[] ungroupedNodes = new AbstractNode[count];

            if (nodeGroup.isEnabled()) {
                business.expand(nodeGroup);
            }
            for (int i = 0; i < count; i++) {
                AbstractNode node = treeStructure.getNodeAt(nodeGroup.getPre() + 1);
                business.moveToGroup(node, nodeGroup.parent);
                ungroupedNodes[i] = node;
            }

            business.deleteNode(nodeGroup, view);
            return ungroupedNodes;
        }

        private void moveToGroup(AbstractNode node, AbstractNode nodeGroup) {

            AbstractNode toMoveAncestor = treeStructure.getEnabledAncestor(node);
            AbstractNode destinationAncestor = treeStructure.getEnabledAncestorOrSelf(nodeGroup);

            if (toMoveAncestor != destinationAncestor) {
                if (toMoveAncestor != null) {
                    //The node has an enabled ancestor
                    //We delete edges from potential meta edges
                    if (node.size > 0) {
                        for (DescendantAndSelfIterator itr = new DescendantAndSelfIterator(treeStructure, node, Tautology.instance); itr.hasNext();) {
                            AbstractNode descendant = itr.next();
                            edgeProcessor.clearEdgesWithoutRemove(descendant);
                        }
                    } else {
                        edgeProcessor.clearEdgesWithoutRemove(node);
                    }
                } else if (node.isEnabled()) {
                    //The node is enabled
                    if (destinationAncestor != null) {
                        //The destination is enabled or has enabled ancestor
                        //Node is thus disabled
                        edgeProcessor.clearMetaEdges(node);
                        node.setEnabled(false);
                        view.decNodesEnabled(1);
                        edgeProcessor.decrementEdgesCouting(node, null);
                        //DO
                    } else {
                        //The node is kept enabled
                        //Meta edges are still valid only if their target is out of the dest cluster
                        edgeProcessor.clearMetaEdgesOutOfRange(node, nodeGroup);
                    }
                } else if (node.size > 0) {
                    if (destinationAncestor != null) {
                        //The node may have some enabled descendants and we set them disabled
                        for (DescendantIterator itr = new DescendantIterator(treeStructure, node, Tautology.instance); itr.hasNext();) {
                            AbstractNode descendant = itr.next();
                            if (descendant.isEnabled()) {
                                edgeProcessor.clearMetaEdges(descendant);
                                descendant.setEnabled(false);
                                view.decNodesEnabled(1);
                                edgeProcessor.decrementEdgesCouting(descendant, null);
                                //TODO
                            }
                        }
                        //DO
                    } else {
                        //The node may have some enabled descendants and we keep them enabled
                        for (DescendantIterator itr = new DescendantIterator(treeStructure, node, Tautology.instance); itr.hasNext();) {
                            AbstractNode descendant = itr.next();
                            if (descendant.isEnabled()) {
                                //Enabled descendants meta edges are still valid only if their target is out of
                                //the destination cluster
                                edgeProcessor.clearMetaEdgesOutOfRange(node, nodeGroup);
                            }
                        }
                    }

                }
            }

            treeStructure.move(node, nodeGroup);

            if (destinationAncestor != null) {
                destinationAncestor.getPre();
                //Compute all meta edges for the descendants of node and append them to the enabled
                //destinationAncestor
                edgeProcessor.computeMetaEdges(node, destinationAncestor);
            }
        }
        /*
        private void cloneDescedantAndSelft(AbstractNode node, AbstractNode parentNode) {
        if (node.size > 0) {
        DescendantAndSelfIterator itr = new DescendantAndSelfIterator(treeStructure, node, Tautology.instance);
        for (; itr.hasNext();) {
        AbstractNode desc = itr.next();
        CloneNode clone = new CloneNode(desc);
        if (desc == node) {
        //Parent is the given parentNode
        clone.parent = parentNode;
        } else {
        clone.parent = desc.parent.getOriginalNode().getClones();       //The last clone added
        }
        addNode(clone);
        }
        } else {
        CloneNode clone = new CloneNode(node);
        clone.parent = parentNode;
        addNode(clone);
        }
        }

        private void unAttachClones(CloneNode node) {
        for (int i = node.getPre(); i <= node.pre + node.size; i++) {
        AbstractNode n = treeStructure.getNodeAt(i);
        if (n.isClone()) {
        n.getOriginalNode().removeClone((CloneNode) n);
        }
        }
        }*/
    }
}
