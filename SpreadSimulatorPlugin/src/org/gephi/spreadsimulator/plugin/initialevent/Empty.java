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
package org.gephi.spreadsimulator.plugin.initialevent;

import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.spi.InitialEvent;
import org.gephi.spreadsimulator.spi.TransitionAlgorithm;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class Empty implements InitialEvent {
	private TransitionAlgorithm algorithm;

	public Empty() {
		algorithm = new EmptyAlgorithm();
	}

	@Override
	public boolean isOccuring(SimulationData simulationData) {
		return true;
	}

	@Override
	public TransitionAlgorithm getAlgorithm() {
		return algorithm;
	}
}
