/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.partition.impl;

import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.api.AttributeEvent;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeListener;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphEvent;
import org.gephi.graph.api.GraphListener;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.partition.api.EdgePartition;
import org.gephi.partition.api.NodePartition;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.api.PartitionModel;
import org.gephi.partition.spi.Transformer;
import org.gephi.partition.spi.TransformerBuilder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = PartitionController.class)
public class PartitionControllerImpl implements PartitionController, AttributeListener {

    private PartitionModelImpl model;
    private boolean refreshPartitions = true;

    public PartitionControllerImpl() {

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
                workspace.add(new PartitionModelImpl());
            }

            public void select(Workspace workspace) {
                model = workspace.getLookup().lookup(PartitionModelImpl.class);
                refreshPartitions = true;
                GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
                trachViewChange(gm);
                AttributeModel attributeModel = workspace.getLookup().lookup(AttributeModel.class);
                attributeModel.getNodeTable().addAttributeListener(PartitionControllerImpl.this);
                attributeModel.getEdgeTable().addAttributeListener(PartitionControllerImpl.this);
            }

            public void unselect(Workspace workspace) {
                model = null;
                AttributeModel attributeModel = workspace.getLookup().lookup(AttributeModel.class);
                attributeModel.getNodeTable().removeAttributeListener(PartitionControllerImpl.this);
                attributeModel.getEdgeTable().removeAttributeListener(PartitionControllerImpl.this);
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
                untrackViewChange(null);
            }
        });
        if (pc.getCurrentWorkspace() != null) {
            refreshPartitions=true;
            model = pc.getCurrentWorkspace().getLookup().lookup(PartitionModelImpl.class);
            if (model == null) {
                model = new PartitionModelImpl();
                Workspace workspace = pc.getCurrentWorkspace();
                pc.getCurrentWorkspace().add(model);
                GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
                trachViewChange(gm);

                AttributeModel attributeModel = workspace.getLookup().lookup(AttributeModel.class);
                attributeModel.getNodeTable().addAttributeListener(PartitionControllerImpl.this);
                attributeModel.getEdgeTable().addAttributeListener(PartitionControllerImpl.this);
            }
        }
    }
    private GraphListener graphListener;

    private void trachViewChange(final GraphModel graphModel) {
        untrackViewChange(graphModel);
        if (model.getVisibleViewId() == -1) {
            model.setVisibleViewId(graphModel.getVisibleView().getViewId());
        }
        graphListener = new GraphListener() {

            public void graphChanged(GraphEvent event) {
                if (event.is(GraphEvent.EventType.VISIBLE_VIEW)) {
                    if (model.getVisibleViewId() != graphModel.getVisibleView().getViewId()) {
                        //View has been updated
                        model.setVisibleViewId(graphModel.getVisibleView().getViewId());
                        setSelectedPartition(null);
                    }
                }
            }
        };

        graphModel.addGraphListener(graphListener);
    }

    private void untrackViewChange(GraphModel graphModel) {
        if (graphListener != null && graphModel != null) {
            graphModel.removeGraphListener(graphListener);
        }
        graphListener = null;
    }

    public void attributesChanged(AttributeEvent event) {
        refreshPartitions = true;
    }

    public void setSelectedPartition(final Partition partition) {
        model.setWaiting(true);
        if (model.getSelectedPartitioning() == PartitionModel.NODE_PARTITIONING) {
            Thread t = new Thread(new Runnable() {

                public void run() {
                    if (partition != null) {
                        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                        PartitionFactory.buildNodePartition((NodePartition) partition, graphModel.getGraphVisible());
                    }
                    model.setNodePartition(partition);
                    if (model.getNodeTransformerBuilder() == null) {
                        //Select the first transformer
                        TransformerBuilder[] builders = Lookup.getDefault().lookupAll(TransformerBuilder.class).toArray(new TransformerBuilder[0]);
                        for (int i = 0; i < builders.length; i++) {
                            TransformerBuilder t = builders[i];
                            if (t instanceof TransformerBuilder.Node) {
                                model.setNodeBuilder(t);
                                break;
                            }
                        }
                    }
                    model.setWaiting(false);
                }
            }, "Partition Model refresh");
            t.start();
        } else {
            Thread t = new Thread(new Runnable() {

                public void run() {
                    if (partition != null) {
                        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                        PartitionFactory.buildEdgePartition((EdgePartition) partition, graphModel.getGraphVisible());
                    }
                    model.setEdgePartition(partition);
                    if (model.getEdgeTransformerBuilder() == null) {
                        //Select the first transformer
                        TransformerBuilder[] builders = Lookup.getDefault().lookupAll(TransformerBuilder.class).toArray(new TransformerBuilder[0]);
                        for (int i = 0; i < builders.length; i++) {
                            TransformerBuilder t = builders[i];
                            if (t instanceof TransformerBuilder.Edge) {
                                model.setEdgeBuilder(t);
                                break;
                            }
                        }
                    }
                    model.setWaiting(false);
                }
            }, "Partition Model refresh");
            t.start();
        }
    }

    public Partition buildPartition(AttributeColumn column, Graph graph) {
        if (AttributeUtils.getDefault().isNodeColumn(column)) {
            NodePartition partition = PartitionFactory.createNodePartition(column);
            PartitionFactory.buildNodePartition(partition, graph);
            return partition;
        } else {
            EdgePartition partition = PartitionFactory.createEdgePartition(column);
            PartitionFactory.buildEdgePartition(partition, graph);
            return partition;
        }
    }

    public void setSelectedPartitioning(final int partitioning) {
        model.setWaiting(true);

        Thread t = new Thread(new Runnable() {

            public void run() {
                model.setSelectedPartitioning(partitioning);
                model.setWaiting(false);
            }
        }, "Partition Model refresh");
        t.start();
    }

    public void setSelectedTransformerBuilder(final TransformerBuilder builder) {
        model.setWaiting(true);
        Thread t = new Thread(new Runnable() {

            public void run() {
                if (model.getSelectedPartitioning() == PartitionModel.NODE_PARTITIONING) {
                    model.setNodeBuilder(builder);
                } else {
                    model.setEdgeBuilder(builder);
                }
                model.setWaiting(false);
            }
        }, "Partition Model refresh");
        t.start();
    }

    public void refreshPartitions() {
        if (refreshPartitions) {
            refreshPartitions = false;
            AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
            GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

            //Nodes
            List<NodePartition> nodePartitions = new ArrayList<NodePartition>();
            AttributeTable nodeClass = ac.getModel().getNodeTable();
            for (AttributeColumn column : nodeClass.getColumns()) {
                if (PartitionFactory.isNodePartitionColumn(column, graphModel.getGraphVisible())) {
                    nodePartitions.add(PartitionFactory.createNodePartition(column));
                }
            }
            model.setNodePartitions(nodePartitions.toArray(new NodePartition[0]));

            //Edges
            List<EdgePartition> edgePartitions = new ArrayList<EdgePartition>();
            AttributeTable edgeClass = ac.getModel().getEdgeTable();
            for (AttributeColumn column : edgeClass.getColumns()) {
                if (PartitionFactory.isEdgePartitionColumn(column, graphModel.getGraphVisible())) {
                    edgePartitions.add(PartitionFactory.createEdgePartition(column));
                }
            }
            model.setEdgePartitions(edgePartitions.toArray(new EdgePartition[0]));
        }
    }

    public void transform(Partition partition, Transformer transformer) {
        if (transformer != null && partition != null) {
            transformer.transform(partition);
        }
    }

    public boolean isGroupable(Partition partition) {
        if (partition instanceof NodePartition) {
            if (partition.getPartsCount() > 0) {
                NodePartition nodePartition = (NodePartition) partition;
                Node n0 = nodePartition.getParts()[0].getObjects()[0];
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
                if (graph.getParent(n0) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUngroupable(Partition partition) {
        if (partition instanceof NodePartition) {
            if (partition.getPartsCount() > 0) {
                NodePartition nodePartition = (NodePartition) partition;
                Node n0 = nodePartition.getParts()[0].getObjects()[0];
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
                if (graph.getParent(n0) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void group(Partition partition) {
        NodePartition nodePartition = (NodePartition) partition;
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        for (Part<Node> p : nodePartition.getParts()) {
            Node[] nodes = p.getObjects();
            if (graph.getParent(nodes[0]) == null) {
                float centroidX = 0;
                float centroidY = 0;
                float sizes = 0;
                float r = 0;
                float g = 0;
                float b = 0;
                int len = 0;
                for (Node n : nodes) {
                    centroidX += n.getNodeData().x();
                    centroidY += n.getNodeData().y();
                    sizes += n.getNodeData().getSize() / 10f;
                    r += n.getNodeData().r();
                    g += n.getNodeData().g();
                    b += n.getNodeData().b();
                    len++;
                }
                Node metaNode = graph.groupNodes(nodes);
                metaNode.getNodeData().setX(centroidX / len);
                metaNode.getNodeData().setY(centroidY / len);
                metaNode.getNodeData().setLabel(p.getDisplayName());
                metaNode.getNodeData().setSize(sizes);
                metaNode.getNodeData().setColor(r / len, g / len, b / len);
            }
        }
    }

    public void ungroup(Partition partition) {
        NodePartition nodePartition = (NodePartition) partition;
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        HierarchicalGraph graph = graphModel.getHierarchicalGraphVisible();
        for (Part<Node> p : nodePartition.getParts()) {
            Node[] nodes = p.getObjects();
            Node metaNode = graph.getParent(nodes[0]);
            if (metaNode != null) {
                graph.ungroupNodes(metaNode);
            }
        }
    }

    public void showPie(boolean showPie) {
        model.setPie(showPie);
    }

    public PartitionModel getModel() {
        return model;
    }
}
