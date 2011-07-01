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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.spi.TransitionAlgorithm;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class NeighbourhoodAlgorithm implements TransitionAlgorithm {
	private String[] states;

	public NeighbourhoodAlgorithm(String[] states) {
		this.states = states;
	}

	@Override
	public Edge tryDoTransition(SimulationData simulationData, Map<Edge, Double> probs) {
		Node ceNode = simulationData.getCurrentlyExaminedNode();
		for (Node node : simulationData.getNetworkModel().getGraph().getNeighbors(ceNode).toArray()) {
			double quality = (Double)node.getNodeData().getAttributes().getValue("Quality");
			for (String state : states)
				if (state.equals(node.getNodeData().getAttributes().getValue(simulationData.NM_CURRENT_STATE))) {
					double p = new Random().nextDouble();
					double sum = 0.0;
					for (Entry<Edge, Double> prob : probs.entrySet()) {
						sum += prob.getValue() * quality;
						if (p <= sum)
							return prob.getKey();
					}
				}
		}
		return null;
	}
}
