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
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceProvider;
import org.gephi.spreadsimulator.api.ModifyStrategyType;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.api.StateChangeStrategy;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = StateChangeStrategy.class)
public class StateChangeStrategyImpl implements StateChangeStrategy {
	private AttributeColumn attributeColumn = null;
	private int k = 0;
	private ModifyStrategyType mstype = ModifyStrategyType.RANDOM;
	private String stateName = "";

	@Override
	public void changeStates() {
		final AttributeColumn column = attributeColumn;
		final ModifyStrategyType modstype = mstype;
		Random random = new Random();
		
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		Workspace[] workspaces = pc.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces();
		GraphController gc = Lookup.getDefault().lookup(GraphController.class);
		GraphModel gm = gc.getModel(workspaces[0]);

		Node[] nodes = gm.getGraph().getNodes().toArray();
		List<Node> modNodes = new ArrayList<Node>();
		switch (mstype) {
			case ATTRIBUTE_LOWEST:
			case ATTRIBUTE_HIGHEST:
				Arrays.sort(nodes, new Comparator<Node>() {
					@Override
					public int compare(Node n1, Node n2) {
						double v1 = getScalarValueForColumn(n1, column);
						double v2 = getScalarValueForColumn(n2, column);
						int c = Double.compare(v1, v2);
						if (c == 0)
							return 0;
						if (modstype == ModifyStrategyType.ATTRIBUTE_LOWEST)
							return c;
						return -c;
					}
				});
				for (int i = 0; i < k; ++i)
					modNodes.add(nodes[i]);
				break;
			case RANDOM:
				List<Node> rNodes = new LinkedList<Node>(Arrays.asList(nodes));
				for (int i = 0; i < k; ++i)
					modNodes.add(rNodes.remove(random.nextInt(rNodes.size())));
				break;
			case RANDOM_RANDOM:
				rNodes = new LinkedList<Node>(Arrays.asList(nodes));
				for (int i = 0; i < k; ++i) {
					Node rNode = rNodes.remove(random.nextInt(rNodes.size()));
					Node[] neighbors = gm.getGraph().getNeighbors(rNode).toArray();
					Node node = neighbors[random.nextInt(neighbors.length)];
					if (!modNodes.contains(node))
						modNodes.add(node);
				}
				break;
			default:
				modNodes = Arrays.asList(nodes);
				break;
		}

		for (Node node : modNodes)
			node.getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, stateName);
	}

	private double getScalarValueForColumn(Node node, AttributeColumn column) {
		Number value = (Number)node.getNodeData().getAttributes().getValue(column.getId());
		return value.doubleValue();
	}

	public AttributeColumn getAttributeColumn() {
		return attributeColumn;
	}

	public int getK() {
		return k;
	}

	public ModifyStrategyType getMstype() {
		return mstype;
	}

	public String getStateName() {
		return stateName;
	}

	public void setAttributeColumn(AttributeColumn attributeColumn) {
		this.attributeColumn = attributeColumn;
	}

	public void setK(int k) {
		this.k = k;
	}

	public void setMstype(ModifyStrategyType mstype) {
		this.mstype = mstype;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
}
