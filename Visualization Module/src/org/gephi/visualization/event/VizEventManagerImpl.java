/*
Copyright 2008-2011 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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
package org.gephi.visualization.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.event.VizEvent;
import org.gephi.visualization.api.event.VizEventListener;
import org.gephi.visualization.api.event.VizEventManager;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.controller.Controller;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Mathieu Bastian
 * @author Vojtech Bardiovsky
 */
@ServiceProvider(service = VizEventManager.class)
public class VizEventManagerImpl implements VizEventManager {

    //Architecture
    private ThreadPoolExecutor pool;
    private VizEventTypeHandler[] handlers;

    // LEFT_PRESSING event attributes
    private static final int PRESSING_FREQUENCY = 5;
    private int pressingTick = 0;

    public VizEventManagerImpl() {
        pool = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(10));

        //Set handlers
        ArrayList<VizEventTypeHandler> handlersList = new ArrayList<VizEventTypeHandler>();
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_LEFT_CLICK, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_LEFT_PRESS, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_MIDDLE_CLICK, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_MIDDLE_PRESS, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_RIGHT_CLICK, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_RIGHT_PRESS, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_MOVE, true));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.START_DRAG, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.DRAG, true));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.STOP_DRAG, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.NODE_LEFT_CLICK, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_LEFT_PRESSING, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.MOUSE_RELEASED, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.NODE_LEFT_PRESS, false));
        handlersList.add(new VizEventTypeHandler(VizEvent.Type.NODE_LEFT_PRESSING, false));
        Collections.sort(handlersList, new Comparator<VizEventTypeHandler>() {
            @Override
            public int compare(VizEventTypeHandler v1, VizEventTypeHandler v2) {
                return v1.type.compareTo(v2.type);
            }
        });
        handlers = handlersList.toArray(new VizEventTypeHandler[0]);
    }

    @Override
    public void mouseLeftClick() {
        //Node Left click
        VizEventTypeHandler nodeLeftHandler = handlers[VizEvent.Type.NODE_LEFT_CLICK.ordinal()];
        if (nodeLeftHandler.hasListeners() && Lookup.getDefault().lookup(SelectionManager.class).isSelectionEnabled()) {
            //Check if some node are selected
            SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
            Collection<Node> nodes = selectionManager.getSelectedNodes();
            if (!nodes.isEmpty()) {
                nodeLeftHandler.dispatch(nodes.toArray(new Node[]{}));
            }
        }

        //Mouse left click
        VizEventTypeHandler mouseLeftHandler = handlers[VizEvent.Type.MOUSE_LEFT_CLICK.ordinal()];
        if (mouseLeftHandler.hasListeners()) {
            SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
            MotionManager motionManager = Controller.getInstance().getMotionManager();
            Collection<Node> nodes = selectionManager.getSelectedNodes();
            if (nodes.isEmpty() || !selectionManager.isSelectionEnabled()) {
                float[] mousePositionViewport = motionManager.getMousePosition();
                float[] mousePosition3d = motionManager.getMousePosition3d();
                float[] mousePos = new float[]{mousePositionViewport[0], mousePositionViewport[1], mousePosition3d[0], mousePosition3d[1], mousePosition3d[2]};
                handlers[VizEvent.Type.MOUSE_LEFT_CLICK.ordinal()].dispatch(mousePos);
            }
        }
    }

    @Override
    public void mouseLeftPress() {
        handlers[VizEvent.Type.MOUSE_LEFT_PRESS.ordinal()].dispatch();
        pressingTick = PRESSING_FREQUENCY;
        VizEventTypeHandler pressHandler = handlers[VizEvent.Type.NODE_LEFT_PRESS.ordinal()];
        if (pressHandler.hasListeners()) {
            //Check if some node are selected
            SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
            Collection<Node> nodes = selectionManager.getSelectedNodes();
            if (!nodes.isEmpty()) {
                pressHandler.dispatch(nodes.toArray(new Node[]{}));
            }
        }
    }

    @Override
    public void mouseMiddleClick() {
        handlers[VizEvent.Type.MOUSE_MIDDLE_CLICK.ordinal()].dispatch();
    }

    @Override
    public void mouseMiddlePress() {
        handlers[VizEvent.Type.MOUSE_LEFT_PRESS.ordinal()].dispatch();
    }

    @Override
    public void mouseMove() {
        handlers[VizEvent.Type.MOUSE_MOVE.ordinal()].dispatch();
    }

    @Override
    public void mouseRightClick() {
        handlers[VizEvent.Type.MOUSE_RIGHT_CLICK.ordinal()].dispatch();
    }

    @Override
    public void mouseRightPress() {
        handlers[VizEvent.Type.MOUSE_RIGHT_PRESS.ordinal()].dispatch();
    }

    @Override
    public void mouseLeftPressing() {
        if (pressingTick++ >= PRESSING_FREQUENCY) {
            pressingTick = 0;
            VizEventTypeHandler nodeHandler = handlers[VizEvent.Type.NODE_LEFT_PRESSING.ordinal()];
            if (nodeHandler.hasListeners()) {
                //Check if some node are selected
                SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
                Collection<Node> nodes = selectionManager.getSelectedNodes();
                if (!nodes.isEmpty()) {
                    nodeHandler.dispatch(nodes.toArray(new Node[]{}));
                }
            }
        }
    }

    @Override
    public void startDrag() {
        handlers[VizEvent.Type.START_DRAG.ordinal()].dispatch();
    }

    @Override
    public void stopDrag() {
        handlers[VizEvent.Type.STOP_DRAG.ordinal()].dispatch();
    }
    private static final int DRAGGING_FREQUENCY = 1;
    private int draggingTick = 0;

    @Override
    public void drag() {
        if (draggingTick++ >= DRAGGING_FREQUENCY) {
            draggingTick = 0;
            VizEventTypeHandler handler = handlers[VizEvent.Type.DRAG.ordinal()];
            if (handler.hasListeners()) {
                MotionManager motionManager = Controller.getInstance().getMotionManager();
                float[] mouseDrag = Arrays.copyOf(motionManager.getDrag(), 5);
                float[] mouseDrag3d = motionManager.getDrag3d();
                mouseDrag[2] = mouseDrag3d[0];
                mouseDrag[3] = mouseDrag3d[1];
                mouseDrag[4] = mouseDrag3d[2];
                handler.dispatch(mouseDrag);
            }
        }
    }

    @Override
    public void mouseReleased() {
        handlers[VizEvent.Type.MOUSE_RELEASED.ordinal()].dispatch();
    }

    //Listeners
    public boolean hasListeners(VizEvent.Type type) {
        return handlers[type.ordinal()].hasListeners();
    }

    public void addListener(VizEventListener listener) {
        handlers[listener.getType().ordinal()].addListener(listener);
    }

    public void removeListener(VizEventListener listener) {
        handlers[listener.getType().ordinal()].removeListener(listener);
    }

    public void addListener(VizEventListener[] listeners) {
        for (int i = 0; i < listeners.length; i++) {
            handlers[listeners[i].getType().ordinal()].addListener(listeners[i]);
        }
    }

    public void removeListener(VizEventListener[] listeners) {
        for (int i = 0; i < listeners.length; i++) {
            handlers[listeners[i].getType().ordinal()].removeListener(listeners[i]);
        }
    }

    private class VizEventTypeHandler {

        //Settings
        private final boolean limitRunning;
        //Data
        protected List<WeakReference<VizEventListener>> listeners;
        protected final VizEvent.Type type;
        protected Runnable runnable;
        //States
        protected boolean running;

        public VizEventTypeHandler(VizEvent.Type type, boolean limitRunning) {
            this.limitRunning = limitRunning;
            this.type = type;
            this.listeners = new ArrayList<WeakReference<VizEventListener>>();
            runnable = new Runnable() {

                public void run() {
                    fireVizEvent(null);
                    running = false;
                }
            };
        }

        protected synchronized void addListener(VizEventListener listener) {
            WeakReference<VizEventListener> weakListener = new WeakReference<VizEventListener>(listener);
            listeners.add(weakListener);
        }

        protected synchronized void removeListener(VizEventListener listener) {
            for (Iterator<WeakReference<VizEventListener>> itr = listeners.iterator(); itr.hasNext();) {
                WeakReference<VizEventListener> li = itr.next();
                if (li.get() == listener) {
                    itr.remove();
                }
            }
        }

        protected void dispatch() {
            if (limitRunning && running) {
                return;
            }
            if (listeners.size() > 0) {
                running = true;
                pool.submit(runnable);
            }
        }

        protected void dispatch(final Object data) {
            if (limitRunning && running) {
                return;
            }
            if (listeners.size() > 0) {
                running = true;
                pool.submit(new Runnable() {

                    public void run() {
                        fireVizEvent(data);
                        running = false;
                    }
                });
            }
        }

        protected boolean isRunning() {
            return running;
        }

        private synchronized void fireVizEvent(Object data) {
            VizEvent event = new VizEvent(this, type, data);
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<VizEventListener> weakListener = listeners.get(i);
                VizEventListener v = weakListener.get();
                v.handleEvent(event);
            }
        }

        public boolean hasListeners() {
            return listeners.size() > 0;
        }

        protected int getIndex() {
            return type.ordinal();
        }
    }
}
