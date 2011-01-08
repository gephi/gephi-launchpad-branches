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

import Jama.Matrix;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.lib.hungarianalgorithm.HungarianAlgorithm;
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
	private double p = 1.0;

	private double[] dqn;

	private class Matcher {
		private UndirectedGraph gA;
		private UndirectedGraph gB;

		public Matcher(UndirectedGraph g1, UndirectedGraph g2) {
			gA = g1;
			gB = g2;
		}

		public double countDqn() {
			if (columns.length == 0)
				return 1.0;

			List<Matrix> v = new ArrayList<Matrix>();
			Node[] nodesB = gB.getNodes().toArray();
			Node[] nodesA = gA.getNodes().toArray();
			int nB = gB.getNodeCount();
			int nA = gA.getNodeCount();
			int n = Math.max(nB, nA);
			int lf = columns.length;

			for (int k = 0; k < lf; ++k) {
				Matrix vk = new Matrix(n, n);
				
				for (int i = 0; i < nB; ++i)
					for (int j = 0; j < nA; ++j) {
						double fkB = getValueForColumn(nodesB[i], columns[k]);
						double fkA = getValueForColumn(nodesA[j], columns[k]);
						double sum = 0.0;
						for (int r = 0; r < n; ++r) {
							double fkrB = /* r < nB ? Math.abs(fkB - getValueForColumn(nodesB[r], columns[k])) : */ fkB;
							double fkrA = /* r < nA ? Math.abs(fkA - getValueForColumn(nodesA[r], columns[k])) : */ fkA;
							sum += Math.pow(Math.abs(fkrB - fkrA), p);
						}
						vk.set(i, j, Math.pow(sum, 1.0 / p));
					}

				double max = getMaxValue(vk) + 0.01;

				for (int i = nB; i < n; ++i)
					for (int j = 0; j < n; ++j)
						vk.set(i, j, max);
				for (int j = nA; j < n; ++j)
					for (int i = 0; i < n; ++i)
						vk.set(i, j, max);

				v.add(vk);
			}

			for (int k = 0; k < lf; ++k) {
				double denominator = v.get(k).normF();
				if (denominator != 0)
					for (int i = 0; i < n; ++i)
						for (int j = 0; j < n; ++j)
							v.get(k).set(i, j, v.get(k).get(i, j) / denominator);
			}

			Matrix vij = new Matrix(n, n);
			for (int k = 0; k < lf; ++k)
				for (int i = 0; i < n; ++i)
					for (int j = 0; j < n; ++j)
						vij.set(i, j, vij.get(i, j) + v.get(k).get(i, j) / lf);

			Matrix s = new Matrix(n, n);
			for (int i = 0; i < n; ++i)
				for (int j = 0; j < n; ++j)
					s.set(i, j, -vij.get(i, j));

			int[][] x = HungarianAlgorithm.hgAlgorithm(s.getArray(), "max");
			double ds = 0.0;
			for (int i = 0; i < n; ++i)
				ds += s.get(x[i][0], x[i][1]);

			double maxMeasureValue = getMaxMeasureValue(vij);
			double dqn = maxMeasureValue != 0 ? -ds / maxMeasureValue : 0.0;
			return dqn;
		}

		private double getValueForColumn(Node node, AttributeColumn column) {
			Number value = (Number)node.getNodeData().getAttributes().getValue(column.getId());
			return value.doubleValue();
		}

		private double getMaxValue(Matrix m) {
			double max = Double.MIN_VALUE;
			for (int i = 0; i < m.getRowDimension(); ++i)
				for (int j = 0; j < m.getColumnDimension(); ++j)
					if (m.get(i, j) > max)
						max = m.get(i, j);
			return max;
		}

		private double getMaxMeasureValue(Matrix vij) {
			double sum = 0.0;
			for (int i = 0; i < vij.getColumnDimension(); ++i) {
				double max = 0.0;
				for (int j = 0; j < vij.getColumnDimension(); ++j)
					if (vij.get(i, j) > max)
						max = vij.get(i, j);
				sum += max;
			}
			return sum;
		}
	}

	public AttributeColumn[] getColumns() {
		return columns;
	}

	public void setColumns(AttributeColumn[] columns) {
		this.columns = Arrays.copyOf(columns, columns.length);
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
