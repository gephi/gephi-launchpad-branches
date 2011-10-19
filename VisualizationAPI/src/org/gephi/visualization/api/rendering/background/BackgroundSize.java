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

import org.gephi.math.Vec2;

/**
 * Defines how big the background image is. Inspired by the CSS 
 * background-size property.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class BackgroundSize {
    /**
     * See {@link Mode} for details.
     */
    public final Mode mode;
    
    /**
     * 2D vector containing the image size. <code>this.mode</code> 
     * determines how the vector should be interpreted to get the final 
     * size.
     */
    public final Vec2 parameter;

    /**
     * Creates a background size without specifying any parameter. The 
     * default zero parameter is used. It is useful when using modes which 
     * ignores the parameter.
     * 
     * @param mode the  background size mode
     */
    public BackgroundSize(Mode mode) {
        this(mode, Vec2.ZERO);
    }
    
    /**
     * Creates a background size from a mode and parameter.
     * 
     * @param mode the background size mode
     * @param parameter the parameter
     */
    public BackgroundSize(Mode mode, Vec2 parameter) {
        this.mode = mode;
        this.parameter = parameter;
    }    
    
    /**
     * Defines how the parameter should be interpreted or sets a "fixed" size.
     * Only LENGHT and PERCENTAGE can be used in world coordinates.
     */
    public static enum Mode {
        /**
         * The parameter contains the absolute size of the image. If the 
         * parameter is zero the image size is used, if only one of the two 
         * components of the parameter vector is zero, the size in that 
         * component is automatically computed to maintains the image ratio.
         */
        LENGTH,
        
        /**
         * The parameter contains the scaling factor applied to the image. A 
         * value of 1.0 in one of the components means a 100% size. If the 
         * parameter is zero the parameter (1.0, 1.0) is used instead. If only
         * one of the two components of the parameter vector is zero, the size 
         * in that component is automatically computed to maintains the image 
         * ratio, i.e. the value of the other component is used.
         */
        PERCENTAGE,
        
        /**
         * Scales the image to the smallest size such that the screen is 
         * completely contained in the image. It is equivalent to <br />
         * 
         * <code>PERCENTAGE (max(screenWidth/imageWidth, screenHeight/imageHeight), 0)</code>. 
         */
        COVER,
        
        /**
         * Scales the image to the largest size such that the image is 
         * completely contained in the screen. It is equivalent to <br />
         * 
         * <code>PERCENTAGE (min(screenWidth/imageWidth, screenHeight/imageHeight), 0)</code>. 
         */
        CONTAIN;
        
        @Override
        public String toString() {
            switch (this) {
                case LENGTH: return "Length";
                case PERCENTAGE: return "Percentage";
                case COVER: return "Cover";
                case CONTAIN: return "Contain";
            }
            return null;
        }
        
        public boolean requiresParameter() {
            return this == LENGTH || this == PERCENTAGE;
        }
    }
}
