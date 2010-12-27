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
package org.gephi.similarity.plugin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.similarity.spi.Similarity;
import org.gephi.utils.TempDirUtils;
import org.gephi.utils.TempDirUtils.TempDir;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class QuantitativeNodesSimilarity implements Similarity, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;
	private String[] names;

	private AttributeColumn[] columns = new AttributeColumn[0];

	private double[] dqn;

	private class Matcher {
		public Matcher(UndirectedGraph g1, UndirectedGraph g2) {

		}

		public double countDqn() {
			return 0.5;
		}
	}

	public AttributeColumn[] getColumns() {
		return columns;
	}

	public void setColumns(AttributeColumn[] columns) {
		this.columns = columns;
	}

	@Override
	public void execute(GraphModel sourceGraphModel, GraphModel[] targetGraphModels,
						AttributeModel sourceAttributeModel, AttributeModel[] targetAttributeModels,
						String[] graphNames) {
		cancel = false;
		names = graphNames;

		Graph   sourceGraph;
		Graph[] targetGraphs = new Graph[targetGraphModels.length];
		sourceGraph = sourceGraphModel.getUndirectedGraph();
		for (int i = 0; i < targetGraphModels.length; ++i)
			targetGraphs[i] = targetGraphModels[i].getUndirectedGraph();

		sourceGraph.readLock();
		for (Graph targetGraph : targetGraphs)
			targetGraph.readLock();

		Progress.start(progressTicket);

		dqn = new double[targetGraphs.length];
		for (int i = 0; i < targetGraphs.length; ++i) {
			if (cancel) {
				sourceGraph.readUnlockAll();
				for (Graph targetGraph : targetGraphs)
					targetGraph.readUnlockAll();
				return;
			}

			try {
				dqn[i] = new Matcher(
						(UndirectedGraph)sourceGraph,
						(UndirectedGraph)targetGraphs[i]).countDqn();
			}
			catch (Exception ex) {
				sourceGraph.readUnlockAll();
				for (Graph targetGraph : targetGraphs)
					targetGraph.readUnlockAll();
				return;
			}
		}

		sourceGraph.readUnlock();
		for (Graph targetGraph : targetGraphs)
			targetGraph.readUnlock();
	}

	@Override
	public String getReport() {
		double[][] data = new double[][] { dqn };
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				new String[] { "dqn" }, names, data);

		CategoryItemRenderer renderer = new CategoryStepRenderer(true);
		CategoryAxis domainAxis = new CategoryAxis("Graphs");
		ValueAxis rangeAxis = new NumberAxis("dqn (less = better)");
		CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis, renderer);
		JFreeChart chart = new JFreeChart("Quantitative Nodes Similarity Chart", plot);

		chart.setBackgroundPaint(Color.WHITE);

		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.WHITE);

		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);

		rangeAxis.setRange(0.0, 1.0);

		renderer.setSeriesStroke(0, new BasicStroke(10.0f));

		String image = "";
		try {
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			TempDir tempDir = TempDirUtils.createTempDir();
			final String fileName = "qns.png";
			final File file = tempDir.createFile(fileName);
			image = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
			ChartUtilities.saveChartAsPNG(file, chart, 600, 400, info);
		}
		catch (Exception e) { }

		String report = "<html><body><h1>Quantitative Nodes Similarity Report</h1><hr><br>";
		report += image + "</body></html>";
		return report;
	}

	@Override
	public boolean cancel() {
		cancel = true;
		return true;
	}

	@Override
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
}
