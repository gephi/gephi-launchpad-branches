/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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
package org.gephi.visualization.vizmodel;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.graph.api.NodeShape;
import org.gephi.math.linalg.Vec2;
import org.gephi.math.linalg.Vec3;
import org.gephi.project.api.Workspace;
import org.gephi.ui.utils.ColorUtils;
import org.gephi.visualization.api.Camera;
import org.gephi.visualization.api.rendering.background.Background;
import org.gephi.visualization.api.vizmodel.GraphLimits;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.TextModel;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;

/**
 * Model class for visualization. Contains most visualization related settings.
 *
 * @author Mathieu Bastian
 */
public class VizModelImpl implements VizModel {

    protected final VizConfigImpl config;
    protected final TextModel textModel;
    
    Map<String, Object> modelData;
    
    //Listener
    protected List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
    private boolean defaultModel = false;

    public VizModelImpl(boolean defaultModel) {
        this.defaultModel = defaultModel;
        this.modelData = new ConcurrentHashMap<String, Object>();
        this.config = new VizConfigImpl(this);
        this.textModel = new TextModelImpl(this);
    }

    public VizModelImpl() {
        this(false);
    }
    
    @Override
    public void init() {
        final PropertyChangeEvent evt = new PropertyChangeEvent(this, "init", null, null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (PropertyChangeListener l : listeners) {
                    l.propertyChange(evt);
                }
            }
        });
    }

    public Map<String, Object> getModelData() {
        return modelData;
    }

    @Override
    public boolean isDefaultModel() {
        return defaultModel;
    }

    @Override
    public TextModel getTextModel() {
        return textModel;
    }
        
    @Override
    public VizConfig getConfig() {
        return config;
    }
    
    //GETTERS
    @Override
    public boolean isAdjustByText() {
        return config.getBooleanProperty(VizConfig.ADJUST_BY_TEXT);
    }

    @Override
    public boolean isAutoSelectNeighbor() {
        return config.getBooleanProperty(VizConfig.AUTO_SELECT_NEIGHBOUR);
    }

    @Override
    public Background getBackground() {
        return config.getProperty(Background.class, VizConfig.BACKGROUND);
    }

    @Override
    public Camera getCamera() {
        return config.getProperty(Camera.class, VizConfig.CAMERA);
    }

    @Override
    public boolean isShowEdges() {
        return config.getBooleanProperty(VizConfig.SHOW_EDGES);
    }

    @Override
    public boolean isEdgeHasUniColor() {
        return config.getBooleanProperty(VizConfig.EDGE_HAS_UNIQUE_COLOR);
    }

    @Override
    public Color getEdgeUniColor() {
        return config.getColorProperty(VizConfig.EDGE_UNIQUE_COLOR);
    }

    @Override
    public GraphLimits getGraphLimits() {
        return config.getProperty(GraphLimits.class, VizConfig.GRAPH_LIMITS);
    }

    @Override
    public boolean isHideNonSelectedEdges() {
        return config.getBooleanProperty(VizConfig.HIDE_NONSELECTED_EDGES);
    }

    @Override
    public boolean isHighlightNonSelectedEnabled() {
        return config.getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED_ENABLED);
    }

    @Override
    public boolean isRotatingEnable() {
        return config.getBooleanProperty(VizConfig.ROTATING);
    }

    @Override
    public boolean isNodeSelectedUniqueColor() {
        return config.getBooleanProperty(VizConfig.SELECTEDNODE_UNIQUE_COLOR);
    }

    @Override
    public boolean isUse3d() {
        return config.getBooleanProperty(VizConfig.CAMERA_USE_3D);
    }

    @Override
    public boolean isEdgeSelectionColor() {
        return config.getBooleanProperty(VizConfig.SELECTEDNODE_UNIQUE_COLOR);
    }
    
    @Override
    public int getAntiAliasing() {
        return config.getIntProperty(VizConfig.ANTIALIASING);
    }

    @Override
    public Color getEdgeInSelectionColor() {
        return config.getColorProperty(VizConfig.SELECTEDEDGE_IN_COLOR);
    }

    @Override
    public Color getEdgeOutSelectionColor() {
        return config.getColorProperty(VizConfig.SELECTEDEDGE_OUT_COLOR);
    }

    @Override
    public Color getEdgeBothSelectionColor() {
        return config.getColorProperty(VizConfig.SELECTEDEDGE_BOTH_COLOR);
    }

    @Override
    public boolean isShowHulls() {
        return config.getBooleanProperty(VizConfig.SHOW_HULLS);
    }

    @Override
    public float getEdgeScale() {
        return config.getFloatProperty(VizConfig.EDGE_SCALE);
    }

    @Override
    public float getMetaEdgeScale() {
        return config.getFloatProperty(VizConfig.META_EDGE_SCALE);
    }

    @Override
    public NodeShape getGlobalNodeShape() {
        return config.getProperty(NodeShape.class, VizConfig.NODE_GLOBAL_SHAPE);
    }

    //SETTERS
    @Override
    public void setAntiAliasing(int antiAliasing) {
        config.setProperty(VizConfig.ANTIALIASING, antiAliasing);
        // no need to fire an event
    }
    
    @Override
    public void setAdjustByText(boolean adjustByText) {
        config.setProperty(VizConfig.ADJUST_BY_TEXT, adjustByText);
        fireProperyChange(VizConfig.ADJUST_BY_TEXT, null, adjustByText);
    }

    @Override
    public void setAutoSelectNeighbor(boolean autoSelectNeighbor) {
        config.setProperty(VizConfig.AUTO_SELECT_NEIGHBOUR, autoSelectNeighbor);
        fireProperyChange(VizConfig.AUTO_SELECT_NEIGHBOUR, null, autoSelectNeighbor);
    }

    @Override
    public void setBackground(Background background) {
        config.setProperty(VizConfig.BACKGROUND, background);
        fireProperyChange(VizConfig.BACKGROUND, null, background);
    }

    @Override
    public void setCamera(Camera camera) {
        config.setProperty(VizConfig.CAMERA, camera);
        fireProperyChange(VizConfig.CAMERA, null, camera);
    }

    @Override
    public void setShowEdges(boolean showEdges) {
        config.setProperty(VizConfig.SHOW_EDGES, showEdges);
        fireProperyChange(VizConfig.SHOW_EDGES, null, showEdges);
    }

    @Override
    public void setEdgeHasUniColor(boolean edgeHasUniColor) {
        config.setProperty(VizConfig.EDGE_HAS_UNIQUE_COLOR, edgeHasUniColor);
        fireProperyChange(VizConfig.EDGE_HAS_UNIQUE_COLOR, null, edgeHasUniColor);
    }

    @Override
    public void setEdgeUniColor(Color edgeUniColor) {
        config.setProperty(VizConfig.EDGE_UNIQUE_COLOR, edgeUniColor);
        fireProperyChange(VizConfig.EDGE_UNIQUE_COLOR, null, edgeUniColor);
    }

    @Override
    public void setHideNonSelectedEdges(boolean hideNonSelectedEdges) {
        config.setProperty(VizConfig.HIDE_NONSELECTED_EDGES, hideNonSelectedEdges);
        fireProperyChange(VizConfig.HIDE_NONSELECTED_EDGES, null, hideNonSelectedEdges);
    }

    @Override
    public void setHighlightNonSelectedEnabled(boolean lightenNonSelectedAuto) {
        config.setProperty(VizConfig.HIGHLIGHT_NON_SELECTED, lightenNonSelectedAuto);
        fireProperyChange(VizConfig.HIGHLIGHT_NON_SELECTED, null, lightenNonSelectedAuto);
    }

    @Override
    public void setNodeSelectedUniqueColor(boolean uniColorSelected) {
        config.setProperty(VizConfig.NODE_SELECTED_UNIQUE_COLOR, uniColorSelected);
        fireProperyChange(VizConfig.NODE_SELECTED_UNIQUE_COLOR, null, uniColorSelected);
    }

    @Override
    public void setUse3d(boolean use3d) {
        config.setProperty(VizConfig.CAMERA_USE_3D, use3d);
        fireProperyChange(VizConfig.CAMERA_USE_3D, null, use3d);
    }

    @Override
    public void setEdgeSelectionColor(boolean edgeSelectionColor) {
        config.setProperty(VizConfig.SELECTEDEDGE_HAS_COLOR, edgeSelectionColor);
        fireProperyChange(VizConfig.SELECTEDEDGE_HAS_COLOR, null, edgeSelectionColor);
    }

    @Override
    public void setEdgeInSelectionColor(Color edgeInSelectionColor) {
        config.setProperty(VizConfig.SELECTEDEDGE_IN_COLOR, edgeInSelectionColor);
        fireProperyChange(VizConfig.SELECTEDEDGE_IN_COLOR, null, edgeInSelectionColor);
    }

    @Override
    public void setEdgeOutSelectionColor(Color edgeOutSelectionColor) {
        config.setProperty(VizConfig.SELECTEDEDGE_OUT_COLOR, edgeOutSelectionColor);
        fireProperyChange(VizConfig.SELECTEDEDGE_OUT_COLOR, null, edgeOutSelectionColor);
    }

    @Override
    public void setEdgeBothSelectionColor(Color edgeBothSelectionColor) {
        config.setProperty(VizConfig.SELECTEDEDGE_BOTH_COLOR, edgeBothSelectionColor);
        fireProperyChange(VizConfig.SELECTEDEDGE_BOTH_COLOR, null, edgeBothSelectionColor);
    }

    @Override
    public void setShowHulls(boolean showHulls) {
        config.setProperty(VizConfig.SHOW_HULLS, showHulls);
        fireProperyChange(VizConfig.SHOW_HULLS, null, showHulls);
    }

    @Override
    public void setEdgeScale(float edgeScale) {
        config.setProperty(VizConfig.EDGE_SCALE, edgeScale);
        fireProperyChange(VizConfig.EDGE_SCALE, null, edgeScale);
    }

    @Override
    public void setMetaEdgeScale(float metaEdgeScale) {
        config.setProperty(VizConfig.META_EDGE_SCALE, metaEdgeScale);
        fireProperyChange(VizConfig.META_EDGE_SCALE, null, metaEdgeScale);
    }

    @Override
    public void setGlobalNodeShape(NodeShape nodeShape) {
        config.setProperty(VizConfig.NODE_GLOBAL_SHAPE, nodeShape);
        fireProperyChange(VizConfig.NODE_GLOBAL_SHAPE, null, nodeShape);
    }

    @Override
    public void setGraphLimits(GraphLimits graphLimits) {
        config.setProperty(VizConfig.GRAPH_LIMITS, graphLimits);
        fireProperyChange(VizConfig.GRAPH_LIMITS, null, graphLimits);
    }
    
    @Override
    public boolean isReduceFPSWhenMouseOut() {
        return config.getBooleanProperty(VizConfig.REDUCE_FPS_MOUSE_OUT);
    }

    @Override
    public int getReduceFPSWhenMouseOutValue() {
        return config.getIntProperty(VizConfig.REDUCE_FPS_MOUSE_OUT_VALUE);
    }

    @Override
    public void setReduceFPSWhenMouseOut(boolean reduceFPS) {
        config.setProperty(VizConfig.REDUCE_FPS_MOUSE_OUT, reduceFPS);
        // no need to fire an event
    }

    @Override
    public void setReduceFPSWhenMouseOutValue(int fps) {
        config.setProperty(VizConfig.REDUCE_FPS_MOUSE_OUT_VALUE, fps);
        // no need to fire an event
    }
    
    @Override
    public int getNormalFPS() {
        return config.getIntProperty(VizConfig.NORMAL_FPS);
    }

    @Override
    public void setNormalFPS(int fps) {
        config.setProperty(VizConfig.NORMAL_FPS, fps);
        fireProperyChange(VizConfig.NORMAL_FPS, null, fps);
    }
    
    @Override
    public boolean isShowFPS() {
        return config.getBooleanProperty(VizConfig.SHOW_FPS);
    }

    @Override
    public void setShowFPS(boolean showFPS) {
        config.setProperty(VizConfig.SHOW_FPS, showFPS);
    }

    //EVENTS
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public List<PropertyChangeListener> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(List<PropertyChangeListener> listeners) {
        this.listeners = listeners;
    }
    
    @Override
    public void fireProperyChange(String propertyName, Object oldvalue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldvalue, newValue);
        for (PropertyChangeListener l : listeners) {
            l.propertyChange(evt);
        }
    }

    //XML
    @Override
    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException {
        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    String name = reader.getLocalName();
                    if ("textmodel".equalsIgnoreCase(name)) {
                        textModel.readXML(reader, workspace);
                    } else {
                        readXmlAttribute(config, reader, name);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ("vizmodel".equalsIgnoreCase(reader.getLocalName())) {
                        end = true;
                    }
                    break;
            }
        }
    }

    @Override
    public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("vizmodel");
        
        // Text model properties
        textModel.writeXML(writer);

        // Model properties
        writeXmlAttribute(config, writer, VizConfig.ADJUST_BY_TEXT);
        writeXmlAttribute(config, writer, VizConfig.ANTIALIASING);
        writeXmlAttribute(config, writer, VizConfig.AUTO_SELECT_NEIGHBOUR);
        writeXmlAttribute(config, writer, VizConfig.CAMERA_USE_3D);
        writeXmlAttribute(config, writer, VizConfig.CAMERA);
        writeXmlAttribute(config, writer, VizConfig.CLEAN_DELETED_MODELS);
        writeXmlAttribute(config, writer, VizConfig.CONTEXT_MENU);
        writeXmlAttribute(config, writer, VizConfig.EDGE_HAS_UNIQUE_COLOR);
        writeXmlAttribute(config, writer, VizConfig.EDGE_SCALE);
        writeXmlAttribute(config, writer, VizConfig.EDGE_UNIQUE_COLOR);
        writeXmlAttribute(config, writer, VizConfig.HIDE_NONSELECTED_EDGES);
        writeXmlAttribute(config, writer, VizConfig.HIGHLIGHT_NON_SELECTED_ANIMATION);
        writeXmlAttribute(config, writer, VizConfig.HIGHLIGHT_NON_SELECTED_COLOR);
        writeXmlAttribute(config, writer, VizConfig.HIGHLIGHT_NON_SELECTED);
        writeXmlAttribute(config, writer, VizConfig.META_EDGE_SCALE);
        writeXmlAttribute(config, writer, VizConfig.NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR);
        writeXmlAttribute(config, writer, VizConfig.NODE_SELECTED_UNIQUE_COLOR);
        writeXmlAttribute(config, writer, VizConfig.NODE_GLOBAL_SHAPE);
        writeXmlAttribute(config, writer, VizConfig.OCTREE_DEPTH);
        writeXmlAttribute(config, writer, VizConfig.OCTREE_WIDTH);
        writeXmlAttribute(config, writer, VizConfig.PROPERTIES_BAR);
        writeXmlAttribute(config, writer, VizConfig.RECTANGLE_SELECTION);
        writeXmlAttribute(config, writer, VizConfig.RECTANGLE_SELECTION_COLOR);
        writeXmlAttribute(config, writer, VizConfig.REDUCE_FPS_MOUSE_OUT);
        writeXmlAttribute(config, writer, VizConfig.REDUCE_FPS_MOUSE_OUT_VALUE);
        writeXmlAttribute(config, writer, VizConfig.SELECTEDEDGE_BOTH_COLOR);
        writeXmlAttribute(config, writer, VizConfig.SELECTEDEDGE_HAS_COLOR);
        writeXmlAttribute(config, writer, VizConfig.SELECTEDEDGE_IN_COLOR);
        writeXmlAttribute(config, writer, VizConfig.SELECTEDEDGE_OUT_COLOR);
        writeXmlAttribute(config, writer, VizConfig.SELECTEDNODE_UNIQUE_COLOR);
        writeXmlAttribute(config, writer, VizConfig.SHOW_EDGES);
        writeXmlAttribute(config, writer, VizConfig.SHOW_FPS);
        writeXmlAttribute(config, writer, VizConfig.SHOW_HULLS);
        writeXmlAttribute(config, writer, VizConfig.TOOLBAR);
        writeXmlAttribute(config, writer, VizConfig.VIZBAR);

        writer.writeEndElement();
    }
    
    protected static void readXmlAttribute(VizConfigImpl config, XMLStreamReader reader, String attribute) throws XMLStreamException {
        Class<?> type = null;
        try {
            type = config.getPropertyType(attribute);
        } catch (VizConfig.PropertyNotAvailableException e) {
            return;
        }
        if (type.isAssignableFrom(Vec3.class)) {
            float x = Float.parseFloat(reader.getAttributeValue(null, "x"));
            float y = Float.parseFloat(reader.getAttributeValue(null, "y"));
            float z = Float.parseFloat(reader.getAttributeValue(null, "z"));
            config.setProperty(attribute, new Vec3(x, y, z));
        } else if (type.isAssignableFrom(Vec2.class)) {
            float x = Float.parseFloat(reader.getAttributeValue(null, "x"));
            float y = Float.parseFloat(reader.getAttributeValue(null, "y"));
            config.setProperty(attribute, new Vec2(x, y));
        } else if (type.isAssignableFrom(Color.class)) {
            Color color = ColorUtils.decode(reader.getAttributeValue(null, "value"));
            config.setProperty(attribute, color);
        } else if (type.isAssignableFrom(Font.class)) {
            String edgeFontName = reader.getAttributeValue(null, "name");
            int edgeFontSize = Integer.parseInt(reader.getAttributeValue(null, "size"));
            int edgeFontStyle = Integer.parseInt(reader.getAttributeValue(null, "style"));
            Font font = new Font(edgeFontName, edgeFontStyle, edgeFontSize);
            config.setProperty(attribute, font);
        } else if (type.isAssignableFrom(Integer.class)) {
            config.setProperty(attribute, Integer.parseInt(reader.getAttributeValue(null, "value")));
        } else if (type.isAssignableFrom(Float.class)) {
            config.setProperty(attribute, Float.parseFloat(reader.getAttributeValue(null, "value")));
        } else if (type.isAssignableFrom(Boolean.class)) {
            config.setProperty(attribute, Boolean.parseBoolean(reader.getAttributeValue(null, "value")));
        } else if (type.isAssignableFrom(String.class)) {
            config.setProperty(attribute, reader.getAttributeValue(null, "value"));
        } else if (type.isAssignableFrom(Enum.class)) {
            Enum value = config.getProperty(Enum.class, attribute);
            config.setProperty(attribute, Enum.valueOf(((Enum<?>) value).getDeclaringClass(), reader.getAttributeValue(null, "value")));
        } else if (type.isAssignableFrom(Camera.class)) {
            // TODO make a nicer XML
            Camera camera = null;
            float[] position = new float[] {
                Float.parseFloat(reader.getAttributeValue(null, "position_x")), 
                Float.parseFloat(reader.getAttributeValue(null, "position_y")), 
                Float.parseFloat(reader.getAttributeValue(null, "position_z"))
            };
            float[] lookat = new float[] {
                Float.parseFloat(reader.getAttributeValue(null, "lookat_x")), 
                Float.parseFloat(reader.getAttributeValue(null, "lookat_y")), 
                Float.parseFloat(reader.getAttributeValue(null, "lookat_z"))
            };
            if (config.getBooleanProperty(VizConfig.CAMERA_USE_3D)) {
                camera = new Camera3d();
                ((Camera3d) camera).lookAt(new Vec3(position[0], position[1], position[2]), new Vec3(lookat[0], lookat[1], lookat[2]), Vec3.E2);
            } else {
                camera = new Camera2d();
                camera.lookAt(new Vec3(position[0], position[1], position[2]), Vec3.E2);
            }
        }
    }
    
    protected static void writeXmlAttribute(VizConfigImpl config, XMLStreamWriter writer, String attribute) throws XMLStreamException {
        Class<?> type = config.getPropertyType(attribute);
        writer.writeStartElement(attribute);
        if (type.isAssignableFrom(Vec3.class)) {
            Vec3 v = config.getVec3Property(attribute);
            writer.writeAttribute("x", Float.toString(v.x()));
            writer.writeAttribute("y", Float.toString(v.y()));
            writer.writeAttribute("z", Float.toString(v.z()));
        } else if (type.isAssignableFrom(Vec2.class)) {
            Vec2 v = config.getVec2Property(attribute);
            writer.writeAttribute("x", Float.toString(v.x()));
            writer.writeAttribute("y", Float.toString(v.y()));
        } else if (type.isAssignableFrom(Color.class)) {
            Color color = config.getColorProperty(attribute);
            writer.writeAttribute("value", ColorUtils.encode(color));
        } else if (type.isAssignableFrom(Font.class)) {
            Font font = config.getFontProperty(attribute);
            writer.writeAttribute("name", font.getName());
            writer.writeAttribute("size", Integer.toString(font.getSize()));
            writer.writeAttribute("style", Integer.toString(font.getStyle()));
        } else if (type.isAssignableFrom(AttributeColumn[].class)) {
            AttributeColumn[] columns = config.getProperty(AttributeColumn[].class, attribute);
            for (AttributeColumn c : columns) {
                writer.writeStartElement("column");
                writer.writeAttribute("id", c.getId());
                writer.writeEndElement();
            }
        } else if (type.isAssignableFrom(Camera.class)) {
            // TODO make a nicer XML
            Camera camera = config.getProperty(Camera.class, VizConfig.CAMERA);
            writer.writeAttribute("position_x", Float.toString(camera.getPosition()[0]));
            writer.writeAttribute("position_y", Float.toString(camera.getPosition()[1]));
            writer.writeAttribute("position_z", Float.toString(camera.getPosition()[2]));
            writer.writeAttribute("lookat_x", Float.toString(camera.getLookAt()[0]));
            writer.writeAttribute("lookat_y", Float.toString(camera.getLookAt()[1]));
            writer.writeAttribute("lookat_z", Float.toString(camera.getLookAt()[2]));
        }
        writer.writeEndElement();
    }

}
