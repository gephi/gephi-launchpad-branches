/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either vers  ion 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.visualization.api.rendering.background;

import org.gephi.visualization.api.color.Color;

/**
 * Simple immutable class representing the current background. Inspired by CSS
 * properties. It does not implement background-clip and background-origin since
 * there are no border/padding/content boxes.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class Background {
    /**
     * Background color. It is used for clearing the framebuffer.
     */
    public final Color color;
    
    /**
     * URL of the background image.
     */
    public final String image;
    
    /**
     * Background position property. See {@link BackgroundPosition}.
     */
    public final BackgroundPosition position;
    
    /**
     * Background size property. See {@link BackgroundSize}.
     */
    public final BackgroundSize size;
    
    /**
     * Background repeat property. See {@link BackgroundRepeat}.
     */
    public final BackgroundRepeat repeat;
    
    /**
     * Background attachment property. See {@link BackgroundAttachment}.
     */
    public final BackgroundAttachment attachment;

    /**
     * Defines a background from its properties.
     * 
     * @param color the background color
     * @param image the background image (url)
     * @param position the background position
     * @param size the background size
     * @param repeat the background repeat property
     * @param attachment the background attachment property
     */
    public Background(java.awt.Color color, String image, BackgroundPosition position, BackgroundSize size, BackgroundRepeat repeat, BackgroundAttachment attachment) {
        this.color = new Color(color);
        this.image = image;
        this.position = position;
        this.size = size;
        this.repeat = repeat;
        this.attachment = attachment;
    }
    
    public Background deriveBackground(java.awt.Color color) {
        return new Background(color, image, position, size, repeat, attachment);
    }
    
    public java.awt.Color getColor() {
        return this.color.toAWTColor();
    }
    
}
