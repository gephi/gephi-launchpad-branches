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
package org.gephi.layout.plugin.circularlayout.dualcirclelayout;


import org.gephi.layout.plugin.circularlayout.nodecomparator.BasicNodeComparator;
import org.gephi.layout.plugin.circularlayout.nodedatacomparator.BasicNodeDataComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Map;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphController;
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
import javax.swing.JOptionPane;


/**
 *
 * @author Matt Groeninger
 */
public class DualCircleLayout extends AbstractLayout implements Layout {

    private Graph graph;
    private boolean converged;
    private boolean highdegreeoutside;
    private int secondarynodecount;
    static double TWO_PI = (2 * Math.PI);
    private String strNodePlacementDirection;
    private String attribute;


     public static Map getAttributeMap() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getModel();
        Map<String, String> map = new TreeMap<String, String>();
        map.put("NodeID", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.NodeID.name"));
        map.put("Degree", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.Degree.name"));
        if (objGraphModel.isDirected()) {
            map.put("InDegree", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.InDegree.name"));
            map.put("OutDegree", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.OutDegree.name"));
            map.put("MutualDegree", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.Mutual.name"));
        } else if (objGraphModel.isHierarchical()) {
            map.put("ChildrenCount", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.Children.name"));
            map.put("DescendantCount", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.Descendents.name"));
        }
        AttributeModel attModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        for (AttributeColumn c : attModel.getNodeTable().getColumns()) {
           map.put(c.getTitle()+"-Att", c.getTitle()+" (Attribute)");
        }
        return map;
    }

    public static Map getRotationMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("CCW", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.CCW"));
        map.put("CW", NbBundle.getMessage(DualCircleLayout.class, "DualCircleLayout.NodePlacement.CW"));
        return map;
    }



    public DualCircleLayout(LayoutBuilder layoutBuilder, int secondarynodecount) {
        super(layoutBuilder);
        this.secondarynodecount = secondarynodecount;
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
        double tmpsecondarycirc = 0;
        double tmpprimarycirc = 0;
        int index = 0;
        double twopi = TWO_PI;
        double lasttheta = 0;
        double primary_theta = 0;
        double secondary_theta = 0;
        double primary_scale = 1;
        double secondary_scale = 1;
        double correct_theta = 0;
        if (this.strNodePlacementDirection.equals("CW")) {
            twopi = -twopi;
        }
        Node[] nodes = graph.getNodes().toArray();
       if (this.attribute.equals("NodeID")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, "NodeID", false));
            //TODO need to figure out new language around sort stuff (High degree outside circle doesn't work)
        } else if (this.attribute.endsWith("-Att")) {
            Arrays.sort(nodes, new BasicNodeDataComparator(nodes, this.attribute.substring(0,this.attribute.length()-4), false));
        } else if (getAttributeMap().containsKey(this.attribute)) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes, this.attribute, false));
        }


        for (Node n : nodes) {
            if (index < this.secondarynodecount) {
                tmpsecondarycirc += (n.getNodeData().getRadius() * 2);
            } else {
                tmpprimarycirc += (n.getNodeData().getRadius() * 2);
            }
            index++;
        }
        index = 0;//reset index

        double circum_ratio = tmpprimarycirc / tmpsecondarycirc;

        if (circum_ratio < 2) {
            primary_scale = (2 / circum_ratio);
            tmpprimarycirc = 2 * tmpsecondarycirc;
        }

        if (this.isHighDegreeOutside()) {
            secondary_scale = ((2 * tmpprimarycirc) / tmpsecondarycirc); //Need to know how much the circumference has changed from the original
            tmpsecondarycirc = tmpprimarycirc * 2; //Scale to a better relationship
        } else {
            secondary_scale = (tmpprimarycirc / (2 * tmpsecondarycirc)); //Need to know how much the circumference has changed from the original
            tmpsecondarycirc = tmpprimarycirc / 2; //Scale to a better relationship
        }

        tmpprimarycirc = tmpprimarycirc * 1.2;
        primary_theta = (twopi / tmpprimarycirc);
        double primaryradius = (tmpprimarycirc / Math.PI) / 2;


        tmpsecondarycirc = tmpsecondarycirc * 1.2;
        secondary_theta = (twopi / tmpsecondarycirc);
        double secondaryradius = (tmpsecondarycirc / Math.PI) / 2;

        for (Node n : nodes) {
            if (index < this.secondarynodecount) {
                //Draw secondary circle
                double noderadius = (n.getNodeData().getRadius());
                //This step is hackish... but it makes small numbers of nodes symetrical on both the secondary circles.
                if (secondary_scale > 2) {
                    noderadius = (tmpsecondarycirc / (2 * this.secondarynodecount * secondary_scale * 1.2));
                }
                double noderadian = (secondary_theta * noderadius * 1.2 * secondary_scale);
                if (index == 0) {
                    correct_theta = noderadian; //correct for cosmetics... overlap prevention offsets the first node by it's radius which looks weird.
                }
                nodeCoords = this.cartCoors(secondaryradius, 1, lasttheta + noderadian - correct_theta);
                lasttheta += (noderadius * 2 * secondary_theta * 1.2 * secondary_scale);
            } else {
                double noderadius = (n.getNodeData().getRadius());
                double noderadian = (primary_theta * noderadius * 1.2 * primary_scale);
                if (index == this.secondarynodecount) {
                    lasttheta = 0;
                    correct_theta = noderadian; //correct for cosmetics... overlap prevention offsets the first node by it's radius which looks weird.
                }
                //Draw primary circle
                nodeCoords = this.cartCoors(primaryradius, 1, lasttheta + noderadian - correct_theta);
                lasttheta += (noderadius * 2 * primary_theta * 1.2 * primary_scale);
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
                    NbBundle.getMessage(getClass(), "DualCircleLayout.HighDegreeOutside.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.Category.NodePlacement.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.HighDegreeOutside.desc"),
                    "isHighDegreeOutside", "setHighDegreeOutside"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "DualCircleLayout.InnerNodeCount.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.Category.NodePlacement.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.InnerNodeCount.desc"),
                    "getInnerNodeCount", "setInnerNodeCount"));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "DualCircleLayout.attribue.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.Category.Sorting.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.attribue.desc"),
                    "getAttribute", "setAttribute",LayoutComboBoxEditor.class));
            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    NbBundle.getMessage(getClass(), "DualCircleLayout.NodePlacement.Direction.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.Category.NodePlacement.name"),
                    NbBundle.getMessage(getClass(), "DualCircleLayout.NodePlacement.Direction.desc"),
                    "getNodePlacementDirection", "setNodePlacementDirection", RotationComboBoxEditor.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        setInnerNodeCount(4);
        setHighDegreeOutside(false);
        setNodePlacementDirection("CCW");
        setAttribute("NodeID");
    }

    public void setInnerNodeCount(Integer intsecondarynodecount) {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel objGraphModel = graphController.getModel();
        graph = objGraphModel.getGraphVisible();
        if (intsecondarynodecount > graph.getNodeCount()) {
            JOptionPane.showMessageDialog(null,
                NbBundle.getMessage(getClass(), "DualCircleLayout.setInnerNodeCount.TooHigh.message"),
                NbBundle.getMessage(getClass(), "DualCircleLayout.setInnerNodeCount.TooHigh.title"),
                 JOptionPane.WARNING_MESSAGE);
        } else if (intsecondarynodecount < 1) {
            JOptionPane.showMessageDialog(null,
                NbBundle.getMessage(getClass(), "DualCircleLayout.setInnerNodeCount.TooLow.message"),
                NbBundle.getMessage(getClass(), "DualCircleLayout.setInnerNodeCount.TooLow.title"),
                 JOptionPane.WARNING_MESSAGE);        } else {
            //TODO: add node count check to do boundary checking on user input
            this.secondarynodecount = intsecondarynodecount;
        }
    }

    public Integer getInnerNodeCount() {
        return secondarynodecount;
    }

    public Boolean isHighDegreeOutside() {
        return highdegreeoutside;
    }

    public void setHighDegreeOutside(Boolean highdegreeoutside) {
        this.highdegreeoutside = highdegreeoutside;
    }

    public String getNodePlacementDirection() {
        return this.strNodePlacementDirection;
    }

    public void setNodePlacementDirection(String strNodePlacementDirection) {
        this.strNodePlacementDirection = strNodePlacementDirection;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    private float[] cartCoors(double radius, int whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt) + (Math.PI / 2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt) + (Math.PI / 2))));
        return coOrds;
    }

}


