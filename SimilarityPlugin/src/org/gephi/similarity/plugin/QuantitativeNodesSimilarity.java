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
	private int p = 1;
	private boolean[] doNorm = new boolean[0];
	private double[] lambdas = new double[0];

	private Node[][][] xs;
	private double[][] xsvals;
	private double[] normMaxRow;
	private double[] normMaxCol;
	private double[] normMaxRowReal;
	private double[] normMaxColReal;
	private double[] normSrc;
	private double[] normTgt;
	private double[] dqnArtf;
	private double[] dqnReal;
	private double[] dqnMaxRow;
	private double[] dqnMaxCol;
	private double[] dqnMaxRowReal;
	private double[] dqnMaxColReal;
	private double[] dqnSrc;
	private double[] dqnTgt;

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
				normMaxRow[index] = 1.0;
				normMaxCol[index] = 1.0;
				normMaxRowReal[index] = 1.0;
				normMaxColReal[index] = 1.0;
				normSrc[index] = 1.0;
				normTgt[index] = 1.0;
				dqnArtf[index] = 1.0;
				dqnReal[index] = 1.0;
				dqnMaxRow[index] = 1.0;
				dqnMaxCol[index] = 1.0;
				dqnMaxRowReal[index] = 1.0;
				dqnMaxColReal[index] = 1.0;
				dqnSrc[index] = 1.0;
				dqnTgt[index] = 1.0;
				return;
			}

			List<Matrix> v = new ArrayList<Matrix>();
			Node[] nodesB = gB.getNodes().toArray();
			Node[] nodesA = gA.getNodes().toArray();
			int nB = gB.getNodeCount();
			int nA = gA.getNodeCount();
			int n = Math.max(nB, nA);
			int lf = columns.length;

			for (int k = 0; k < lf && !cancel; ++k) {
				Matrix vk = new Matrix(nB, nA);
				
				for (int i = 0; i < nB && !cancel; ++i)
					for (int j = 0; j < nA && !cancel; ++j) {
						double sum = 0.0;

						if (columns[k].getType().isListType()) {
							DoubleList fkB = getVectorValueForColumn(nodesB[i], columns[k]);
							DoubleList fkA = getVectorValueForColumn(nodesA[j], columns[k]);

							int size = Math.min(fkB.size(), fkA.size());
							for (int r = 0; r < size && !cancel; ++r) {
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

				v.add(vk);

				Progress.progress(progressTicket);
			}

			for (int k = 0; k < lf && !cancel; ++k) {
				if (doNorm[k]) {
					double denominator = v.get(k).normF();
					if (denominator != 0)
						for (int i = 0; i < nB && !cancel; ++i)
							for (int j = 0; j < nA && !cancel; ++j)
								v.get(k).set(i, j, v.get(k).get(i, j) / denominator);
				}
				Progress.progress(progressTicket);
			}

			Matrix vij = new Matrix(n, n);
			for (int k = 0; k < lf && !cancel; ++k) {
				for (int i = 0; i < n && !cancel; ++i)
					for (int j = 0; j < n && !cancel; ++j)
						if (v.get(k).getRowDimension() <= i || v.get(k).getColumnDimension() <= j)
							vij.set(i, j, vij.get(i, j) + 1.0 * lambdas[k]);
						else vij.set(i, j, vij.get(i, j) + v.get(k).get(i, j) * lambdas[k]);
				Progress.progress(progressTicket);
			}

			Matrix s = new Matrix(n, n);
			for (int i = 0; i < n && !cancel; ++i)
				for (int j = 0; j < n && !cancel; ++j)
					s.set(i, j, -vij.get(i, j));

			int[][] x = new int[0][0];
			if (!cancel)
				x = HungarianAlgorithm.hgAlgorithm(s.getArray(), "max");
			xs[index] = new Node[n][2];
			xsvals[index] = new double[n];
			double dsArtf = 0.0;
			double dsReal = 0.0;
			for (int i = 0; i < n && !cancel; ++i) {
				xs[index][i][0] = x[i][0] >= nodesB.length ? null : nodesB[x[i][0]];
				xs[index][i][1] = x[i][1] >= nodesA.length ? null : nodesA[x[i][1]];
				xsvals[index][i] = -s.get(x[i][0], x[i][1]);
				dsArtf += s.get(x[i][0], x[i][1]);
				if (xs[index][i][0] != null && xs[index][i][1] != null)
					dsReal += s.get(x[i][0], x[i][1]);
			}

			normMaxRow[index] = getMaxRowMeasureValue(vij, n, n);
			normMaxCol[index] = getMaxColMeasureValue(vij, n, n);
			normMaxRowReal[index] = getMaxRowMeasureValue(vij, nB, nA);
			normMaxColReal[index] = getMaxColMeasureValue(vij, nA, nB);
			normSrc[index] = nA;
			normTgt[index] = nB;
			dqnArtf[index] = -dsArtf;
			dqnReal[index] = -dsReal;
			dqnMaxRow[index] = normMaxRow[index] != 0 ? -dsArtf / normMaxRow[index] : 0.0;
			dqnMaxCol[index] = normMaxCol[index] != 0 ? -dsArtf / normMaxCol[index] : 0.0;
			dqnMaxRowReal[index] = normMaxRowReal[index] != 0 ? -dsReal / normMaxRowReal[index] : 0.0;
			dqnMaxColReal[index] = normMaxColReal[index] != 0 ? -dsReal / normMaxColReal[index] : 0.0;
			dqnSrc[index] = (nA > nB ? -dsArtf : -dsReal) / normSrc[index];
			dqnTgt[index] = (nB > nA ? -dsArtf : -dsReal) / normTgt[index];
		}

		private double getScalarValueForColumn(Node node, AttributeColumn column) {
			Number value = (Number)node.getNodeData().getAttributes().getValue(column.getId());
			return value.doubleValue();
		}

		private DoubleList getVectorValueForColumn(Node node, AttributeColumn column) {
			NumberList<Number> vector = (NumberList<Number>)node.getNodeData().getAttributes().getValue(column.getId());
			double[] values = new double[vector.size()];
			for (int i = 0; i < vector.size() && !cancel; ++i)
				values[i] = ((Number)vector.getItem(i)).doubleValue();
			return new DoubleList(values);
		}

		private double getMaxRowMeasureValue(Matrix vij, int maxi, int maxj) {
			double sum = 0.0;
			for (int i = 0; i < maxi && !cancel; ++i) {
				double max = 0.0;
				for (int j = 0; j < maxj && !cancel; ++j)
					if (vij.get(i, j) > max)
						max = vij.get(i, j);
				sum += max;
			}
			return sum;
		}

		private double getMaxColMeasureValue(Matrix vij, int maxj, int maxi) {
			double sum = 0.0;
			for (int j = 0; j < maxj && !cancel; ++j) {
				double max = 0.0;
				for (int i = 0; i < maxi && !cancel; ++i)
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

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public boolean[] getDoNorm() {
		return doNorm;
	}

	public void setDoNorm(boolean[] doNorm) {
		this.doNorm = doNorm;
	}

	public double[] getLambdas() {
		return lambdas;
	}

	public void setLambdas(double[] lambdas) {
		this.lambdas = lambdas;
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

		Progress.start(progressTicket, targetGraphs.length * columns.length * 3);

		xs = new Node[targetGraphs.length][][];
		xsvals = new double[targetGraphs.length][];
		normMaxRow = new double[targetGraphs.length];
		normMaxCol = new double[targetGraphs.length];
		normMaxRowReal = new double[targetGraphs.length];
		normMaxColReal = new double[targetGraphs.length];
		normSrc = new double[targetGraphs.length];
		normTgt = new double[targetGraphs.length];
		dqnArtf = new double[targetGraphs.length];
		dqnReal = new double[targetGraphs.length];
		dqnMaxRow = new double[targetGraphs.length];
		dqnMaxCol = new double[targetGraphs.length];
		dqnMaxRowReal = new double[targetGraphs.length];
		dqnMaxColReal = new double[targetGraphs.length];
		dqnSrc = new double[targetGraphs.length];
		dqnTgt = new double[targetGraphs.length];
		for (int i = 0; i < targetGraphs.length && !cancel; ++i) {
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

		Progress.finish(progressTicket);

		sourceGraph.readUnlock();
		for (Graph targetGraph : targetGraphs)
			targetGraph.readUnlock();
	}

	private JFreeChart getChart(double[] dqn, String title) {
		double[][] data = new double[][] { dqn };
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				new String[] { "dqn" }, names, data);

		JFreeChart chart = ChartFactory.createBarChart(
				title,
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

		return chart;
	}

	@Override
	public String getReport() {
		JFreeChart[] charts = new JFreeChart[6];
		charts[0] = getChart(dqnMaxRow, "QNS Chart (max row sum norm)");
		charts[1] = getChart(dqnMaxRowReal, "QNS Chart (max row sum real norm)");
		charts[2] = getChart(dqnMaxCol, "QNS Chart (max col sum norm)");
		charts[3] = getChart(dqnMaxColReal, "QNS Chart (max col sum real norm)");
		charts[4] = getChart(dqnSrc, "QNS Chart (src node count norm)");
		charts[5] = getChart(dqnTgt, "QNS Chart (tgt node count norm)");

		String[] images = new String[6];
		for (int i = 0; i < 6; ++i)
			try {
				final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
				TempDir tempDir = TempDirUtils.createTempDir();
				final String fileName = "qns.png";
				final File file = tempDir.createFile(fileName);
				images[i] = "<img src=\"file:" + file.getAbsolutePath() + "\" " + "width=\"600\" height=\"400\" border=\"0\" usemap=\"#chart\"></img>";
				ChartUtilities.saveChartAsPNG(file, charts[i], 600, 400, info);
			}
			catch (Exception e) { }

		String xss = "";
		for (int i = 0; i < xs.length; ++i) {
			xss += "x for source (" + new DecimalFormat("#0").format(normSrc[i]) + " nodes) - " + names[i] +
					" (" + new DecimalFormat("#0").format(normTgt[i]) + " nodes):<br>";
			xss += "{dqnArtf = " + new DecimalFormat("0.000000").format(dqnArtf[i]) +
					", dqnReal = " + new DecimalFormat("0.000000").format(dqnReal[i]) +
					", max row sum = " + new DecimalFormat("0.000000").format(normMaxRow[i]) +
					", max row sum real = " + new DecimalFormat("0.000000").format(normMaxRowReal[i]) +
					", max col sum = " + new DecimalFormat("0.000000").format(normMaxCol[i]) +
					", max col sum real = " + new DecimalFormat("0.000000").format(normMaxColReal[i]) + "}<br>";
			if (xs[i].length == 0)
				xss += "&nbsp;&nbsp;&nbsp;" + "none<br>";
			else for (int j = 0; j < xs[i].length; ++j) {
				String label1 = "Node x";
				String label2 = "Node y";
				if (xs[i][j][0] != null)
					label2 = xs[i][j][0].getNodeData().getLabel();
				if (xs[i][j][1] != null)
					label1 = xs[i][j][1].getNodeData().getLabel();
				xss += "&nbsp;&nbsp;&nbsp;\"" + label1 + "\" - \"" + label2 +
						"\" with " + new DecimalFormat("0.000000").format(xsvals[i][j]) + "<br>";
			}
			xss += "<br>";
		}

		String report = "<html><body><h1>Quantitative Nodes Similarity Report</h1><hr><br>";
		report += images[0] + "<br><br>";
		report += images[1] + "<br><br>";
		report += images[2] + "<br><br>";
		report += images[3] + "<br><br>";
		report += images[4] + "<br><br>";
		report += images[5] + "<br><br>";
		report += xss + "</body></html>";
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
