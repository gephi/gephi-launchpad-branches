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
package org.gephi.visualization.rendering.apiimpl;

import java.awt.Component;
import java.util.List;
import org.gephi.visualization.api.rendering.RenderingController;
import org.gephi.visualization.api.rendering.ScreenshotSettings;
import org.openide.util.lookup.ServiceProvider;

/**
 * Default implementation of the RenderingController service.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
@ServiceProvider(service = RenderingController.class)
public class RenderingControllerImpl implements RenderingController {

    @Override
    public void setFPS(int fps) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getFPS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setScreenshotSettings(ScreenshotSettings screenshotSettings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ScreenshotSettings setScreenshotSettings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Component renderingCanvas() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startRendering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopRendering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void makeScreenshot(String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAASamples(int samples) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getAAMethods() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAAMethod(String method) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAASamples() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
