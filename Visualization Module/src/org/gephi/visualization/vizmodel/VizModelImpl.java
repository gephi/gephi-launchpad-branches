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
package org.gephi.visualization.vizmodel;

import java.awt.Color;
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
import org.gephi.graph.api.NodeShape;
import org.gephi.project.api.Workspace;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.vizmodel.TextModel;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.util.lookup.ServiceProvider;

/**
 * Model class for visualization. Contains most visualization related settings.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = VizModel.class)
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
        this(true);
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
    public Color getBackgroundColor() {
        return config.getColorProperty(VizConfig.BACKGROUND_COLOR);
    }

    @Override
    public float[] getCameraPosition() {
        return config.getFloatArrayProperty(VizConfig.CAMERA_POSITION);
    }

    @Override
    public float[] getCameraTarget() {
        return config.getFloatArrayProperty(VizConfig.CAMERA_TARGET);
    }

    @Override
    public boolean isCulling() {
        return config.getBooleanProperty(VizConfig.CULLING);
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
    public boolean isHideNonSelectedEdges() {
        return config.getBooleanProperty(VizConfig.HIDE_NONSELECTED_EDGES);
    }

    @Override
    public boolean isLightenNonSelectedAuto() {
        return config.getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED);
    }

    @Override
    public boolean isLighting() {
        return config.getBooleanProperty(VizConfig.LIGHTING);
    }

    @Override
    public boolean isMaterial() {
        return config.getBooleanProperty(VizConfig.MATERIAL);
    }

    @Override
    public boolean isRotatingEnable() {
        return config.getBooleanProperty(VizConfig.ROTATING);
    }

    @Override
    public boolean isUniColorSelected() {
        return config.getBooleanProperty(VizConfig.SELECTEDNODE_UNIQUE_COLOR);
    }

    @Override
    public boolean isUse3d() {
        return config.getBooleanProperty(VizConfig.USE_3D);
    }

    @Override
    public boolean isEdgeSelectionColor() {
        return config.getBooleanProperty(VizConfig.SELECTEDNODE_UNIQUE_COLOR);
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
        return config.getEnumProperty(NodeShape.class, VizConfig.NODE_GLOBAL_SHAPE);
    }

    @Override
    public float getZoomFactor() {
        return config.getFloatProperty(VizConfig.ZOOM_FACTOR);
    }

    //SETTERS
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
    public void setBackgroundColor(Color backgroundColor) {
        config.setProperty(VizConfig.BACKGROUND_COLOR, backgroundColor);
        fireProperyChange(VizConfig.BACKGROUND_COLOR, null, backgroundColor);
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
    public void setLightenNonSelectedAuto(boolean lightenNonSelectedAuto) {
        config.setProperty(VizConfig.HIGHLIGHT_NON_SELECTED, lightenNonSelectedAuto);
        fireProperyChange(VizConfig.HIGHLIGHT_NON_SELECTED, null, lightenNonSelectedAuto);
    }

    @Override
    public void setUniColorSelected(boolean uniColorSelected) {
        config.setProperty(VizConfig.NODE_SELECTED_UNIQUE_COLOR, uniColorSelected);
        fireProperyChange(VizConfig.NODE_SELECTED_UNIQUE_COLOR, null, uniColorSelected);
    }

    @Override
    public void setUse3d(boolean use3d) {
        config.setProperty(VizConfig.USE_3D, use3d);
        fireProperyChange(VizConfig.USE_3D, null, use3d);
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
    public void setZoomFactor(float distance) {
        config.setProperty(VizConfig.ZOOM_FACTOR, distance);
        fireProperyChange(VizConfig.ZOOM_FACTOR, null, distance);
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
    public void fireProperyChange(String propertyName, Object oldvalue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldvalue, newValue);
        for (PropertyChangeListener l : listeners) {
            l.propertyChange(evt);
        }
    }

    //XML
    @Override
    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException {
/*
        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    String name = reader.getLocalName();
                    if ("textmodel".equalsIgnoreCase(name)) {
                        textModel.readXML(reader, workspace);
                    } else if ("cameraposition".equalsIgnoreCase(name)) {
                        cameraPosition[0] = Float.parseFloat(reader.getAttributeValue(null, "x"));
                        cameraPosition[1] = Float.parseFloat(reader.getAttributeValue(null, "y"));
                        cameraPosition[2] = Float.parseFloat(reader.getAttributeValue(null, "z"));
                    } else if ("cameratarget".equalsIgnoreCase(name)) {
                        cameraTarget[0] = Float.parseFloat(reader.getAttributeValue(null, "x"));
                        cameraTarget[1] = Float.parseFloat(reader.getAttributeValue(null, "y"));
                        cameraTarget[2] = Float.parseFloat(reader.getAttributeValue(null, "z"));
                    } else if ("use3d".equalsIgnoreCase(name)) {
                        use3d = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("lighting".equalsIgnoreCase(name)) {
                        lighting = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("culling".equalsIgnoreCase(name)) {
                        culling = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("material".equalsIgnoreCase(name)) {
                        material = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("rotatingenable".equalsIgnoreCase(name)) {
                        rotatingEnable = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("showedges".equalsIgnoreCase(name)) {
                        showEdges = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("lightennonselectedauto".equalsIgnoreCase(name)) {
                        lightenNonSelectedAuto = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("autoselectneighbor".equalsIgnoreCase(name)) {
                        autoSelectNeighbor = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("hidenonselectededges".equalsIgnoreCase(name)) {
                        hideNonSelectedEdges = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("unicolorselected".equalsIgnoreCase(name)) {
                        uniColorSelected = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("edgehasunicolor".equalsIgnoreCase(name)) {
                        edgeHasUniColor = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("adjustbytext".equalsIgnoreCase(name)) {
                        adjustByText = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("edgeSelectionColor".equalsIgnoreCase(name)) {
                        edgeSelectionColor = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("showHulls".equalsIgnoreCase(name)) {
                        showHulls = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("backgroundcolor".equalsIgnoreCase(name)) {
                        backgroundColor = ColorUtils.decode(reader.getAttributeValue(null, "value"));
                    } else if ("edgeunicolor".equalsIgnoreCase(name)) {
                        edgeUniColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("edgeInSelectionColor".equalsIgnoreCase(name)) {
                        edgeInSelectionColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("edgeOutSelectionColor".equalsIgnoreCase(name)) {
                        edgeOutSelectionColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("edgeBothSelectionColor".equalsIgnoreCase(name)) {
                        edgeBothSelectionColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("nodeshape".equalsIgnoreCase(name)) {
                        globalNodeShape = NodeShape.valueOf(reader.getAttributeValue(null, "value"));
                    } else if ("edgeScale".equalsIgnoreCase(name)) {
                        edgeScale = Float.parseFloat(reader.getAttributeValue(null, "value"));
                    } else if ("metaEdgeScale".equalsIgnoreCase(name)) {
                        metaEdgeScale = Float.parseFloat(reader.getAttributeValue(null, "value"));
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ("vizmodel".equalsIgnoreCase(reader.getLocalName())) {
                        end = true;
                    }
                    break;
            }
        }*/
    }

    @Override
    public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
/*
        writer.writeStartElement("vizmodel");

        //Fast refreh
        Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        float[] cameraPosition = camera.position().toArray();
        float[] cameraTarget = camera.lookAtPoint().toArray();

        //TextModel
        textModel.writeXML(writer);

        //Camera
        writer.writeStartElement("cameraposition");
        writer.writeAttribute("x", Float.toString(cameraPosition[0]));
        writer.writeAttribute("y", Float.toString(cameraPosition[1]));
        writer.writeAttribute("z", Float.toString(cameraPosition[2]));
        writer.writeEndElement();
        writer.writeStartElement("cameratarget");
        writer.writeAttribute("x", Float.toString(cameraTarget[0]));
        writer.writeAttribute("y", Float.toString(cameraTarget[1]));
        writer.writeAttribute("z", Float.toString(cameraTarget[2]));
        writer.writeEndElement();

        //Boolean values
        writer.writeStartElement("use3d");
        writer.writeAttribute("value", String.valueOf(use3d));
        writer.writeEndElement();

        writer.writeStartElement("lighting");
        writer.writeAttribute("value", String.valueOf(lighting));
        writer.writeEndElement();

        writer.writeStartElement("culling");
        writer.writeAttribute("value", String.valueOf(culling));
        writer.writeEndElement();

        writer.writeStartElement("material");
        writer.writeAttribute("value", String.valueOf(material));
        writer.writeEndElement();

        writer.writeStartElement("rotatingenable");
        writer.writeAttribute("value", String.valueOf(rotatingEnable));
        writer.writeEndElement();

        writer.writeStartElement("showedges");
        writer.writeAttribute("value", String.valueOf(showEdges));
        writer.writeEndElement();

        writer.writeStartElement("lightennonselectedauto");
        writer.writeAttribute("value", String.valueOf(lightenNonSelectedAuto));
        writer.writeEndElement();

        writer.writeStartElement("autoselectneighbor");
        writer.writeAttribute("value", String.valueOf(autoSelectNeighbor));
        writer.writeEndElement();

        writer.writeStartElement("hidenonselectededges");
        writer.writeAttribute("value", String.valueOf(hideNonSelectedEdges));
        writer.writeEndElement();

        writer.writeStartElement("unicolorselected");
        writer.writeAttribute("value", String.valueOf(uniColorSelected));
        writer.writeEndElement();

        writer.writeStartElement("edgehasunicolor");
        writer.writeAttribute("value", String.valueOf(edgeHasUniColor));
        writer.writeEndElement();

        writer.writeStartElement("adjustbytext");
        writer.writeAttribute("value", String.valueOf(adjustByText));
        writer.writeEndElement();

        writer.writeStartElement("edgeSelectionColor");
        writer.writeAttribute("value", String.valueOf(edgeSelectionColor));
        writer.writeEndElement();

        writer.writeStartElement("showHulls");
        writer.writeAttribute("value", String.valueOf(showHulls));
        writer.writeEndElement();

        //Colors
        writer.writeStartElement("backgroundcolor");
        writer.writeAttribute("value", ColorUtils.encode(backgroundColor));
        writer.writeEndElement();

        writer.writeStartElement("edgeunicolor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(edgeUniColor)));
        writer.writeEndElement();

        writer.writeStartElement("edgeInSelectionColor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(edgeInSelectionColor)));
        writer.writeEndElement();

        writer.writeStartElement("edgeOutSelectionColor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(edgeOutSelectionColor)));
        writer.writeEndElement();

        writer.writeStartElement("edgeBothSelectionColor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(edgeBothSelectionColor)));
        writer.writeEndElement();

        //Misc
        writer.writeStartElement("nodeshape");
        writer.writeAttribute("value", globalNodeShape.toString());
        writer.writeEndElement();

        //Float
        writer.writeStartElement("edgeScale");
        writer.writeAttribute("value", String.valueOf(edgeScale));
        writer.writeEndElement();

        writer.writeStartElement("metaEdgeScale");
        writer.writeAttribute("value", String.valueOf(metaEdgeScale));
        writer.writeEndElement();

        writer.writeEndElement();*/
    }

}
