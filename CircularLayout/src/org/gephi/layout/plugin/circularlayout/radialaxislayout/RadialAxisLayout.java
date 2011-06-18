/*
Copyright (c) 2010, Matt Groeninger
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list
of conditions and the following disclaimer in the documentation and/or other materials
provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.gephi.layout.plugin.circularlayout.radialaxislayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.spi.LayoutData;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.layout.plugin.circularlayout.nodecomparator.NodeComparator;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;

/**
 *
 * @author Matt
 */
public class RadialAxisLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private String strNodeplacement;
    private String strNodePlacementDirection;
    private String strKnockdown;
    private String strSparNodePlacement;
    private Boolean boolKnockdownSpars;
    private Boolean boolSparOrderingDirection;
    private Boolean boolSparSpiral;
    private Integer intSparCount;
    static final double TWO_PI = (2 * Math.PI);
    private Boolean boolResizeNode;
    private Integer intNodeSize;
    private Double dScalingWidth;

    public RadialAxisLayout(LayoutBuilder layoutBuilder, double diameter, boolean boolfixeddiameter) {
        super(layoutBuilder);
    }

    public static Map getPlacementMap() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getModel();
        Map<String, String> map = new TreeMap<String, String>();
        map.put("Degree", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Degree.name"));
        if (objGraphModel.isDirected()) {
            map.put("InDegree", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.InDegree.name"));
            map.put("OutDegree", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.OutDegree.name"));
            map.put("MutualDegree", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Mutual.name"));
        } else if (objGraphModel.isHierarchical()) {
            map.put("ChildrenCount", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Children.name"));
            map.put("DescendantCount", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Descendents.name"));
        }
        AttributeModel attModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        for (AttributeColumn c : attModel.getNodeTable().getColumns()) {
            map.put(c.getTitle() + "-Att", c.getTitle() + " (Attribute)");
        }
        return map;
    }

    public static Map getRotationMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("CCW", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.CCW"));
        map.put("CW", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.CW"));
        return map;
    }

    public static Map getKnockDownRangeMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("TOP", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.TOP"));
        map.put("MIDDLE", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.MIDDLE"));
        map.put("BOTTOM", NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.BOTTOM"));
        return map;
    }

    @Override
    public void initAlgo() {
        converged = false;
        this.graph = graphModel.getGraphVisible();
    }

    @Override
    public void goAlgo() {
        graph.readLock();
        float[] nodeCoords = new float[2];
        ArrayList<Integer> ArrayLayers = new ArrayList<Integer>();
        double radius = 0;
        double SparArrayCount = 0;
        double tmpcirc = 0;
        double theta;

        Node[] nodes = graph.getNodes().toArray();
        double nodecount = nodes.length;

        if (this.strNodeplacement.equals("NodeID")) {
            Arrays.sort(nodes, new NodeComparator(graph, nodes, NodeComparator.CompareType.NODEID, null, true));
        } else if (this.strNodeplacement.endsWith("-Att")) {
            Arrays.sort(nodes, new NodeComparator(graph, nodes, NodeComparator.CompareType.ATTRIBUTE, this.strNodeplacement.substring(0, this.strNodeplacement.length() - 4), true));
        } else if (getPlacementMap().containsKey(this.strNodeplacement)) {
            Arrays.sort(nodes, new NodeComparator(graph, nodes, NodeComparator.CompareType.METHOD, this.strNodeplacement, true));
        }

        int i = 0;
        Object lastlayer = null;
        Object currentlayer = null;

        for (Node n : nodes) {
            if (!n.getNodeData().isFixed()) {
                if (this.boolResizeNode) {
                    n.getNodeData().setSize(this.intNodeSize);
                }
                currentlayer = getLayerAttribute(n, this.strNodeplacement);
                if (i == 0) {
                    lastlayer = currentlayer;
                    ArrayLayers.add(Integer.valueOf(i));
                }
                if (i == nodecount - 1) {
                    ArrayLayers.add(Integer.valueOf(i));
                }
                if (lastlayer != currentlayer) {
                    lastlayer = currentlayer;
                    ArrayLayers.add(Integer.valueOf(i));
                }
                i++;
            }
        }
        List<Node> NodeList = new ArrayList<Node>(Arrays.asList(nodes));

        nodes = null;

        SparArrayCount = ArrayLayers.size();
        if (this.boolKnockdownSpars && (SparArrayCount - this.getSparCount() > 1)) {
            double doHigh = 0;
            double doLow = 0;
            double doDiff = SparArrayCount - this.getSparCount();
            if ("TOP".equals(this.strKnockdown)) {
                doLow = this.getSparCount();
                doHigh = SparArrayCount - 1;
            } else if ("BOTTOM".equals(this.strKnockdown)) {
                doLow = 1;
                doHigh = doDiff;
            } else {
                double doRemain = this.getSparCount() / 2;
                double doMod = this.getSparCount() % 2;
                doLow = doRemain + 1;
                doHigh = 0;
                if (doMod == 0) {
                    doHigh = SparArrayCount - doRemain;
                } else {
                    doHigh = SparArrayCount - doRemain - 1;
                }
            }
            ArrayLayers.subList((int) doLow, (int) doHigh).clear();
            SparArrayCount = this.getSparCount();
        }

        double circ = 0;
        Integer previousindex = 0;
        Integer currentindex = 0;
        List<Node> SparBaseList = new ArrayList<Node>();
        double[] SparNodeCount = new double[(int) SparArrayCount];

        int group = 0;
        Iterator it = ArrayLayers.iterator();
        GroupLayoutData posData;
        while (it.hasNext()) {
            currentindex = (Integer) it.next();
            if (!it.hasNext()) {
                currentindex++;
            }
            if (currentindex > previousindex) {
                Node[] shortnodes = NodeList.subList(previousindex, currentindex).toArray(new Node[0]);
                if (this.strSparNodePlacement.equals("NodeID")) {
                    Arrays.sort(shortnodes, new NodeComparator(graph, shortnodes, NodeComparator.CompareType.NODEID, null, this.boolSparOrderingDirection));
                } else if (this.strSparNodePlacement.endsWith("-Att")) {
                    Arrays.sort(shortnodes, new NodeComparator(graph, shortnodes, NodeComparator.CompareType.ATTRIBUTE, this.strSparNodePlacement.substring(0, this.strSparNodePlacement.length() - 4), this.boolSparOrderingDirection));
                } else if (getPlacementMap().containsKey(this.strSparNodePlacement)) {
                    Arrays.sort(shortnodes, new NodeComparator(graph, shortnodes, NodeComparator.CompareType.METHOD, this.strSparNodePlacement, this.boolSparOrderingDirection));
                }

                NodeList.removeAll(Arrays.asList(shortnodes));
                NodeList.addAll(previousindex, Arrays.asList(shortnodes));
                SparNodeCount[group] = shortnodes.length;
                int order = 0;
                for (Node n : shortnodes) {
                    if (!n.getNodeData().isFixed()) {
                        if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof GroupLayoutData)) {
                            posData = new GroupLayoutData();
                            posData.group = group;
                            posData.order = order;
                            n.getNodeData().setLayoutData(posData);
                        }
                        if (order == 0) {
                            double noderadius = n.getNodeData().getRadius();
                            circ += noderadius * 2;
                            SparBaseList.add(n);
                        }
                        order++;
                    }
                }
                group++;
            }
            previousindex = currentindex;
        }

        tmpcirc = (circ * this.dScalingWidth);
        radius = tmpcirc / TWO_PI;
        theta = (TWO_PI / tmpcirc);
        double thetainc = TWO_PI / group;

        if ("CW".equals(this.strNodePlacementDirection)) {
            theta = -theta;
        }

        GroupLayoutData position = null;
        double tmpspartheta;
        double lasttheta = 0;
        group = 0;
        double[] ArraySparLength = new double[(int) SparArrayCount];
        double[] ArraySparTheta = new double[(int) SparArrayCount];
        for (Node n : SparBaseList) {
            double noderadius = (n.getNodeData().getRadius());
            double noderadian = (theta * noderadius * this.dScalingWidth);
            ArraySparTheta[group] = lasttheta + noderadian;
            ArraySparLength[group] = noderadius * this.dScalingWidth;
            nodeCoords = this.cartCoors(radius, 1, lasttheta + noderadian);
            n.getNodeData().setX(nodeCoords[0]);
            n.getNodeData().setY(nodeCoords[1]);
            lasttheta += (noderadian * 2);
            group++;
        }

        double tmpsparlength;
        for (Node n : NodeList) {
            if (!n.getNodeData().isFixed()) {
                if (n.getNodeData().getLayoutData() != null) {
                    position = n.getNodeData().getLayoutData();
                }
                if (position.order != 0) {
                    tmpsparlength = ArraySparLength[position.group];
                    tmpspartheta = ArraySparTheta[position.group];

                    double noderadius = (n.getNodeData().getRadius());
                    if (this.boolSparSpiral) {
                        nodeCoords = this.cartCoors(tmpsparlength + radius + (noderadius * this.dScalingWidth), 1, tmpspartheta + (position.order * (thetainc / SparNodeCount[position.group])));
                    } else {
                        nodeCoords = this.cartCoors(tmpsparlength + radius + (noderadius * this.dScalingWidth), 1, tmpspartheta);
                    }
                    n.getNodeData().setX(nodeCoords[0]);
                    n.getNodeData().setY(nodeCoords[1]);
                    ArraySparLength[position.group] = tmpsparlength + noderadius * this.dScalingWidth * 2;
                }
            }
            n.getNodeData().setLayoutData(null);
        }
        graph.readUnlock();
        converged = true;
    }

    @Override
    public boolean canAlgo() {
        return !converged;
    }

    @Override
    public void endAlgo() {
    }

    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String PLACEMENT_CATEGORY = NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name");
        final String SPARCONTROL_CATEGORY = NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.SparControl.name");
        final String LAYOUTTUNING_CATEGORY = NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.LayoutTuning.name");

        try {
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.NodeOrdering.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.NodeOrdering.desc"),
                    "getNodePlacement", "setNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.Direction.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection", RotationComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.NodeOrdering.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.NodeOrdering.desc"),
                    "getSparNodePlacement", "setSparNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.SparOrderingDirection.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.SparOrderingDirection.desc"),
                    "isSparOrderingDirection", "setSparOrderingDirection"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.Spiral.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.Spiral.desc"),
                    "isSparSpiral", "setSparSpiral"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.name"),
                    SPARCONTROL_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.desc"),
                    "isKnockdownSpars", "setKnockdownSpars"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.SparCount.name"),
                    SPARCONTROL_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.SparCount.desc"),
                    "getSparCount", "setSparCount"));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.Range.name"),
                    SPARCONTROL_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.Range.desc"),
                    "getKnockDownRange", "setKnockDownRange", KnockDownSparRange.class));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.ScalingWidth.name"),
                    LAYOUTTUNING_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.ScalingWidth.desc"),
                    "getScalingWidth", "setScalingWidth"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.ResizeNode.name"),
                    LAYOUTTUNING_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.ResizeNode.desc"),
                    "isResizeNode", "setResizeNode"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.NodeSize.name"),
                    LAYOUTTUNING_CATEGORY,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.LayoutTuning.NodeSize.desc"),
                    "getNodeSize", "setNodeSize"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        setNodePlacement("Degree");
        setNodePlacementDirection("CCW");
        setSparSpiral(false);
        setKnockdownSpars(false);
        setSparOrderingDirection(false);
        setKnockDownRange("MIDDLE");
        setSparCount(3);
        setSparNodePlacement("Degree");
        setResizeNode(false);
        setNodeSize(5);
        setScalingWidth(1.2);
    }

    public void setNodePlacement(String strNodeplacement) {
        this.strNodeplacement = strNodeplacement;
    }

    public String getNodePlacement() {
        return this.strNodeplacement;
    }

    public String getNodePlacementDirection() {
        return this.strNodePlacementDirection;
    }

    public void setNodePlacementDirection(String strNodePlacementDirection) {
        this.strNodePlacementDirection = strNodePlacementDirection;
    }

    public Boolean isKnockdownSpars() {
        return this.boolKnockdownSpars;
    }

    public void setKnockdownSpars(Boolean boolKnockdownSpars) {
        this.boolKnockdownSpars = boolKnockdownSpars;
    }

    public Boolean isSparOrderingDirection() {
        return this.boolSparOrderingDirection;
    }

    public void setSparOrderingDirection(Boolean boolSparOrderingDirection) {
        this.boolSparOrderingDirection = boolSparOrderingDirection;

    }

    public void setKnockDownRange(String strKnockdown) {
        this.strKnockdown = strKnockdown;
    }

    public String getKnockDownRange() {
        return this.strKnockdown;
    }

    public Integer getSparCount() {
        return this.intSparCount;
    }

    public void setSparCount(Integer intSparCount) {
        this.intSparCount = intSparCount;
    }

    public void setSparNodePlacement(String strSparNodePlacement) {
        this.strSparNodePlacement = strSparNodePlacement;
    }

    public String getSparNodePlacement() {
        return this.strSparNodePlacement;
    }

    public void setSparSpiral(Boolean boolSparSpiral) {
        this.boolSparSpiral = boolSparSpiral;
    }

    public Boolean isSparSpiral() {
        return this.boolSparSpiral;
    }

    public Boolean isResizeNode() {
        return this.boolResizeNode;
    }

    public void setResizeNode(Boolean boolResizeNode) {
        this.boolResizeNode = boolResizeNode;
    }

    public Integer getNodeSize() {
        return this.intNodeSize;
    }

    public void setNodeSize(Integer intNodeSize) {
        this.intNodeSize = intNodeSize;
    }

    public Double getScalingWidth() {
        return this.dScalingWidth;
    }

    public void setScalingWidth(Double dScalingWidth) {
        this.dScalingWidth = dScalingWidth;
    }

    public Object getLayerAttribute(Node n, String Placement) {
        Object layout = null;
        if (Placement.equals("NodeID")) {
            layout = n.getNodeData().getId();
        } else if (Placement.equals("Degree")) {
            layout = graph.getDegree(n);
        } else if (Placement.equals("InDegree")) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getInDegree(n);
        } else if (Placement.equals("OutDegree")) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getOutDegree(n);
        } else if (Placement.equals("MutualDegree")) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getMutualDegree(n);
        } else if (Placement.equals("ChildrenCount")) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            HierarchicalGraph objGraph = objGraphModel.getHierarchicalGraph();
            layout = objGraph.getChildrenCount(n);
        } else if (Placement.equals("DescendantCount")) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            HierarchicalGraph objGraph = objGraphModel.getHierarchicalGraph();
            layout = objGraph.getDescendantCount(n);
        } else {
            Placement = Placement.substring(0, Placement.length() - 4);
            layout = n.getNodeData().getAttributes().getValue(Placement);
        }
        return layout;
    }

    private float[] cartCoors(double radius, double whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt) + (Math.PI / 2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt) + (Math.PI / 2))));
        return coOrds;
    }

    public class GroupLayoutData implements LayoutData {

        public int group = 0;
        public int order = 0;
    }
}
