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
 * Defines if the background is defined in screen coordinate or world 
 * coordinate. Inspired by the CSS background-attachment property with names
 * adapted to Gephi application.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public enum BackgroundAttachment {
    /**
     * The background image position is specified in world coordinates (the same
     * as the graph) and it moves when the camera moves.
     */
    WORLD,
    
    /**
     * The background image position is defined in screen coordinates and it is 
     * fixed on the screen. When the camera position is changed, the background 
     * remains at the same position of the screen.
     */
    SCREEN,
}
