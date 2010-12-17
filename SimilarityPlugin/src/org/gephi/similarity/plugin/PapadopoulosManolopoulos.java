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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.similarity.spi.Similarity;
import org.gephi.utils.TempDirUtils;
import org.gephi.utils.TempDirUtils.TempDir;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

/**
 * A. N. Papadopoulos, Y. Manolopoulos. "Structure-based similarity search with
 * graph histograms". DEXA '99 Proceedings of the 10th International Workshop on
 * Database & Expert Systems Applications. 1999, 174-178.
 *
 * @author Cezary Bartosiak
 */
public class PapadopoulosManolopoulos implements Similarity, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;
	private String[] names;

	private double[] dist;

	private class Matcher {
		private UndirectedGraph gA;
		private UndirectedGraph gB;

		public Matcher(UndirectedGraph g1, UndirectedGraph g2) {
			gA = g1;
			gB = g2;
		}

		public double countDistance() {
			int n = Math.max(gA.getNodeCount(), gB.getNodeCount());
			List<Integer> histogramA = getHistogram(gA, n);
			List<Integer> histogramB = getHistogram(gB, n);
			double d = 0.0;
			for (int i = 0; i < n; ++i)
				d += Math.abs(histogramA.get(i) - histogramB.get(i));
			return d / getMaxDistance();
		}

		private List<Integer> getHistogram(UndirectedGraph g, int n) {
			List<Integer> histogram = new ArrayList<Integer>();
			for (Node node : g.getNodes().toArray()) {
				int degree = g.getDegree(node);
				histogram.add(degree + 1);
			}
			while (histogram.size() < n)
				histogram.add(0);
			Integer[] array = histogram.toArray(new Integer[] { });
			Arrays.sort(array);
			return Arrays.asList(array);
		}

		private double getMaxDistance() {
			int maxN = Math.max(gA.getNodeCount(), gB.getNodeCount());
			int minN = Math.min(gA.getNodeCount(), gB.getNodeCount());
			int max = maxN * maxN + maxN - minN;
			return max != 0 ? max : 1;
		}
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

		dist = new double[targetGraphs.length];
		for (int i = 0; i < targetGraphs.length; ++i) {
			if (cancel) {
				sourceGraph.readUnlockAll();
				for (Graph targetGraph : targetGraphs)
					targetGraph.readUnlockAll();
				return;
			}

			try {
				dist[i] = new Matcher(
						(UndirectedGraph)sourceGraph,
						(UndirectedGraph)targetGraphs[i]).countDistance();
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
		double[][] data = new double[][] { dist };
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				new String[] { "Edit distance" }, names, data);

		JFreeChart chart = ChartFactory.createBarChart(
				"Papadopoulos & Manolopoulos Method Chart",
				"Graphs",
				"Edit distance (less = better)",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				false,
				false
		);

		chart.setBackgroundPaint(Color.WHITE);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.WHITE);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(0.0, 1.0);

		String image = "";
		try {
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			TempDir tempDir = TempDirUtils.createTempDir();
			final String fileName = "pmm.png";
			final File file = tempDir.createFile(fileName);
			image = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
			ChartUtilities.saveChartAsPNG(file, chart, 600, 400, info);
		}
		catch (Exception e) { }

		String report = "<html><body><h1>Papadopoulos & Manolopoulos Method Report</h1><hr><br>";
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
