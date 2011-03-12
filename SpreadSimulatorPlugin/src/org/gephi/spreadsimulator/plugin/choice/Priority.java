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
package org.gephi.spreadsimulator.plugin.choice;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.gephi.graph.api.Edge;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.spi.Choice;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class Priority implements Choice {
	@Override
	public List<List<Edge>> chooseEdges(List<List<Edge>> redges) {
		Collections.sort(redges, new PrioComparator());
		return redges;
	}

	private class PrioComparator implements Comparator<List<Edge>> {
		@Override
		public int compare(List<Edge> le1, List<Edge> le2) {
			int le1Prio = Integer.parseInt(((String)le1.get(0).getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).split(":")[1]);
			int le2Prio = Integer.parseInt(((String)le2.get(0).getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).split(":")[1]);
			return le1Prio - le2Prio;
		}
	}
}
