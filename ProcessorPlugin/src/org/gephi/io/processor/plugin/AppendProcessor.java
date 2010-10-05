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
package org.gephi.io.processor.plugin;

import java.util.HashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.properties.PropertiesColumn;
import org.gephi.dynamic.api.DynamicController;
import org.gephi.dynamic.api.DynamicModel.TimeFormat;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.EdgeDraft.EdgeType;
import org.gephi.io.importer.api.EdgeDraftGetter;
import org.gephi.io.importer.api.NodeDraftGetter;
import org.gephi.io.processor.spi.Processor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Processor 'Append graph' that tries to find in the current workspace nodes and
 * edges in the container to only append new elements. It uses elements' id and
 * label to do the matching.
 * <p>
 * The attibutes are not merged and values are from the latest element imported.
 * 
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Processor.class)
public class AppendProcessor extends AbstractProcessor implements Processor {

    public String getDisplayName() {
        return NbBundle.getMessage(AppendProcessor.class, "AppendProcessor.displayName");
    }

    public void process() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        //Workspace
        if (workspace == null) {
            workspace = pc.getCurrentWorkspace();
            if (workspace == null) {
                //Append mode but no workspace
                workspace = pc.newWorkspace(pc.getCurrentProject());
                pc.openWorkspace(workspace);
            }
        }
        if (container.getSource() != null) {
            pc.setSource(workspace, container.getSource());
        }

        //Architecture
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        HierarchicalGraph graph = null;
        switch (container.getEdgeDefault()) {
            case DIRECTED:
                graph = graphModel.getHierarchicalDirectedGraph();
                break;
            case UNDIRECTED:
                graph = graphModel.getHierarchicalUndirectedGraph();
                break;
            case MIXED:
                graph = graphModel.getHierarchicalMixedGraph();
                break;
            default:
                graph = graphModel.getHierarchicalMixedGraph();
                break;
        }
        GraphFactory factory = graphModel.factory();

        //Attributes - Creates columns for properties
        attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        attributeModel.mergeModel(container.getAttributeModel());

        //Dynamic
        if (container.getTimeFormat() != null) {
            DynamicController dynamicController = Lookup.getDefault().lookup(DynamicController.class);
            dynamicController.setTimeFormat(container.getTimeFormat());
        }

        //Index existing graph
        Map<String, Node> map = new HashMap<String, Node>();
        for (Node n : graph.getNodes()) {
            String id = n.getNodeData().getId();
            if (id != null && !id.equalsIgnoreCase(String.valueOf(n.getId()))) {
                map.put(id, n);
            }
            if (n.getNodeData().getLabel() != null && !n.getNodeData().getLabel().isEmpty()) {
                map.put(n.getNodeData().getLabel(), n);
            }
        }

        int nodeCount = 0;
        //Create all nodes
        for (NodeDraftGetter draftNode : container.getNodes()) {
            Node n;
            String id = draftNode.getId();
            String label = draftNode.getLabel();
            if (!draftNode.isAutoId() && id != null && map.get(id) != null) {
                n = map.get(id);
            } else if (label != null && map.get(label) != null) {
                n = map.get(label);
            } else {
                n = factory.newNode(draftNode.isAutoId()?null:id);
                nodeCount++;
            }
            flushToNode(draftNode, n);
            draftNode.setNode(n);
        }

        //Push nodes in data structure
        for (NodeDraftGetter draftNode : container.getNodes()) {
            Node n = draftNode.getNode();
            NodeDraftGetter[] parents = draftNode.getParents();
            if (parents != null) {
                for (int i = 0; i < parents.length; i++) {
                    Node parent = parents[i].getNode();
                    graph.addNode(n, parent);
                }
            } else {
                graph.addNode(n);
            }
        }

        //Create all edges and push to data structure
        int edgeCount = 0;
        for (EdgeDraftGetter edge : container.getEdges()) {
            Node source = edge.getSource().getNode();
            Node target = edge.getTarget().getNode();
            if (graph.getEdge(source, target) == null) {
                Edge e = null;
                switch (container.getEdgeDefault()) {
                    case DIRECTED:
                        e = factory.newEdge(edge.isAutoId()?null:edge.getId(), source, target, edge.getWeight(), true);
                        break;
                    case UNDIRECTED:
                        e = factory.newEdge(edge.isAutoId()?null:edge.getId(), source, target, edge.getWeight(), false);
                        break;
                    case MIXED:
                        e = factory.newEdge(edge.isAutoId()?null:edge.getId(), source, target, edge.getWeight(), edge.getType().equals(EdgeType.UNDIRECTED) ? false : true);
                        break;
                }

                flushToEdge(edge, e);
                edgeCount++;
                graph.addEdge(e);
            }
        }

        System.out.println("# New Nodes appended: " + nodeCount + "\n# New Edges appended: " + edgeCount);
        workspace = null;
    }
}
