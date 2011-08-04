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
import java.awt.Component;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import org.gephi.visualization.api.rendering.RenderingController;
import org.gephi.visualization.controller.VisualizationControllerImpl;
import org.gephi.visualization.data.FrameDataBridge;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RenderingEngine {
    
    static { GLProfile.initSingleton(true); }
    
    private final GLCanvas drawable;
    private final GLEventListener eventListener;
    private final RenderingScheduler scheduler;
    
    private final VisualizationControllerImpl controller;
    
    private final FrameDataBridge bridge;

    public RenderingEngine(VisualizationControllerImpl controller) {
        this.controller = controller;
        
        final GLCapabilities caps = createGLCapabilities();
        this.drawable = new GLCanvas(caps);
        this.drawable.setAutoSwapBufferMode(true);
        this.drawable.setVisible(false);
        
        this.drawable.addKeyListener(controller);
	this.drawable.addMouseListener(controller);
        this.drawable.addMouseMotionListener(controller);
        this.drawable.addMouseWheelListener(controller);
        
        this.eventListener = new GLEventListener() {

            @Override
            public void init(GLAutoDrawable glad) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void dispose(GLAutoDrawable glad) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void display(GLAutoDrawable glad) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        
        final RenderingController renderingController = Lookup.getDefault().lookup(RenderingController.class);
        this.scheduler = new RenderingScheduler(this.drawable, renderingController.getFPS());
        
        this.bridge = new FrameDataBridge();
    }
    
    private GLCapabilities createGLCapabilities() {
        final GLCapabilities result = new GLCapabilities(GLProfile.getDefault());
                
        result.setDoubleBuffered(true);
        result.setHardwareAccelerated(true);
        
        
        final RenderingController renderingController = Lookup.getDefault().lookup(RenderingController.class);
        final int antiAA = renderingController.getAASamples();
        if (antiAA > 0) {
            result.setSampleBuffers(true);
            result.setNumSamples(antiAA);
        } else {
            result.setSampleBuffers(false);
        }
        
        return result;
    }
    
    public Component renderingCanvas() {
        return this.drawable;
    }

    public void startRendering() {
        this.drawable.setVisible(true);
        this.drawable.addGLEventListener(this.eventListener);        
        this.scheduler.startRendering();
    }
    
    public void stopRendering() {
        this.scheduler.stopRendering();
        this.drawable.removeGLEventListener(this.eventListener);
        this.drawable.setVisible(false);
    }
    
    /**
     * Changes the rate at which the screen is displayed.
     * 
     * @param fps the new frame rate
     */
    public void setFPS(int fps) {
        this.scheduler.setFPS(fps);
    }
    
    /**
     * Gets the current rate at which the screen is displayed.
     * 
     * @return the frame rate
     */
    public int getFPS() {
        return (int) this.scheduler.getFPS();
    }
}
