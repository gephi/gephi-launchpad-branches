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

import org.gephi.layout.plugin.circularlayout.nodecomparator.BasicNodeComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.Iterator;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;

/**
 *
 * @author Matt
 */
public class RadialAxisLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private Enum enumNodeplacement;
    private Enum enumNodePlacementDirection;
    private Enum enumKnockdown;
    private Enum enumSparNodePlacement;
    private Boolean boolKnockdownSpars = false;
    private Boolean boolSparOrderingDirection = false;
    private Boolean boolSparSpiral = true;
    private Integer intSparCount = 3;
    static final double TWO_PI = (2 * Math.PI);

    public RadialAxisLayout(LayoutBuilder layoutBuilder, double diameter, boolean boolfixeddiameter) {
        super(layoutBuilder);
    }


    private enum PlacementEnum {

        Degree,
        Indegree,
        Outdegree,
        Mutual,
        Children,
        Descendents;
    }

    public static Map getPlacementEnumMap() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getModel();
        Map<PlacementEnum, String> map = new EnumMap<PlacementEnum, String>(PlacementEnum.class);
        map.put(PlacementEnum.Degree, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Degree.name"));
        if (objGraphModel.isDirected()) {
            map.put(PlacementEnum.Indegree, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.InDegree.name"));
            map.put(PlacementEnum.Outdegree, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.OutDegree.name"));
            map.put(PlacementEnum.Mutual, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Mutual.name"));
        } else if (objGraphModel.isHierarchical()) {
            map.put(PlacementEnum.Children, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Children.name"));
            map.put(PlacementEnum.Descendents, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Descendents.name"));
        }
        return map;
    }

    private enum RotationEnum {

        CCW,
        CW;
    }

    public static Map getRotationEnumMap() {
        Map<RotationEnum, String> map = new EnumMap<RotationEnum, String>(RotationEnum.class);
        map.put(RotationEnum.CCW, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.CCW"));
        map.put(RotationEnum.CW, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.CW"));
        return map;
    }

    private enum KnockDownRangeEnum {

        TOP,
        MIDDLE,
        BOTTOM;
    }

    public static Map getKnockDownRangeEnumMap() {
        Map<KnockDownRangeEnum, String> map = new EnumMap<KnockDownRangeEnum, String>(KnockDownRangeEnum.class);
        map.put(KnockDownRangeEnum.TOP, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.TOP"));
        map.put(KnockDownRangeEnum.MIDDLE, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.MIDDLE"));
        map.put(KnockDownRangeEnum.BOTTOM, NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockDownRange.BOTTOM"));
        return map;
    }

    @Override
    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
    }

    @Override
    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        float[] nodeCoords = new float[2];
        ArrayList<Integer> ArrayLayers = new ArrayList<Integer>();
        List<Node> NodeList = new ArrayList<Node>();
        double maxlength = 0;
        double minlength = 0;
        double tmpradius = 0;
        double doArrayEnd = 0;
        double tmpcirc = 0;
        double theta;

        Node[] nodes = graph.getNodes().toArray();
        double nodecount = nodes.length;
        if (this.enumNodeplacement == PlacementEnum.Degree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "Degree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Indegree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "InDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Outdegree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "OutDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Mutual) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "MutualDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Children) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "Children", true));
        } else if (this.enumNodeplacement == PlacementEnum.Descendents) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, "Descendent", true));
        }
        int i = 0;
        int lastlayer = 0;
        int currentlayer = 0;

        for (Node n : nodes) {
            currentlayer = getLayerAttribute(n, this.enumNodeplacement);
            if (i == 0) {
                lastlayer = currentlayer;
                ArrayLayers.add(Integer.valueOf(i));
            } else if (i == (nodes.length - 1)) {
                ArrayLayers.add(Integer.valueOf(i));
                ArrayLayers.add(Integer.valueOf(i + 1));
            } else {
                if (lastlayer != currentlayer) {
                    lastlayer = currentlayer;
                    ArrayLayers.add(Integer.valueOf(i));
                }
            }
            double tmplength = n.getNodeData().getRadius() * 2;
            if (tmplength > maxlength) {
                maxlength = tmplength;
            }
            if (tmplength < minlength || minlength == 0) {
                maxlength = tmplength;
            }
            NodeList.add(n);

            i++;
        }
        doArrayEnd = ArrayLayers.size() - 1;
        tmpcirc = (doArrayEnd * maxlength);
        tmpradius = tmpcirc / TWO_PI;

        theta = (TWO_PI / doArrayEnd);

        if (this.boolKnockdownSpars && (doArrayEnd / this.getSparCount() > 1)) {
            theta = (TWO_PI / this.getSparCount());
            tmpcirc = (this.getSparCount() * maxlength * 1.2);
            tmpradius = tmpcirc / TWO_PI;
            double doHigh = 0;
            double doLow = 0;
            double doDiff = doArrayEnd - this.getSparCount();
            if (this.enumKnockdown == KnockDownRangeEnum.TOP) {
                doLow = this.getSparCount();
                doHigh = doArrayEnd;
            } else if (this.enumKnockdown == KnockDownRangeEnum.BOTTOM) {
                doLow = 1;
                doHigh = doDiff + 1;
            } else {
                double doRemain = this.getSparCount() / 2;
                double doMod = this.getSparCount() % 2;
                doLow = doRemain + 1;
                doHigh = 0;
                if (doMod == 0) {
                    doHigh = doArrayEnd - (doRemain - 1);
                } else {
                    doHigh = doArrayEnd - doRemain;
                }
            }
            ArrayLayers.subList((int) doLow, (int) doHigh).clear();
        }

        if (this.enumNodePlacementDirection == RotationEnum.CW) {
            theta = -theta;
        }


        Integer previousindex = 0;
        Integer currentindex = 0;
        i = 0;
        Iterator it = ArrayLayers.iterator();
        while (it.hasNext()) {
            currentindex = (Integer) it.next();
            if (currentindex > previousindex) {
                Node[] shortnodes = NodeList.subList(previousindex, currentindex).toArray(new Node[0]);
                
                if ((this.enumSparNodePlacement != this.enumNodeplacement) || (!this.boolSparOrderingDirection)) {
                    if (this.enumSparNodePlacement == PlacementEnum.Degree) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "Degree", this.boolSparOrderingDirection));
                    } else if (this.enumSparNodePlacement == PlacementEnum.Indegree) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "InDegree", this.boolSparOrderingDirection));
                    } else if (this.enumSparNodePlacement == PlacementEnum.Outdegree) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "OutDegree", this.boolSparOrderingDirection));
                    } else if (this.enumSparNodePlacement == PlacementEnum.Mutual) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "MutualDegree", this.boolSparOrderingDirection));
                    } else if (this.enumSparNodePlacement == PlacementEnum.Children) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "Children", this.boolSparOrderingDirection));
                    } else if (this.enumSparNodePlacement == PlacementEnum.Descendents) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, nodes, "Descendent", this.boolSparOrderingDirection));
                    }
                }
                double tmptotallength = tmpradius;
                double thetainc = 0;
                for (Node n : shortnodes) {
                    double tmplength = n.getNodeData().getRadius(); 
                    if (this.boolSparSpiral) {
                        tmptotallength += tmplength*0.6;
                        System.out.println("thetatInc " + (thetainc));
                        System.out.println("ArrayEnd " + (nodecount));
                        System.out.println("ratio " + (thetainc/nodecount));
                        nodeCoords = this.cartCoors(tmptotallength, i+(thetainc/nodecount), theta);
                        tmptotallength += tmplength*0.6;   
                    } else {
                        tmptotallength += tmplength* 1.2;                        
                        nodeCoords = this.cartCoors(tmptotallength, i, theta);
                    }
                    tmptotallength += tmplength* 1.2;
                    n.getNodeData().setX(nodeCoords[0]);
                    n.getNodeData().setY(nodeCoords[1]);
                    thetainc++;
                }
            }
            previousindex = currentindex;
            i++;
        }

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
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Enum.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.NodeOrdering.name"),
                    "Node Placement",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.NodeOrdering.desc"),
                    "getNodePlacement", "setNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Enum.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Direction.name"),
                    "Node Placement",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection", RotationComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Enum.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.NodeOrdering.name"),
                    "Node Placement",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.NodeOrdering.desc"),
                    "getSparNodePlacement", "setSparNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.SparOrderingDirection.name"),
                    "Node Placement",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.SparOrderingDirection.desc"),
                    "isSparOrderingDirection", "setSparOrderingDirection"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.Spiral.name"),
                    "Node Placement",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.Spars.Spiral.desc"),
                    "isSparSpiral", "setSparSpiral"));               
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockdownSpars.name"),
                    "Axis/Spar Control",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockdownSpars.desc"),
                    "isKnockdownSpars", "setKnockdownSpars"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.SparCount.name"),
                    "Axis/Spar Control",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.SparCount.desc"),
                    "getSparCount", "setSparCount"));
            properties.add(LayoutProperty.createProperty(
                    this, Enum.class,
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockdownSpars.Range.name"),
                    "Axis/Spar Control",
                    NbBundle.getMessage(RadialAxisLayout.class, "RadialAxisLayout.KnockdownSpars.Range.desc"),
                    "getKnockDownRange", "setKnockDownRange", KnockDownSparRange.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        setNodePlacement(PlacementEnum.Degree);
        setNodePlacementDirection(RotationEnum.CCW);
        setSparSpiral(false);
        setKnockdownSpars(false);
        setSparOrderingDirection(false);
        setKnockDownRange(KnockDownRangeEnum.MIDDLE);
        setSparCount(3);
        setSparNodePlacement(PlacementEnum.Degree);
    }

    public void setNodePlacement(Enum enumNodeplacement) {
        this.enumNodeplacement = enumNodeplacement;
    }

    public Enum getNodePlacement() {
        return this.enumNodeplacement;
    }

    public Enum getNodePlacementDirection() {
        return this.enumNodePlacementDirection;
    }

    public void setNodePlacementDirection(Enum enumNodePlacementDirection) {
        this.enumNodePlacementDirection = enumNodePlacementDirection;
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
        
    public void setKnockDownRange(Enum enumKnockdown) {
        this.enumKnockdown = enumKnockdown;
    }

    public Enum getKnockDownRange() {
        return this.enumKnockdown;
    }

    public Integer getSparCount() {
        return this.intSparCount;
    }

    public void setSparCount(Integer intSparCount) {
        this.intSparCount = intSparCount;
    }

    public void setSparNodePlacement(Enum enumSparNodePlacement) {
        this.enumSparNodePlacement = enumSparNodePlacement;
    }

    public Enum getSparNodePlacement() {
        return this.enumSparNodePlacement;
    }
    
    public void setSparSpiral(Boolean boolSparSpiral) {
        this.boolSparSpiral = boolSparSpiral;
    }

    public Boolean isSparSpiral() {
        return this.boolSparSpiral;
    }
    
    public int getLayerAttribute(Node n, Enum Placement) {
        int layout = 0;
        if (Placement == PlacementEnum.Degree) {
            layout = graph.getDegree(n);
        } else if (Placement == PlacementEnum.Indegree) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getInDegree(n);
        } else if (Placement == PlacementEnum.Outdegree) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getOutDegree(n);
        } else if (Placement == PlacementEnum.Mutual) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getMutualDegree(n);
        } else if (Placement == PlacementEnum.Children) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            HierarchicalGraph objGraph = objGraphModel.getHierarchicalGraph();
            layout = objGraph.getChildrenCount(n);
        } else if (Placement == PlacementEnum.Descendents) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            HierarchicalGraph objGraph = objGraphModel.getHierarchicalGraph();
            layout = objGraph.getDescendantCount(n);
        }
        return layout;
    }

    private float[] cartCoors(double radius, double whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt) + (Math.PI / 2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt) + (Math.PI / 2))));
        return coOrds;
    }
}
