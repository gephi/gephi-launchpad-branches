/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
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
package org.gephi.visualization.api.rendering.background;

import org.gephi.math.linalg.Vec2;

/**
 * Defines how the background image is positioned on the screen or world. 
 * Inspired by the CSS background-position property.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class BackgroundPosition {
    /**
     * See {@link Mode} for details.
     */
    public final Mode mode;
    
    /**
     * 2D vector containing the image position. <code>this.mode</code> 
     * determines how the vector should be interpreted to get the final 
     * position.
     */
    public final Vec2 parameter;

    /**
     * Creates a background position without specifying any parameter. The 
     * default zero parameter is used. It is useful when using modes which 
     * ignores the parameter.
     * 
     * @param mode the  background position mode
     */
    public BackgroundPosition(Mode mode) {
        this(mode, Vec2.ZERO);
    }
    
    /**
     * Creates a background position from a mode and parameter.
     * 
     * @param mode the background position mode
     * @param parameter the parameter
     */
    public BackgroundPosition(Mode mode, Vec2 parameter) {
        this.mode = mode;
        this.parameter = parameter;
    }    
    
    /**
     * Defines how the parameter should be interpreted or sets a fixed position.
     * LEFT, RIGHT, TOP, BOTTOM mean the image should be aligned to the left, 
     * right, top or bottom borders. CENTER means the image should be centered,
     * i.e. the center of the image along some direction is aligned with the
     * center of the screen or the other axis in world coordinate. In world
     * coordinates only CENTER_CENTER and POINT can be used. 
     */
    public static enum Mode {
        LEFT_TOP,
        LEFT_CENTER,
        LEFT_BOTTOM,
        CENTER_TOP,
        CENTER_CENTER,
        CENTER_BOTTOM,
        RIGHT_TOP,
        RIGHT_CENTER,
        RIGHT_BOTTOM,
        
        /**
         * It requires floating point numbers where 1.0 means 100%.
         */
        PERCENTAGE,
        
        /**
         * Absolute position of the top left corner of the image.
         */
        POINT;
        
        @Override
        public String toString() {
            switch (this) {
                case LEFT_TOP: return "Left top";
                case LEFT_CENTER: return "Left center";
                case LEFT_BOTTOM: return "Left bottom";
                case CENTER_TOP: return "Center top";
                case CENTER_CENTER: return "Center center";
                case CENTER_BOTTOM: return "Center bottom";
                case RIGHT_TOP: return "Roght top";
                case RIGHT_CENTER: return "Right center";
                case RIGHT_BOTTOM: return "Right bottom";
                case PERCENTAGE: return "Percentage";
                case POINT: return "Point";
            }
            return null;
        }
        
        public boolean requiresParameter() {
            return this == POINT || this == PERCENTAGE;
        }
    }
}
