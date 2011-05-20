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
import org.gephi.layout.plugin.circularlayout.nodedatacomparator.BasicNodeDataComparator;
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
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
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
           map.put(c.getTitle()+"-Att", c.getTitle()+" (Attribute)");
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

       if (this.strNodeplacement.equals("NodeID")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, "NodeID", true));
        } else if (this.strNodeplacement.endsWith("-Att")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, this.strNodeplacement.substring(0,this.strNodeplacement.length()-4), true));
        } else if (getPlacementMap().containsKey(this.strNodeplacement)) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, this.strNodeplacement, true));
        }        
        
        int i = 0;
        Object lastlayer = null;
        Object currentlayer = null;

        for (Node n : nodes) {
            currentlayer = getLayerAttribute(n, this.strNodeplacement);

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
            if ("TOP".equals(this.strKnockdown)) {
                doLow = this.getSparCount();
                doHigh = doArrayEnd;
            } else if ("BOTTOM".equals(this.strKnockdown)) {
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

        if ("CW".equals(this.strNodePlacementDirection)) {
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
                if ((!this.strNodeplacement.equals(this.strSparNodePlacement)) || (!this.boolSparOrderingDirection)) {
                     if (this.strSparNodePlacement.equals("NodeID")) {
                        Arrays.sort(shortnodes, new BasicNodeDataComparator(shortnodes, "NodeID", this.boolSparOrderingDirection));
                    } else if (this.strSparNodePlacement.endsWith("-Att")) {
                        Arrays.sort(shortnodes, new BasicNodeDataComparator(shortnodes, this.strSparNodePlacement.substring(0, this.strSparNodePlacement.length() - 4), this.boolSparOrderingDirection));
                    } else if (getPlacementMap().containsKey(this.strSparNodePlacement)) {
                        Arrays.sort(shortnodes, new BasicNodeComparator(graph, shortnodes, this.strSparNodePlacement, this.boolSparOrderingDirection));
                    } 
                }
                double tmptotallength = tmpradius;
                double thetainc = 0;
                for (Node n : shortnodes) {
                    double tmplength = n.getNodeData().getRadius();
                    if (thetainc!=0) {
                        tmptotallength += tmplength*1.2;
                    }
                    if (this.boolSparSpiral) {
                        nodeCoords = this.cartCoors(tmptotallength, i+(thetainc/nodecount), theta);
                    } else {
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
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.NodeOrdering.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.NodeOrdering.desc"),
                    "getNodePlacement", "setNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.Direction.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection", RotationComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.NodeOrdering.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.NodeOrdering.desc"),
                    "getSparNodePlacement", "setSparNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.SparOrderingDirection.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.SparOrderingDirection.desc"),
                    "isSparOrderingDirection", "setSparOrderingDirection"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.Spiral.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.Placement.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Spars.Spiral.desc"),
                    "isSparSpiral", "setSparSpiral"));               
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.SparControl.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.desc"),
                    "isKnockdownSpars", "setKnockdownSpars"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.SparCount.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.SparControl.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.SparCount.desc"),
                    "getSparCount", "setSparCount"));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.Range.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.Category.SparControl.name"),
                    NbBundle.getMessage(getClass(), "RadialAxisLayout.KnockdownSpars.Range.desc"),
                    "getKnockDownRange", "setKnockDownRange", KnockDownSparRange.class));
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
}
