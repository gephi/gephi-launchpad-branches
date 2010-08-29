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
package org.gephi.graph.dhns.utils;

import org.gephi.graph.api.EdgeData;
import org.gephi.graph.api.NodeData;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.GraphStructure;
import org.gephi.graph.dhns.core.TreeStructure;
import org.gephi.graph.dhns.edge.AbstractEdge;
import org.gephi.graph.dhns.edge.EdgeDataImpl;
import org.gephi.graph.dhns.edge.iterators.EdgeIterator;
import org.gephi.graph.dhns.node.AbstractNode;
import org.gephi.graph.dhns.node.NodeDataImpl;
import org.gephi.graph.dhns.node.iterators.TreeListIterator;
import org.gephi.graph.dhns.predicate.Tautology;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mathieu Bastian
 */
public class DataSerializer {

    private static final String ELEMENT_DATA = "Data";
    private static final String ELEMENT_NODEDATA = "nodedata";
    private static final String ELEMENT_NODEDATA_POSITION = "position";
    private static final String ELEMENT_NODEDATA_COLOR = "color";
    private static final String ELEMENT_NODEDATA_SIZE = "size";
    private static final String ELEMENT_EDGEDATA = "edgedata";
    private static final String ELEMENT_EDGEDATA_COLOR = "edgedata";

    public Element writeData(Document document, Dhns dhns) {
        Element dataE = document.createElement(ELEMENT_DATA);

        TreeStructure treeStructure = dhns.getGraphStructure().getMainView().getStructure();
        TreeListIterator itr = new TreeListIterator(treeStructure.getTree(), 1);
        for (; itr.hasNext();) {
            AbstractNode absNode = itr.next();
            NodeDataImpl nodeData = absNode.getNodeData();
            if (nodeData != null) {
                Element nodeDataE = writeNodeData(document, nodeData);
                dataE.appendChild(nodeDataE);
            }
        }

        EdgeIterator edgeIterator = new EdgeIterator(treeStructure, new TreeListIterator(treeStructure.getTree(), 1), false, Tautology.instance, Tautology.instance);
        for (; edgeIterator.hasNext();) {
            EdgeDataImpl edgeData = edgeIterator.next().getEdgeData();
            if (edgeData != null) {
                Element edgeDataE = writeEdgeData(document, edgeData);
                dataE.appendChild(edgeDataE);
            }
        }

        return dataE;
    }

    public void readData(Element dataE, Dhns dhns) {

        GraphStructure structure = dhns.getGraphStructure();
        TreeStructure treeStructure = structure.getMainView().getStructure();
        NodeList dataListE = dataE.getChildNodes();
        for (int i = 0; i < dataListE.getLength(); i++) {
            if (dataListE.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element itemE = (Element) dataListE.item(i);
                if (itemE.getTagName().equals(ELEMENT_NODEDATA)) {
                    AbstractNode node = treeStructure.getNodeAt(Integer.parseInt(itemE.getAttribute("nodepre")));
                    NodeDataImpl nodeDataImpl = (NodeDataImpl) node.getNodeData();
                    readNodeData(itemE, nodeDataImpl, structure);
                } else if (itemE.getTagName().equals(ELEMENT_EDGEDATA)) {
                    AbstractNode source = treeStructure.getNodeAt(Integer.parseInt(itemE.getAttribute("sourcepre")));
                    AbstractNode target = treeStructure.getNodeAt(Integer.parseInt(itemE.getAttribute("targetpre")));
                    AbstractEdge edge = source.getEdgesOutTree().getItem(target.getId());
                    EdgeDataImpl edgeDataImpl = (EdgeDataImpl) edge.getEdgeData();
                    readEdgeData(itemE, edgeDataImpl, structure);
                }
            }
        }

    }

    public Element writeNodeData(Document document, NodeDataImpl nodeData) {
        Element nodeDataE = document.createElement(ELEMENT_NODEDATA);
        AbstractNode node = nodeData.getRootNode();

        nodeDataE.setAttribute("nodepre", String.valueOf(node.getPre()));

        if (nodeData.getId() != null && !nodeData.getId().equals("" + node.getId())) {
            nodeDataE.setAttribute("id", nodeData.getId());
        }

        Element positionE = document.createElement(ELEMENT_NODEDATA_POSITION);
        positionE.setAttribute("x", String.valueOf(nodeData.x()));
        positionE.setAttribute("y", String.valueOf(nodeData.y()));
        positionE.setAttribute("z", String.valueOf(nodeData.z()));
        nodeDataE.appendChild(positionE);

        Element colorE = document.createElement(ELEMENT_NODEDATA_COLOR);
        colorE.setAttribute("r", String.valueOf(nodeData.r()));
        colorE.setAttribute("g", String.valueOf(nodeData.g()));
        colorE.setAttribute("b", String.valueOf(nodeData.b()));
        colorE.setAttribute("a", String.valueOf(nodeData.alpha()));
        nodeDataE.appendChild(colorE);

        Element sizeE = document.createElement(ELEMENT_NODEDATA_SIZE);
        sizeE.setAttribute("value", String.valueOf(nodeData.getSize()));
        nodeDataE.appendChild(sizeE);

        return nodeDataE;
    }

    public void readNodeData(Element nodeDataE, NodeData nodeData, GraphStructure structure) {
        if (nodeDataE.hasAttribute("id")) {
            structure.getNodeIDDictionnary().put(nodeDataE.getAttribute("id"), nodeData);
        }

        NodeList dataE = nodeDataE.getChildNodes();
        for (int i = 0; i < dataE.getLength(); i++) {
            if (dataE.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element itemE = (Element) dataE.item(i);
                if (itemE.getTagName().equals(ELEMENT_NODEDATA_POSITION)) {
                    nodeData.setX(Float.parseFloat(itemE.getAttribute("x")));
                    nodeData.setY(Float.parseFloat(itemE.getAttribute("y")));
                    nodeData.setZ(Float.parseFloat(itemE.getAttribute("z")));
                } else if (itemE.getTagName().equals(ELEMENT_NODEDATA_COLOR)) {
                    nodeData.setR(Float.parseFloat(itemE.getAttribute("r")));
                    nodeData.setG(Float.parseFloat(itemE.getAttribute("g")));
                    nodeData.setB(Float.parseFloat(itemE.getAttribute("b")));
                    nodeData.setAlpha(Float.parseFloat(itemE.getAttribute("a")));
                } else if (itemE.getTagName().equals(ELEMENT_NODEDATA_SIZE)) {
                    nodeData.setSize(Float.parseFloat(itemE.getAttribute("value")));
                }
            }
        }
    }

    public Element writeEdgeData(Document document, EdgeDataImpl edgeData) {
        Element edgeDataE = document.createElement(ELEMENT_EDGEDATA);
        AbstractEdge edge = edgeData.getEdge();

        edgeDataE.setAttribute("sourcepre", String.valueOf(edge.getSource().getPre()));
        edgeDataE.setAttribute("targetpre", String.valueOf(edge.getTarget().getPre()));

        if (edgeData.getId() != null && !edgeData.getId().equals("" + edge.getId())) {
            edgeDataE.setAttribute("id", edgeData.getId());
        }

        Element colorE = document.createElement(ELEMENT_NODEDATA_COLOR);
        colorE.setAttribute("r", String.valueOf(edgeData.r()));
        colorE.setAttribute("g", String.valueOf(edgeData.g()));
        colorE.setAttribute("b", String.valueOf(edgeData.b()));
        colorE.setAttribute("a", String.valueOf(edgeData.alpha()));
        edgeDataE.appendChild(colorE);

        return edgeDataE;
    }

    public void readEdgeData(Element edgeDataE, EdgeData edgeData, GraphStructure structure) {
        if (edgeDataE.hasAttribute("id")) {
            structure.getEdgeIDDIctionnary().put(edgeDataE.getAttribute("id"), edgeData.getEdge());
        }

        NodeList dataE = edgeDataE.getChildNodes();
        for (int i = 0; i < dataE.getLength(); i++) {
            if (dataE.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element itemE = (Element) dataE.item(i);
                if (itemE.getTagName().equals(ELEMENT_EDGEDATA_COLOR)) {
                    edgeData.setR(Float.parseFloat(itemE.getAttribute("r")));
                    edgeData.setG(Float.parseFloat(itemE.getAttribute("g")));
                    edgeData.setB(Float.parseFloat(itemE.getAttribute("b")));
                    edgeData.setAlpha(Float.parseFloat(itemE.getAttribute("a")));
                }
            }
        }
    }
}
