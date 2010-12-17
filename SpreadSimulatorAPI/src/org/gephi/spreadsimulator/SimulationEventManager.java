/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.spreadsimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import org.gephi.spreadsimulator.api.SimulationEvent;
import org.gephi.spreadsimulator.api.SimulationListener;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class SimulationEventManager implements Runnable {
	private static final long DELAY = 1;

	private final List<SimulationListener> listeners;
	private final AtomicReference<Thread> thread = new AtomicReference<Thread>();
	private final LinkedBlockingQueue<SimulationEvent> eventQueue;
	private final Object lock = new Object();
	
	private boolean stop;

	public SimulationEventManager() {
		this.eventQueue = new LinkedBlockingQueue<SimulationEvent>();
		this.listeners = Collections.synchronizedList(new ArrayList<SimulationListener>());
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			SimulationEvent precEvt = null;
			SimulationEvent evt = null;
			while ((evt = eventQueue.peek()) != null) {
				eventQueue.poll();
				precEvt = evt;
			}

			if (precEvt != null) {
				SimulationEvent event = createEvent(precEvt);
				for (SimulationListener l : listeners.toArray(new SimulationListener[0]))
					l.simulationChanged(event);
			}

			while (eventQueue.isEmpty()) {
				try {
					synchronized (lock) {
						lock.wait();
					}
				} catch (InterruptedException e) { }
			}
		}
	}

	private SimulationEvent createEvent(SimulationEvent event) {
		final SimulationEventImpl simulationEvent = new SimulationEventImpl(event.getEventType());
		return simulationEvent;
	}

	public void stop(boolean stop) {
		this.stop = stop;
	}

	public void fireEvent(SimulationEvent event) {
		eventQueue.add(event);
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setName("simulation-event-bus");
		if (thread.compareAndSet(null, t))
			t.start();
	}

	public boolean isRunning() {
		return thread.get() != null;
	}

	public void addSimulationListener(SimulationListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeSimulationListener(SimulationListener listener) {
		listeners.remove(listener);
	}
}
