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

	private ProjectController pc;
	private Workspace[] workspaces;
	private GraphController gc;

	private List<StopCondition> stopConditions;
	private List<SimulationDataImpl> simulationDatas;
	private InitialEventFactoryImpl ieFactory;
	private ChoiceFactoryImpl cFactory;

	private long delay;
	private int simnr;
	private int count;
	private boolean cancel;
	private ProgressTicket progressTicket;
	private boolean finished;
	private boolean nextSim;

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
						node.getNodeData().setR(simulationDatas.get(simnr).getRForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
						node.getNodeData().setG(simulationDatas.get(simnr).getGForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
						node.getNodeData().setB(simulationDatas.get(simnr).getBForState(
								(String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
					}
				else if (event.is(AttributeEvent.EventType.SET_VALUE))
					for (int i = 0; i < event.getData().getTouchedValues().length; ++i) {
						AttributeValue av = event.getData().getTouchedValues()[i];
						Object ob = event.getData().getTouchedObjects()[i];
						if (av.getColumn().getId().equals(SimulationData.NM_CURRENT_STATE) && ob instanceof NodeData) {
							NodeData nodeData = (NodeData)ob;
							try {
								if (simulationDatas.size() > simnr)
									nodeData.setR(simulationDatas.get(simnr).getRForState(
										(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
								if (simulationDatas.size() > simnr)
									nodeData.setG(simulationDatas.get(simnr).getGForState(
										(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
								if (simulationDatas.size() > simnr)
									nodeData.setB(simulationDatas.get(simnr).getBForState(
										(String)nodeData.getAttributes().getValue(SimulationData.NM_CURRENT_STATE)));
							}
							catch (Exception ex) { }
						}
					}
			}
		};
	}

	@Override
	public void init() {
		pc = Lookup.getDefault().lookup(ProjectController.class);
		workspaces = pc.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces();
		gc = Lookup.getDefault().lookup(GraphController.class);

		stopConditions = new ArrayList<StopCondition>();
		simulationDatas = new ArrayList<SimulationDataImpl>();
		simulationDatas.add(new SimulationDataImpl(gc.getModel(workspaces[0]), gc.getModel(workspaces[1])));
		ieFactory = new InitialEventFactoryImpl();
		cFactory = new ChoiceFactoryImpl();

		delay = 1000;
		simnr = 0;
		count = 1;
		cancel = true;
		finished = false;
		nextSim = false;

		AttributeModel networkAttributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(workspaces[0]);

		networkAttributeModel.removeAttributeListener(al);
		networkAttributeModel.addAttributeListener(al);

		if (!networkAttributeModel.getNodeTable().hasColumn(SimulationDataImpl.NM_CURRENT_STATE))
			networkAttributeModel.getNodeTable().addColumn(SimulationDataImpl.NM_CURRENT_STATE,
					SimulationData.NM_CURRENT_STATE_TITLE, AttributeType.STRING,
					AttributeOrigin.DATA, simulationDatas.get(simnr).getDefaultState());
		else for (Node node : gc.getModel(workspaces[0]).getGraph().getNodes())
			node.getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, simulationDatas.get(simnr).getDefaultState());

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
		return simulationDatas.get(simnr);
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

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (!cancel && !finished)
					nextStep();
				else {
					timer.cancel();
					timer.purge();
					Progress.finish(progressTicket);
				}
			}
		};
		cancel = false;
		timer.schedule(task, delay, delay);
	}

	@Override
	public void start(int count) {
		this.count = count;
		if (count < 1)
			throw new IllegalArgumentException("Count must be greater than 0.");

		Map<Node, String> nsmap = new HashMap<Node, String>();
		for (Node node : gc.getModel(workspaces[0]).getGraph().getNodes())
			nsmap.put(node, (String)node.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE));

		if (count == 1)
			start();
		else {
			Progress.start(progressTicket, count);
			fireSimulationEvent(new SimulationEventImpl(EventType.START));
			cancel = false;

			for (simnr = 0; simnr < count; ++simnr) {
				while (!cancel && !nextSim)
					nextStep();
				for (Node node : gc.getModel(workspaces[0]).getGraph().getNodes())
					node.getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, nsmap.get(node));
				if (cancel) {
					simulationDatas = new ArrayList<SimulationDataImpl>();
					simulationDatas.add(new SimulationDataImpl(gc.getModel(workspaces[0]), gc.getModel(workspaces[1])));
					simnr = 0;
					count = 1;
					Progress.finish(progressTicket);
					return;
				}
				nextSim = false;
				if (simnr < count - 1)
					simulationDatas.add(new SimulationDataImpl(gc.getModel(workspaces[0]), gc.getModel(workspaces[1])));
				Progress.progress(progressTicket, simnr);
			}
			simnr--;

			finished = true;
			fireSimulationEvent(new SimulationEventImpl(EventType.FINISHED));
			Progress.finish(progressTicket);
		}
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

		SimulationDataImpl simd = simulationDatas.get(simnr);
		Map<Node, String> newStates = new HashMap<Node, String>();
		for (Node nmNode : simd.getNetworkModel().getGraph().getNodes()) {
			simd.setCurrentlyExaminedNode(nmNode);
			String currentState = (String)nmNode.getNodeData().getAttributes().getValue(SimulationData.NM_CURRENT_STATE);
			Node smNode = simd.getStateMachineNodeForState(currentState);
			Edge[] outsmEdges = simd.getStateMachineModel().getDirectedGraph().getOutEdges(smNode).toArray();
			Map<Edge, InitialEvent> edgeIEventMap = new HashMap<Edge, InitialEvent>();
			for (Edge smEdge : outsmEdges)
				edgeIEventMap.put(smEdge, ieFactory.getInitialEvent(
						(String)smEdge.getEdgeData().getAttributes().getValue(SimulationData.SM_INITIAL_EVENT)));
			Map<InitialEvent, List<Edge>> ieventEdgesMap = new HashMap<InitialEvent, List<Edge>>();
			for (Edge smEdge : outsmEdges) {
				InitialEvent ie = edgeIEventMap.get(smEdge);
				if (!ieventEdgesMap.containsKey(ie))
					ieventEdgesMap.put(ie, new ArrayList<Edge>());
				if (ie.isOccuring(simd))
					ieventEdgesMap.get(ie).add(smEdge);
			}
			List<List<Edge>> redges = new ArrayList<List<Edge>>();
			for (Entry<InitialEvent, List<Edge>> entry : ieventEdgesMap.entrySet())
				if (!entry.getValue().isEmpty())
					redges.add(entry.getValue());

			if (!redges.isEmpty())
				redges = cFactory.getChoice(
							(String)redges.get(0).get(0).getEdgeData().getAttributes().getValue(SimulationData.SM_CHOICE)).
							chooseEdges(redges);
			String state = null;
			for (List<Edge> edges : redges) {
				Map<Edge, Double> probs = new HashMap<Edge, Double>();
				for (Edge e : edges) {
					Double probability = (Double)e.getEdgeData().getAttributes().getValue(SimulationData.SM_PROBABILITY);
					probs.put(e, probability);
				}
				InitialEvent ie = edgeIEventMap.get(edges.get(0));
				Edge redge = ie.getAlgorithm().tryDoTransition(simd, probs);
				if (redge != null) {
					state = (String)redge.getTarget().getNodeData().getAttributes().getValue(SimulationData.SM_STATE_NAME);
					break;
				}
			}
			newStates.put(nmNode, state);
		}
		for (Entry<Node, String> entry : newStates.entrySet())
			if (entry.getValue() != null)
				entry.getKey().getNodeData().getAttributes().setValue(SimulationData.NM_CURRENT_STATE, entry.getValue());

		simulationDatas.get(simnr).incrementCurrentStep();
		for (StopCondition sc : stopConditions)
			if (sc.isOccuring(simulationDatas.get(simnr))) {
				if (count == 1)
					finished = true;
				else nextSim = true;
				break;
			}

		if (finished)
			fireSimulationEvent(new SimulationEventImpl(EventType.FINISHED));
		else fireSimulationEvent(new SimulationEventImpl(EventType.NEXT_STEP));
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public String getReport() {
		String[] states = simulationDatas.get(simnr).getStates();
		
		Color[] colors = new Color[states.length];
		for (int i = 0; i < states.length; ++i) {
			int r = (int)(255.0f * simulationDatas.get(simnr).getRForState(states[i]));
			int g = (int)(255.0f * simulationDatas.get(simnr).getGForState(states[i]));
			int b = (int)(255.0f * simulationDatas.get(simnr).getBForState(states[i]));
			float[] hsbValues = new float[3];
			hsbValues = Color.RGBtoHSB(r, g, b, hsbValues);
			colors[i] = Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]);
		}

		int maxStep = 0;
		for (int i = 0; i <= simnr; ++i)
			if (maxStep < simulationDatas.get(i).getCurrentStep())
				maxStep = simulationDatas.get(i).getCurrentStep();
		List<Map<String, Integer>> ncss = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i <= maxStep; ++i) {
			ncss.add(new HashMap<String, Integer>());
			for (int j = 0; j < states.length; ++j)
				ncss.get(i).put(states[j], 0);
		}

		XYSeriesCollection[] datasets = new XYSeriesCollection[states.length];
		for (int i = 0; i < states.length; ++i) {
			XYSeries series = new XYSeries(states[i]);
			for (int j = 0; j <= maxStep; ++j) {
				int sum = 0;
				for (int k = 0; k <= simnr; ++k)
					if (simulationDatas.get(k).getCurrentStep() >= j)
						sum += simulationDatas.get(k).getNodesCountInStateAndStep(states[i], j);
					else sum += simulationDatas.get(k).getNodesCountInStateAndStep(states[i],
								simulationDatas.get(k).getCurrentStep());
				ncss.get(j).put(states[i], sum / (simnr + 1));
				series.add(j, ncss.get(j).get(states[i]));
			}
			datasets[i] = new XYSeriesCollection();
			datasets[i].addSeries(series);
		}

		String stable = "<table border=\"1\">";
		for (int i = 0; i <= maxStep + 1; ++i) {
			stable += "<tr>";
			for (int j = 0; j < states.length + 1; ++j) {
				stable += "<td>";
				if (i == 0 && j == 0)
					; // nothing
				else if (i == 0)
					stable += states[j - 1];
				else if (j == 0)
					stable += (i - 1);
				else stable += ncss.get(i - 1).get(states[j - 1]);
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
			plot.getDomainAxis().setRange(0, maxStep);
			plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			plot.getRangeAxis().setRange(0, simulationDatas.get(simnr).getNetworkModel().getGraph().getNodeCount());
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
