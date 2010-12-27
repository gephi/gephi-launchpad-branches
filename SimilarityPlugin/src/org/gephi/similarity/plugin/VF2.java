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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
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
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.openide.util.Lookup;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class VF2 implements Similarity, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;
	private String[] names;

	private boolean directed;

	private boolean[] isomorphic;

	public VF2() {
		GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
		if (graphController != null && graphController.getModel() != null)
			directed = graphController.getModel().isDirected();
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public boolean isDirected() {
		return directed;
	}

	private class UndirectedMatcher {
		private UndirectedGraph g1;
		private UndirectedGraph g2;
		private Set<Node> g1Nodes;
		private Set<Node> g2Nodes;

		private List<Node> core1;
		private List<Node> core2;
		private List<Node> inout1;
		private List<Node> inout2;

		public UndirectedMatcher(UndirectedGraph g1, UndirectedGraph g2) {
			this.g1 = g1;
			this.g2 = g2;
			g1Nodes = new HashSet<Node>();
			g2Nodes = new HashSet<Node>();
			g1Nodes.addAll(Arrays.asList(g1.getNodes().toArray()));
			g2Nodes.addAll(Arrays.asList(g2.getNodes().toArray()));

			// recursion limit?

			initialize();
		}

		private void initialize() {
			core1 = new ArrayList<Node>();
			core2 = new ArrayList<Node>();
			inout1 = new ArrayList<Node>();
			inout2 = new ArrayList<Node>();
		}

		public boolean isIsomorphic() {
			return true;
		}
	}

	private class DirectedMatcher {
		public DirectedMatcher(DirectedGraph g1, DirectedGraph g2) {

		}

		public boolean isIsomorphic() {
			return true;
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
		if (directed) {
			sourceGraph = sourceGraphModel.getDirectedGraph();
			for (int i = 0; i < targetGraphModels.length; ++i)
				targetGraphs[i] = targetGraphModels[i].getDirectedGraph();
		}
		else {
			sourceGraph = sourceGraphModel.getUndirectedGraph();
			for (int i = 0; i < targetGraphModels.length; ++i)
				targetGraphs[i] = targetGraphModels[i].getUndirectedGraph();
		}

		sourceGraph.readLock();
		for (Graph targetGraph : targetGraphs)
			targetGraph.readLock();

		Progress.start(progressTicket);

		isomorphic = new boolean[targetGraphs.length];
		for (int i = 0; i < targetGraphs.length; ++i) {
			if (cancel) {
				sourceGraph.readUnlockAll();
				for (Graph targetGraph : targetGraphs)
					targetGraph.readUnlockAll();
				return;
			}

			try {
				if (directed)
					isomorphic[i] = new DirectedMatcher(
							(DirectedGraph)sourceGraph,
							(DirectedGraph)targetGraphs[i]).isIsomorphic();
				else isomorphic[i] = new UndirectedMatcher(
							(UndirectedGraph)sourceGraph,
							(UndirectedGraph)targetGraphs[i]).isIsomorphic();
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
		double[][] data = new double[1][isomorphic.length];
		for (int i = 0; i < isomorphic.length; ++i)
			data[0][i] = isomorphic[i] ? 1.0 : 0.0;
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				new String[] { "Isomorphism" }, names, data);

		CategoryItemRenderer renderer = new CategoryStepRenderer(true);
		CategoryAxis domainAxis = new CategoryAxis("Graphs");
		ValueAxis rangeAxis = new SymbolAxis("Is isomorphic", new String[] { "False", "True" });
		CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis, renderer);
		JFreeChart chart = new JFreeChart("Isomorphism Chart", plot);

		chart.setBackgroundPaint(Color.WHITE);

		plot.setBackgroundPaint(Color.GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.WHITE);

		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);

		renderer.setSeriesStroke(0, new BasicStroke(10.0f));

		String image = "";
		try {
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			TempDir tempDir = TempDirUtils.createTempDir();
			final String fileName = "vf2.png";
			final File file = tempDir.createFile(fileName);
			image = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
			ChartUtilities.saveChartAsPNG(file, chart, 600, 400, info);
		}
		catch (Exception e) { }

		String report = "<html><body><h1>VF2 Report</h1><hr><br>";
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
