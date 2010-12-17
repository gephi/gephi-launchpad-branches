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

import org.gephi.spreadsimulator.api.SimulationEvent;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public final class SimulationEventImpl implements SimulationEvent {
	private final EventType type;

	public SimulationEventImpl(EventType type) {
		this.type = type;
	}

	@Override
	public EventType getEventType() {
		return type;
	}

	@Override
	public boolean is(EventType... type) {
		for (EventType t : type)
			if (t.equals(this.type))
				return true;
		return false;
	}
}
