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
package org.gephi.io.importer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeValueFactory;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.ContainerUnloader;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Issue;
import org.gephi.io.importer.api.Issue.Level;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.api.EdgeDraftGetter;
import org.gephi.io.importer.api.NodeDraftGetter;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ImportContainerImpl implements Container, ContainerLoader, ContainerUnloader {

    //MetaData
    private String source;
    //Factory
    private FactoryImpl factory;
    //Parameters
    private ImportContainerParameters parameters;
    //Maps
    private HashMap<String, NodeDraftImpl> nodeMap;
    private HashMap<String, EdgeDraftImpl> edgeMap;
    private HashMap<String, EdgeDraftImpl> edgeSourceTargetMap;
    //Attributes
    private AttributeModel attributeModel;
    //Management
    private boolean dynamicGraph = false;
    private boolean hierarchicalGraph = false;
    private Report report;
    //Counting
    private int directedEdgesCount = 0;
    private int undirectedEdgesCount = 0;
    //Dynamic
    private String timeIntervalMin;
    private String timeIntervalMax;

    public ImportContainerImpl() {
        parameters = new ImportContainerParameters();
        nodeMap = new LinkedHashMap<String, NodeDraftImpl>();//to maintain the order
        edgeMap = new LinkedHashMap<String, EdgeDraftImpl>();
        edgeSourceTargetMap = new HashMap<String, EdgeDraftImpl>();
        attributeModel = Lookup.getDefault().lookup(AttributeController.class).newModel();
        factory = new FactoryImpl();
    }

    public ContainerLoader getLoader() {
        return this;
    }

    public synchronized ContainerUnloader getUnloader() {
        return this;
    }

    public DraftFactory factory() {
        return factory;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void addNode(NodeDraft nodeDraft) {
        if (nodeDraft == null) {
            throw new NullPointerException();
        }
        NodeDraftImpl nodeDraftImpl = (NodeDraftImpl) nodeDraft;

        if (nodeMap.containsKey(nodeDraftImpl.getId())) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_nodeExist", nodeDraftImpl.getId());
            report.logIssue(new Issue(message, Level.WARNING));
            report.log("Duplicated node id=" + nodeDraftImpl.getId() + "label=" + nodeDraftImpl.getLabel() + " is ignored");
            return;
        }

        if (nodeDraftImpl.getSlices() != null) {
            dynamicGraph = true;
        }

        nodeMap.put(nodeDraftImpl.getId(), nodeDraftImpl);
    }

    public NodeDraftImpl getNode(String id) {
        if (id == null || id.isEmpty()) {
            throw new NullPointerException();
        }
        NodeDraftImpl node = nodeMap.get(id);
        if (node == null) {
            if (parameters.isAutoNode()) {
                //Creates the missing node
                node = factory.newNodeDraft();
                node.setId(id);
                addNode(node);
                report.logIssue(new Issue("Unknow Node id", Level.WARNING));
                report.log("Automatic node creation from id=" + id);
            } else {
                String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_UnknowNodeId", id);
                report.logIssue(new Issue(message, Level.SEVERE));
            }
        }
        return node;
    }

    public boolean nodeExists(String id) {
        if (id == null || id.isEmpty()) {
            throw new NullPointerException();
        }
        return nodeMap.containsKey(id);
    }

    public void addEdge(EdgeDraft edgeDraft) {
        if (edgeDraft == null) {
            throw new NullPointerException();
        }
        EdgeDraftImpl edgeDraftImpl = (EdgeDraftImpl) edgeDraft;
        if (edgeDraftImpl.getSource() == null) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_MissingNodeSource");
            report.logIssue(new Issue(message, Level.SEVERE));
            return;
        }
        if (edgeDraftImpl.getTarget() == null) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_MissingNodeTarget");
            report.logIssue(new Issue(message, Level.SEVERE));
            return;
        }

        //Self loop
        if (edgeDraftImpl.getSource() == edgeDraftImpl.getTarget() && !parameters.isSelfLoops()) {
            String message = NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_SelfLoop");
            report.logIssue(new Issue(message, Level.SEVERE));
            return;
        }

        if (edgeDraftImpl.getType() != null) {
            //Counting
            switch (edgeDraftImpl.getType()) {
                case DIRECTED:
                    directedEdgesCount++;
                    break;
                case UNDIRECTED:
                    undirectedEdgesCount++;
                    break;
                case MUTUAL:
                    directedEdgesCount += 2;
                    break;
            }

            //Test if the given type match with parameters
            switch (parameters.getEdgeDefault()) {
                case DIRECTED:
                    EdgeDraft.EdgeType type1 = edgeDraftImpl.getType();
                    if (type1.equals(EdgeDraft.EdgeType.UNDIRECTED)) {
                        report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Bad_Edge_Type"), Level.WARNING));
                    }
                    break;
                case UNDIRECTED:
                    EdgeDraft.EdgeType type2 = edgeDraftImpl.getType();
                    if (type2.equals(EdgeDraft.EdgeType.DIRECTED)) {
                        report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Bad_Edge_Type"), Level.WARNING));
                    }
                    break;
                case MIXED:
                    break;
            }
        }


        String id = edgeDraftImpl.getId();
        String sourceTargetId = edgeDraftImpl.getSource().getId() + "-" + edgeDraftImpl.getTarget().getId();
        if (edgeMap.containsKey(id) || edgeSourceTargetMap.containsKey(sourceTargetId)) {
            if (!parameters.isParallelEdges()) {
                report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_edgeExist"), Level.WARNING));
                return;
            } else {
                //Manage parallel edges
                report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Parallel_Edge", id), Level.INFO));
                return;
            }
        }

        edgeSourceTargetMap.put(sourceTargetId, edgeDraftImpl);
        edgeMap.put(id, edgeDraftImpl);

        //Mutual
        if (edgeDraftImpl.getType() != null && edgeDraftImpl.getType().equals(EdgeDraft.EdgeType.MUTUAL)) {
            id = edgeDraftImpl.getId() + "-mutual";
            sourceTargetId = edgeDraftImpl.getTarget().getId() + "-" + edgeDraftImpl.getSource().getId();
            if (edgeSourceTargetMap.containsKey(sourceTargetId)) {
                if (!parameters.isParallelEdges()) {
                    report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_edgeExist"), Level.WARNING));
                    return;
                } else {
                    //Manage parallel edges
                    report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Parallel_Edge", id), Level.INFO));
                    return;
                }
            }

            edgeSourceTargetMap.put(sourceTargetId, edgeDraftImpl);
            edgeMap.put(id, edgeDraftImpl);
        }
    }

    public void removeEdge(EdgeDraft edgeDraft) {
        if (edgeDraft == null) {
            throw new NullPointerException();
        }
        EdgeDraftImpl edgeDraftImpl = (EdgeDraftImpl) edgeDraft;
        String id = edgeDraftImpl.getId();
        String sourceTargetId = edgeDraftImpl.getSource().getId() + "-" + edgeDraftImpl.getTarget().getId();

        if (!edgeMap.containsKey(id) && !edgeSourceTargetMap.containsKey(sourceTargetId)) {
            return;
        }

        if (edgeDraftImpl.getType() != null) {
            //UnCounting
            switch (edgeDraftImpl.getType()) {
                case DIRECTED:
                    directedEdgesCount--;
                    break;
                case UNDIRECTED:
                    undirectedEdgesCount--;
                    break;
                case MUTUAL:
                    directedEdgesCount -= 2;
                    break;
            }
        }

        edgeSourceTargetMap.remove(sourceTargetId);
        edgeMap.remove(id);

        if (edgeDraftImpl.getType() != null && edgeDraftImpl.getType().equals(EdgeDraft.EdgeType.MUTUAL)) {
            id = edgeDraftImpl.getId() + "-mutual";
            sourceTargetId = edgeDraftImpl.getTarget().getId() + "-" + edgeDraftImpl.getSource().getId();
            edgeSourceTargetMap.remove(sourceTargetId);
            edgeMap.remove(id);
        }
    }

    public boolean edgeExists(String id) {
        if (id == null || id.isEmpty()) {
            throw new NullPointerException();
        }
        return edgeMap.containsKey(id);
    }

    public boolean edgeExists(NodeDraft source, NodeDraft target) {
        if (source == null || target == null) {
            throw new NullPointerException();
        }
        return edgeSourceTargetMap.containsKey(((NodeDraftImpl) source).getId() + "-" + ((NodeDraftImpl) target).getId());
    }

    public EdgeDraft getEdge(String id) {
        if (id == null || id.isEmpty()) {
            throw new NullPointerException();
        }
        return edgeMap.get(id);
    }

    public EdgeDraft getEdge(NodeDraft source, NodeDraft target) {
        if (source == null || target == null) {
            throw new NullPointerException();
        }
        return edgeSourceTargetMap.get(((NodeDraftImpl) source).getId() + "-" + ((NodeDraftImpl) target).getId());
    }

    public EdgeDraftGetter getEdge(NodeDraftGetter source, NodeDraftGetter target) {
        if (source == null || target == null) {
            throw new NullPointerException();
        }
        return edgeSourceTargetMap.get(((NodeDraftImpl) source).getId() + "-" + ((NodeDraftImpl) target).getId());
    }

    public Collection<? extends NodeDraftGetter> getNodes() {
        return nodeMap.values();
    }

    public Collection<? extends EdgeDraftGetter> getEdges() {
        return edgeMap.values();
    }

    public AttributeModel getAttributeModel() {
        return attributeModel;
    }

    public AttributeValueFactory getFactory() {
        return attributeModel.valueFactory();
    }

    public String getTimeIntervalMin() {
        return timeIntervalMin;
    }

    public String getTimeIntervalMax() {
        return timeIntervalMax;
    }

    public void setTimeIntervalMax(String timeIntervalMax) {
        this.timeIntervalMax = timeIntervalMax;
    }

    public void setTimeIntervalMin(String timeIntervalMin) {
        this.timeIntervalMin = timeIntervalMin;
    }

    public boolean verify() {

        //Edge weight 0
        for (EdgeDraftImpl edge : edgeMap.values().toArray(new EdgeDraftImpl[0])) {
            if (edge.getWeight() <= 0f) {
                String id = edge.getId();
                String sourceTargetId = edge.getSource().getId() + "-" + edge.getTarget().getId();
                if (parameters.isRemoveEdgeWithWeightZero()) {
                    edgeMap.remove(id);
                    edgeSourceTargetMap.remove(sourceTargetId);
                    report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Weight_Zero_Ignored", id), Level.SEVERE));
                } else {
                    report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Weight_Zero", id), Level.WARNING));
                }
            }
        }

        //Graph EdgeDefault
        if (directedEdgesCount > 0 && undirectedEdgesCount == 0) {
            parameters.setEdgeDefault(EdgeDefault.DIRECTED);
        } else if (directedEdgesCount == 0 && undirectedEdgesCount > 0) {
            parameters.setEdgeDefault(EdgeDefault.UNDIRECTED);
        } else if (directedEdgesCount > 0 && undirectedEdgesCount > 0) {
            parameters.setEdgeDefault(EdgeDefault.MIXED);
        }
        return true;
    }

    public void closeLoader() {
        //Clean undirected edges
        if (parameters.getEdgeDefault().equals(EdgeDefault.UNDIRECTED)) {
            for (Iterator<EdgeDraftImpl> itr = edgeMap.values().iterator(); itr.hasNext();) {
                EdgeDraftImpl edge = itr.next();
                String oppositekey = edge.getTarget().getId() + "-" + edge.getSource().getId();
                EdgeDraftImpl opposite = edgeSourceTargetMap.get(oppositekey);
                if (opposite != null) {
                    itr.remove();
                    edgeSourceTargetMap.remove(edge.getSource().getId() + "-" + edge.getTarget().getId());
                }
            }
        } else if (parameters.getEdgeDefault().equals(EdgeDefault.MIXED)) {
            //Clean undirected edges when graph is mixed
            for (EdgeDraftImpl edge : edgeMap.values().toArray(new EdgeDraftImpl[0])) {
                if (edge.getType() == null) {
                    edge.setType(EdgeDraft.EdgeType.UNDIRECTED);
                }
                if (edge.getType().equals(EdgeDraft.EdgeType.UNDIRECTED)) {
                    String oppositekey = edge.getTarget().getId() + "-" + edge.getSource().getId();
                    EdgeDraftImpl opposite = edgeSourceTargetMap.get(oppositekey);
                    if (opposite != null) {
                        edgeMap.remove(opposite.getId());
                        edgeSourceTargetMap.remove(oppositekey);
                    }
                }
            }
        }

        //Sort nodes by height
        LinkedHashMap<String, NodeDraftImpl> sortedNodeMap = new LinkedHashMap<String, NodeDraftImpl>();
        ArrayList<NodeDraftImpl> sortedMapValues = new ArrayList<NodeDraftImpl>(nodeMap.values());
        Collections.sort(sortedMapValues, new Comparator<NodeDraftImpl>() {

            public int compare(NodeDraftImpl o1, NodeDraftImpl o2) {
                return new Integer(o2.getHeight()).compareTo(o1.getHeight());
            }
        });
        for (NodeDraftImpl n : sortedMapValues) {
            sortedNodeMap.put(n.getId(), n);
        }
        nodeMap = sortedNodeMap;

        //Set id as label for nodes that miss label
        for (NodeDraftImpl node : nodeMap.values()) {
            if (node.getLabel() == null) {
                node.setLabel(node.getId());
            }
        }
    }

    /**
     * Factory for draft objects
     */
    public class FactoryImpl implements DraftFactory {

        private int nodeIDgen = 0;
        private int edgeIDgen = 0;

        public NodeDraftImpl newNodeDraft() {
            NodeDraftImpl node = new NodeDraftImpl(ImportContainerImpl.this, source);
            node.setId("n" + nodeIDgen);
            nodeIDgen++;
            return node;
        }

        public EdgeDraftImpl newEdgeDraft() {
            EdgeDraftImpl edge = new EdgeDraftImpl(ImportContainerImpl.this, source);
            edge.setId("e" + edgeIDgen);
            edgeIDgen++;
            return edge;
        }
    }

    //MANAGEMENT
    public boolean isDynamicGraph() {
        return dynamicGraph;
    }

    public boolean isHierarchicalGraph() {
        return hierarchicalGraph;
    }

    public void setDynamicGraph(boolean dynamicGraph) {
        this.dynamicGraph = dynamicGraph;
    }

    public void setHierarchicalGraph(boolean hierarchicalGraph) {
        this.hierarchicalGraph = hierarchicalGraph;
    }

    //REPORT
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    //PARAMETERS
    public void setAllowAutoNode(boolean value) {
        parameters.setAutoNode(value);
    }

    public void setAllowParallelEdge(boolean value) {
        parameters.setParallelEdges(value);
    }

    public void setAllowSelfLoop(boolean value) {
        parameters.setSelfLoops(value);
    }

    public void setEdgeDefault(EdgeDefault edgeDefault) {
        parameters.setEdgeDefault(edgeDefault);
        report.logIssue(new Issue(NbBundle.getMessage(ImportContainerImpl.class, "ImportContainerException_Set_EdgeDefault", edgeDefault.toString()), Level.INFO));
    }

    public boolean allowAutoNode() {
        return parameters.isAutoNode();
    }

    public boolean allowParallelEdges() {
        return parameters.isParallelEdges();
    }

    public boolean allowSelfLoop() {
        return parameters.isSelfLoops();
    }

    public EdgeDefault getEdgeDefault() {
        return parameters.getEdgeDefault();
    }

    public boolean isAutoScale() {
        return parameters.isAutoScale();
    }

    public void setAutoScale(boolean autoscale) {
        parameters.setAutoScale(autoscale);
    }
}
