/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.plugins.exportjs;

import org.json.JSONStringer;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.project.api.Workspace;

/**
 *
 * @author raph
 */
public class ExportJsGraph {
    private JSONStringer JsWrite;
    private boolean exportVisible = false;
    
    public void BuildGraph(Workspace workspace) throws Exception {
        
        GraphModel graphModel = workspace.getLookup().lookup(GraphModel.class);
        Graph graph = exportVisible ? graphModel.getGraph() : graphModel.getGraphVisible();
        
        JsWrite = new JSONStringer();
        JsWrite.object()
                .key("edge_type")
                .value(graph instanceof DirectedGraph ? "directed" : graph instanceof UndirectedGraph ? "undirected" : "mixed")
                .key("nodes")
                .array();

        NodeIterable nodeIterable = graph.getNodes();
        for (Node node : nodeIterable) {
            
            JsWrite.object()
                    .key("id")
                    .value(node.getNodeData().getId())
                    .key("label")
                    .value((node.getNodeData().getLabel() != null && !node.getNodeData().getLabel().isEmpty() ) ? node.getNodeData().getLabel() : node.getNodeData().getId());
            
            float x = node.getNodeData().x();
            float y = node.getNodeData().y();
            float z = node.getNodeData().z();
            if (x != 0 || y != 0 || z != 0) {
                JsWrite.key("position")
                        .object()
                        .key("x")
                        .value(x)
                        .key("y")
                        .value(y);
                if (z != 0) {
                    JsWrite.key("z")
                            .value(z);
                }
                JsWrite.endObject();
            }
            JsWrite.key("size")
                    .value(node.getNodeData().getSize())
                    .key("color")
                    .object()
                        .key("r")
                        .value(Math.round(node.getNodeData().r() * 255f))
                        .key("g")
                        .value(Math.round(node.getNodeData().g() * 255f))
                        .key("b")
                        .value(Math.round(node.getNodeData().b() * 255f))
                    .endObject();
           
            if (node.getNodeData().getAttributes() != null) {
                JsWrite.key("attributes")
                        .object();
                AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
                for (AttributeValue val : row.getValues()) {
                    AttributeColumn col = val.getColumn();
                    if (!col.getOrigin().equals(AttributeOrigin.PROPERTY) && val.getValue() != null) {
                        JsWrite.key(col.getId())
                                .value(val.getValue().toString());
                    }
                }
                JsWrite.endObject();
            }
            JsWrite.endObject();
            
        }
        JsWrite.endArray()
                .key("edges")
                .array();
        
        EdgeIterable edgeIterable = graph.getEdges();
        
        for (Edge edge : edgeIterable) {
            
            JsWrite.object();

            if (edge.getEdgeData().getId() != null && !edge.getEdgeData().getId().equals(Integer.toString(edge.getId()))) {
                JsWrite.key("id").value(edge.getEdgeData().getId());
            }
 
            if (edge.getEdgeData().getLabel() != null && !edge.getEdgeData().getLabel().isEmpty()) {
                JsWrite.key("label").value(edge.getEdgeData().getLabel());
            }
            
            JsWrite.key("source").value(edge.getSource().getNodeData().getId());
            JsWrite.key("target").value(edge.getTarget().getNodeData().getId());

            if (edge.isDirected() && graphModel.isMixed()) {
                JsWrite.key("edge_type").value("directed");
            } else if (!edge.isDirected() && graphModel.isMixed()) {
                JsWrite.key("edge_type").value("undirected");
            }
            
            JsWrite.key("weight")
                    .value(edge.getWeight());
            
            if (edge.getEdgeData().r() != -1) {
                JsWrite.key("color")
                    .object()
                        .key("r")
                        .value(Math.round(edge.getEdgeData().r() * 255f))
                        .key("g")
                        .value(Math.round(edge.getEdgeData().g() * 255f))
                        .key("b")
                        .value(Math.round(edge.getEdgeData().b() * 255f))
                    .endObject();
            }
            
            if (edge.getEdgeData().getAttributes() != null) {
                JsWrite.key("attributes")
                        .object();
                AttributeRow row = (AttributeRow) edge.getEdgeData().getAttributes();
                for (AttributeValue val : row.getValues()) {
                    AttributeColumn col = val.getColumn();
                    if (!col.getOrigin().equals(AttributeOrigin.PROPERTY) && val.getValue() != null) {
                        JsWrite.key(col.getId())
                                .value(val.getValue().toString());
                    }
                }
                
                JsWrite.endObject();
            }
            JsWrite.endObject();
            
        }
        JsWrite.endArray()
                .endObject();
        
    }
    
    @Override
    public String toString() {
        try {
           return JsWrite.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setExportVisible(boolean exportVisible) {
        this.exportVisible = exportVisible;
    }
}
