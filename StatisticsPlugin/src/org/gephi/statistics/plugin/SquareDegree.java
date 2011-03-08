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
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class SquareDegree implements Statistics, LongTask {
	public static final String SDEGREE = "square degree";

	private boolean        cancel;
	private ProgressTicket progress;
	private double         avgSDegree;

	public double getAverageDegree() {
		return avgSDegree;
	}

	public void execute(GraphModel graphModel, AttributeModel attributeModel) {
		HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
		execute(graph, attributeModel);
	}

	public void execute(HierarchicalGraph graph, AttributeModel attributeModel) {
		cancel = false;

		AttributeTable  nodeTable = attributeModel.getNodeTable();
		AttributeColumn degCol    = nodeTable.getColumn(SDEGREE);
		if (degCol == null)
			degCol = nodeTable.addColumn(SDEGREE, "Square Degree", AttributeType.INT, AttributeOrigin.COMPUTED, 0);

		graph.readLock();

		Progress.start(progress, graph.getNodeCount());
		int i = 0;

		for (Node n : graph.getNodes()) {
			AttributeRow row = (AttributeRow)n.getNodeData().getAttributes();

			int sdeg = graph.getDegree(n) * graph.getDegree(n);
			row.setValue(degCol, sdeg);
			avgSDegree += sdeg;

			if (cancel)
				break;
			i++;
			Progress.progress(progress, i);
		}

		avgSDegree /= graph.getNodeCount();

		graph.readUnlockAll();
	}

	public String getReport() {
		NumberFormat f = new DecimalFormat("#0.000");

		String report = "<html><body><h1>Square Degree Report</h1>"
						+ "<hr>"
						+ "<br />"
						+ "<br /><h2>Results:</h2>"
						+ "Average Square Degree: " + f.format(avgSDegree)
						+ "</body></html>";

		return report;
	}

	public boolean cancel() {
		cancel = true;
		return true;
	}

	public void setProgressTicket(ProgressTicket progressTicket) {
		progress = progressTicket;
	}
}
