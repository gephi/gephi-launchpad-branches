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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.NodeDraftGetter;

/**
 *
 * @author Mathieu Bastian
 */
public class NodeDraftImpl implements NodeDraft, NodeDraftGetter {

    //Architecture
    private ImportContainerImpl container;
    //Flag
    private boolean autoId;
    //Basic
    private String id;
    private String label;
    private NodeDraftImpl[] parents;
    //Viz attributes
    private Color color;
    private float size;
    private float x;
    private float y;
    private float z;
    private boolean visible = true;
    private boolean fixed = false;
    //Text
    private float labelSize = -1f;
    private boolean labelVisible = true;
    private Color labelColor;
    //Dynamic
    private List<String[]> slices;
    //Attributes
    private List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
    //Result
    private Node node;
    private int height;

    public NodeDraftImpl(ImportContainerImpl container, String id) {
        this.container = container;
        this.id = id;
        this.autoId = true;
    }

    //SETTERS
    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String r, String g, String b) {
        setColor(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
    }

    public void setColor(float r, float g, float b) {
        r = Math.max(Math.min(r, 1f), 0f);
        g = Math.max(Math.min(g, 1f), 0f);
        b = Math.max(Math.min(b, 1f), 0f);
        setColor(new Color(r, g, b));
    }

    public void setColor(int r, int g, int b) {
        setColor(r / 255f, g / 255f, b / 255f);
    }

    public void setColor(String color) {
        setColor(Color.getColor(color));
    }

    public void setId(String id) {
        this.id = id;
        this.autoId = false;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabelSize(float size) {
        this.labelSize = size;
    }

    public void setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
    }

    public void setLabelColor(Color color) {
        this.labelColor = color;
    }

    public void setLabelColor(String r, String g, String b) {
        setColor(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
    }

    public void setLabelColor(float r, float g, float b) {
        r = Math.max(Math.min(r, 1f), 0f);
        g = Math.max(Math.min(g, 1f), 0f);
        b = Math.max(Math.min(b, 1f), 0f);
        setColor(new Color(r, g, b));
    }

    public void setLabelColor(int r, int g, int b) {
        setColor(r / 255f, g / 255f, b / 255f);
    }

    public void setLabelColor(String color) {
        setColor(Color.getColor(color));
    }

    public void setParent(NodeDraft draft) {
        NodeDraftImpl draftImpl = (NodeDraftImpl) draft;
        if (this.parents == null) {
            this.parents = new NodeDraftImpl[1];
            this.parents[0] = draftImpl;
        } else {
            this.parents = Arrays.copyOf(this.parents, this.parents.length + 1);
            this.parents[this.parents.length - 1] = draftImpl;
        }
        draftImpl.setHeight(height + 1);
        container.setHierarchicalGraph(true);
    }

    public void setHeight(int height) {
        if (height > this.height) {
            this.height = height;
            if (parents != null) {
                for (NodeDraftGetter p : parents) {
                    ((NodeDraftImpl) p).setHeight(height + 1);
                }
            }
        }
    }

    public void addChild(NodeDraft child) {
        child.setParent(this);
    }

    public void addAttributeValue(AttributeColumn column, Object value) {
        AttributeValue attValue = container.getFactory().newValue(column, value);
        attributeValues.add(attValue);
    }

    public void addTimeSlice(String dateFrom, String dateTo) {
        if (slices == null) {
            slices = new ArrayList<String[]>();
        }
        slices.add(new String[]{dateFrom, dateTo});
    }

    //GETTERS
    public List<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public Color getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public float getSize() {
        return size;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public boolean isFixed() {
        return fixed;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getLabel() {
        return label;
    }

    public float getLabelSize() {
        return labelSize;
    }

    public boolean isLabelVisible() {
        return labelVisible;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public int getHeight() {
        return height;
    }

    public NodeDraftGetter[] getParents() {
        return parents;
    }

    public List<String[]> getSlices() {
        return slices;
    }

    @Override
    public String toString() {
        String res = "node";
        if (!autoId) {
            res += " id=" + id;
        } else if (label != null) {
            res += " label=" + label;
        } else {
            res += id;
        }
        return res;
    }

    //RESULT
    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
