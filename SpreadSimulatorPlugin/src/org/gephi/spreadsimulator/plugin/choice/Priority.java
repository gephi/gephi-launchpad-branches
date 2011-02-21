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

import java.util.Arrays;
import java.util.Comparator;
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
	public Edge[] chooseEdges(Edge[] edges) {
		Arrays.sort(edges, new PrioComparator());
		return edges;
	}

	private class PrioComparator implements Comparator<Edge> {
		@Override
		public int compare(Edge e1, Edge e2) {
			int e1Prio = Integer.parseInt(((String)e1.getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).split(":")[1]);
			int e2Prio = Integer.parseInt(((String)e2.getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).split(":")[1]);
			return e1Prio - e2Prio;
		}
	}
}
