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
package org.gephi.visualization.api.vizmodel;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.graph.api.NodeShape;
import org.gephi.math.linalg.Vec3;
import org.gephi.project.api.Workspace;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.rendering.background.Background;

/**
 * Model class for visualization. Contains most visualization related settings.
 * <p>
 * Contains an instance of the {@link VizConfig} class. Retrieve it using the
 * {@link #getConfig()} method.
 * <p>
 * @author Mathieu Bastian
 * @author Vojtech Bardiovsky
 */
public interface VizModel {

    public void init();

    public boolean isDefaultModel();

    //GETTERS
    public boolean isAdjustByText();

    public boolean isAutoSelectNeighbor();

    public Background getBackground();
    
    public Camera getCamera();
    
    public boolean isCulling();

    public boolean isShowEdges();

    public boolean isEdgeHasUniColor();

    public Color getEdgeUniColor();
    
    public GraphLimits getGraphLimits();
    
    public boolean isHideNonSelectedEdges();

    public boolean isHighlightNonSelectedEnabled();

    public boolean isLighting();

    public boolean isMaterial();

    public boolean isRotatingEnable();

    public boolean isNodeSelectedUniqueColor();

    public boolean isUse3d();

    public VizConfig getConfig();

    public boolean isEdgeSelectionColor();

    public Color getEdgeInSelectionColor();

    public Color getEdgeOutSelectionColor();

    public Color getEdgeBothSelectionColor();

    public boolean isShowHulls();

    public float getEdgeScale();

    public float getMetaEdgeScale();

    public TextModel getTextModel();

    public NodeShape getGlobalNodeShape();

    //SETTERS
    public void setAdjustByText(boolean adjustByText);

    public void setAutoSelectNeighbor(boolean autoSelectNeighbor);

    public void setBackground(Background background);

    public void setCamera(Camera camera);
    
    public void setShowEdges(boolean showEdges);

    public void setEdgeHasUniColor(boolean edgeHasUniColor);

    public void setEdgeUniColor(Color edgeUniColor);
    
    public void setGraphLimits(GraphLimits graphLimits);

    public void setHideNonSelectedEdges(boolean hideNonSelectedEdges);

    public void setHighlightNonSelectedEnabled(boolean highlightNonSelectedEnabled);

    public void setNodeSelectedUniqueColor(boolean uniColorSelected);

    public void setUse3d(boolean use3d);

    public void setEdgeSelectionColor(boolean edgeSelectionColor);

    public void setEdgeInSelectionColor(Color edgeInSelectionColor);

    public void setEdgeOutSelectionColor(Color edgeOutSelectionColor);

    public void setEdgeBothSelectionColor(Color edgeBothSelectionColor);

    public void setShowHulls(boolean showHulls);

    public void setEdgeScale(float edgeScale);

    public void setMetaEdgeScale(float metaEdgeScale);
    
    public void setGlobalNodeShape(NodeShape nodeShape);

    //EVENTS
    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public List<PropertyChangeListener> getListeners();

    public void setListeners(List<PropertyChangeListener> listeners);
    
    public void fireProperyChange(String propertyName, Object oldvalue, Object newValue);

    //XML
    public void readXML(XMLStreamReader reader, Workspace workspace) throws XMLStreamException;

    public void writeXML(XMLStreamWriter writer) throws XMLStreamException;
}
