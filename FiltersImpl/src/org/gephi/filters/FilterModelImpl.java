/*
Copyright 2008-2011 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Sébastien Heymann <sebastien.heymann@gephi.org>
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gephi.filters.api.FilterLibrary;
import org.gephi.filters.api.FilterModel;
import org.gephi.filters.api.Query;
import org.gephi.filters.spi.Filter;
import org.gephi.filters.spi.FilterBuilder;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class FilterModelImpl implements FilterModel {

    private FilterLibraryImpl filterLibraryImpl;
    private LinkedList<Query> queries;
    private FilterThread filterThread;
    private GraphModel graphModel;
    private Query currentQuery;
    private boolean filtering;
    private boolean selecting;
    private GraphView currentResult;
    private boolean autoRefresh;
    private FilterAutoRefreshor autoRefreshor;
    //Listeners
    private List<ChangeListener> listeners;

    public FilterModelImpl(Workspace workspace) {
        filterLibraryImpl = new FilterLibraryImpl();
        queries = new LinkedList<Query>();
        listeners = new ArrayList<ChangeListener>();
        autoRefresh = true;

        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
        autoRefreshor = new FilterAutoRefreshor(this, graphModel);
    }

    public FilterLibrary getLibrary() {
        return filterLibraryImpl;
    }

    public Query[] getQueries() {
        return queries.toArray(new Query[0]);
    }

    public boolean hasQuery(Query query) {
        for (Query q : getQueries()) {
            if (q == query) {
                return true;
            }
        }
        return false;
    }

    public void addFirst(Query function) {
        queries.addFirst(function);
        currentQuery = function;
        fireChangeEvent();
    }

    public void addLast(Query function) {
        queries.addLast(function);
        fireChangeEvent();
    }

    public void set(int index, Query function) {
        queries.set(index, function);
    }

    public void remove(Query query) {
        if (query == currentQuery) {
            currentQuery = query.getParent();
        }
        queries.remove(query);
        destroyQuery(query);
        fireChangeEvent();
    }

    public void rename(Query query, String name) {
        ((AbstractQueryImpl) query).setName(name);
        fireChangeEvent();
    }

    public void setSubQuery(Query query, Query subQuery) {
        //Clean
        if (queries.contains(subQuery)) {
            queries.remove(subQuery);
        }
        if (subQuery.getParent() != null) {
            ((AbstractQueryImpl) subQuery.getParent()).removeSubQuery(subQuery);
        }
        if (subQuery == currentQuery) {
            currentQuery = ((AbstractQueryImpl) query).getRoot();
        }

        //Set
        AbstractQueryImpl impl = (AbstractQueryImpl) query;
        impl.addSubQuery(subQuery);
        fireChangeEvent();
        autoRefreshor.manualRefresh();
    }

    public void removeSubQuery(Query query, Query parent) {
        AbstractQueryImpl impl = (AbstractQueryImpl) parent;
        impl.removeSubQuery(query);
        ((AbstractQueryImpl) query).setParent(null);
        if (query == currentQuery) {
            currentQuery = parent;
        }
        fireChangeEvent();
        autoRefreshor.manualRefresh();
    }

    public int getIndex(Query function) {
        int i = 0;
        for (Query f : queries) {
            if (f == function) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean isFiltering() {
        return currentQuery != null && filtering;
    }

    public boolean isSelecting() {
        return currentQuery != null && selecting;
    }

    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
        if (filtering) {
            this.selecting = false;
        }
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
        if (selecting) {
            this.filtering = false;
        }
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        fireChangeEvent();
        if (!autoRefresh) {
            autoRefreshor.setEnable(false);
        } else if (autoRefresh && currentResult != null) {
            autoRefreshor.setEnable(true);
        }
    }

    public Query getCurrentQuery() {
        return currentQuery;
    }

    public void setCurrentQuery(Query currentQuery) {
        if (currentQuery != null) {
            currentQuery = ((AbstractQueryImpl) currentQuery).getRoot();
        }
        if (this.currentQuery != currentQuery) {
            this.currentQuery = currentQuery;
            fireChangeEvent();
        }
    }

    public void updateParameters(Query query) {
        if (query instanceof FilterQueryImpl) {
            ((FilterQueryImpl) query).updateParameters();
            fireChangeEvent();
        }
    }

    public Query getQuery(Filter filter) {
        for (Query q : getAllQueries()) {
            if (filter == q.getFilter()) {
                return q;
            }
        }
        return null;
    }

    public Query[] getAllQueries() {
        List<Query> result = new ArrayList<Query>();
        LinkedList<Query> stack = new LinkedList<Query>();
        stack.addAll(queries);
        while (!stack.isEmpty()) {
            Query q = stack.pop();
            result.add(q);
            for (Query child : q.getChildren()) {
                stack.add(child);
            }
        }
        return result.toArray(new Query[0]);
    }

    public FilterThread getFilterThread() {
        return filterThread;
    }

    public FilterAutoRefreshor getAutoRefreshor() {
        return autoRefreshor;
    }

    public void setFilterThread(FilterThread filterThread) {
        this.filterThread = filterThread;
    }

    public void setCurrentResult(GraphView currentResult) {
        this.currentResult = currentResult;
        if (currentResult != null && autoRefresh) {
            autoRefreshor.setEnable(true);
        } else if (currentResult == null && autoRefresh) {
            autoRefreshor.setEnable(false);
        }
    }

    public GraphView getCurrentResult() {
        return currentResult;
    }

    public GraphModel getGraphModel() {
        return graphModel;
    }

    public void destroy() {
        if (filterThread != null) {
            filterThread.setRunning(false);
        }
        autoRefreshor.setRunning(false);
        currentResult = null;
        listeners = null;
        for (Query q : queries) {
            destroyQuery(q);
        }
    }

    private void destroyQuery(Query query) {
        if (query instanceof AbstractQueryImpl) {
            AbstractQueryImpl absQuery = (AbstractQueryImpl) query;
            for (Query q : absQuery.getDescendantsAndSelf()) {
                if (q instanceof FilterQueryImpl) {
                    Filter f = ((FilterQueryImpl) q).getFilter();
                    FilterBuilder builder = filterLibraryImpl.getBuilder(f);
                    if (builder != null) {
                        builder.destroy(f);
                    }
                }
            }
        }
    }

    //EVENTS
    public void addChangeListener(ChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(ChangeListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(evt);
        }
    }
}
