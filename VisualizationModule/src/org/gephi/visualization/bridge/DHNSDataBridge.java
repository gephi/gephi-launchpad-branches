/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.visualization.bridge;

import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.dynamic.DynamicUtilities;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Group;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Model;
import org.gephi.graph.api.NodeIterable;
import org.gephi.visualization.GraphLimits;
import org.gephi.visualization.VizArchitecture;
import org.gephi.visualization.VizController;
import org.gephi.visualization.apiimpl.ModelImpl;
import org.gephi.visualization.apiimpl.VizConfig;
import org.gephi.visualization.api.initializer.Modeler;
import org.gephi.visualization.api.objects.ModelClass;
import org.gephi.visualization.hull.ConvexHull;
import org.gephi.visualization.mode.ModeManager;
import org.gephi.visualization.opengl.AbstractEngine;
import org.gephi.visualization.opengl.compatibility.objects.Arrow2dModel;
import org.gephi.visualization.opengl.compatibility.objects.ConvexHullModel;
import org.gephi.visualization.opengl.compatibility.objects.Edge2dModel;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class DHNSDataBridge implements DataBridge, VizArchitecture {

    //Architecture
    protected AbstractEngine engine;
    protected GraphController controller;
    protected HierarchicalGraph graph;
    private VizConfig vizConfig;
    protected ModeManager modeManager;
    protected GraphLimits limits;
    protected DynamicModel dynamicModel;
    protected boolean undirected = false;
    //Version
    protected int nodeVersion = -1;
    protected int edgeVersion = -1;
    protected int graphView = 0;
    protected GraphModel gm;
    //Attributes
    private int cacheMarker = 0;

    @Override
    public void initArchitecture() {
        this.engine = VizController.getInstance().getEngine();
        controller = Lookup.getDefault().lookup(GraphController.class);
        this.vizConfig = VizController.getInstance().getVizConfig();
        this.modeManager = VizController.getInstance().getModeManager();
        this.limits = VizController.getInstance().getLimits();
    }

    public void updateWorld() {
        //System.out.println("update world");
        cacheMarker++;

        GraphModel graphModel = controller.getModel();
        if (graphModel == null) {
            engine.worldUpdated(cacheMarker);
            return;
        }
        if (gm != null && gm != graphModel) {
            reset();
        }
        gm = graphModel;
        HierarchicalGraph graph;
        if (graphModel.isDirected()) {
            undirected = false;
            graph = graphModel.getHierarchicalDirectedGraphVisible();
        } else if (graphModel.isUndirected()) {
            undirected = true;
            graph = graphModel.getHierarchicalUndirectedGraphVisible();
        } else if (graphModel.isMixed()) {
            undirected = false;
            graph = graphModel.getHierarchicalMixedGraphVisible();
        } else {
            undirected = false;
            graph = graphModel.getHierarchicalDirectedGraphVisible();
        }

        if (dynamicModel == null) {
            DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
            dynamicModel = dynamicController.getModel();
        }

        graphView = graph.getView().getViewId();

        ModelClass[] object3dClasses = engine.getModelClasses();

        graph.readLock();



        ModelClass nodeClass = object3dClasses[AbstractEngine.CLASS_NODE];
        if (nodeClass.isEnabled() && (graph.getNodeVersion() > nodeVersion || modeManager.requireModeChange())) {
            updateNodes(graph);
            nodeClass.setCacheMarker(cacheMarker);
        }

        ModelClass edgeClass = object3dClasses[AbstractEngine.CLASS_EDGE];
        if (edgeClass.isEnabled() && (graph.getEdgeVersion() > edgeVersion || modeManager.requireModeChange())) {
            updateEdges(graph);
            updateMetaEdges(graph);
            edgeClass.setCacheMarker(cacheMarker);
            if (!undirected && vizConfig.isShowArrows()) {
                object3dClasses[AbstractEngine.CLASS_ARROW].setCacheMarker(cacheMarker);
            }
        }

        ModelClass potatoClass = object3dClasses[AbstractEngine.CLASS_POTATO];
        if (potatoClass.isEnabled() && (graph.getNodeVersion() > nodeVersion || modeManager.requireModeChange())) {
            updatePotatoes(graph);
            potatoClass.setCacheMarker(cacheMarker);
        }

        nodeVersion = graph.getNodeVersion();
        edgeVersion = graph.getEdgeVersion();

        graph.readUnlock();

        engine.worldUpdated(cacheMarker);
    }

    private void updateNodes(HierarchicalGraph graph) {
        Modeler nodeInit = engine.getModelClasses()[AbstractEngine.CLASS_NODE].getCurrentModeler();

        NodeIterable nodeIterable;
        nodeIterable = graph.getNodes();


        for (Node node : nodeIterable) {

            Model obj = node.getNodeData().getModel();
            if (obj == null) {
                //Model is null, ADD
                obj = nodeInit.initModel(node.getNodeData());
                engine.addObject(AbstractEngine.CLASS_NODE, (ModelImpl) obj);
            } else if (!obj.isValid()) {
                engine.addObject(AbstractEngine.CLASS_NODE, (ModelImpl) obj);
            }
            obj.setCacheMarker(cacheMarker);

            //Modeaction
            if (modeManager.getMode().equals(ModeManager.AVAILABLE_MODES.HIGHLIGHT)) {
                ModelImpl impl = (ModelImpl) obj;
//                if (!node.isVisible()) {
//                    ColorLayer.layerColor(impl, 0.8f, 0.8f, 0.8f);
//                }
            }
        }
    }

    private void updateEdges(HierarchicalGraph graph) {
        Modeler edgeInit = engine.getModelClasses()[AbstractEngine.CLASS_EDGE].getCurrentModeler();
        Modeler arrowInit = engine.getModelClasses()[AbstractEngine.CLASS_ARROW].getCurrentModeler();

        EdgeIterable edgeIterable;
        edgeIterable = graph.getEdges();

        float minWeight = Float.POSITIVE_INFINITY;
        float maxWeight = Float.NEGATIVE_INFINITY;

        TimeInterval timeInterval = DynamicUtilities.getVisibleInterval(dynamicModel);

        for (Edge edge : edgeIterable) {
            if (edge.getSource().getNodeData().getModel() == null || edge.getTarget().getNodeData().getModel() == null) {
                continue;
            }
            float weight = 1f;
            if (timeInterval == null) {
                weight = edge.getWeight();
            } else {
                weight = edge.getWeight(timeInterval.getLow(), timeInterval.getHigh());
            }
            minWeight = Math.min(minWeight, weight);
            maxWeight = Math.max(maxWeight, weight);
            Edge2dModel obj = (Edge2dModel) edge.getEdgeData().getModel();
            if (obj == null) {
                //Model is null, ADD
                obj = (Edge2dModel) edgeInit.initModel(edge.getEdgeData());
                engine.addObject(AbstractEngine.CLASS_EDGE, obj);
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop()) {
                    Arrow2dModel arrowObj = (Arrow2dModel) arrowInit.initModel(edge.getEdgeData());
                    engine.addObject(AbstractEngine.CLASS_ARROW, arrowObj);
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                    obj.setArrow(arrowObj);
                }
            } else if (!obj.isValid()) {
                engine.addObject(AbstractEngine.CLASS_EDGE, obj);
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop()) {
                    Arrow2dModel arrowObj = obj.getArrow();
                    engine.addObject(AbstractEngine.CLASS_ARROW, arrowObj);
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                }
            } else {
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop() && edge.isDirected()) {
                    Arrow2dModel arrowObj = obj.getArrow();
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                }
            }
            obj.setWeight(weight);
            obj.setCacheMarker(cacheMarker);
        }

        limits.setMinWeight(minWeight);
        limits.setMaxWeight(maxWeight);
    }

    public void updateMetaEdges(HierarchicalGraph graph) {
        Modeler edgeInit = engine.getModelClasses()[AbstractEngine.CLASS_EDGE].getCurrentModeler();
        Modeler arrowInit = engine.getModelClasses()[AbstractEngine.CLASS_ARROW].getCurrentModeler();

        float minWeight = Float.POSITIVE_INFINITY;
        float maxWeight = Float.NEGATIVE_INFINITY;

        TimeInterval timeInterval = DynamicUtilities.getVisibleInterval(dynamicModel);

        for (Edge edge : graph.getMetaEdges()) {
            if (edge.getSource().getNodeData().getModel() == null || edge.getTarget().getNodeData().getModel() == null) {
                continue;
            }
            float weight = 1f;
            if (timeInterval == null) {
                weight = edge.getWeight();
            } else {
                weight = edge.getWeight(timeInterval.getLow(), timeInterval.getHigh());
            }
            minWeight = Math.min(minWeight, weight);
            maxWeight = Math.max(maxWeight, weight);
            Edge2dModel obj = (Edge2dModel) edge.getEdgeData().getModel();
            if (obj == null) {
                //Model is null, ADD
                obj = (Edge2dModel) edgeInit.initModel(edge.getEdgeData());
                engine.addObject(AbstractEngine.CLASS_EDGE, obj);
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop()) {
                    Arrow2dModel arrowObj = (Arrow2dModel) arrowInit.initModel(edge.getEdgeData());
                    engine.addObject(AbstractEngine.CLASS_ARROW, arrowObj);
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                    obj.setArrow(arrowObj);
                }
            } else if (!obj.isValid()) {
                engine.addObject(AbstractEngine.CLASS_EDGE, obj);
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop()) {
                    Arrow2dModel arrowObj = obj.getArrow();
                    engine.addObject(AbstractEngine.CLASS_ARROW, arrowObj);
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                }
            } else {
                if (!undirected && vizConfig.isShowArrows() && !edge.isSelfLoop() && edge.isDirected()) {
                    Arrow2dModel arrowObj = obj.getArrow();
                    arrowObj.setCacheMarker(cacheMarker);
                    arrowObj.setWeight(weight);
                }
            }
            obj.setWeight(weight);
            obj.setCacheMarker(cacheMarker);
        }

        limits.setMinMetaWeight(minWeight);
        limits.setMaxMetaWeight(maxWeight);
    }

    public void updatePotatoes(HierarchicalGraph graph) {

        ModelClass potatoClass = engine.getModelClasses()[AbstractEngine.CLASS_POTATO];
        if (potatoClass.isEnabled()) {
            Modeler potInit = engine.getModelClasses()[AbstractEngine.CLASS_POTATO].getCurrentModeler();

            List<ModelImpl> hulls = new ArrayList<ModelImpl>();
            Node[] nodes = graph.getNodes().toArray();
            for (Node n : nodes) {
                Node parent = graph.getParent(n);
                if (parent != null) {
                    Group group = (Group) parent;
                    Model hullModel = group.getGroupData().getHullModel();
                    if (hullModel != null && hullModel.isCacheMatching(cacheMarker)) {
                        ConvexHull hull = (ConvexHull) hullModel.getObj();
                        hull.addNode(n);
                        hull.setModel(hullModel);
                    } else if (hullModel != null) {
                        //Its not the first time the hull exist
                        ConvexHullModel model = (ConvexHullModel) hullModel;
                        model.setScale(1f);
                        hullModel.setCacheMarker(cacheMarker);
                        hulls.add((ModelImpl) hullModel);
                    } else {
                        ConvexHull ch = new ConvexHull();
                        ch.setMetaNode(parent);
                        ch.addNode(n);
                        ModelImpl obj = potInit.initModel(ch);
                        group.getGroupData().setHullModel(obj);
                        obj.setCacheMarker(cacheMarker);
                        hulls.add(obj);
                    }
                }
            }
            for (ModelImpl im : hulls) {
                ConvexHull hull = (ConvexHull) im.getObj();
                hull.recompute();
                engine.addObject(AbstractEngine.CLASS_POTATO, im);
            }
        }
    }

    public boolean requireUpdate() {
        if (graph == null) {
            //Try to get a graph
            GraphModel graphModel = controller.getModel();
            if (graphModel != null) {
                graph = graphModel.getHierarchicalGraphVisible();
            }
        }
        //Refresh reader if sight changed
        Graph g = graph;
        if (g != null) {
            if (g.getGraphModel().getVisibleView().getViewId() != graphView) {
                reset();
            }
            return g.getNodeVersion() > nodeVersion || g.getEdgeVersion() > edgeVersion;
        }
        return false;
    }

    public void resetGraph() {
        graph = null;
        dynamicModel = null;
    }

    public void reset() {
        nodeVersion = -1;
        edgeVersion = -1;
    }

    private void resetClasses() {
        for (ModelClass objClass : engine.getModelClasses()) {
            if (objClass.isEnabled()) {
                engine.resetObjectClass(objClass);
            }
        }
    }
}
