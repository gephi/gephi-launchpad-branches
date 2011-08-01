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

/**
 * Defines how the background is repeated on the screen. Inspired by the CSS 
 * background-repeat property.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public enum BackgroundRepeat {
    /**
     * The background image is repeated on the screen in both directions.
     */
    REPEAT,
    
    /**
     * The background image is repeated on the screen in the x-direction, but 
     * it is repeated only once along the y-direction.
     */
    REPEAT_X,
    
    /**
     * The background image is repeated on the screen in the y-direction, but 
     * it is repeated only once along the x-direction.
     */
    REPEAT_Y,
    
    /**
     * A single copy of the background image is shown on the screen.
     */
    NO_REPEAT,
}
