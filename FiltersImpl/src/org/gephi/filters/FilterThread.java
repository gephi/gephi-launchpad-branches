/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.filters;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.gephi.filters.api.PropertyExecutor.Callback;
import org.gephi.filters.api.Query;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.utils.progress.ProgressTicketProvider;
import org.gephi.visualization.api.VisualizationController;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class FilterThread extends Thread {

    private FilterModelImpl model;
    private AtomicReference<AbstractQueryImpl> rootQuery;
    ConcurrentHashMap<String, PropertyModifier> modifiersMap;
    private boolean running = true;
    private final Object lock = new Object();
    private final boolean filtering;

    public FilterThread(FilterModelImpl model) {
        super("Filter Thread");
        this.model = model;
        this.filtering = model.isFiltering();
        rootQuery = new AtomicReference<AbstractQueryImpl>();
        modifiersMap = new ConcurrentHashMap<String, PropertyModifier>();
    }

    @Override
    public void run() {

        while (running) {
            AbstractQueryImpl q;
            while ((q = rootQuery.getAndSet(null)) == null && running) {
                try {
                    synchronized (this.lock) {
                        lock.wait();
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if(!running) {
                return;
            }
            Query modifiedQuery = null;
            for (Iterator<PropertyModifier> itr = modifiersMap.values().iterator(); itr.hasNext();) {
                PropertyModifier pm = itr.next();
                itr.remove();
                pm.callback.setValue(pm.value);
                modifiedQuery = pm.query;
            }
            if (modifiedQuery != null) {
                model.updateParameters(modifiedQuery);
            }

            //Progress
            ProgressTicket progressTicket = null;
            ProgressTicketProvider progressTicketProvider = Lookup.getDefault().lookup(ProgressTicketProvider.class);
            if (progressTicketProvider != null) {
                progressTicket = progressTicketProvider.createTicket("Filtering", null);
                Progress.start(progressTicket);
            }

            if (filtering) {
                filter(q);
            } else {
                select(q);
            }

            Progress.finish(progressTicket);
            /*try {
            //System.out.println("filter query " + q.getName());
            Thread.sleep(5000);
            } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            }*/
        }
        //clear map
        Query q = null;
        for (Iterator<PropertyModifier> itr = modifiersMap.values().iterator(); itr.hasNext();) {
            PropertyModifier pm = itr.next();
            pm.callback.setValue(pm.value);
            q = pm.query;
        }
        modifiersMap.clear();
        if (q != null) {
            model.updateParameters(q);
        }
    }

    private void filter(AbstractQueryImpl query) {
        FilterProcessor processor = new FilterProcessor();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        Graph result = processor.process((AbstractQueryImpl) query, graphModel);
//        System.out.println("#Nodes: " + result.getNodeCount());
//        System.out.println("#Edges: " + result.getEdgeCount());
        if (running) {
            GraphView view = result.getView();
            graphModel.setVisibleView(view);
            if (model.getCurrentResult() != null) {
                graphModel.destroyView(model.getCurrentResult());
            }
            model.setCurrentResult(view);
        } else {
            //destroy view
            graphModel.destroyView(result.getView());
        }
    }

    private void select(AbstractQueryImpl query) {
        FilterProcessor processor = new FilterProcessor();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        Graph result = processor.process((AbstractQueryImpl) query, graphModel);
//        System.out.println("#Nodes: " + result.getNodeCount());
//        System.out.println("#Edges: " + result.getEdgeCount());
        if (running) {
            VisualizationController visController = Lookup.getDefault().lookup(VisualizationController.class);
            if (visController != null) {
                visController.selectNodes(result.getNodes().toArray());
                visController.selectEdges(result.getEdges().toArray());
            }
        } else {
            //destroy view 
        }
        graphModel.destroyView(result.getView());
    }

    public void setRootQuery(AbstractQueryImpl rootQuery) {
        this.rootQuery.set(rootQuery);
        synchronized (this.lock) {
            lock.notify();
        }
    }

    public AbstractQueryImpl getRootQuery() {
        return rootQuery.get();
    }

    public void setRunning(boolean running) {
        this.running = running;
        synchronized (this.lock) {
            lock.notify();
        }
    }

    public void addModifier(PropertyModifier modifier) {
        modifiersMap.put(modifier.property.getName(), modifier);
    }

    protected static class PropertyModifier {

        protected final Object value;
        protected final Callback callback;
        protected final FilterProperty property;
        protected final Query query;

        public PropertyModifier(Query query, FilterProperty property, Object value, Callback callback) {
            this.query = query;
            this.property = property;
            this.value = value;
            this.callback = callback;
        }
    }
}
