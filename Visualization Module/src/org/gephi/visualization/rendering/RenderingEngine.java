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
package org.gephi.visualization.rendering;

import com.jogamp.opengl.util.FPSAnimator;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import org.gephi.visualization.controller.VisualizationControllerImpl;
import org.gephi.visualization.data.FrameDataBridge;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RenderingEngine {
    
    private final GLCanvas drawable;
    
    private final FPSAnimator animator;
    
    private final VisualizationControllerImpl controller;
    
    private final FrameDataBridge bridge;

    public RenderingEngine(VisualizationControllerImpl controller) {
        this.controller = controller;
        
        final GLCapabilities caps = createGLCapabilities();
        this.drawable = new GLCanvas(caps);
        this.drawable.setAutoSwapBufferMode(true);
        
        this.drawable.addKeyListener(controller);
	this.drawable.addMouseListener(controller);
        this.drawable.addMouseMotionListener(controller);
        this.drawable.addMouseWheelListener(controller);
        
        this.animator = new FPSAnimator(this.drawable, 30);
        
        this.bridge = new FrameDataBridge();
    }
    
    private GLCapabilities createGLCapabilities() {
        final GLCapabilities result = new GLCapabilities(GLProfile.getDefault());
                
        result.setDoubleBuffered(true);
        result.setHardwareAccelerated(true);
        /*
        final VizConfig config = Lookup.getDefault().lookup(VizConfig.class);
        final int antiAA = config.getIntProperty(VizConfig.ANTIALIASING);
        if (antiAA > 0) {
            result.setSampleBuffers(true);
            result.setNumSamples(antiAA);
        } else {
            result.setSampleBuffers(false);
        }
        */
        return result;
    }
}
