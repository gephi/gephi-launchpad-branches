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

import java.awt.Component;
import java.util.List;

/**
 * Controls the behaviour of the rendering engine.
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
    
    /**
     * Makes a screenshot of the screen using the current settings and saves it
     * in filename.
     * 
     * @param filename the filename of the screeshot file
     */
    public void makeScreenshot(String filename);
    
    /**
     * Returns the current component used for drawing or it creates a new one
     * if none exists.
     * 
     * @return the current component used for drawing or <code>null</code> if
     *         it fails to create it
     */
    public Component renderingCanvas();
    
    /**
     * Starts the rendering loop. It also creates a new rendering engine if none
     * exists.
     */
    public void startRendering();
    
    /**
     * Stops the rendering loop and releases the used resources.
     */    
    public void stopRendering();
    
    /**
     * Sets the number of samples used with multisampling AA. It may requires a
     * reboot to make the changes active.
     * 
     * @param samples the number of samples
     */
    public void setAASamples(int samples);
    
    /**
     * Gets the number of samples used with multisampling AA.
     * 
     * @return samples the number of samples
     */
    public int getAASamples();
    
    /**
     * Returns the available AA methods.
     * 
     * @return the AA methods supported on this machine.
     */
    public List<String> getAAMethods();
    
    /**
     * Sets the desired AA method to use. It may requires a reboot to make the
     * changes active.
     * 
     * @param method 
     */
    public void setAAMethod(String method);
}
