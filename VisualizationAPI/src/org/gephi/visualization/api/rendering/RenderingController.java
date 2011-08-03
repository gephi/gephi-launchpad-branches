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
package org.gephi.visualization.api.rendering;

import java.awt.Image;
import org.gephi.visualization.api.rendering.background.Background;

/**
 * Controls the behaviours of the rendering engine.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface RenderingController {
    
    /**
     * Changes the rate at which the screen is displayed.
     * 
     * @param fps the new frame rate
     */
    public void setFPS(int fps);
    
    /**
     * Gets the current rate at which the screen is displayed.
     * 
     * @return the frame rate
     */
    public int getFPS();
    
    /**
     * Sets the new screenshot settings.
     * 
     * @param screenshotSettings the new screenshot settings
     */
    public void setScreenshotSettings(ScreenshotSettings screenshotSettings);
    
    /**
     * Gets the current screenshot settings.
     * 
     * @return the screenshot settings 
     */
    public ScreenshotSettings setScreenshotSettings();
    
    public String[] getSupportedImageFileFormats();
    
}
