package org.gephi.visualization.api.config;

import java.awt.Color;
import java.awt.Font;
import org.gephi.graph.api.NodeShape;
import org.gephi.visualization.api.selection.SelectionType;

/**
 *
 * @author Vojtech Bardiovsky
 */
public interface VizConfig {
    String ANTIALIASING = "VizConfig.antialiasing";
    String BACKGROUND_COLOR = "VizConfig.defaultBackgroundColor";
    String BLENDING = "VizConfig.blending";
    String CAMERA_CONTROL = "VizConfig.cameraControlEnable";
    String CLEAN_DELETED_MODELS = "VizConfig.cleanDeletedModels";
    String CONTEXT_MENU = "VizConfig.contextMenu";
    int DEFAULT_ANTIALIASING = 4;
    Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    boolean DEFAULT_BLENDING = true;
    boolean DEFAULT_CAMERA_CONTROL = true;
    boolean DEFAULT_CLEAN_DELETED_MODELS = true;
    boolean DEFAULT_CONTEXT_MENU = true;
    boolean DEFAULT_DISABLE_LOD = false;
    boolean DEFAULT_DRAGGING = true;
    boolean DEFAULT_EDGE_HAS_UNIQUE_COLOR = false;
    boolean DEFAULT_EDGE_LABELS = false;
    Color DEFAULT_EDGE_LABEL_COLOR = new Color(0.5F, 0.5F, 0.5F, 1.0F);
    Font DEFAULT_EDGE_LABEL_FONT = new Font("Arial", Font.BOLD, 20);
    float DEFAULT_EDGE_SCALE = 1.0F;
    Color DEFAULT_EDGE_UNIQUE_COLOR = new Color(0.5F, 0.5F, 0.5F, 0.5F);
    boolean DEFAULT_GLJPANEL = false;
    boolean DEFAULT_HIDE_NONSELECTED_EDGES = false;
    boolean DEFAULT_HIGHLIGHT = true;
    boolean DEFAULT_HIGHTLIGHT_ANIMATION = true;
    Color DEFAULT_HIGHTLIGHT_COLOR = new Color(0.95F, 0.95F, 0.95F, 1.0F);
    boolean DEFAULT_LABEL_ANTIALIASED = true;
    boolean DEFAULT_LABEL_FRACTIONAL_METRICS = true;
    boolean DEFAULT_LABEL_MIPMAP = true;
    boolean DEFAULT_LABEL_SELECTION_ONLY = false;
    float DEFAULT_META_EDGE_SCALE = 1.0F;
    int DEFAULT_MOUSE_SELECTION_DIAMETER = 30;
    boolean DEFAULT_MOUSE_SELECTION_WHILE_DRAGGING = false;
    boolean DEFAULT_MOUSE_SELECTION_ZOOM_PROPORTIONAL = false;
    boolean DEFAULT_NEIGHBOUR_SELECT = false;
    boolean DEFAULT_NODE_LABELS = false;
    Color DEFAULT_NODE_LABEL_COLOR = new Color(0.0F, 0.0F, 0.0F, 1.0F);
    Font DEFAULT_NODE_LABEL_FONT = new Font("Arial", Font.BOLD, 20);
    Color DEFAULT_NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR = new Color(0.2F, 1.0F, 0.3F);
    Color DEFAULT_NODE_SELECTED_UNIQUE_COLOR = new Color(0.8F, 0.2F, 0.2F);
    int DEFAULT_OCTREE_DEPTH = 5;
    int DEFAULT_OCTREE_WIDTH = 50000;
    boolean DEFAULT_PAUSE_LOOP_MOUSE_OUT = false;
    boolean DEFAULT_RECTANGLE_SELECTION = false;
    Color DEFAULT_RECTANGLE_SELECTION_COLOR = new Color(0.16F, 0.48F, 0.81F, 0.2F);
    boolean DEFAULT_REDUCE_FPS_MOUSE_OUT = true;
    int DEFAULT_REDUCE_FPS_MOUSE_OUT_VALUE = 20;
    Color DEFAULT_SELECTEDEDGE_BOTH_COLOR = new Color(248, 215, 83, 255);
    boolean DEFAULT_SELECTEDEDGE_HAS_COLOR = false;
    Color DEFAULT_SELECTEDEDGE_IN_COLOR = new Color(32, 95, 154, 255);
    Color DEFAULT_SELECTEDEDGE_OUT_COLOR = new Color(196, 66, 79, 255);
    NodeShape DEFAULT_NODE_SHAPE = NodeShape.CIRCLE;
    boolean DEFAULT_SELECTEDNODE_UNIQUE_COLOR = false;
    boolean DEFAULT_SELECTION = true;
    boolean DEFAULT_SHOW_EDGES = true;
    boolean DEFAULT_SHOW_FPS = true;
    boolean DEFAULT_SHOW_HULLS = true;
    boolean DEFAULT_TOOLBAR = true;
    boolean DEFAULT_USE_3D = false;
    boolean DEFAULT_VIZBAR = true;
    boolean DEFAULT_WIREFRAME = false;
    String DISABLE_LOD = "VizConfig.disableLOD";
    String DRAGGING = "VizConfig.draggingEnable";
    String EDGE_HAS_UNIQUE_COLOR = "VizConfig.defaultEdgeHasUniColor";
    String EDGE_LABELS = "VizConfig.defaultShowEdgeLabels";
    String EDGE_LABEL_COLOR = "VizConfig.defaultEdgeLabelColor";
    String EDGE_LABEL_FONT = "VizConfig.defaultEdgeLabelFont";
    String EDGE_SCALE = "VizConfig.defaultEdgeScale";
    String EDGE_UNIQUE_COLOR = "VizConfig.defaultEdgeUniColor";
    String GLJPANEL = "VizConfig.useGLJPanel";
    String HIDE_NONSELECTED_EDGES = "VizConfig.defaultHideNonSelectedEdges";
    String HIGHLIGHT = "VizConfig.defaultLightenNonSelectedAuto";
    String HIGHTLIGHT_ANIMATION = "VizConfig.lightenNonSelectedAnimation";
    String HIGHTLIGHT_COLOR = "VizConfig.lightenNonSelectedColor";
    String LABEL_ANTIALIASED = "VizConfig.labelAntialiased";
    String LABEL_FRACTIONAL_METRICS = "VizConfig.labelFractionalMetrics";
    String LABEL_MIPMAP = "VizConfig.labelMipMap";
    String LABEL_SELECTION_ONLY = "VizConfig.defaultShowLabelOnSelectedOnly";
    String META_EDGE_SCALE = "VizConfig.defaultMetaEdgeScale";
    String MOUSE_SELECTION_DIAMETER = "VizConfig.mouseSelectionDiameter";
    String MOUSE_SELECTION_WHILE_DRAGGING = "VizConfig.mouseSelectionUpdateWhileDragging";
    String MOUSE_SELECTION_ZOOM_PROPORTIONAL = "VizConfig.mouseSelectionZoomProportionnal";
    String NEIGHBOUR_SELECT = "VizConfig.defaultAutoSelectNeighbor";
    String NODE_LABELS = "VizConfig.defaultShowNodeLabels";
    String NODE_LABEL_COLOR = "VizConfig.defaultNodeLabelColor";
    String NODE_LABEL_FONT = "VizConfig.defaultNodeLabelFont";
    String NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR = "VizConfig.uniColorSelectedNeigborColor";
    String NODE_SELECTED_UNIQUE_COLOR = "VizConfig.uniColorSelectedColor";
    String OCTREE_DEPTH = "VizConfig.octreeDepth";
    String OCTREE_WIDTH = "VizConfig.octreeWidth";
    String PAUSE_LOOP_MOUSE_OUT = "VizConfig.pauseLoopWhenMouseOut";
    String RECTANGLE_SELECTION = "VizConfig.rectangleSelection";
    String RECTANGLE_SELECTION_COLOR = "VizConfig.rectangleSelectionColor";
    String REDUCE_FPS_MOUSE_OUT = "VizConfig.reduceFpsWhenMouseOut";
    String REDUCE_FPS_MOUSE_OUT_VALUE = "VizConfig.reduceFpsWhenMouseOutValue";
    String SELECTEDEDGE_BOTH_COLOR = "VizConfig.defaultEdgeBothSelectedColor";
    String SELECTEDEDGE_HAS_COLOR = "VizConfig.defaultEdgeSelectionColor";
    String SELECTEDEDGE_IN_COLOR = "VizConfig.defaultEdgeInSelectedColor";
    String SELECTEDEDGE_OUT_COLOR = "VizConfig.defaultEdgeOutSelectedColor";
    String SELECTEDNODE_UNIQUE_COLOR = "VizConfig.defaultHideNonSelectedEdges";
    String SELECTION = "VizConfig.selectionEnable";
    String SHOW_EDGES = "VizConfig.defaultShowEdges";
    String SHOW_FPS = "VizConfig.showFPS";
    String SHOW_HULLS = "VizConfig.defaultShowHulls";
    String TOOLBAR = "VizConfig.toolbar";
    String USE_3D = "VizConfig.defaultUse3d";
    String VIZBAR = "VizConfig.showVizVar";
    String WIREFRAME = "VizConfig.wireFrame";

    int getAntialiasing();

    Color getDefaultBackgroundColor();

    float[] getDefaultCameraPosition();

    float[] getDefaultCameraTarget();

    Color getDefaultEdgeBothSelectedColor();

    Color getDefaultEdgeInSelectedColor();

    Color getDefaultEdgeLabelColor();

    Font getDefaultEdgeLabelFont();

    Color getDefaultEdgeOutSelectedColor();

    float getDefaultEdgeScale();

    Color getDefaultEdgeUniColor();

    float getDefaultMetaEdgeScale();

    Color getDefaultNodeLabelColor();

    Font getDefaultNodeLabelFont();

    NodeShape getDefaultNodeShape();

    float[] getLightenNonSelectedColor();

    float getLightenNonSelectedFactor();

    int getMouseSelectionDiameter();

    float[] getNodeSelectedColor();

    int getOctreeDepth();

    int getOctreeWidth();

    Color getRectangleSelectionColor();

    int getReduceFpsWhenMouseOutValue();

    float[] getUniColorSelectedColor();

    float[] getUniColorSelectedNeigborColor();

    boolean isBlendCinema();

    boolean isBlending();

    boolean isCameraControlEnabled();

    boolean isCleanDeletedModels();

    boolean isContextMenu();

    boolean isDefaultAdjustByText();

    boolean isDefaultAutoSelectNeighbor();

    boolean isDefaultCulling();

    boolean isDefaultEdgeHasUniColor();

    boolean isDefaultEdgeSelectionColor();

    boolean isDefaultHideNonSelectedEdges();

    boolean isDefaultLightenNonSelectedAuto();

    boolean isDefaultLighting();

    boolean isDefaultMaterial();

    boolean isDefaultRotatingEnable();

    boolean isDefaultShowEdgeLabels();

    boolean isDefaultShowEdges();

    boolean isDefaultShowHulls();

    boolean isDefaultShowLabelOnSelectedOnly();

    boolean isDefaultShowNodeLabels();

    boolean isDefaultUniColorSelected();

    boolean isDefaultUse3d();

    boolean isDisableLOD();

    boolean isDraggingEnabled();

    boolean isEnableAutoSelect();

    boolean isLabelAntialiased();

    boolean isLabelFractionalMetrics();

    boolean isLabelMipMap();

    boolean isLightenNonSelected();

    boolean isLightenNonSelectedAnimation();

    boolean isLineSmooth();

    boolean isLineSmoothNicest();

    boolean isMouseSelectionZoomProportionnal();

    boolean isPauseLoopWhenMouseOut();

    boolean isPointSmooth();

    boolean isPropertiesbar();

    boolean isReduceFpsWhenMouseOut();

    boolean isSelectionEnabled();

    boolean isShowArrows();

    boolean isShowFPS();

    boolean isShowVizVar();

    boolean isToolbar();

    boolean isUseGLJPanel();

    boolean isUseLabelRenderer3d();

    boolean isWireFrame();

    boolean isDirectMouseSelection();

    boolean isMovementEnabled();

    SelectionType getSelectionType();

    void setDisableLOD(boolean disableLOD);

    void setDraggingEnable(boolean draggingEnable);

    void setEnableAutoSelect(boolean enableAutoSelect);

    void setLightenNonSelected(boolean lightenNonSelected);

    void setLightenNonSelectedFactor(float lightenNonSelectedFactor);

    void setPauseLoopWhenMouseOut(boolean pauseLoopWhenMouseOut);

    void setSelectionEnable(boolean selectionEnable);

    void setSelectionType(SelectionType selectionType);

    void setDirectMouseSelection(boolean directMouseSelection);

    void setMovementEnabled(boolean enabled);


}
