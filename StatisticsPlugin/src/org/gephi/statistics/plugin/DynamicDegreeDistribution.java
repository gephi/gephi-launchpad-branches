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

import java.util.ArrayList;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicGraph;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.statistics.DynamicStatisticsImpl;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

/**
 * This class measures how closely the degree distribution of a
 * network follows a power-law scale.  An alpha value between 2 and 3
 * implies a power law. It's a dynamic version that means the statistic
 * is measured for every part (year, month etc. it depends on user's
 * settings) of the given time interval.
 *
 * @author Cezary Bartosiak
 */
public class DynamicDegreeDistribution extends DynamicStatisticsImpl implements Statistics, LongTask {
	private String         report = "";
	private boolean        cancel;
	private ProgressTicket progressTicket;

	private boolean           directed;
	private ArrayList<String> reports;

	/**
	 * Indicates if this network should be directed or undirected.
	 *
	 * @param directed indicates the metric's interpretation of this network
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	/**
	 * Returns {@code true} if this network should be directed,
	 * otherwise {@code false}.
	 *
	 * @return {@code true} if this network should be directed,
	 *         otherwise {@code false}.
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * Executes the metric.
	 * 
	 * @param graphModel     graph's model to work on
	 * @param attributeModel attributes' model to work on
	 */
	public void execute(GraphModel graphModel, AttributeModel attributeModel) {
		Graph graph = graphModel.getGraphVisible();

		DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
		DynamicModel      dynamicModel      = dynamicController.getModel();
		DynamicGraph      dynamicGraph      = dynamicModel.createDynamicGraph(graph, timeInterval);

		report = "TEST";
		cancel = false;
		graph.writeLock();
		
		int progress = 0;
		Progress.start(progressTicket, progress);

		reports = new ArrayList<String>();
		for (double low = timeInterval.getLow(); low < timeInterval.getHigh(); low += window) {
			double high = low + window;

			Graph              g  = dynamicGraph.getSnapshotGraph(low, high, estimator);
			DegreeDistribution dd = new DegreeDistribution();
			dd.setDirected(directed);
			dd.execute(g, attributeModel);
			reports.add(dd.getReport());

			Progress.progress(progressTicket, ++progress);
			if (cancel) {
				graph.writeUnlock();
				return;
			}
		}

		graph.writeUnlock();
	}

	/**
	 * Returns the report based on the interpretation of the network.
	 * 
	 * @return the report based on the interpretation of the network.
	 */
	public String getReport() {
		return report;
	}

	/**
	 * Stops the execution of this metric.
	 *
	 * @return true if the execution was stopped, otherwise false.
	 */
	public boolean cancel() {
		cancel = true;
		return true;
	}

	/**
	 * Sets the progress ticket for the metric.
	 *
	 * @param progressTicket progress ticket to set
	 */
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
}
