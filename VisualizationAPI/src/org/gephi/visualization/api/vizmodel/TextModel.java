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
package org.gephi.visualization.api.vizmodel;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.event.ChangeListener;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.project.api.Workspace;

/**
 * @author Mathieu Bastian
 */
public interface TextModel {

    //Event
    public void addChangeListener(ChangeListener changeListener);

    public void removeChangeListener(ChangeListener changeListener);

    public void setListeners(List<ChangeListener> listeners);

    public List<ChangeListener> getListeners();

    //Getter & Setters
    public boolean isShowEdgeLabels();

    public boolean isShowNodeLabels();

    public void setShowEdgeLabels(boolean showEdgeLabels);

    public void setShowNodeLabels(boolean showNodeLabels);

    public void setEdgeFont(Font edgeFont);

    public void setEdgeSizeFactor(float edgeSizeFactor);

    public void setNodeFont(Font nodeFont);

    public void setNodeSizeFactor(float nodeSizeFactor);

    public Font getEdgeFont();

    public float getEdgeSizeFactor();

    public Font getNodeFont();

    public float getNodeSizeFactor();

    //public ColorMode getColorMode();

    //public void setColorMode(ColorMode colorMode);

    public boolean isSelectedOnly();

    public void setSelectedOnly(boolean value);

    //public SizeMode getSizeMode();

    //public void setSizeMode(SizeMode sizeMode);

    public Color getNodeColor();

    public void setNodeColor(Color color);

    public Color getEdgeColor();

    public void setEdgeColor(Color color);

    public AttributeColumn[] getEdgeTextColumns();

    public void setTextColumns(AttributeColumn[] nodeTextColumns, AttributeColumn[] edgeTextColumns);

    public AttributeColumn[] getNodeTextColumns();

    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException;

    public void writeXML(XMLStreamWriter writer) throws XMLStreamException;

}
