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
import org.gephi.ui.utils.ColorUtils;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.vizmodel.TextModel;
import org.openide.util.Lookup;

/**
 * @author Mathieu Bastian
 */
public class TextModelImpl implements TextModel {

    protected final VizModelImpl vizModel;
    protected final VizConfigImpl config;
    protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    protected AttributeColumn[] nodeTextColumns = new AttributeColumn[0];
    protected AttributeColumn[] edgeTextColumns = new AttributeColumn[0];

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

    public AttributeColumn[] getEdgeTextColumns() {
        return edgeTextColumns;
    }

    public void setTextColumns(AttributeColumn[] nodeTextColumns, AttributeColumn[] edgeTextColumns) {
        this.nodeTextColumns = nodeTextColumns;
        this.edgeTextColumns = edgeTextColumns;
        fireChangeEvent();
    }

    public AttributeColumn[] getNodeTextColumns() {
        return nodeTextColumns;
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

    private void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : listeners) {
            l.stateChanged(evt);
        }
    }

    // FIXME uncomment color and size modes
    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException {
        /*
        AttributeController attributeController = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel attributeModel = attributeController != null ? attributeController.getModel(workspace) : null;
        List<AttributeColumn> nodeCols = new ArrayList<AttributeColumn>();
        List<AttributeColumn> edgeCols = new ArrayList<AttributeColumn>();

        boolean nodeColumn = false;
        boolean edgeColumn = false;
        boolean nodeSizeFac = false;
        boolean edgeSizeFac = false;
        boolean end = false;
        while (reader.hasNext() && !end) {
            int type = reader.next();

            switch (type) {
                case XMLStreamReader.START_ELEMENT:
                    String name = reader.getLocalName();
                    if ("shownodelabels".equalsIgnoreCase(name)) {
                        showNodeLabels = Boolean.parseBoolean(reader.getAttributeValue(null, "enable"));
                    } else if ("showedgelabels".equalsIgnoreCase(name)) {
                        showEdgeLabels = Boolean.parseBoolean(reader.getAttributeValue(null, "enable"));
                    } else if ("selectedOnly".equalsIgnoreCase(name)) {
                        selectedOnly = Boolean.parseBoolean(reader.getAttributeValue(null, "value"));
                    } else if ("nodefont".equalsIgnoreCase(name)) {
                        String nodeFontName = reader.getAttributeValue(null, "name");
                        int nodeFontSize = Integer.parseInt(reader.getAttributeValue(null, "size"));
                        int nodeFontStyle = Integer.parseInt(reader.getAttributeValue(null, "style"));
                        nodeFont = new Font(nodeFontName, nodeFontStyle, nodeFontSize);
                    } else if ("edgefont".equalsIgnoreCase(name)) {
                        String edgeFontName = reader.getAttributeValue(null, "name");
                        int edgeFontSize = Integer.parseInt(reader.getAttributeValue(null, "size"));
                        int edgeFontStyle = Integer.parseInt(reader.getAttributeValue(null, "style"));
                        edgeFont = new Font(edgeFontName, edgeFontStyle, edgeFontSize);
                    } else if ("nodecolor".equalsIgnoreCase(name)) {
                        nodeColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("edgecolor".equalsIgnoreCase(name)) {
                        edgeColor = ColorUtils.decode(reader.getAttributeValue(null, "value")).getRGBComponents(null);
                    } else if ("nodesizefactor".equalsIgnoreCase(name)) {
                        nodeSizeFac = true;
                    } else if ("edgesizefactor".equalsIgnoreCase(name)) {
                        edgeSizeFac = true;
                    } else if ("colormode".equalsIgnoreCase(name)) {
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
                    } else if ("nodecolumns".equalsIgnoreCase(name)) {
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
                    }

                    break;
                case XMLStreamReader.CHARACTERS:
                    if (!reader.isWhiteSpace() && nodeSizeFac) {
                        nodeSizeFactor = Float.parseFloat(reader.getText());
                    } else if (!reader.isWhiteSpace() && edgeSizeFac) {
                        edgeSizeFactor = Float.parseFloat(reader.getText());
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    nodeSizeFac = false;
                    edgeSizeFac = false;
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

        nodeTextColumns = nodeCols.toArray(new AttributeColumn[0]);
        edgeTextColumns = edgeCols.toArray(new AttributeColumn[0]);
*/
    }
    
    // FIXME uncomment color and size modes
    public void writeXML(XMLStreamWriter writer) throws XMLStreamException {
/*
        writer.writeStartElement("textmodel");

        //Show
        writer.writeStartElement("shownodelabels");
        writer.writeAttribute("enable", String.valueOf(showNodeLabels));
        writer.writeEndElement();
        writer.writeStartElement("showedgelabels");
        writer.writeAttribute("enable", String.valueOf(showEdgeLabels));
        writer.writeEndElement();

        //Selectedonly
        writer.writeStartElement("selectedOnly");
        writer.writeAttribute("value", String.valueOf(selectedOnly));
        writer.writeEndElement();

        //Font
        writer.writeStartElement("nodefont");
        writer.writeAttribute("name", nodeFont.getName());
        writer.writeAttribute("size", Integer.toString(nodeFont.getSize()));
        writer.writeAttribute("style", Integer.toString(nodeFont.getStyle()));
        writer.writeEndElement();

        writer.writeStartElement("edgefont");
        writer.writeAttribute("name", edgeFont.getName());
        writer.writeAttribute("size", Integer.toString(edgeFont.getSize()));
        writer.writeAttribute("style", Integer.toString(edgeFont.getStyle()));
        writer.writeEndElement();

        //Size factor
        writer.writeStartElement("nodesizefactor");
        writer.writeCharacters(String.valueOf(nodeSizeFactor));
        writer.writeEndElement();

        writer.writeStartElement("edgesizefactor");
        writer.writeCharacters(String.valueOf(edgeSizeFactor));
        writer.writeEndElement();

        //Colors
        writer.writeStartElement("nodecolor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(nodeColor)));
        writer.writeEndElement();

        writer.writeStartElement("edgecolor");
        writer.writeAttribute("value", ColorUtils.encode(ColorUtils.decode(edgeColor)));
        writer.writeEndElement();

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
        }*//*
        writer.writeEndElement();

        //NodeColumns
        writer.writeStartElement("nodecolumns");
        for (AttributeColumn c : nodeTextColumns) {
            writer.writeStartElement("column");
            writer.writeAttribute("id", c.getId());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        //EdgeColumns
        writer.writeStartElement("edgecolumns");
        for (AttributeColumn c : edgeTextColumns) {
            writer.writeStartElement("column");
            writer.writeAttribute("id", c.getId());
            writer.writeEndElement();
        }
        writer.writeEndElement();

        writer.writeEndElement();*/
    }
}
