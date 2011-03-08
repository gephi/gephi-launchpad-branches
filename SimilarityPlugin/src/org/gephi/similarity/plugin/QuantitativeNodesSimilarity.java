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
import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.type.DoubleList;
import org.gephi.data.attributes.type.NumberList;
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
 * Z. Tarapata. "Multicriteria weighted graphs similarity and its application for
 * decision situation pattern matching problem". Proceedings of the 13th IEEE/IFAC
 * International Conference on Methods and Models in Automation and Robotics.
 * 2007, 1149-1155.
 *
 * @author Cezary Bartosiak
 */
public class QuantitativeNodesSimilarity implements Similarity, LongTask {
	private boolean cancel = false;
	private ProgressTicket progressTicket;
	private String[] names;

	private AttributeColumn[] columns = new AttributeColumn[0];
	private double p = 1.0;
	private boolean doNorm = true;

	private Node[][][] xs;
	private double[][] xsvals;
	private double[] dqn;

	private class Matcher {
		private UndirectedGraph gA;
		private UndirectedGraph gB;

		public Matcher(UndirectedGraph g1, UndirectedGraph g2) {
			gA = g1;
			gB = g2;
		}

		public void countDqn(int index) {
			if (columns.length == 0) {
				xs[index] = new Node[0][0];
				xsvals[index] = new double[0];
				dqn[index] = 1.0;
				return;
			}

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
						double sum = 0.0;

						if (columns[k].getType().isListType()) {
							DoubleList fkB = getVectorValueForColumn(nodesB[i], columns[k]);
							DoubleList fkA = getVectorValueForColumn(nodesA[j], columns[k]);

							int size = Math.min(fkB.size(), fkA.size());
							for (int r = 0; r < size; ++r) {
								double fkrB = fkB.getItem(r);
								double fkrA = fkA.getItem(r);
								sum += Math.pow(Math.abs(fkrB - fkrA), p);
							}
						}
						else {
							double fkB = getScalarValueForColumn(nodesB[i], columns[k]);
							double fkA = getScalarValueForColumn(nodesA[j], columns[k]);
							sum += Math.pow(Math.abs(fkB - fkA), p);
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

			if (doNorm)
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
			xs[index] = new Node[n][2];
			xsvals[index] = new double[n];
			double ds = 0.0;
			for (int i = 0; i < n; ++i) {
				xs[index][i][0] = nodesA[x[i][0]];
				xs[index][i][1] = nodesB[x[i][1]];
				xsvals[index][i] = -s.get(x[i][0], x[i][1]);
				ds += s.get(x[i][0], x[i][1]);
			}

			double maxMeasureValue = getMaxMeasureValue(vij);
			dqn[index] = maxMeasureValue != 0 ? -ds / maxMeasureValue : 0.0;
		}

		private double getScalarValueForColumn(Node node, AttributeColumn column) {
			Number value = (Number)node.getNodeData().getAttributes().getValue(column.getId());
			return value.doubleValue();
		}

		private DoubleList getVectorValueForColumn(Node node, AttributeColumn column) {
			NumberList<Number> vector = (NumberList<Number>)node.getNodeData().getAttributes().getValue(column.getId());
			double[] values = new double[vector.size()];
			for (int i = 0; i < vector.size(); ++i)
				values[i] = ((Number)vector.getItem(i)).doubleValue();
			return new DoubleList(values);
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

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public boolean getDoNorm() {
		return doNorm;
	}

	public void setDoNorm(boolean doNorm) {
		this.doNorm = doNorm;
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

		xs = new Node[targetGraphs.length][][];
		xsvals = new double[targetGraphs.length][];
		dqn = new double[targetGraphs.length];
		for (int i = 0; i < targetGraphs.length; ++i) {
			if (cancel) {
				sourceGraph.readUnlockAll();
				for (Graph targetGraph : targetGraphs)
					targetGraph.readUnlockAll();
				return;
			}

			try {
				new Matcher(
					(UndirectedGraph)sourceGraph,
					(UndirectedGraph)targetGraphs[i]).countDqn(i);
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

		JFreeChart chart = ChartFactory.createBarChart(
				"Quantitative Nodes Similarity Chart",
				"Graphs",
				"dqn (less = better)",
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
			final String fileName = "qns.png";
			final File file = tempDir.createFile(fileName);
			image = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
			ChartUtilities.saveChartAsPNG(file, chart, 600, 400, info);
		}
		catch (Exception e) { }

		String xss = "";
		for (int i = 0; i < xs.length; ++i) {
			xss += "x for source - " + names[i] + ":<br>";
			if (xs[i].length == 0)
				xss += "&nbsp;&nbsp;&nbsp;" + "none<br>";
			else for (int j = 0; j < xs[i].length; ++j)
				xss += "&nbsp;&nbsp;&nbsp;\"" + xs[i][j][0].getNodeData().getLabel() +
						"\" - \"" + xs[i][j][1].getNodeData().getLabel() +
						"\" with " + new DecimalFormat("0.000000").format(xsvals[i][j]) + "<br>";
			xss += "<br>";
		}

		String report = "<html><body><h1>Quantitative Nodes Similarity Report</h1><hr><br>";
		report += image + "<br><br>" + xss + "</body></html>";
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
