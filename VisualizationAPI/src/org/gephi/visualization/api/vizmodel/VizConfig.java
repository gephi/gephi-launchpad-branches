/*
Copyright 2008-2010 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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
import org.gephi.math.Vec2;
import org.gephi.math.Vec3;

/**
 * Visualization configuration interface that holds every visualization property
 * name.
 * <p>
 * Use by calling the desired <code>getProperty()</code> method providing a 
 * String constant as a parameter, 
 * e.g.: <code>getBooleanProperty(VizConfig.CONTEXT_MENU)</code>
 * </p>
 * <p>
 * To add a visualization property, first create its name, and add it here. Then
 * initialize this property in the implementation of this class (most possibly
 * <code>VizConfigImpl</code>).
 * </p>
 * <p>
 * If an important property controlled with "fast accessible" UI control is
 * being added, add its getters and setters to the VizModel.
 * </p>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public interface VizConfig {
    
    // State and variable names
    // Model variables
    String ADJUST_BY_TEXT = "VizConfig.adjustByText";
    String ANTIALIASING = "VizConfig.antialiasing";
    String AUTO_SELECT_NEIGHBOUR = "VizConfig.autoSelectNeighbor";
    String BACKGROUND_COLOR = "VizConfig.backgroundColor";
    String BLENDING = "VizConfig.blending";
    String CLEAN_DELETED_MODELS = "VizConfig.cleanDeletedModels";
    String CONTEXT_MENU = "VizConfig.contextMenu";
    String CULLING = "VizConfig.culling";
    String DISABLE_LOD = "VizConfig.disableLOD";
    String EDGE_HAS_UNIQUE_COLOR = "VizConfig.edgeHasUniColor";
    String EDGE_LABELS = "VizConfig.showEdgeLabels";
    String EDGE_LABEL_COLOR = "VizConfig.edgeLabelColor";
    String EDGE_LABEL_FONT = "VizConfig.edgeLabelFont";
    String EDGE_LABEL_SIZE_FACTOR = "VizConfig.edgeLabelSizeFactor";
    String EDGE_SCALE = "VizConfig.edgeScale";
    String EDGE_TEXT_COLUMNS = "VizConfig.edgeTextColumns";
    String EDGE_UNIQUE_COLOR = "VizConfig.edgeUniColor";
    String GLJPANEL = "VizConfig.useGLJPanel";
    String HIDE_NONSELECTED_EDGES = "VizConfig.hideNonSelectedEdges";
    String HIGHLIGHT_NON_SELECTED_ANIMATION = "VizConfig.highlightNonSelectedAnimation";
    String HIGHLIGHT_NON_SELECTED_COLOR = "VizConfig.highlightNonSelectedColor";
    String HIGHLIGHT_NON_SELECTED_FACTOR = "VizConfig.highlightNonSelectedFactor";
    String HIGHLIGHT_NON_SELECTED = "VizConfig.highlightNonSelected";
    String LABEL_ANTIALIASED = "VizConfig.labelAntialiased";
    String LABEL_FRACTIONAL_METRICS = "VizConfig.labelFractionalMetrics";
    String LABEL_MIPMAP = "VizConfig.labelMipMap";
    String LABEL_SELECTION_ONLY = "VizConfig.showLabelOnSelectedOnly";
    String LIGHTING = "VizConfig.lighting";
    String MATERIAL = "VizConfig.material";
    String META_EDGE_SCALE = "VizConfig.metaEdgeScale";
    String NODE_GLOBAL_SHAPE = "VizConfig.nodeGlobalShape";
    String NODE_LABELS = "VizConfig.showNodeLabels";
    String NODE_LABEL_COLOR = "VizConfig.nodeLabelColor";
    String NODE_LABEL_FONT = "VizConfig.nodeLabelFont";
    String NODE_LABEL_SIZE_FACTOR = "VizConfig.nodeLabelSizeFactor";
    String NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR = "VizConfig.uniColorSelectedNeigborColor";
    String NODE_SELECTED_UNIQUE_COLOR = "VizConfig.uniColorSelectedColor";
    String NODE_TEXT_COLUMNS = "VizConfig.nodeTextColumns";
    String OCTREE_DEPTH = "VizConfig.octreeDepth";
    String OCTREE_WIDTH = "VizConfig.octreeWidth";
    String PAUSE_LOOP_MOUSE_OUT = "VizConfig.pauseLoopWhenMouseOut";
    String PROPERTIES_BAR = "VizConfig.propertiesBar";
    String RECTANGLE_SELECTION = "VizConfig.rectangleSelection";
    String RECTANGLE_SELECTION_COLOR = "VizConfig.rectangleSelectionColor";
    String REDUCE_FPS_MOUSE_OUT = "VizConfig.reduceFpsWhenMouseOut";
    String REDUCE_FPS_MOUSE_OUT_VALUE = "VizConfig.reduceFpsWhenMouseOutValue";
    String SELECTEDEDGE_BOTH_COLOR = "VizConfig.edgeBothSelectedColor";
    String SELECTEDEDGE_HAS_COLOR = "VizConfig.edgeSelectionColor";
    String SELECTEDEDGE_IN_COLOR = "VizConfig.edgeInSelectedColor";
    String SELECTEDEDGE_OUT_COLOR = "VizConfig.edgeOutSelectedColor";
    String SELECTEDNODE_UNIQUE_COLOR = "VizConfig.hideNonSelectedEdges";
    String SHOW_EDGES = "VizConfig.showEdges";
    String SHOW_FPS = "VizConfig.showFPS";
    String SHOW_HULLS = "VizConfig.showHulls";
    String TOOLBAR = "VizConfig.toolbar";
    String USE_3D = "VizConfig.use3d";
    String VIZBAR = "VizConfig.showVizVar";
    String WIREFRAME = "VizConfig.wireFrame";

    // Other configuration
    String CAMERA_CONTROL = "VizConfig.cameraControlEnable";
    String CAMERA_POSITION = "VizConfig.cameraPosition";
    String CAMERA_TARGET = "VizConfig.cameraTarget";
    String DIRECT_MOUSE_SELECTION = "VizConfig.directMouseSelection";
    String DRAGGING = "VizConfig.draggingEnable";
    String MOUSE_SELECTION_DIAMETER = "VizConfig.mouseSelectionDiameter";
    String MOUSE_SELECTION_WHILE_DRAGGING = "VizConfig.mouseSelectionUpdateWhileDragging";
    String MOUSE_SELECTION_ZOOM_PROPORTIONAL = "VizConfig.mouseSelectionZoomProportionnal";
    String NODE_DRAGGING = "VizConfig.nodeDraggingEnabled";
    String ROTATING = "VizConfig.rotatingEnabled";
    String SELECTION = "VizConfig.selectionEnable";
    String SELECTION_TYPE = "VizConfig.selectionType";
    String ZOOM_FACTOR = "VizConfig.zoomFactor";
    
    public void setProperty(String key, String value);

    public void setProperty(String key, Integer value);

    public void setProperty(String key, Float value);

    public void setProperty(String key, Boolean value);

    public void setProperty(String key, Vec2 value);
    
    public void setProperty(String key, Vec3 value);
    
    public void setProperty(String key, Font font);

    public void setProperty(String key, Color color);

    public void setProperty(String key, Enum value);
    
    public String getStringProperty(String key);

    public Integer getIntProperty(String key);

    public Float getFloatProperty(String key);

    public Boolean getBooleanProperty(String key);

    public Vec2 getVec2Property(String key);
    
    public Vec3 getVec3Property(String key);
    
    public Font getFontProperty(String key);

    public Color getColorProperty(String key);
    
    public <T> T getEnumProperty(Class<T> type, String key);
    
    public Class getPropertyType(String key);
    
    public static class PropertyNotAvailableException extends RuntimeException {
        
        public PropertyNotAvailableException(String message) {
            super(message);
        }
        
    }

}
