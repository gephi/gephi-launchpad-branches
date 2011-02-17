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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.openide.util.Lookup;

/**
 * L. P. Cordella, P. Foggia, C. Sansone, M. Vento. "An improved algorithm for
 * matching large graphs". Proc. of the 3rd IAPR TC-15 Workshop on Graphbased
 * Representations in Pattern Recognition. 2001, 149-159.
 *
 * Based on IGraph library implementation.
 *
 * http://igraph.sourceforge.net/
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

	private class Matcher {
		private Graph graph1;
		private Graph graph2;

		public Matcher(Graph g1, Graph g2) {
			graph1 = g1;
			graph2 = g2;
		}

		public boolean isIsomorphic() {
			int noOfNodes = graph1.getNodeCount();
			List<Integer> core1, core2;
			List<Integer> in1, in2, out1, out2;
			int in1size = 0, in2size = 0, out1size = 0, out2size = 0;
			List<Integer> inneis1, inneis2, outneis1, outneis2;
			int matchedNodes = 0;
			int depth;
			int cand1, cand2;
			int last1, last2;
			Stack<Integer> path;
			List<List<Integer>> inadj1, inadj2, outadj1, outadj2;
			List<Integer> indeg1, indeg2, outdeg1, outdeg2;

			if (noOfNodes != graph2.getNodeCount() || graph1.getEdgeCount() != graph2.getEdgeCount())
				return false;

			core1 = new ArrayList<Integer>(noOfNodes);
			core2 = new ArrayList<Integer>(noOfNodes);
			in1 = new ArrayList<Integer>(noOfNodes);
			in2 = new ArrayList<Integer>(noOfNodes);
			out1 = new ArrayList<Integer>(noOfNodes);
			out2 = new ArrayList<Integer>(noOfNodes);
			for (int i = 0; i < noOfNodes; ++i) {
				core1.add(0);
				core2.add(0);
				in1.add(0);
				in2.add(0);
				out1.add(0);
				out2.add(0);
			}
			path = new Stack<Integer>();
			inadj1 = new ArrayList<List<Integer>>();
			outadj1 = new ArrayList<List<Integer>>();
			inadj2 = new ArrayList<List<Integer>>();
			outadj2 = new ArrayList<List<Integer>>();
			initAdjList(graph1, inadj1, false);
			initAdjList(graph1, outadj1, true);
			initAdjList(graph2, inadj2, false);
			initAdjList(graph2, outadj2, true);
			indeg1 = new ArrayList<Integer>();
			indeg2 = new ArrayList<Integer>();
			outdeg1 = new ArrayList<Integer>();
			outdeg2 = new ArrayList<Integer>();

			degree(graph1, indeg1, false);
			degree(graph2, indeg2, false);
			degree(graph1, outdeg1, true);
			degree(graph2, outdeg2, true);

			depth = 0; last1 = -1; last2 = -1;
			while (depth >= 0) {
				int i;

				cand1 = -1; cand2 = -1;
				if (in1size != in2size || out1size != out2size);
				else if (out1size > 0 && out2size > 0) {
					if (last2 >= 0)
						cand2 = last2;
					else {
						i = 0;
						while (cand2 < 0) {
							if (out2.get(i) > 0 && core2.get(i) == 0)
								cand2 = i;
							i++;
						}
					}

					i = last1 + 1;
					while (cand1 < 0 && i < noOfNodes) {
						if (out1.get(i) > 0 && core1.get(i) == 0)
							cand1 = i;
						i++;
					}
				}
				else if (in1size > 0 && in2size > 0) {
					if (last2 >= 0)
						cand2 = last2;
					else {
						i = 0;
						while (cand2 < 0) {
							if (in2.get(i) > 0 && core2.get(i) == 0)
								cand2 = i;
							i++;
						}
					}

					i = last1 + 1;
					while (cand1 < 0 && i < noOfNodes) {
						if (in1.get(i) > 0 && core1.get(i) == 0)
							cand1 = i;
						i++;
					}
				}
				else {
					if (last2 >= 0)
						cand2 = last2;
					else {
						i = 0;
						while (cand2 < 0) {
							if (core2.get(i) == 0)
								cand2 = i;
							i++;
						}
					}

					i = last1 + 1;
					while (cand1 < 0 && i < noOfNodes) {
						if (core1.get(i) == 0)
							cand1 = i;
						i++;
					}
				}

				if (cand1 < 0 || cand2 < 0) {
					if (depth >= 1) {
						last2 = path.pop();
						last1 = path.pop();
				 		matchedNodes--;
						core1.set(last1, 0);
						core2.set(last2, 0);

						if (in1.get(last1) != 0)
							in1size++;
						if (out1.get(last1) != 0)
							out1size++;
						if (in2.get(last2) != 0)
							in2size++;
						if (out2.get(last2) != 0)
							out2size++;

						inneis1 = inadj1.get(last1);
						for (i = 0; i < inneis1.size(); ++i) {
							int node = inneis1.get(i);
							if (in1.get(node) == depth) {
								in1.set(node, 0);
								in1size--;
							}
						}
						outneis1 = outadj1.get(last1);
						for (i = 0; i < outneis1.size(); ++i) {
							int node = outneis1.get(i);
							if (out1.get(node) == depth) {
								out1.set(node, 0);
								out1size--;
							}
						}
						inneis2 = inadj2.get(last2);
						for (i = 0; i < inneis2.size(); ++i) {
							int node = inneis2.get(i);
							if (in2.get(node) == depth) {
								in2.set(node, 0);
								in2size--;
							}
						}
						outneis2 = outadj2.get(last2);
						for (i = 0; i < outneis2.size(); ++i) {
							int node = outneis2.get(i);
							if (out2.get(node) == depth) {
								out2.set(node, 0);
								out2size--;
							}
						}
					}
					
					depth--;
				}
				else {
					int xin1 = 0, xin2 = 0, xout1 = 0, xout2 = 0;
					boolean end = false;
					inneis1 = inadj1.get(cand1);
					outneis1 = outadj1.get(cand1);
					inneis2 = inadj2.get(cand2);
					outneis2 = outadj2.get(cand2);
					if (indeg1.get(cand1) != indeg2.get(cand2) ||
							outdeg1.get(cand1) != outdeg2.get(cand2))
						end = true;

					for (i = 0; !end && i < inneis1.size(); ++i) {
						int node = inneis1.get(i);
						if (core1.get(node) != 0) {
							int node2 = core1.get(node) - 1;
							if (!inneis2.contains(node2)) // ???
								end = true;
						}
						else {
							if (in1.get(node) != 0)
								xin1++;
							if (out1.get(node) != 0)
								xout1++;
						}
					}
					for (i = 0; !end && i < outneis1.size(); ++i) {
						int node = outneis1.get(i);
						if (core1.get(node) != 0) {
							int node2 = core1.get(node) - 1;
							if (!outneis2.contains(node2)) // ???
								end = true;
						}
						else {
							if (in1.get(node) != 0)
								xin1++;
							if (out1.get(node) != 0)
								xout1++;
						}
					}
					for (i = 0; !end && i < inneis2.size(); ++i) {
						int node = inneis2.get(i);
						if (core2.get(node) != 0) {
							int node2 = core2.get(node) - 1;
							if (!inneis1.contains(node2)) // ???
								end = true;
						}
						else {
							if (in2.get(node) != 0)
								xin2++;
							if (out2.get(node) != 0)
								xout2++;
						}
					}
					for (i = 0; !end && i < outneis2.size(); ++i) {
						int node = outneis2.get(i);
						if (core2.get(node) != 0) {
							int node2 = core2.get(node) - 1;
							if (!outneis1.contains(node2)) // ???
								end = true;
						}
						else {
							if (in2.get(node) != 0)
								xin2++;
							if (out2.get(node) != 0)
								xout2++;
						}
					}

					if (!end && xin1 == xin2 && xout1 == xout2) {
						depth++;
						path.push(cand1);
						path.push(cand2);
						matchedNodes++;
						core1.set(cand1, cand2 + 1);
						core2.set(cand2, cand1 + 1);

						if (in1.get(cand1) != 0)
							in1size--;
						if (out1.get(cand1) != 0)
							out1size--;
						if (in2.get(cand2) != 0)
							in2size--;
						if (out2.get(cand2) != 0)
							out2size--;

						inneis1 = inadj1.get(cand1);
						for (i = 0; i < inneis1.size(); ++i) {
							int node = inneis1.get(i);
							if (in1.get(node) == 0 && core1.get(node) == 0) {
								in1.set(node, depth);
								in1size++;
							}
						}
						outneis1 = outadj1.get(cand1);
						for (i = 0; i < outneis1.size(); ++i) {
							int node = outneis1.get(i);
							if (out1.get(node) == 0 && core1.get(node) == 0) {
								out1.set(node, depth);
								out1size++;
							}
						}
						inneis2 = inadj2.get(cand2);
						for (i = 0; i < inneis2.size(); ++i) {
							int node = inneis2.get(i);
							if (in2.get(node) == 0 && core2.get(node) == 0) {
								in2.set(node, depth);
								in2size++;
							}
						}
						outneis2 = outadj2.get(cand2);
						for (i = 0; i < outneis2.size(); ++i) {
							int node = outneis2.get(i);
							if (out2.get(node) == 0 && core2.get(node) == 0) {
								out2.set(node, depth);
								out2size++;
							}
						}
						last1 = -1; last2 = -1;
					}
					else {
						last1 = cand1;
						last2 = cand2;
					}
				}

				if (matchedNodes == noOfNodes)
					return true;
			}

			return false;
		}

		private void initAdjList(Graph graph, List<List<Integer>> adjList, boolean out) {
			Map<Node, Integer> map = new HashMap<Node, Integer>();
			Node[] nodes = graph.getNodes().toArray();
			for (int i = 0; i < nodes.length; ++i)
				map.put(nodes[i], i);
			for (int i = 0; i < nodes.length; ++i) {
				List<Integer> neighbours = new ArrayList<Integer>();
				if (!directed)
					for (Edge edge : ((UndirectedGraph)graph).getEdges(nodes[i])) {
						if (!edge.isSelfLoop())
							neighbours.add(map.get(graph.getOpposite(nodes[i], edge)));
					}
				else if (out)
					for (Edge edge : ((DirectedGraph)graph).getOutEdges(nodes[i])) {
						if (!edge.isSelfLoop())
							neighbours.add(map.get(graph.getOpposite(nodes[i], edge)));
					}
				else for (Edge edge : ((DirectedGraph)graph).getInEdges(nodes[i]))
						if (!edge.isSelfLoop())
							neighbours.add(map.get(graph.getOpposite(nodes[i], edge)));
				adjList.add(neighbours);
			}
		}

		private void degree(Graph graph, List<Integer> deg, boolean out) {
			Node[] nodes = graph.getNodes().toArray();
			for (int i = 0; i < nodes.length; ++i)
				if (!directed)
					deg.add(((UndirectedGraph)graph).getDegree(nodes[i]));
				else if (out)
					deg.add(((DirectedGraph)graph).getOutDegree(nodes[i]));
				else deg.add(((DirectedGraph)graph).getInDegree(nodes[i]));
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
				isomorphic[i] = new Matcher(sourceGraph, targetGraphs[i]).isIsomorphic();
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
			data[0][i] = isomorphic[i] ? 1.0 : 0.1;
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				new String[] { "Isomorphism" }, names, data);

		JFreeChart chart = ChartFactory.createBarChart(
				"Isomorphism Chart",
				"Graphs",
				"Is isomorphic",
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

		ValueAxis rangeAxis = new SymbolAxis("Is isomorphic", new String[] { "False", "True" });
		plot.setRangeAxis(rangeAxis);

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
