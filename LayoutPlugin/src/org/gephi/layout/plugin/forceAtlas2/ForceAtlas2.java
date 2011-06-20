/*
Copyright 2008-2011 Gephi
Authors : Mathieu Jacomy <mathieu.jacomy@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.layout.plugin.forceAtlas2;

import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.layout.plugin.forceAtlas2.ForceFactory.AttractionForce;
import org.gephi.layout.plugin.forceAtlas2.ForceFactory.RepulsionForce;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * ForceAtlas 2 Layout, manages each step of the computations.
 * @author Mathieu Jacomy
 */
public class ForceAtlas2 implements Layout {

    private GraphModel graphModel;
    private HierarchicalGraph graph;
    private final ForceAtlas2Builder layoutBuilder;
    private DynamicModel dynamicModel;
    private double edgeWeightInfluence;
    private double jitterTolerance;
    private double scalingRatio;
    private double gravity;
    private double speed;
    private boolean outboundAttractionDistribution;
    private boolean adjustSizes;
    private boolean barnesHutOptimize;
    private double barnesHutTheta;
    private boolean linLogMode;
    private Region rootRegion;
    double outboundAttCompensation = 1;
    //Dynamic Weight
    private TimeInterval timeInterval;

    public ForceAtlas2(ForceAtlas2Builder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
    }

    @Override
    public void initAlgo() {
        speed = 1.;

        graph = graphModel.getHierarchicalGraphVisible();
        this.timeInterval = DynamicUtilities.getVisibleInterval(dynamicModel);

        graph.readLock();
        Node[] nodes = graph.getNodes().toArray();

        // Initialise layout data
        for (Node n : nodes) {
            if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof ForceAtlas2LayoutData)) {
                ForceAtlas2LayoutData nLayout = new ForceAtlas2LayoutData();
                n.getNodeData().setLayoutData(nLayout);
            }
            NodeData nData = n.getNodeData();
            ForceAtlas2LayoutData nLayout = nData.getLayoutData();
            nLayout.mass = 1 + graph.getDegree(n);
            nLayout.old_dx = 0;
            nLayout.old_dy = 0;
            nLayout.dx = 0;
            nLayout.dy = 0;
        }
    }

    @Override
    public void goAlgo() {
        // Initialize graph data
        if (graphModel == null) {
            return;
        }
        graph = graphModel.getHierarchicalGraphVisible();
        this.timeInterval = DynamicUtilities.getVisibleInterval(dynamicModel);

        graph.readLock();
        Node[] nodes = graph.getNodes().toArray();
        Edge[] edges = graph.getEdgesAndMetaEdges().toArray();

        // Initialise layout data
        for (Node n : nodes) {
            if (n.getNodeData().getLayoutData() == null || !(n.getNodeData().getLayoutData() instanceof ForceAtlas2LayoutData)) {
                ForceAtlas2LayoutData nLayout = new ForceAtlas2LayoutData();
                n.getNodeData().setLayoutData(nLayout);
            }
            NodeData nData = n.getNodeData();
            ForceAtlas2LayoutData nLayout = nData.getLayoutData();
            nLayout.mass = 1 + graph.getDegree(n);
            nLayout.old_dx = nLayout.dx;
            nLayout.old_dy = nLayout.dy;
            nLayout.dx = 0;
            nLayout.dy = 0;
        }

        // If Barnes Hut active, initialize root region
        if (isBarnesHutOptimize()) {
            rootRegion = new Region(nodes);
            rootRegion.buildSubRegions();
        }

        // If outboundAttractionDistribution active, compensate.
        if (isOutboundAttractionDistribution()) {
            outboundAttCompensation = 0;
            for (Node n : nodes) {
                NodeData nData = n.getNodeData();
                ForceAtlas2LayoutData nLayout = nData.getLayoutData();
                outboundAttCompensation += nLayout.mass;
            }
            outboundAttCompensation /= nodes.length;
        }

        // Repulsion
        RepulsionForce Repulsion = ForceFactory.builder.buildRepulsion(isAdjustSizes(), getScalingRatio());
        if (isBarnesHutOptimize()) {
            for (Node n : nodes) {
                rootRegion.applyForce(n, Repulsion, getBarnesHutTheta());
            }
        } else {
            for (int n1Index = 0; n1Index < nodes.length; n1Index++) {
                Node n1 = nodes[n1Index];
                for (int n2Index = 0; n2Index < n1Index; n2Index++) {
                    Node n2 = nodes[n2Index];
                    Repulsion.apply(n1, n2);
                }
            }
        }

        // Attraction
        AttractionForce Attraction = ForceFactory.builder.buildAttraction(isLinLogMode(), isOutboundAttractionDistribution(), isAdjustSizes(), 1 * ((isOutboundAttractionDistribution()) ? (outboundAttCompensation) : (1)));
        for (Edge e : edges) {
            Attraction.apply(e.getSource(), e.getTarget(), Math.pow(getWeight(e), getEdgeWeightInfluence()));
        }

        // Gravity
        for (Node n : nodes) {
            Repulsion.apply(n, getGravity() / getScalingRatio());
        }

        // Auto adjust speed
        double totalSwinging = 0d;  // How much irregular movement
        double totalEffectiveTraction = 0d;  // Hom much useful movement
        for (Node n : nodes) {
            NodeData nData = n.getNodeData();
            ForceAtlas2LayoutData nLayout = nData.getLayoutData();
            if (!nData.isFixed()) {
                double swinging = Math.sqrt(Math.pow(nLayout.old_dx - nLayout.dx, 2) + Math.pow(nLayout.old_dy - nLayout.dy, 2));
                totalSwinging += nLayout.mass * swinging;   // If the node has a burst change of direction, then it's not converging.
                totalEffectiveTraction += nLayout.mass * 0.5 * Math.sqrt(Math.pow(nLayout.old_dx + nLayout.dx, 2) + Math.pow(nLayout.old_dy + nLayout.dy, 2));
            }
        }
        // We want that swingingMovement < tolerance * convergenceMovement
        double targetSpeed = getJitterTolerance() * getJitterTolerance() * totalEffectiveTraction / totalSwinging;

        // But the speed shoudn't rise too much too quickly, since it would make the convergence drop dramatically.
        double maxRise = 0.5;   // Max rise: 50%
        speed = speed + Math.min(targetSpeed - speed, maxRise * speed);

        // Apply forces
        if (isAdjustSizes()) {
            // If nodes overlap prevention is active, it's not possible to trust the swinging mesure.
            for (Node n : nodes) {
                NodeData nData = n.getNodeData();
                ForceAtlas2LayoutData nLayout = nData.getLayoutData();
                if (!nData.isFixed()) {

                    // Adaptive auto-speed: the speed of each node is lowered
                    // when the node swings.
                    double swinging = Math.sqrt((nLayout.old_dx - nLayout.dx) * (nLayout.old_dx - nLayout.dx) + (nLayout.old_dy - nLayout.dy) * (nLayout.old_dy - nLayout.dy));
                    double factor = 0.1 * speed / (1f + speed * Math.sqrt(swinging));

                    double df = Math.sqrt(Math.pow(nLayout.dx, 2) + Math.pow(nLayout.dy, 2));
                    factor = Math.min(factor * df, 10.) / df;

                    double x = nData.x() + nLayout.dx * factor;
                    double y = nData.y() + nLayout.dy * factor;

                    nData.setX((float) x);
                    nData.setY((float) y);
                }
            }
        } else {
            for (Node n : nodes) {
                NodeData nData = n.getNodeData();
                ForceAtlas2LayoutData nLayout = nData.getLayoutData();
                if (!nData.isFixed()) {

                    // Adaptive auto-speed: the speed of each node is lowered
                    // when the node swings.
                    double swinging = Math.sqrt((nLayout.old_dx - nLayout.dx) * (nLayout.old_dx - nLayout.dx) + (nLayout.old_dy - nLayout.dy) * (nLayout.old_dy - nLayout.dy));
                    //double factor = speed / (1f + Math.sqrt(speed * swinging));
                    double factor = speed / (1f + speed * Math.sqrt(swinging));

                    double x = nData.x() + nLayout.dx * factor;
                    double y = nData.y() + nLayout.dy * factor;

                    nData.setX((float) x);
                    nData.setY((float) y);
                }
            }
        }
        graph.readUnlockAll();
    }

    @Override
    public boolean canAlgo() {
        return graphModel != null;
    }

    @Override
    public void endAlgo() {
        for (Node n : graph.getNodes()) {
            n.getNodeData().setLayoutData(null);
        }
        graph.readUnlockAll();
    }

    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String FORCEATLAS2_TUNING = NbBundle.getMessage(getClass(), "ForceAtlas2.tuning");
        final String FORCEATLAS2_BEHAVIOR = NbBundle.getMessage(getClass(), "ForceAtlas2.behavior");
        final String FORCEATLAS2_PERFORMANCE = NbBundle.getMessage(getClass(), "ForceAtlas2.performance");

        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.scalingRatio.name"),
                    FORCEATLAS2_TUNING,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.scalingRatio.desc"),
                    "getScalingRatio", "setScalingRatio"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.gravity.name"),
                    FORCEATLAS2_TUNING,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.gravity.desc"),
                    "getGravity", "setGravity"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.distributedAttraction.name"),
                    FORCEATLAS2_BEHAVIOR,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.distributedAttraction.desc"),
                    "isOutboundAttractionDistribution", "setOutboundAttractionDistribution"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.linLogMode.name"),
                    FORCEATLAS2_BEHAVIOR,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.linLogMode.desc"),
                    "isLinLogMode", "setLinLogMode"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.adjustSizes.name"),
                    FORCEATLAS2_BEHAVIOR,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.adjustSizes.desc"),
                    "isAdjustSizes", "setAdjustSizes"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.edgeWeightInfluence.name"),
                    FORCEATLAS2_BEHAVIOR,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.edgeWeightInfluence.desc"),
                    "getEdgeWeightInfluence", "setEdgeWeightInfluence"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.jitterTolerance.name"),
                    FORCEATLAS2_PERFORMANCE,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.jitterTolerance.desc"),
                    "getJitterTolerance", "setJitterTolerance"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutOptimization.name"),
                    FORCEATLAS2_PERFORMANCE,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutOptimization.desc"),
                    "isBarnesHutOptimize", "setBarnesHutOptimize"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutTheta.name"),
                    FORCEATLAS2_PERFORMANCE,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutTheta.desc"),
                    "getBarnesHutTheta", "setBarnesHutTheta"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        int nodesCount = 0;

        if (graphModel != null) {
            nodesCount = graphModel.getHierarchicalGraphVisible().getNodeCount();
        }

        // Tuning
        if (nodesCount >= 100) {
            setScalingRatio(2.0);
        } else {
            setScalingRatio(10.0);
        }
        setGravity(1.);

        // Behavior
        setOutboundAttractionDistribution(false);
        setLinLogMode(false);
        setAdjustSizes(false);
        setEdgeWeightInfluence(1.);

        // Performance
        if (nodesCount >= 50000) {
            setJitterTolerance(10d);
        } else if (nodesCount >= 5000) {
            setJitterTolerance(1d);
        } else {
            setJitterTolerance(0.1d);
        }
        if (nodesCount >= 1000) {
            setBarnesHutOptimize(true);
        } else {
            setBarnesHutOptimize(false);
        }
        setBarnesHutTheta(1.2);
    }

    @Override
    public LayoutBuilder getBuilder() {
        return layoutBuilder;
    }

    @Override
    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
        Workspace workspace = graphModel.getWorkspace();
        DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
        if (dynamicController != null && workspace != null) {
            dynamicModel = dynamicController.getModel(workspace);
        }
        // Trick: reset here to take the profile of the graph in account for default values
        resetPropertiesValues();
    }

    public Double getBarnesHutTheta() {
        return barnesHutTheta;
    }

    public void setBarnesHutTheta(Double barnesHutTheta) {
        this.barnesHutTheta = barnesHutTheta;
    }

    public Double getEdgeWeightInfluence() {
        return edgeWeightInfluence;
    }

    public void setEdgeWeightInfluence(Double edgeWeightInfluence) {
        this.edgeWeightInfluence = edgeWeightInfluence;
    }

    public Double getJitterTolerance() {
        return jitterTolerance;
    }

    public void setJitterTolerance(Double jitterTolerance) {
        this.jitterTolerance = jitterTolerance;
    }

    public Boolean isLinLogMode() {
        return linLogMode;
    }

    public void setLinLogMode(Boolean linLogMode) {
        this.linLogMode = linLogMode;
    }

    public Double getScalingRatio() {
        return scalingRatio;
    }

    public void setScalingRatio(Double scalingRatio) {
        this.scalingRatio = scalingRatio;
    }

    public Double getGravity() {
        return gravity;
    }

    public void setGravity(Double gravity) {
        this.gravity = gravity;
    }

    public Boolean isOutboundAttractionDistribution() {
        return outboundAttractionDistribution;
    }

    public void setOutboundAttractionDistribution(Boolean outboundAttractionDistribution) {
        this.outboundAttractionDistribution = outboundAttractionDistribution;
    }

    public Boolean isAdjustSizes() {
        return adjustSizes;
    }

    public void setAdjustSizes(Boolean adjustSizes) {
        this.adjustSizes = adjustSizes;
    }

    public Boolean isBarnesHutOptimize() {
        return barnesHutOptimize;
    }

    public void setBarnesHutOptimize(Boolean barnesHutOptimize) {
        this.barnesHutOptimize = barnesHutOptimize;
    }

    private float getWeight(Edge edge) {
        if (timeInterval != null) {
            return edge.getWeight(timeInterval.getLow(), timeInterval.getHigh());
        } else {
            return edge.getWeight();
        }
    }
}
