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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author Matt
 */
public class CircleLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private double diameter;
    private boolean boolfixeddiameter;
    private int nodeplacement = 1;
    private boolean boolNodePlacementNodeID = true;
    private boolean boolNodePlacementRandom = false;
    private boolean boolNodePlacementDegree = false;
    private boolean boolNoOverlap = true;
    private String stringNodePlacementDirection = "CCW";


    //Node Placement types
    static final int NODE_ID_PLACEMENT = 1;
    static final int RANDOM_PLACEMENT = 2;
    static final int DEGREE_PLACEMENT = 3;
    static final double TWO_PI = (2*Math.PI);


    public CircleLayout(LayoutBuilder layoutBuilder, double diameter, boolean boolfixeddiameter) {
        super(layoutBuilder);
        this.diameter = diameter;
        this.boolfixeddiameter = boolfixeddiameter;
    }

    @Override
    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
    }

    @Override
    public void goAlgo() {
        //Determine Radius of Circle
        graph = graphModel.getGraphVisible();
        float[] nodeCoords = new float[2];
        double tmpcirc = 0;
        double tmpdiameter =0;
        int index = 0;
        int nodecount = graph.getNodeCount();
        double noderadius =0;
        double theta = TWO_PI/nodecount;
        double lasttheta = 0;
        
        if (!this.boolfixeddiameter) {
            Node[] nodes = graph.getNodes().toArray();
            for (Node n : nodes) {
                tmpcirc += (n.getNodeData().getRadius()*2);
            }
            tmpcirc=(tmpcirc*1.2);
            tmpdiameter = tmpcirc/Math.PI;
            if (this.boolNoOverlap) {
                theta = (TWO_PI/tmpcirc);
            }
        } else {
            tmpdiameter = this.diameter;
        }
        double radius = tmpdiameter/2;

        //determine Node placement
        Node[] nodes = graph.getNodes().toArray();

        switch (nodeplacement) {
            case NODE_ID_PLACEMENT:
            default:
                break;
            case RANDOM_PLACEMENT:
                   List nodesList = Arrays.asList(nodes);
                   Collections.shuffle(nodesList);
                break;
            case DEGREE_PLACEMENT:
                   Arrays.sort(nodes, new Comparator<Node>() {
                        @Override
                        public int compare(Node o1, Node o2) {
                            int f1 = graph.getDegree(o1);
                            int f2 = graph.getDegree(o2);
                          return f2-f1;
                        }
                    });
                    break;
        }
        
        if (this.stringNodePlacementDirection == null ? "CW" == null : this.stringNodePlacementDirection.equals("CW")) {
            theta = -theta;
        }
        for (Node n : nodes) {
            if (this.boolNoOverlap) {
                noderadius = (n.getNodeData().getRadius());
                double noderadian = (theta*noderadius*1.2);
                nodeCoords = this.cartCoors(radius, 1,lasttheta+noderadian);
                lasttheta += (noderadius*2*theta*1.2);
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
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.BoolFixedDiameter.name"),
                    "Circle Properties",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.BoolFixedDiameter.desc"),
                    "isBoolFixedDiameter", "setBoolFixedDiameter"));
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.Diameter.name"),
                    "Circle Properties",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.Diameter.desc"),
                    "getDiameter", "setDiameter"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NodeID.name"),
                    "Node Placement",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NodeID.desc"),
                    "isNodePlacementNodeID", "setNodePlacementNodeID"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Random.name"),
                    "Node Placement",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Random.desc"),
                    "isNodePlacementRandom", "setNodePlacementRandom"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Degree.name"),
                    "Node Placement",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Degree.desc"),
                    "isNodePlacementDegree", "setNodePlacementDegree"));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Direction.name"),
                    "Node Placement",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection",RotationComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NoOverlap.name"),
                    "Node Placement",
                    NbBundle.getMessage(CircleLayout.class, "CircleLayout.NodePlacement.NoOverlap.desc"),
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
        setNodePlacementNodeID(true);
        setNodePlacementNoOverlap(true);
    }

    public void setNodePlacementNodeID(Boolean boolNodePlacementNodeID) {
        if (boolNodePlacementNodeID == true) {
            setPlacement(NODE_ID_PLACEMENT);
        }
    }

    public boolean isNodePlacementNodeID() {
        return boolNodePlacementNodeID;
    }


    public void setNodePlacementRandom(Boolean boolNodePlacementRandom) {
        if (boolNodePlacementRandom == true) {
            setPlacement(RANDOM_PLACEMENT);
        }
    }

    public boolean isNodePlacementRandom() {
        return boolNodePlacementRandom;
    }

    public void setNodePlacementDegree(Boolean boolNodePlacementDegree) {
        if (boolNodePlacementDegree == true) {
            setPlacement(DEGREE_PLACEMENT);
        }
    }

    public boolean isNodePlacementDegree() {
        return boolNodePlacementDegree;
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
        return this.stringNodePlacementDirection;
    }
    
    public void setNodePlacementDirection(String stringNodePlacementDirection) {
        if ((stringNodePlacementDirection == null ? "CCW" == null : stringNodePlacementDirection.equals("CCW")) || (stringNodePlacementDirection == null ? "CW" == null : stringNodePlacementDirection.equals("CW"))) {
            this.stringNodePlacementDirection = stringNodePlacementDirection;
        } else {
            this.stringNodePlacementDirection="CCW";
        }
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

    private float[] cartCoors(double radius, int whichInt,double theta) {
       	float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt)+(Math.PI/2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt)+(Math.PI/2))));
        return coOrds;
    }

    private void setPlacement(int layout) {
        this.nodeplacement = CircleLayout.NODE_ID_PLACEMENT;
        this.boolNodePlacementRandom = false;
        this.boolNodePlacementNodeID = false;
        this.boolNodePlacementDegree = false;
        switch (layout) {
            case NODE_ID_PLACEMENT:
            default:
                this.nodeplacement = CircleLayout.NODE_ID_PLACEMENT;
                this.boolNodePlacementNodeID = true;
                break;
            case RANDOM_PLACEMENT:
                this.nodeplacement = CircleLayout.RANDOM_PLACEMENT;
                this.boolNodePlacementRandom = true;
                break;
            case DEGREE_PLACEMENT:
                this.nodeplacement = CircleLayout.DEGREE_PLACEMENT;
                this.boolNodePlacementDegree = true;
                break;
        }
    }
}
