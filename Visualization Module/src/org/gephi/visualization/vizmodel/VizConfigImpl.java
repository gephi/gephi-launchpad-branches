/*
Copyright 2008-2011 Gephi
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
import java.util.Map;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.graph.api.NodeShape;
import org.gephi.math.linalg.Vec2;
import org.gephi.math.linalg.Vec3;
import org.gephi.ui.utils.ColorUtils;
import org.gephi.ui.utils.FontUtils;
import org.gephi.visualization.api.rendering.ScreenshotSettings;
import org.gephi.visualization.api.rendering.background.Background;
import org.gephi.visualization.api.rendering.background.BackgroundAttachment;
import org.gephi.visualization.api.rendering.background.BackgroundPosition;
import org.gephi.visualization.api.rendering.background.BackgroundRepeat;
import org.gephi.visualization.api.rendering.background.BackgroundSize;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.vizmodel.GraphLimits;
import org.gephi.visualization.camera.Camera2d;
import org.openide.util.NbPreferences;

/**
 * Basic implementation of VizConfig interface. Initializes all values from user
 * preferences and hard-coded details.
 *
 * @author Mathieu Bastian
 * @author Vojtech Bardiovsky
 */
public class VizConfigImpl implements VizConfig {

    private final Map<String, Object> modelData;
    
    public VizConfigImpl(VizModelImpl vizModel) {
        this.modelData = vizModel.getModelData();
        
        // Initialize default values
        // Default configuration - loaded in the VizModel
        initDefault(ADJUST_BY_TEXT, false);
        initDefault(ANTIALIASING, 4);
        initDefault(AUTO_SELECT_NEIGHBOUR, false);
        initDefault(BACKGROUND, new Background(Color.WHITE, null, new BackgroundPosition(BackgroundPosition.Mode.LEFT_TOP), new BackgroundSize(BackgroundSize.Mode.CONTAIN), BackgroundRepeat.NO_REPEAT, BackgroundAttachment.SCREEN));
        initDefault(BLENDING, true);
        initDefault(CLEAN_DELETED_MODELS, true);
        initDefault(CONTEXT_MENU, true);
        initDefault(CULLING, false);
        initDefault(DISABLE_LOD, false);
        initDefault(EDGE_HAS_UNIQUE_COLOR, false);
        initDefault(EDGE_LABELS, false);
        initDefault(EDGE_LABEL_COLOR, new Color(0.5F, 0.5F, 0.5F, 1.0F));
        initDefault(EDGE_LABEL_FONT, new Font("Arial", Font.BOLD, 20));
        initDefault(EDGE_LABEL_SIZE_FACTOR, 0.5f);
        initDefault(EDGE_SCALE, 1.0F);
        initDefault(EDGE_TEXT_COLUMNS, new AttributeColumn[0]);
        initDefault(EDGE_UNIQUE_COLOR, new Color(0.5F, 0.5F, 0.5F, 0.5F));
        initDefault(GLJPANEL, false);
        initDefault(GRAPH_LIMITS, new GraphLimits());
        initDefault(HIDE_NONSELECTED_EDGES, false);
        initDefault(HIGHLIGHT_NON_SELECTED_ENABLED, true);
        initDefault(LABEL_ANTIALIASED, true);
        initDefault(LABEL_FRACTIONAL_METRICS, true);
        initDefault(LABEL_MIPMAP, true);
        initDefault(LABEL_SELECTION_ONLY, false);
        initDefault(META_EDGE_SCALE, 1.0F);
        initDefault(NODE_GLOBAL_SHAPE, NodeShape.CIRCLE);
        initDefault(NODE_LABELS, false);
        initDefault(NODE_LABEL_COLOR, new Color(0.0F, 0.0F, 0.0F, 1.0F));
        initDefault(NODE_LABEL_FONT, new Font("Arial", Font.BOLD, 20));
        initDefault(NODE_LABEL_SIZE_FACTOR, 0.5f);
        initDefault(NODE_NEIGHBOR_SELECTED_UNIQUE_COLOR, new Color(0.2F, 1.0F, 0.3F));
        initDefault(NODE_SELECTED_UNIQUE_COLOR, new Color(0.8F, 0.2F, 0.2F));
        initDefault(NODE_TEXT_COLUMNS, new AttributeColumn[0]);
        initDefault(PAUSE_LOOP_MOUSE_OUT, false);
        initDefault(PROPERTIES_BAR, true);
        initDefault(RECTANGLE_SELECTION, false);
        initDefault(RECTANGLE_SELECTION_COLOR, new Color(0.16F, 0.48F, 0.81F, 0.2F));
        initDefault(REDUCE_FPS_MOUSE_OUT, true);
        initDefault(REDUCE_FPS_MOUSE_OUT_VALUE, 20);
        initDefault(SELECTEDEDGE_BOTH_COLOR, new Color(248, 215, 83, 255));
        initDefault(SELECTEDEDGE_HAS_COLOR, false);
        initDefault(SELECTEDEDGE_IN_COLOR, new Color(32, 95, 154, 255));
        initDefault(SELECTEDEDGE_OUT_COLOR, new Color(196, 66, 79, 255));
        initDefault(SELECTEDNODE_UNIQUE_COLOR, false);
        initDefault(SHOW_EDGES, true);
        initDefault(SHOW_FPS, true);
        initDefault(SHOW_HULLS, true);
        initDefault(TOOLBAR, true);
        initDefault(CAMERA_USE_3D, false);
        initDefault(VIZBAR, true);
        initDefault(WIREFRAME, false);

        // Other configuration
        initDefault(CAMERA_CONTROL, true);
        initDefault(CAMERA, new Camera2d());
        initDefault(DIRECT_MOUSE_SELECTION, true);
        initDefault(DRAGGING, true);
        initDefault(HIGHLIGHT_NON_SELECTED, false);
        initDefault(HIGHLIGHT_NON_SELECTED_ANIMATION, true);
        initDefault(HIGHLIGHT_NON_SELECTED_COLOR, new Color(0.95F, 0.95F, 0.95F, 1.0F));
        initDefault(HIGHLIGHT_NON_SELECTED_FACTOR, 0.5f);
        initDefault(MOUSE_SELECTION_DIAMETER, 1);
        initDefault(MOUSE_SELECTION_WHILE_DRAGGING, false);
        initDefault(MOUSE_SELECTION_ZOOM_PROPORTIONAL, false);
        initDefault(NODE_DRAGGING, false);
        initDefault(OCTREE_DEPTH, 5);
        initDefault(OCTREE_WIDTH, 50000);
        initDefault(ROTATING, true);
        initDefault(SCREENSHOT_SETTINGS, new ScreenshotSettings(1024, 876, false, null));
        initDefault(SELECTION, true);
        initDefault(SELECTION_TYPE, SelectionType.NONE);
        initDefault(ZOOM_FACTOR, 0.8f);
    }
    
    private void initDefault(String property, Object defaultValue) {
        if (defaultValue instanceof Boolean) {
            boolean value = NbPreferences.forModule(VizConfig.class).getBoolean("default_" + property, (Boolean) defaultValue);
            modelData.put(property, value);
        } else if (defaultValue instanceof Integer) {
            int value = NbPreferences.forModule(VizConfig.class).getInt("default_" + property, (Integer) defaultValue);
            modelData.put(property, value);
        } else if (defaultValue instanceof Float) {
            float value = NbPreferences.forModule(VizConfig.class).getFloat("default_" + property, (Float) defaultValue);
            modelData.put(property, value);
        } else if (defaultValue instanceof Color) {
            Color value = ColorUtils.decode(NbPreferences.forModule(VizConfig.class).get("default_" + property, ColorUtils.encode(((Color) defaultValue))));
            modelData.put(property, value);
        } else if (defaultValue instanceof Font) {
            Font value = Font.decode(NbPreferences.forModule(VizConfig.class).get("default_" + property, FontUtils.encode(((Font) defaultValue))));
            modelData.put(property, value);
        } else if (defaultValue instanceof Vec3) {
            Vec3 value = Vec3.fromString(NbPreferences.forModule(VizConfig.class).get("default_" + property, ((Vec3) defaultValue).toString()));
            modelData.put(property, value);
        } else if (defaultValue instanceof Vec2) {
            Vec2 value = Vec2.fromString(NbPreferences.forModule(VizConfig.class).get("default_" + property, ((Vec2) defaultValue).toString()));
            modelData.put(property, value);
        } else if (defaultValue instanceof Enum) {
            Enum value = Enum.valueOf(((Enum<?>) defaultValue).getDeclaringClass(), NbPreferences.forModule(VizConfig.class).get("default_" + property, ((Enum<?>) defaultValue).name()));
            modelData.put(property, value);
        } else {
            modelData.put(property, defaultValue);
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        modelData.put(key, value);
    }
    
    @Override
    public String getStringProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof String)) {
            throw new PropertyNotAvailableException(key, !(val instanceof String));
        }
        return (String) val;
    }

    @Override
    public Integer getIntProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Integer)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Integer));
        }
        return (Integer) val;
    }

    @Override
    public Float getFloatProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Float)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Float));
        }
        return (Float) val;
    }

    @Override
    public Boolean getBooleanProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Boolean)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Boolean));
        }
        return (Boolean) val;
    }

    @Override
    public Vec2 getVec2Property(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Vec2)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Vec2));
        }
        return (Vec2) val;
    }

    @Override
    public Vec3 getVec3Property(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Vec3)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Vec3));
        }
        return (Vec3) val;
    }
    
    @Override
    public Font getFontProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Font)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Font));
        }
        return (Font) val;
    }

    @Override
    public Color getColorProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof Color)) {
            throw new PropertyNotAvailableException(key, !(val instanceof Color));
        }
        return (Color) val;
    }

    @Override
    public <T> T getProperty(Class<T> type, String key) {
        Object val = modelData.get(key);
        if (val == null || !type.isAssignableFrom(val.getClass())) {
            throw new PropertyNotAvailableException(key, !type.isAssignableFrom(val.getClass()));
        }
        return type.cast(val);
    }

    @Override
    public Class getPropertyType(String key) {
        Object val = modelData.get(key);
        if (val == null) {
            throw new PropertyNotAvailableException(key, false);
        }
        return val.getClass();
    }

    protected AttributeColumn[] getAttributeColumnArrayProperty(String key) {
        Object val = modelData.get(key);
        if (val == null || !(val instanceof AttributeColumn[])) {
            throw new PropertyNotAvailableException(key, !(val instanceof AttributeColumn[]));
        }
        return (AttributeColumn[]) val;
    }
    
}
