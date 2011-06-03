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
package org.gephi.layout.plugin.circularlayout.circlelayout;


import org.gephi.layout.plugin.circularlayout.nodecomparator.BasicNodeComparator;
import org.gephi.layout.plugin.circularlayout.nodedatacomparator.BasicNodeDataComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
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
public class CircleLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private double diameter;
    private boolean boolfixeddiameter;
    private String strNodeplacement;
    private boolean boolNoOverlap = true;
    private String strNodePlacementDirection;
    static final double TWO_PI = (2 * Math.PI);

    public CircleLayout(LayoutBuilder layoutBuilder, double diameter, boolean boolfixeddiameter) {
        super(layoutBuilder);
        this.diameter = diameter;
        this.boolfixeddiameter = boolfixeddiameter;
    }

    private enum PlacementEnum {
        NodeID,
        Random,
        Degree,
        Indegree,
        Outdegree,
        Mutual,
        Children,
        Descendents;
    }

      public static Map getPlacementMap() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getModel();
        Map<String, String> map = new TreeMap<String, String>();
        map.put("NodeID", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NodeID.name"));
        map.put("Degree", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Degree.name"));
        if (objGraphModel.isDirected()) {
            map.put("InDegree", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.InDegree.name"));
            map.put("OutDegree", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.OutDegree.name"));
            map.put("MutualDegree", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Mutual.name"));
        } else if (objGraphModel.isHierarchical()) {
            map.put("ChildrenCount", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Children.name"));
            map.put("DescendantCount", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Descendents.name"));
        }
        AttributeModel attModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        for (AttributeColumn c : attModel.getNodeTable().getColumns()) {
           map.put(c.getTitle()+"-Att", c.getTitle()+" (Attribute)");
        }
        return map;
    }

    public static Map getRotationMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("CCW", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.CCW"));
        map.put("CW", NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.CW"));
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
        double tmpcirc = 0;
        double tmpdiameter = 0;
        int index = 0;
        int nodecount = graph.getNodeCount();
        double noderadius = 0;
        double theta = TWO_PI / nodecount;
        double lasttheta = 0;

        if (!this.boolfixeddiameter) {
            Node[] nodes = graph.getNodes().toArray();
            for (Node n : nodes) {
                tmpcirc += (n.getNodeData().getRadius() * 2);
            }
            tmpcirc = (tmpcirc * 1.2);
            tmpdiameter = tmpcirc / Math.PI;
            if (this.boolNoOverlap) {
                theta = (TWO_PI / tmpcirc);
            }
        } else {
            tmpdiameter = this.diameter;
        }
        double radius = tmpdiameter / 2;

        //determine Node placement
        Node[] nodes = graph.getNodes().toArray();
        
        if (this.strNodeplacement.equals("NodeID")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, "NodeID", false));
        } else if (this.strNodeplacement.endsWith("-Att")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, this.strNodeplacement.substring(0,this.strNodeplacement.length()-4), false));
        } else if (getPlacementMap().containsKey(this.strNodeplacement)) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, this.strNodeplacement, false));
        }


        if ("CW".equals(this.strNodePlacementDirection)) {
            theta = -theta;
        }
        
        for (Node n : nodes) {
            if (this.boolNoOverlap) {
                noderadius = (n.getNodeData().getRadius());
                double noderadian = (theta * noderadius * 1.2);
                nodeCoords = this.cartCoors(radius, 1, lasttheta + noderadian);
                lasttheta += (noderadius * 2 * theta * 1.2);
            } else {
                nodeCoords = this.cartCoors(radius, index, theta);
            }
            n.getNodeData().setX(nodeCoords[0]);
            n.getNodeData().setY(nodeCoords[1]);
            index++;
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
        final String PLACEMENT_CATEGORY = NbBundle.getMessage(getClass(), "CircleLayout.Category.CircleProperties.name");
        final String SPARCONTROL_CATEGORY = NbBundle.getMessage(getClass(), "CircleLayout.Category.NodePlacement.name");
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "CircleLayout.BoolFixedDiameter.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "CircleLayout.BoolFixedDiameter.desc"),
                    "isBoolFixedDiameter", "setBoolFixedDiameter"));
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "CircleLayout.Diameter.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "CircleLayout.Diameter.desc"),
                    "getDiameter", "setDiameter"));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.NodeOrdering.name"),
                    PLACEMENT_CATEGORY,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.NodeOrdering.desc"),
                    "getNodePlacement", "setNodePlacement", LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.Direction.name"),
                    SPARCONTROL_CATEGORY,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection", RotationComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.NoOverlap.name"),
                    SPARCONTROL_CATEGORY,
                    NbBundle.getMessage(getClass(), "CircleLayout.NodePlacement.NoOverlap.desc"),
                    "isNodePlacementNoOverlap", "setNodePlacementNoOverlap"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        setDiameter(500.0);
        setBoolFixedDiameter(false);
        setNodePlacement("NodeID");
        setNodePlacementNoOverlap(true);
        setNodePlacementDirection("CCW");
    }

    public void setNodePlacement(String strNodeplacement) {
        this.strNodeplacement = strNodeplacement;
    }

    public String getNodePlacement() {
        return this.strNodeplacement;
    }

    public void setBoolFixedDiameter(Boolean boolfixeddiameter) {
        this.boolfixeddiameter = boolfixeddiameter;
        if (this.boolfixeddiameter && this.boolNoOverlap) {
            setNodePlacementNoOverlap(false);
        }
    }

    public boolean isBoolFixedDiameter() {
        return boolfixeddiameter;
    }

    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }

    public Double getDiameter() {
        return diameter;
    }

    public String getNodePlacementDirection() {
        return this.strNodePlacementDirection;
    }

    public void setNodePlacementDirection(String strNodePlacementDirection) {
        this.strNodePlacementDirection = strNodePlacementDirection;
    }

    public boolean isNodePlacementNoOverlap() {
        return boolNoOverlap;
    }

    public void setNodePlacementNoOverlap(Boolean boolNoOverlap) {
        this.boolNoOverlap = boolNoOverlap;
        if (this.boolfixeddiameter && this.boolNoOverlap) {
            setBoolFixedDiameter(false);
        }
    }

    private float[] cartCoors(double radius, int whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt) + (Math.PI / 2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt) + (Math.PI / 2))));
        return coOrds;
    }


}
