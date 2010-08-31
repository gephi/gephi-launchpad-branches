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
package org.gephi.graph.dhns.node.iterators;

import java.util.Iterator;
import org.gephi.graph.api.Node;
import org.gephi.graph.dhns.core.DurableTreeList;
import org.gephi.graph.dhns.core.DurableTreeList.DurableAVLNode;
import org.gephi.graph.dhns.core.TreeStructure;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.predicate.Predicate;

/**
 * Iterates over nodes for a give level.
 *
 * @author Mathieu Bastian
 */
public class LevelIterator extends AbstractNodeIterator implements Iterator<Node> {

    protected DurableTreeList treeList;
    protected int nextIndex;
    protected int diffIndex;
    protected int treeSize;
    protected DurableAVLNode currentNode;
    protected int level;

    //Proposition
    protected Predicate<AbstractNode> predicate;

    public LevelIterator(TreeStructure treeStructure, int level, Predicate<AbstractNode> predicate) {
        this.treeList = treeStructure.getTree();
        this.nextIndex = 1;
        this.diffIndex = 2;
        this.treeSize = treeList.size();
        this.level = level;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        while (true) {
            if (nextIndex < treeSize) {
                if (diffIndex > 1) {
                    currentNode = treeList.getNode(nextIndex);
                } else {
                    currentNode = currentNode.next();
                }

                while (currentNode.getValue().level != level) {
                    ++nextIndex;
                    if (nextIndex >= treeSize) {
                        return false;
                    }
                    currentNode = currentNode.next();
                }

                if (!predicate.evaluate(currentNode.getValue())) {
                    nextIndex = currentNode.getValue().getPre() + 1 + currentNode.getValue().size;
                    diffIndex = nextIndex - currentNode.getValue().pre;
                } else {
                    return true;
                }

            } else {
                return false;
            }
        }
    }

    @Override
    public AbstractNode next() {
        nextIndex = currentNode.getValue().getPre() + 1 + currentNode.getValue().size;
        diffIndex = nextIndex - currentNode.getValue().pre;
        return currentNode.getValue();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
