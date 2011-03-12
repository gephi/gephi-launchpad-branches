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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.Edge;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.spi.Choice;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class Pe implements Choice {
	@Override
	public List<List<Edge>> chooseEdges(List<List<Edge>> redges) {
		List<List<Edge>> cedges = new ArrayList<List<Edge>>();
		double p = new Random().nextDouble();
		double sum = 0.0;
		for (List<Edge> le : redges) {
			double ePe = Double.parseDouble(((String)le.get(0).getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).split(":")[1]);
			sum += ePe;
			if (p <= sum) {
				cedges.add(le);
				break;
			}
		}
		return cedges;
	}
}
