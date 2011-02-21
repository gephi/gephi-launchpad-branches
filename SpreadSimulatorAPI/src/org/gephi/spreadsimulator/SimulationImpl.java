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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeEvent;
import org.gephi.data.attributes.api.AttributeListener;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceProvider;
import org.gephi.spreadsimulator.api.Simulation;
import org.gephi.spreadsimulator.api.SimulationData;
import org.gephi.spreadsimulator.api.SimulationEvent;
import org.gephi.spreadsimulator.api.SimulationEvent.EventType;
import org.gephi.spreadsimulator.api.SimulationListener;
import org.gephi.spreadsimulator.spi.InitialEvent;
import org.gephi.spreadsimulator.spi.StopCondition;
import org.gephi.utils.TempDirUtils;
import org.gephi.utils.TempDirUtils.TempDir;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Simulation.class)
public class SimulationImpl implements Simulation {
	private SimulationEventManager eventManager;

	private List<StopCondition> stopConditions;
	private SimulationDataImpl simulationData;
	private InitialEventFactoryImpl ieFactory;
	private ChoiceFactoryImpl cFactory;

	private long delay;
	private boolean cancel;
	private ProgressTicket progressTicket;
	private boolean finished;

	private AttributeListener al;

	public SimulationImpl() {
		eventManager = new SimulationEventManager();
		eventManager.start();

		al = new AttributeListener() {
			@Override
			public void attributesChanged(AttributeEvent event) {
				ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
				Workspace[] workspaces = pc.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces();
				GraphController gc = Lookup.getDefault().lookup(GraphController.class);
				if (event.is(AttributeEvent.EventType.ADD_COLUMN) &&
						event.getData().getAddedColumns()[0].getId().equals(SimulationData.NM_CURRENT_STATE))
					for (Node node : gc.getModel(workspaces[0]).getGraph().getNodes()) {
						node.getNodeData().setR(simulationData.getRForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
						node.getNodeData().setG(simulationData.getGForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
						node.getNodeData().setB(simulationData.getBForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
					}
				else if (event.is(AttributeEvent.EventType.SET_VALUE))
					for (int i = 0; i < event.getData().getTouchedValues().length; ++i) {
						AttributeValue av = event.getData().getTouchedValues()[i];
						Object ob = event.getData().getTouchedObjects()[i];
						if (av.getColumn().getId().equals(SimulationData.NM_CURRENT_STATE) && ob instanceof NodeData) {
							NodeData nodeData = (NodeData)ob;
							nodeData.setR(simulationData.getRForState(
									(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
							nodeData.setG(simulationData.getGForState(
									(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
							nodeData.setB(simulationData.getBForState(
									(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
						}
					}
			}
		};
	}

	@Override
	public void init() {
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		Workspace[] workspaces = pc.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces();
		GraphController gc = Lookup.getDefault().lookup(GraphController.class);

		stopConditions = new ArrayList<StopCondition>();
		simulationData = new SimulationDataImpl(gc.getModel(workspaces[0]), gc.getModel(workspaces[1]));
		ieFactory = new InitialEventFactoryImpl();
		cFactory = new ChoiceFactoryImpl();

		delay = 1000;
		cancel = true;
		finished = false;

		AttributeModel networkAttributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(workspaces[0]);

		networkAttributeModel.removeAttributeListener(al);
		networkAttributeModel.addAttributeListener(al);

		if (!networkAttributeModel.getNodeTable().hasColumn(SimulationDataImpl.NM_CURRENT_STATE))
			networkAttributeModel.getNodeTable().addColumn(SimulationDataImpl.NM_CURRENT_STATE,
					SimulationData.NM_CURRENT_STATE_TITLE, AttributeType.STRING,
					AttributeOrigin.DATA, simulationData.getDefaultState());
		else for (Node node : gc.getModel(workspaces[0]).getGraph().getNodes())
			node.getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, simulationData.getDefaultState());

		fireSimulationEvent(new SimulationEventImpl(EventType.INIT));
	}

	@Override
	public void addStopCondition(StopCondition stopCondition) {
		stopConditions.add(stopCondition);
		fireSimulationEvent(new SimulationEventImpl(EventType.ADD_STOP_CONDITION));
	}

	@Override
	public void removeStopCondition(StopCondition stopCondition) {
		stopConditions.remove(stopCondition);
		fireSimulationEvent(new SimulationEventImpl(EventType.REMOVE_STOP_CONDITION));
	}

	@Override
	public StopCondition[] getStopConditions() {
		return stopConditions.toArray(new StopCondition[0]);
	}

	@Override
	public SimulationData getSimulationData() {
		return simulationData;
	}

	@Override
	public long getDelay() {
		return delay;
	}

	@Override
	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public void start() {
		final Timer timer = new Timer();
		Progress.start(progressTicket);

		fireSimulationEvent(new SimulationEventImpl(EventType.START));

		final Simulation sim = this;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (!cancel && !finished)
					nextStep();
				else {
					timer.cancel();
					timer.purge();
					Progress.finish(progressTicket);
					sim.cancel();
				}
			}
		};
		cancel = false;
		timer.schedule(task, delay, delay);
	}

	@Override
	public boolean cancel() {
		cancel = true;
		fireSimulationEvent(new SimulationEventImpl(EventType.CANCEL));
		return true;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}

	@Override
	public void previousStep() {
		throw new UnsupportedOperationException("Not supported yet.");
		// fireSimulationEvent(new SimulationEventImpl(EventType.PREVIOUS_STEP));
	}

	@Override
	public void nextStep() {
		if (finished)
			return;

		Map<Node, String> newStates = new HashMap<Node, String>();
		for (Node nmNode : simulationData.getNetworkModel().getGraph().getNodes()) {
			simulationData.setCurrentlyExaminedNode(nmNode);
			String currentState = (String)nmNode.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE);
			Node smNode = simulationData.getStateMachineNodeForState(currentState);

			String state = null;
			List<Edge> edges = new ArrayList<Edge>();
			for (Edge smEdge : simulationData.getStateMachineModel().getDirectedGraph().getOutEdges(smNode).toArray()) {
				InitialEvent ie = ieFactory.getInitialEvent(
						(String)smEdge.getEdgeData().getAttributes().getValue(SimulationData.SM_INITIAL_EVENT));
				if (ie.isOccuring(simulationData))
					edges.add(smEdge);
			}
			if (!edges.isEmpty())
				edges = Arrays.asList(cFactory.getChoice(
							(String)edges.get(0).getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).
							chooseEdges(edges.toArray(new Edge[0])));
			for (Edge smEdge : edges) {
				Double probability = (Double)smEdge.getEdgeData().getAttributes().getValue(SimulationData.SM_PROBABILITY);
				boolean aout = false;
				InitialEvent ie = ieFactory.getInitialEvent(
						(String)smEdge.getEdgeData().getAttributes().getValue(SimulationData.SM_INITIAL_EVENT));
				aout = ie.getAlgorithm().tryDoTransition(simulationData, probability);
				if (aout) {
					state = (String)smEdge.getTarget().getNodeData().getAttributes().getValue(SimulationData.SM_STATE_NAME);
					break;
				}
			}
			newStates.put(nmNode, state);
		}
		for (Entry<Node, String> entry : newStates.entrySet())
			if (entry.getValue() != null)
				entry.getKey().getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, entry.getValue());

		simulationData.incrementCurrentStep();
		for (StopCondition sc : stopConditions)
			if (sc.isOccuring(simulationData)) {
				finished = true;
				break;
			}

		fireSimulationEvent(new SimulationEventImpl(EventType.NEXT_STEP));
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public String getReport() {
		String[] states = simulationData.getStates();
		
		Color[] colors = new Color[states.length];
		for (int i = 0; i < states.length; ++i) {
			int r = (int)(255.0f * simulationData.getRForState(states[i]));
			int g = (int)(255.0f * simulationData.getGForState(states[i]));
			int b = (int)(255.0f * simulationData.getBForState(states[i]));
			float[] hsbValues = new float[3];
			hsbValues = Color.RGBtoHSB(r, g, b, hsbValues);
			colors[i] = Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]);
		}
		
		XYSeriesCollection[] datasets = new XYSeriesCollection[states.length];
		for (int i = 0; i < states.length; ++i) {
			XYSeries series = new XYSeries(states[i]);
			for (int j = 0; j <= simulationData.getCurrentStep(); ++j)
				series.add(j, simulationData.getNodesCountInStateAndStep(states[i], j));
			datasets[i] = new XYSeriesCollection();
			datasets[i].addSeries(series);
		}

		String stable = "<table border=\"1\">";
		for (int i = 0; i <= simulationData.getCurrentStep() + 1; ++i) {
			stable += "<tr>";
			for (int j = 0; j < states.length + 1; ++j) {
				stable += "<td>";
				if (i == 0 && j == 0)
					; // nothing
				else if (i == 0)
					stable += states[j - 1];
				else if (j == 0)
					stable += (i - 1);
				else stable += simulationData.getNodesCountInStateAndStep(states[j - 1], i - 1);
				stable += "</td>";
			}
			stable += "</tr>";
		}
		stable += "</table>";

		JFreeChart[] charts = new JFreeChart[states.length];
		for (int i = 0; i < states.length; ++i) {
			charts[i] = ChartFactory.createXYLineChart(
				states[i],
				"Step",
				"Nodes Count",
				datasets[i],
				PlotOrientation.VERTICAL,
				true,
				false,
				false);
			XYPlot plot = (XYPlot)charts[i].getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			renderer.setSeriesLinesVisible(0, true);
			renderer.setSeriesShapesVisible(0, false);
			renderer.setSeriesPaint(0, colors[i]);
			plot.setBackgroundPaint(Color.GRAY);
			plot.setDomainGridlinePaint(Color.WHITE);
			plot.setRangeGridlinePaint(Color.WHITE);
			plot.setRenderer(renderer);
			plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			plot.getDomainAxis().setRange(0, simulationData.getCurrentStep());
			plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			plot.getRangeAxis().setRange(0, simulationData.getNetworkModel().getGraph().getNodeCount());
		}

		String[] images = new String[states.length];
		for (int i = 0; i < states.length; ++i)
			try {
				final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
				TempDir tempDir = TempDirUtils.createTempDir();
				final String fileName = "nodesCount" + i + ".png";
				final File file = tempDir.createFile(fileName);
				images[i] = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
				ChartUtilities.saveChartAsPNG(file, charts[i], 600, 400, info);
			}
			catch (Exception e) { }

		String report = "<html><body><h1>Nodes Count Report</h1><hr><br>";
		report += stable + "<br><br>";
		for (String image : images)
			report += image + "<br><br>";
		report += "</body></html>";
		return report;
	}

	@Override
	public void addSimulationListener(SimulationListener listener) {
		eventManager.addSimulationListener(listener);
	}

	@Override
	public void removeSimulationListener(SimulationListener listener) {
		eventManager.removeSimulationListener(listener);
	}

	public void fireSimulationEvent(SimulationEvent event) {
		eventManager.fireEvent(event);
	}
}
