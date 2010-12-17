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
package org.gephi.statistics.plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 * http://www.w3.org/People/Massimo/papers/2001/efficiency_prl_01.pdf
 *
 * @author Cezary Bartosiak
 */
public class AverageGlobalConnectionEfficiency implements Statistics, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;

	private double avggce = 0.0;

	// General
	private boolean isDirected = false;

	// Samples count
	private int samplesCount = 10;
	
	// Removal strategy options
	private int k = 0;
	private boolean exactlyK = true;
	private String mstype = "Random"; // or "RandomRandom"

	public void execute(GraphModel graphModel, AttributeModel attributeModel) {
		HierarchicalGraph graph = null;
		if (isDirected)
			graph = graphModel.getHierarchicalDirectedGraphVisible();
		else graph = graphModel.getHierarchicalUndirectedGraphVisible();
		execute(graph, attributeModel);
	}

	public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {
		cancel = false;

		avggce = 0.0;

		graph.readLock();

		Progress.start(progressTicket, samplesCount);

		double sum = 0.0;
		for (int i = 0; i < samplesCount && !cancel; ++i) {
			GraphView sourceView  = graph.getView();
			graph.readUnlock();
			GraphView currentView = graph.getGraphModel().copyView(sourceView);
			Graph g = graph.getGraphModel().getGraph(currentView);
			removeNodes(g);
			graph.readLock();
			sum += getGCE(g);
			Progress.progress(progressTicket);
		}
		avggce = sum / samplesCount;

		graph.readUnlock();
	}

	public void removeNodes(Graph graph) {
		Random random = new Random();

		Node[] nodes = graph.getNodes().toArray();
		List<Node> modNodes = new ArrayList<Node>();
		if (mstype.equals("Random")) {
			List<Node> rNodes = new LinkedList<Node>(Arrays.asList(nodes));
			for (int i = 0; i < k; ++i)
				if (exactlyK)
					modNodes.add(rNodes.remove(random.nextInt(rNodes.size())));
				else {
					Node node = rNodes.get(random.nextInt(rNodes.size()));
					if (!modNodes.contains(node))
						modNodes.add(node);
				}
		}
		else if (mstype.equals("RandomRandom")) {
			List<Node> rNodes = new LinkedList<Node>(Arrays.asList(nodes));
			for (int i = 0; i < k; ++i) {
				Node rNode;
				if (exactlyK)
					rNode = rNodes.remove(random.nextInt(rNodes.size()));
				else rNode = rNodes.get(random.nextInt(rNodes.size()));
				Node[] neighbors = graph.getNeighbors(rNode).toArray();
				Node node = neighbors[random.nextInt(neighbors.length)];
				if (!modNodes.contains(node))
					modNodes.add(node);
			}
		}
		else modNodes = Arrays.asList(nodes);

		for (Node node : modNodes)
			graph.removeNode(node);
	}

	private double getGCE(Graph graph) {
		int n = graph.getNodeCount();

		// FloydWarshall algorithm
		Node[] nodes = graph.getNodes().toArray();
		double[][] d = new double[n][n];
		for (int i = 0; i < n && !cancel; ++i)
			for (int j = 0; j < n && !cancel; ++j)
				if (i == j)
					d[i][j] = 0.0;
				else if (graph.isAdjacent(nodes[i], nodes[j]))
					d[i][j] = 1.0;
				else d[i][j] = Double.POSITIVE_INFINITY;
		for (int k = 0; k < n && !cancel; ++k)
			for (int i = 0; i < n && !cancel; ++i)
				for (int j = 0; j < n && !cancel; ++j)
					d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);

		double sum = 0.0;
		for (int i = 0; i < n && !cancel; ++i)
			for (int j = 0; j < n && !cancel; ++j)
				if (i != j)
					sum += 1.0 / d[i][j];

		if (n <= 1)
			return 0.0;
		return sum / (double)(n * (n - 1));
	}

	public double getAvgGCE() {
		return avggce;
	}

	public void setDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}
	
	public void setSamplesCount(int samplesCount) {
		this.samplesCount = samplesCount;
	}

	public void setK(int k) {
		this.k = k;
	}

	public void setExactlyK(boolean exactlyK) {
		this.exactlyK = exactlyK;
	}

	public void setMstype(String mstype) {
		this.mstype = mstype;
	}

	public boolean isDirected() {
		return isDirected;
	}

	public int getSamplesCount() {
		return samplesCount;
	}

	public String getMstype() {
		return mstype;
	}

	public int getK() {
		return k;
	}

	public boolean isExactlyK() {
		return exactlyK;
	}

	public String getReport() {
		NumberFormat f = new DecimalFormat("#0.0000");

		String report = "<html><body><h1>Average Global Connection Efficiency Report</h1>"
				+ "<hr>"
				+ "<br>"
				+ "<br><h2>Results:</h2>"
				+ "Samples Count: " + samplesCount + "<br>"
				+ "Average Global Connection Efficiency: " + f.format(avggce)
				+ "</body></html>";

		return report;
	}

	public boolean cancel() {
		cancel = true;
		return true;
	}

	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
}
