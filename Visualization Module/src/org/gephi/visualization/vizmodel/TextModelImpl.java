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
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.project.api.Workspace;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.TextModel;
import org.openide.util.Lookup;

/**
 * @author Mathieu Bastian
 */
public class TextModelImpl implements TextModel {

    protected final VizModelImpl vizModel;
    protected final VizConfigImpl config;
    protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public TextModelImpl(VizModelImpl vizModel) {
        this.vizModel = vizModel;
        this.config = vizModel.config;
    }

    // Getters
    public boolean isShowEdgeLabels() {
        return config.getBooleanProperty(VizConfig.EDGE_LABELS);
    }

    public boolean isShowNodeLabels() {
        return config.getBooleanProperty(VizConfig.NODE_LABELS);
    }

    public Font getEdgeFont() {
        return config.getFontProperty(VizConfig.EDGE_LABEL_FONT);
    }

    public float getEdgeSizeFactor() {
        return config.getFloatProperty(VizConfig.EDGE_LABEL_SIZE_FACTOR);
    }

    public Font getNodeFont() {
        return config.getFontProperty(VizConfig.NODE_LABEL_FONT);
    }

    public float getNodeSizeFactor() {
        return config.getFloatProperty(VizConfig.NODE_LABEL_SIZE_FACTOR);
    }
    
    public boolean isSelectedOnly() {
        return config.getBooleanProperty(VizConfig.LABEL_SELECTION_ONLY);
    }
    
    public Color getNodeColor() {
        return config.getColorProperty(VizConfig.NODE_LABEL_COLOR);
    }
    
    public Color getEdgeColor() {
        return config.getColorProperty(VizConfig.EDGE_LABEL_COLOR);
    }

    public AttributeColumn[] getEdgeTextColumns() {
        return config.getAttributeColumnArrayProperty(VizConfig.EDGE_TEXT_COLUMNS);
    }
    
    public AttributeColumn[] getNodeTextColumns() {
        return config.getAttributeColumnArrayProperty(VizConfig.NODE_TEXT_COLUMNS);
    }
    
    /*
    public ColorMode getColorMode() {
        return colorMode;
    }

    public SizeMode getSizeMode() {
        return sizeMode;
    }
     
    // Setters
    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        fireChangeEvent();
    }

    public void setSizeMode(SizeMode sizeMode) {
        this.sizeMode = sizeMode;
        fireChangeEvent();
    }
    */

    public void setShowEdgeLabels(boolean showEdgeLabels) {
        config.setProperty(VizConfig.EDGE_LABELS, showEdgeLabels);
        fireChangeEvent();
    }

    public void setShowNodeLabels(boolean showNodeLabels) {
        config.setProperty(VizConfig.NODE_LABELS, showNodeLabels);
        fireChangeEvent();
    }

    public void setEdgeFont(Font edgeFont) {
        config.setProperty(VizConfig.EDGE_LABEL_FONT, edgeFont);
        fireChangeEvent();
    }

    public void setEdgeSizeFactor(float edgeSizeFactor) {
        config.setProperty(VizConfig.EDGE_LABEL_SIZE_FACTOR, edgeSizeFactor);
        fireChangeEvent();
    }

    public void setNodeFont(Font nodeFont) {
        config.setProperty(VizConfig.NODE_LABEL_FONT, nodeFont);
        fireChangeEvent();
    }

    public void setNodeSizeFactor(float nodeSizeFactor) {
        config.setProperty(VizConfig.NODE_LABEL_SIZE_FACTOR, nodeSizeFactor);
        fireChangeEvent();
    }
    
    public void setSelectedOnly(boolean value) {
        config.setProperty(VizConfig.LABEL_SELECTION_ONLY, value);
        fireChangeEvent();
    }
    
    public void setNodeColor(Color color) {
        config.setProperty(VizConfig.NODE_LABEL_COLOR, color);
        fireChangeEvent();
    }

    public void setEdgeColor(Color color) {
        config.setProperty(VizConfig.EDGE_LABEL_COLOR, color);
        fireChangeEvent();
    }

    public void setTextColumns(AttributeColumn[] nodeTextColumns, AttributeColumn[] edgeTextColumns) {
        config.setProperty(VizConfig.NODE_TEXT_COLUMNS, nodeTextColumns);
        config.setProperty(VizConfig.EDGE_TEXT_COLUMNS, edgeTextColumns);
        fireChangeEvent();
    }
    
    // Events
    @Override
    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }
    
    @Override
    public void setListeners(List<ChangeListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public List<ChangeListener> getListeners() {
        return listeners;
    }

    private void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(evt);
        }
    }

    // FIXME uncomment color and size modes
    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException {
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel attributeModel = attributeController != null ? attributeController.getModel(workspace) : null;
        List<AttributeColumn> nodeCols = new ArrayList<AttributeColumn>();
        List<AttributeColumn> edgeCols = new ArrayList<AttributeColumn>();

        boolean nodeColumn = false;
        boolean edgeColumn = false;
        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    String name = reader.getLocalName();
                    if ("nodecolumns".equalsIgnoreCase(name)) {
                        nodeColumn = true;
                    } else if ("edgecolumns".equalsIgnoreCase(name)) {
                        edgeColumn = true;
                    } else if ("column".equalsIgnoreCase(name)) {
                        String id = reader.getAttributeValue(null, "id");
                        if (nodeColumn && attributeModel != null) {
                            AttributeColumn col = attributeModel.getNodeTable().getColumn(id);
                            if (col != null) {
                                nodeCols.add(col);
                            }
                        } else if (edgeColumn && attributeModel != null) {
                            AttributeColumn col = attributeModel.getEdgeTable().getColumn(id);
                            if (col != null) {
                                edgeCols.add(col);
                            }
                        }
                    } /*else if ("colormode".equalsIgnoreCase(name)) {
                        String colorModeClass = reader.getAttributeValue(null, "class");
                        if (colorModeClass.equals("UniqueColorMode")) {
                            //colorMode = VizController.getInstance().getTextManager().getColorModes()[0];
                        } else if (colorModeClass.equals("ObjectColorMode")) {
                            //colorMode = VizController.getInstance().getTextManager().getColorModes()[1];
                        }
                    } else if ("sizemode".equalsIgnoreCase(name)) {
                        String sizeModeClass = reader.getAttributeValue(null, "class");
                        if (sizeModeClass.equals("FixedSizeMode")) {
                            //sizeMode = VizController.getInstance().getTextManager().getSizeModes()[0];
                        } else if (sizeModeClass.equals("ProportionalSizeMode")) {
                            //sizeMode = VizController.getInstance().getTextManager().getSizeModes()[2];
                        } else if (sizeModeClass.equals("ScaledSizeMode")) {
                            //sizeMode = VizController.getInstance().getTextManager().getSizeModes()[1];
                        }
                    }*/ else {
                        VizModelImpl.readXmlAttribute(config, reader, name);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if ("nodecolumns".equalsIgnoreCase(reader.getLocalName())) {
                        nodeColumn = false;
                    } else if ("edgecolumns".equalsIgnoreCase(reader.getLocalName())) {
                        edgeColumn = false;
                    } else if ("textmodel".equalsIgnoreCase(reader.getLocalName())) {
                        end = true;
                    }

                    break;
            }
        }

        setTextColumns(nodeCols.toArray(new AttributeColumn[0]), edgeCols.toArray(new AttributeColumn[0]));
    }
    
    // FIXME uncomment color and size modes
    @Override
    public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("textmodel");

        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.NODE_LABELS);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.EDGE_LABELS);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.LABEL_SELECTION_ONLY);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.NODE_LABEL_FONT);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.EDGE_LABEL_FONT);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.NODE_LABEL_SIZE_FACTOR);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.EDGE_LABEL_SIZE_FACTOR);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.NODE_LABEL_COLOR);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.EDGE_LABEL_COLOR);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.NODE_TEXT_COLUMNS);
        VizModelImpl.writeXmlAttribute(config, writer, VizConfig.EDGE_TEXT_COLUMNS);
        
        //Colormode
        writer.writeStartElement("colormode");
        /*if (colorMode instanceof UniqueColorMode) {
            writer.writeAttribute("class", "UniqueColorMode");
        } else if (colorMode instanceof ObjectColorMode) {
            writer.writeAttribute("class", "ObjectColorMode");
        }
        writer.writeEndElement();*/

        //SizeMode
        /*writer.writeStartElement("sizemode");
        if (sizeMode instanceof FixedSizeMode) {
            writer.writeAttribute("class", "FixedSizeMode");
        } else if (sizeMode instanceof ProportionalSizeMode) {
            writer.writeAttribute("class", "ProportionalSizeMode");
        } else if (sizeMode instanceof ScaledSizeMode) {
            writer.writeAttribute("class", "ScaledSizeMode");
        }*/
        writer.writeEndElement();

        writer.writeEndElement();
    }
}
