/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.graph.dhns.core;

import org.gephi.graph.api.GraphEvent.EventType;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.iterators.ChildrenIterator;
import org.gephi.graph.dhns.node.iterators.DescendantAndSelfIterator;
import org.gephi.graph.dhns.node.iterators.DescendantIterator;
import org.gephi.graph.dhns.node.iterators.TreeIterator;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;
import org.gephi.graph.dhns.predicate.Tautology;
import org.gephi.utils.collection.avl.ParamAVLIterator;

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
        dhns.getWriteLock().lock();
        if (node.level < treeStructure.getTreeHeight()) {
            business.expand(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void retract(AbstractNode node) {
        dhns.getWriteLock().lock();
        if (node.level < treeStructure.getTreeHeight()) {
            business.retract(node);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void addNode(AbstractNode node, AbstractNode parent) {
        dhns.getWriteLock().lock();
        AbstractNode parentNode;
        if (parent == null) {
            parentNode = treeStructure.getRoot();
        } else {
            parentNode = (parent);
        }
        node.parent = parentNode;
        boolean enabled = (treeStructure.getEnabledAncestor(node) == null);
        node.setEnabled(enabled);
        business.addNode(node);
        if (node.isEnabled()) {
            view.incNodesEnabled(1);
        }
        graphVersion.incNodeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_UPDATED);
    }

    public void deleteNode(AbstractNode node) {
        dhns.getWriteLock().lock();
        business.deleteNode(node);
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void addEdge(AbstractEdge edge) {
        dhns.getWriteLock().lock();
        business.addEdge(edge);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.EDGES_UPDATED);
    }

    public boolean deleteEdge(AbstractEdge edge) {
        dhns.getWriteLock().lock();
        boolean res = business.delEdge(edge);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.EDGES_UPDATED);
        return res;
    }

    public void clear() {
        dhns.getWriteLock().lock();
        business.clearAllEdges();
        business.clearAllNodes();
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void clearEdges() {
        dhns.getWriteLock().lock();
        business.clearAllEdges();
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.EDGES_UPDATED);
    }

    public void clearEdges(AbstractNode node) {
        dhns.getWriteLock().lock();
        business.clearEdges(node);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.EDGES_UPDATED);
    }

    public void clearMetaEdges(AbstractNode node) {
        dhns.getWriteLock().lock();
        business.clearMetaEdges(node);
        graphVersion.incEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.EDGES_UPDATED);
    }

    public void resetViewToLeaves() {
        dhns.getWriteLock().lock();
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
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void resetViewToTopNodes() {
        dhns.getWriteLock().lock();
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
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void resetViewToLevel(int level) {
        dhns.getWriteLock().lock();
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
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public void moveToGroup(AbstractNode node, AbstractNode nodeGroup) {
        dhns.getWriteLock().lock();
        business.moveToGroup(node, nodeGroup);
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
    }

    public Node group(Node[] nodes) {
        dhns.getWriteLock().lock();
        AbstractNode group = dhns.factory().newNode();
        group.setEnabled(true);
        view.incNodesEnabled(1);
        AbstractNode parent = ((AbstractNode) nodes[0]).parent;
        group.parent = parent;
        business.addNode(group);
        for (int i = 0; i < nodes.length; i++) {
            AbstractNode nodeToGroup = (AbstractNode) nodes[i];
            business.moveToGroup(nodeToGroup, group);
        }
        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
        return group;
    }

    public void ungroup(AbstractNode nodeGroup) {
        dhns.getWriteLock().lock();
        //TODO Better implementation. Just remove nodeGroup from the treelist and lower level of children
        int count = 0;
        for (ChildrenIterator itr = new ChildrenIterator(treeStructure, nodeGroup, Tautology.instance); itr.hasNext();) {
            itr.next();
            count++;
        }

        if (nodeGroup.isEnabled()) {
            business.expand(nodeGroup);
        }
        for (int i = 0; i < count; i++) {
            AbstractNode node = treeStructure.getNodeAt(nodeGroup.getPre() + 1);
            view.getStructureModifier().moveToGroup(node, nodeGroup.parent);
        }

        business.deleteNode(nodeGroup);

        graphVersion.incNodeAndEdgeVersion();
        dhns.getWriteLock().unlock();
        dhns.getEventManager().fireEvent(EventType.NODES_AND_EDGES_UPDATED);
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
            treeStructure.insertAsChild(node, node.parent);
            dhns.getGraphStructure().addToDictionnary(node);
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

        private void deleteNode(AbstractNode node) {

            for (DescendantAndSelfIterator itr = new DescendantAndSelfIterator(treeStructure, node, Tautology.instance); itr.hasNext();) {
                AbstractNode descendant = itr.next();
                if (descendant.isEnabled()) {
                    edgeProcessor.clearMetaEdges(descendant);
                    view.decNodesEnabled(1);
                }
                edgeProcessor.clearEdges(descendant);

                if (node.countInViews() == 1) {
                    dhns.getGraphStructure().removeFromDictionnary(descendant);
                }
            }

            treeStructure.deleteDescendantAndSelf(node);
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

        private void clearAllEdges() {
            edgeProcessor.clearAllEdges();
        }

        private void clearAllNodes() {
            treeStructure.clear();
            view.setNodesEnabled(0);
            dhns.getGraphStructure().clearNodeDictionnary();
        }

        private void clearEdges(AbstractNode node) {
            edgeProcessor.clearEdges(node);
        }

        private void clearMetaEdges(AbstractNode node) {
            edgeProcessor.clearMetaEdges(node);
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
