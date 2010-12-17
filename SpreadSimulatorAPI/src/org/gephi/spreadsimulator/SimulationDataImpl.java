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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.spreadsimulator.api.SimulationData;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class SimulationDataImpl implements SimulationData {
	private GraphModel networkModel;
	private GraphModel stateMachineModel;

	private String defaultState;
	private List<String> states;
	private Map<String, Node> smNodes;
	private Map<String, Float> rMap;
	private Map<String, Float> gMap;
	private Map<String, Float> bMap;
	
	private List<Map<String, Integer>> nodesCount;

	private int currentStep;
	private Node ceNode;

	public SimulationDataImpl(GraphModel networkModel, GraphModel stateMachineModel) {
		this.networkModel = networkModel;
		this.stateMachineModel = stateMachineModel;

		states = new ArrayList<String>();
		smNodes = new HashMap<String, Node>();
		rMap = new HashMap<String, Float>();
		gMap = new HashMap<String, Float>();
		bMap = new HashMap<String, Float>();
		for (Node node : stateMachineModel.getGraph().getNodes()) {
			String state = (String)node.getNodeData().getAttributes().getValue(SM_STATE_NAME);
			if ((Boolean)node.getNodeData().getAttributes().getValue(SM_DEFAULT_STATE))
				defaultState = state;
			states.add(state);
			smNodes.put(state, node);
			rMap.put(state, node.getNodeData().r());
			gMap.put(state, node.getNodeData().g());
			bMap.put(state, node.getNodeData().b());
		}

		nodesCount = new ArrayList<Map<String, Integer>>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		nodesCount.add(map);
		for (String state : states)
			if (state.equals(defaultState))
				nodesCount.get(0).put(state, networkModel.getGraph().getNodeCount());
			else nodesCount.get(0).put(state, 0);

		currentStep = 0;
		ceNode = networkModel.getGraph().getNodes().toArray()[0];
	}

	@Override
	public GraphModel getNetworkModel() {
		return networkModel;
	}

	@Override
	public GraphModel getStateMachineModel() {
		return stateMachineModel;
	}

	@Override
	public String getDefaultState() {
		return defaultState;
	}

	@Override
	public int getNodesCountInStateAndStep(String state, int step) {
		return nodesCount.get(step).get(state);
	}

	@Override
	public int getCurrentStep() {
		return currentStep;
	}

	@Override
	public Node getCurrentlyExaminedNode() {
		return ceNode;
	}

	public String[] getStates() {
		return states.toArray(new String[0]);
	}

	public Node getStateMachineNodeForState(String state) {
		return smNodes.get(state);
	}

	public float getRForState(String state) {
		return rMap.get(state);
	}

	public float getGForState(String state) {
		return gMap.get(state);
	}

	public float getBForState(String state) {
		return bMap.get(state);
	}

	public void incrementCurrentStep() {
		currentStep++;
		Map<String, Integer> map = new HashMap<String, Integer>();
		nodesCount.add(map);
		refreshNodesCount();
	}

	public void setCurrentlyExaminedNode(Node ceNode) {
		this.ceNode = ceNode;
	}

	private void refreshNodesCount() {
		for (String state : states)
			nodesCount.get(currentStep).put(state, 0);
		for (Node node : networkModel.getGraph().getNodes()) {
			String state = (String)node.getNodeData().getAttributes().getValue(NM_CURRENT_STATE);
			if (state != null) {
				Integer value = nodesCount.get(currentStep).get(state);
				nodesCount.get(currentStep).put(state, value + 1);
			}
		}
	}
}
