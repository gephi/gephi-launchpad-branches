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
package org.gephi.spreadsimulator.api;

import org.gephi.spreadsimulator.spi.StopCondition;
import org.gephi.utils.longtask.spi.LongTask;

/**
 * It is a service and can therefore be found in Lookup:
 * <pre>Simulation simulation = Lookup.getDefault().lookup(Simulation.class);</pre>
 * 
 * @author Cezary Bartosiak
 */
public interface Simulation extends LongTask {
	public void init();

	public void addStopCondition(StopCondition stopCondition);

	public void removeStopCondition(StopCondition stopCondition);

	public StopCondition[] getStopConditions();

	public SimulationData getSimulationData();

	public long getDelay();

	public void setDelay(long delay);

	public void start();

	public void start(int count);

	public boolean isCancelled();

	public void previousStep();

	public void nextStep();

	public boolean isFinished();

	public String getReport();

	public void addSimulationListener(SimulationListener listener);

	public void removeSimulationListener(SimulationListener listener);
}
