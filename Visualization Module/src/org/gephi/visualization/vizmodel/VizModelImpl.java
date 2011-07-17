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
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.graph.api.NodeShape;
import org.gephi.project.api.Workspace;
import org.gephi.ui.utils.ColorUtils;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.controller.VisualizationController;
import org.gephi.visualization.api.vizmodel.TextModel;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Model class for visualization. Contains most visualization related settings.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = VizModel.class)
public class VizModelImpl implements VizModel {

    protected VizConfig config;
    //Variable
    protected float[] cameraPosition;
    protected float[] cameraTarget;
    protected float cameraDistance;
    protected TextModel textModel;
    protected boolean use3d;
    protected boolean lighting;
    protected boolean culling;
    protected boolean material;
    protected Color backgroundColor;
    protected boolean rotatingEnable;
    protected boolean showEdges;
    protected boolean lightenNonSelectedAuto;
    protected boolean autoSelectNeighbor;
    protected boolean hideNonSelectedEdges;
    protected boolean uniColorSelected;
    protected boolean edgeHasUniColor;
    protected float[] edgeUniColor;
    protected boolean edgeSelectionColor;
    protected float[] edgeInSelectionColor;
    protected float[] edgeOutSelectionColor;
    protected float[] edgeBothSelectionColor;
    protected boolean adjustByText;
    protected boolean showHulls;
    protected float edgeScale;
    protected float metaEdgeScale;
    protected NodeShape globalNodeShape;
    //Listener
    protected List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
    private boolean defaultModel = false;

    public VizModelImpl() {
        defaultValues();
    }

    public VizModelImpl(boolean defaultModel) {
        this.defaultModel = defaultModel;
        defaultValues();
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

    @Override
    public boolean isDefaultModel() {
        return defaultModel;
    }

    @Override
    public List<PropertyChangeListener> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(List<PropertyChangeListener> listeners) {
        this.listeners = listeners;
    }

    private void defaultValues() {
        config = Lookup.getDefault().lookup(VizConfig.class);
        cameraPosition = Arrays.copyOf(config.getDefaultCameraPosition(), 3);
        cameraTarget = Arrays.copyOf(config.getDefaultCameraTarget(), 3);
        textModel = new TextModelImpl();
        use3d = config.isDefaultUse3d();
        lighting = use3d;
        culling = use3d;
        material = use3d;
        rotatingEnable = use3d;
        backgroundColor = config.getDefaultBackgroundColor();

        showEdges = config.isDefaultShowEdges();
        lightenNonSelectedAuto = config.isDefaultLightenNonSelectedAuto();
        autoSelectNeighbor = config.isDefaultAutoSelectNeighbor();
        hideNonSelectedEdges = config.isDefaultHideNonSelectedEdges();
        uniColorSelected = config.isDefaultUniColorSelected();
        edgeHasUniColor = config.isDefaultEdgeHasUniColor();
        edgeUniColor = config.getDefaultEdgeUniColor().getRGBComponents(null);
        adjustByText = config.isDefaultAdjustByText();
        edgeSelectionColor = config.isDefaultEdgeSelectionColor();
        edgeInSelectionColor = config.getDefaultEdgeInSelectedColor().getRGBComponents(null);
        edgeOutSelectionColor = config.getDefaultEdgeOutSelectedColor().getRGBComponents(null);
        edgeBothSelectionColor = config.getDefaultEdgeBothSelectedColor().getRGBComponents(null);
        showHulls = config.isDefaultShowHulls();
        edgeScale = config.getDefaultEdgeScale();
        metaEdgeScale = config.getDefaultMetaEdgeScale();
        globalNodeShape = config.getDefaultNodeShape();
    }

    //GETTERS
    @Override
    public boolean isAdjustByText() {
        return adjustByText;
    }

    @Override
    public boolean isAutoSelectNeighbor() {
        return autoSelectNeighbor;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public float[] getCameraPosition() {
        return cameraPosition;
    }

    @Override
    public float[] getCameraTarget() {
        return cameraTarget;
    }

    @Override
    public boolean isCulling() {
        return culling;
    }

    @Override
    public boolean isShowEdges() {
        return showEdges;
    }

    @Override
    public boolean isEdgeHasUniColor() {
        return edgeHasUniColor;
    }

    @Override
    public float[] getEdgeUniColor() {
        return edgeUniColor;
    }

    @Override
    public boolean isHideNonSelectedEdges() {
        return hideNonSelectedEdges;
    }

    @Override
    public boolean isLightenNonSelectedAuto() {
        return lightenNonSelectedAuto;
    }

    @Override
    public boolean isLighting() {
        return lighting;
    }

    @Override
    public boolean isMaterial() {
        return material;
    }

    @Override
    public boolean isRotatingEnable() {
        return rotatingEnable;
    }

    @Override
    public TextModel getTextModel() {
        return textModel;
    }

    @Override
    public boolean isUniColorSelected() {
        return uniColorSelected;
    }

    @Override
    public boolean isUse3d() {
        return use3d;
    }

    @Override
    public VizConfig getConfig() {
        return config;
    }

    @Override
    public boolean isEdgeSelectionColor() {
        return edgeSelectionColor;
    }

    @Override
    public float[] getEdgeInSelectionColor() {
        return edgeInSelectionColor;
    }

    @Override
    public float[] getEdgeOutSelectionColor() {
        return edgeOutSelectionColor;
    }

    @Override
    public float[] getEdgeBothSelectionColor() {
        return edgeBothSelectionColor;
    }

    @Override
    public boolean isShowHulls() {
        return showHulls;
    }

    @Override
    public float getEdgeScale() {
        return edgeScale;
    }

    @Override
    public float getMetaEdgeScale() {
        return metaEdgeScale;
    }

    @Override
    public NodeShape getGlobalNodeShape() {
        return globalNodeShape;
    }

    @Override
    public float getCameraDistance() {
        return cameraDistance;
    }

    //SETTERS
    @Override
    public void setAdjustByText(boolean adjustByText) {
        this.adjustByText = adjustByText;
        fireProperyChange("adjustByText", null, adjustByText);
    }

    @Override
    public void setAutoSelectNeighbor(boolean autoSelectNeighbor) {
        this.autoSelectNeighbor = autoSelectNeighbor;
        fireProperyChange("autoSelectNeighbor", null, autoSelectNeighbor);
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        fireProperyChange("backgroundColor", null, backgroundColor);
    }

    @Override
    public void setShowEdges(boolean showEdges) {
        this.showEdges = showEdges;
        fireProperyChange("showEdges", null, showEdges);
    }

    @Override
    public void setEdgeHasUniColor(boolean edgeHasUniColor) {
        this.edgeHasUniColor = edgeHasUniColor;
        fireProperyChange("edgeHasUniColor", null, edgeHasUniColor);
    }

    @Override
    public void setEdgeUniColor(float[] edgeUniColor) {
        this.edgeUniColor = edgeUniColor;
        fireProperyChange("edgeUniColor", null, edgeUniColor);
    }

    @Override
    public void setHideNonSelectedEdges(boolean hideNonSelectedEdges) {
        this.hideNonSelectedEdges = hideNonSelectedEdges;
        fireProperyChange("hideNonSelectedEdges", null, hideNonSelectedEdges);
    }

    @Override
    public void setLightenNonSelectedAuto(boolean lightenNonSelectedAuto) {
        this.lightenNonSelectedAuto = lightenNonSelectedAuto;
        fireProperyChange("lightenNonSelectedAuto", null, lightenNonSelectedAuto);
    }

    @Override
    public void setUniColorSelected(boolean uniColorSelected) {
        this.uniColorSelected = uniColorSelected;
        fireProperyChange("uniColorSelected", null, uniColorSelected);
    }

    @Override
    public void setUse3d(boolean use3d) {
        this.use3d = use3d;
        //Additional
        this.lighting = use3d;
        this.culling = use3d;
        this.rotatingEnable = use3d;
        this.material = use3d;
        fireProperyChange("use3d", null, use3d);
    }

    @Override
    public void setEdgeSelectionColor(boolean edgeSelectionColor) {
        this.edgeSelectionColor = edgeSelectionColor;
        fireProperyChange("edgeSelectionColor", null, edgeSelectionColor);
    }

    @Override
    public void setEdgeInSelectionColor(float[] edgeInSelectionColor) {
        this.edgeInSelectionColor = edgeInSelectionColor;
        fireProperyChange("edgeInSelectionColor", null, edgeInSelectionColor);
    }

    @Override
    public void setEdgeOutSelectionColor(float[] edgeOutSelectionColor) {
        this.edgeOutSelectionColor = edgeOutSelectionColor;
        fireProperyChange("edgeOutSelectionColor", null, edgeOutSelectionColor);
    }

    @Override
    public void setEdgeBothSelectionColor(float[] edgeBothSelectionColor) {
        this.edgeBothSelectionColor = edgeBothSelectionColor;
        fireProperyChange("edgeBothSelectionColor", null, edgeBothSelectionColor);
    }

    @Override
    public void setShowHulls(boolean showHulls) {
        this.showHulls = showHulls;
        fireProperyChange("showHulls", null, showHulls);
    }

    @Override
    public void setEdgeScale(float edgeScale) {
        this.edgeScale = edgeScale;
        fireProperyChange("edgeScale", null, edgeScale);
    }

    @Override
    public void setMetaEdgeScale(float metaEdgeScale) {
        this.metaEdgeScale = metaEdgeScale;
        fireProperyChange("metaEdgeScale", null, metaEdgeScale);
    }

    /**
     * Sets relative distance of camera from the world.
     * @param distance float from interval [0.0, 1.0].
     */
    @Override
    public void setCameraDistance(float distance) {
        cameraDistance = distance;
        fireProperyChange("cameraDistance", null, distance);
    }

    @Override
    public void setGlobalNodeShape(NodeShape nodeShape) {
        this.globalNodeShape = nodeShape;
        fireProperyChange("globalNodeShape", null, nodeShape);
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

        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    String name = reader.getLocalName();
                    if ("textmodel".equalsIgnoreCase(name)) {
                        //textModel.readXML(reader, workspace);
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
        }
    }

    @Override
    public void writeXML(XMLStreamWriter writer) throws XMLStreamException {

        writer.writeStartElement("vizmodel");

        //Fast refreh
        Camera camera = Lookup.getDefault().lookup(VisualizationController.class).getCameraCopy();
        cameraPosition = camera.position().toArray();
        cameraTarget = camera.lookAtPoint().toArray();

        //TextModel
        //textModel.writeXML(writer);

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

        writer.writeEndElement();
    }

}
