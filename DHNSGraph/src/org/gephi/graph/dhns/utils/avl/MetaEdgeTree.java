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
package org.gephi.graph.dhns.utils.avl;

import org.gephi.graph.dhns.edge.MetaEdgeImpl;
import org.gephi.utils.collection.avl.AVLItemAccessor;
import org.gephi.utils.collection.avl.ParamAVLTree;
import org.gephi.graph.dhns.node.AbstractNode;

/**
 * Same behaviour as {@link EdgeTree} but with {@link MetaEdgeImpl}.
 *
 * @author Mathieu Bastian
 */
public class MetaEdgeTree extends ParamAVLTree<MetaEdgeImpl> {

    private AbstractNode owner;

    public MetaEdgeTree(AbstractNode owner) {
        super();
        this.owner = owner;
        setAccessor(new MetaEdgeImplAVLItemAccessor());
    }

    public AbstractNode getOwner() {
        return owner;
    }

    public boolean hasNeighbour(AbstractNode node) {
        return getItem(node.getNumber()) != null;
    }

    private class MetaEdgeImplAVLItemAccessor implements AVLItemAccessor<MetaEdgeImpl> {

        @Override
        public int getNumber(MetaEdgeImpl item) {
            if (item.getSource() == owner) {
                return item.getTarget().getNumber();
            } else {
                return item.getSource().getNumber();
            }
        }
    }
}
