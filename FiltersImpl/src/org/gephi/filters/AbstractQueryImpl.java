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
package org.gephi.filters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.gephi.filters.api.Query;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.Operator;
import org.gephi.graph.api.Graph;

/**
 *
 * @author Mathieu Bastian
 */
public abstract class AbstractQueryImpl implements Query {

    protected List<AbstractQueryImpl> children;
    protected Query parent;
    protected Graph result;

    public AbstractQueryImpl() {
        this.children = new ArrayList<AbstractQueryImpl>();
    }

    public abstract int getChildrenSlotsCount();

    public abstract int getPropertiesCount();

    public abstract String getPropertyName(int index);

    public abstract Object getPropertyValue(int index);

    public abstract String getName();

    public abstract void setName(String name);

    public Query[] getChildren() {
        return children.toArray(new Query[0]);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public AbstractQueryImpl getChildAt(int index) {
        return children.get(index);
    }

    public void addSubQuery(Query subQuery) {
        children.add((AbstractQueryImpl) subQuery);
        ((AbstractQueryImpl) subQuery).setParent(this);
    }

    public void removeSubQuery(Query subQuery) {
        children.remove((AbstractQueryImpl) subQuery);
    }

    public Query getParent() {
        return parent;
    }

    public void setParent(Query parent) {
        this.parent = parent;
    }

    public void setResult(Graph result) {
        this.result = result;
    }

    public Graph getResult() {
        return result;
    }

    public AbstractQueryImpl getRoot() {
        AbstractQueryImpl root = this;
        while (root.getParent() != null) {
            root = (AbstractQueryImpl) root.getParent();
        }
        return root;
    }

    public AbstractQueryImpl[] getLeaves() {
        ArrayList<AbstractQueryImpl> leaves = new ArrayList<AbstractQueryImpl>();
        Deque<Query> stack = new ArrayDeque<Query>();
        stack.add(this);
        while (!stack.isEmpty()) {
            AbstractQueryImpl query = (AbstractQueryImpl) stack.pop();
            if (query.children.size() > 0) {
                stack.addAll(query.children);
            } else {
                //Leaf
                leaves.add(query);
            }
        }
        return leaves.toArray(new AbstractQueryImpl[0]);
    }

    public AbstractQueryImpl copy() {
        AbstractQueryImpl copy = null;
        if (this instanceof FilterQueryImpl) {
            copy = new FilterQueryImpl(this.getFilter());
        } else if (this instanceof OperatorQueryImpl) {
            copy = new OperatorQueryImpl((Operator) this.getFilter());
        }

        for (int i = 0; i < children.size(); i++) {
            AbstractQueryImpl child = (AbstractQueryImpl) children.get(i);
            AbstractQueryImpl childCopy = child.copy();
            childCopy.parent = copy;
            copy.children.add(childCopy);
        }

        return copy;
    }

    public Query[] getQueries(Class<? extends Filter> filterClass) {
        List<Query> r = new LinkedList<Query>();
        LinkedList<Query> stack = new LinkedList<Query>();
        stack.add(this);
        while (!stack.isEmpty()) {
            Query q = stack.pop();
            r.add(q);
            stack.addAll(Arrays.asList(q.getChildren()));
        }
        for (Iterator<Query> itr = r.iterator(); itr.hasNext();) {
            Query q = itr.next();
            if (!q.getFilter().getClass().equals(filterClass)) {
                itr.remove();
            }
        }
        return r.toArray(new Query[0]);
    }

    public Query[] getDescendantsAndSelf() {
        List<Query> r = new LinkedList<Query>();
        LinkedList<Query> stack = new LinkedList<Query>();
        stack.add(this);
        while (!stack.isEmpty()) {
            Query q = stack.pop();
            r.add(q);
            stack.addAll(Arrays.asList(q.getChildren()));
        }
        return r.toArray(new Query[0]);
    }
}
