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
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphModel;
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
public class GlobalConnectionEfficiency implements Statistics, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;

	private double gce = 0.0;

	private boolean isDirected = false;

	public void execute(GraphModel graphModel, AttributeModel attributeModel) {
		HierarchicalGraph graph = null;
		if (isDirected)
			graph = graphModel.getHierarchicalDirectedGraphVisible();
		else graph = graphModel.getHierarchicalUndirectedGraphVisible();
		execute(graph, attributeModel);
	}

	public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {
		cancel = false;

		gce = 0.0;

		graph.readLock();

		int n = graph.getNodeCount();
		Progress.start(progressTicket, n * n + n * n + n * n);

		// FloydWarshall algorithm
		Node[] nodes = graph.getNodes().toArray();
		double[][] d = new double[n][n];
		for (int i = 0; i < n && !cancel; ++i)
			for (int j = 0; j < n && !cancel; ++j) {
				if (i == j)
					d[i][j] = 0.0;
				else if (graph.isAdjacent(nodes[i], nodes[j]))
					d[i][j] = 1.0;
				else d[i][j] = Double.POSITIVE_INFINITY;
				Progress.progress(progressTicket);
			}
		for (int k = 0; k < n && !cancel; ++k)
			for (int i = 0; i < n && !cancel; ++i) {
				for (int j = 0; j < n && !cancel; ++j)
					d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
				Progress.progress(progressTicket);
			}

		double sum = 0.0;
		for (int i = 0; i < n && !cancel; ++i)
			for (int j = 0; j < n && !cancel; ++j) {
				if (i != j)
					sum += 1.0 / d[i][j];
				Progress.progress(progressTicket);
			}
		gce = sum / (double)(n * (n - 1));

		graph.readUnlock();
	}

	public void setDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}

	public boolean isDirected() {
		return isDirected;
	}

	public double getGCE() {
		return gce;
	}

	public String getReport() {
		NumberFormat f = new DecimalFormat("#0.0000");

		String report = "<html><body><h1>Global Connection Efficiency Report</h1>"
				+ "<hr>"
				+ "<br>"
				+ "<br><h2>Results:</h2>"
				+ "Global Connection Efficiency: " + f.format(gce)
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
