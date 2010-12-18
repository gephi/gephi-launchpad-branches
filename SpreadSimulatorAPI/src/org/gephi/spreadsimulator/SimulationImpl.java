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

import org.gephi.spreadsimulator.api.Simulation;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.spi.StopCondition;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Simulation.class)
public class SimulationImpl implements Simulation, LongTask {
	@Override
	public void init() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void addStopCondition(StopCondition stopCondition) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public StopCondition[] getStopConditions() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SimulationData getSimulationData() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void nextStep() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getReport() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean cancel() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProgressTicket(ProgressTicket progressTicket) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
