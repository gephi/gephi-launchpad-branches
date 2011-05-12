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
import java.util.LinkedHashMap;
import java.util.Map;
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

    @Override
    public void initAlgo() {
        converged = false;
        graph = graphModel.getGraphVisible();
    }

    @Override
    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        float[] nodeCoords = new float[2];
        Map<Integer, Integer> MapLayers = new LinkedHashMap<Integer, Integer>();
        List<Node> NodeList = new ArrayList<Node>();
        double tmptotallength = 0;
        double tmplength = 0;
        double maxlength = 0;
        double theta;

        Node[] nodes = graph.getNodes().toArray();

        if (this.enumNodeplacement == PlacementEnum.Degree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"Degree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Indegree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"InDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Outdegree) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"OutDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Mutual) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"MutualDegree", true));
        } else if (this.enumNodeplacement == PlacementEnum.Children) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"Children", true));
        } else if (this.enumNodeplacement == PlacementEnum.Descendents) {
            Arrays.sort(nodes, new BasicNodeComparator(graph, nodes,"Descendent", true));
        }
        int i = 0;
        int lastlayer = 0;
        for (Node n : nodes) {
            if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof RadialAxisLayerLayoutData)) {
                n.getNodeData().setLayoutData(new RadialAxisLayerLayoutData());
            }
            RadialAxisLayerLayoutData layoutData = n.getNodeData().getLayoutData();
            layoutData.layer = getLayerAttribute(n, this.enumNodeplacement);
            if (i == 0 || i == (nodes.length)) {
                MapLayers.put(layoutData.layer,i);
                lastlayer = layoutData.layer;
            } else {
                if (lastlayer != layoutData.layer) {
                    tmptotallength = 0;
                    MapLayers.put(layoutData.layer,i);
                    lastlayer = layoutData.layer;
                }
            }
            NodeList.add(n);
            tmplength = n.getNodeData().getRadius() * 2;
            tmptotallength += tmplength;
            if (tmplength > maxlength) {
                maxlength = tmplength;
            }
            layoutData.distance = tmptotallength;
            i++;
        }
        double tmpcirc = (MapLayers.size() * maxlength * 1.2);
        double tmpradius = tmpcirc / TWO_PI;
        theta = (TWO_PI / MapLayers.size());

        if (this.enumNodePlacementDirection == RotationEnum.CW) {
            theta = -theta;
        }

        int previousindex = 0;
        int currentindex = 0;
        i = 0;
        Iterator it = MapLayers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> currentpairs = (Map.Entry<Integer, Integer>)it.next();
            currentindex = currentpairs.getValue();
            if (currentindex > previousindex) {
                System.out.println("Have a range from " + previousindex + " to " +currentindex);
                System.out.println("Should return " + (currentindex - previousindex));
                List<Node> shortnodes = NodeList.subList(previousindex,currentindex);
                System.out.println("Returned " + shortnodes.size() );
                for (Node n : shortnodes) {
                    RadialAxisLayerLayoutData layoutData = n.getNodeData().getLayoutData();
                    nodeCoords = this.cartCoors((layoutData.distance + tmpradius) * 1.2, i, theta);
                    n.getNodeData().setX(nodeCoords[0]);
                    n.getNodeData().setY(nodeCoords[1]);
                }
            }
            previousindex = currentindex;
            i++;
        }

        List<Node> shortnodes = NodeList.subList(previousindex,nodes.length);
        System.out.println("Returned " + shortnodes.size() );
        for (Node n : shortnodes) {
            RadialAxisLayerLayoutData layoutData = n.getNodeData().getLayoutData();
            nodeCoords = this.cartCoors((layoutData.distance + tmpradius) * 1.2, i, theta);
            n.getNodeData().setX(nodeCoords[0]);
            n.getNodeData().setY(nodeCoords[1]);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        setNodePlacement(PlacementEnum.Degree);
        setNodePlacementDirection(RotationEnum.CCW);
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

    public int getLayerAttribute(Node n, Enum Placement) {
        int layout = 0;
        if (Placement == PlacementEnum.Degree) {
            layout = graph.getDegree(n);
        } else if (Placement == PlacementEnum.Indegree) {
            GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            GraphModel objGraphModel = graphController.getModel();
            DirectedGraph objGraph = objGraphModel.getDirectedGraph();
            layout = objGraph.getInDegree( n);
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

    private float[] cartCoors(double radius, int whichInt, double theta) {
        float[] coOrds = new float[2];
        coOrds[0] = (float) (radius * (Math.cos((theta * whichInt) + (Math.PI / 2))));
        coOrds[1] = (float) (radius * (Math.sin((theta * whichInt) + (Math.PI / 2))));
        return coOrds;
    }

    public class RadialAxisLayerLayoutData implements LayoutData {
        //Data

        public int layer = 0;
        public double distance = 0;
    }
}
