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

import java.awt.Component;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.controller.VisualizationControllerImpl;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.data.FrameDataBridge;
import org.gephi.visualization.data.FrameDataBridgeIn;
import org.gephi.visualization.rendering.command.CommandListBuilders;
import org.gephi.visualization.rendering.pipeline.Pipeline;

/**
 * Class which controls the rendering loop and all graphics resources.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RenderingEngine {
    
    static { GLProfile.initSingleton(true); }
    
    private final GLCanvas drawable;
    private final GLEventListener eventListener;
    private final RenderingScheduler scheduler;
    
    private final VizModel model;

    private final Pipeline pipeline;
    
    private final FrameDataBridge bridge;

    public RenderingEngine(final VisualizationControllerImpl controller, VizModel model) {
        this.model = model;
        
        final GLCapabilities caps = createGLCapabilities();
        this.drawable = new GLCanvas(caps);
        this.drawable.setAutoSwapBufferMode(true);
        this.drawable.setVisible(false);
        
        this.drawable.addKeyListener(controller);
	this.drawable.addMouseListener(controller);
        this.drawable.addMouseMotionListener(controller);
        this.drawable.addMouseWheelListener(controller);
        
        /* TODO: make pipeline... */
        this.pipeline = new Pipeline(this, this.model);
        
        this.eventListener = new GLEventListener() {
            private CommandListBuilders commandListBuilders = null;
            
            @Override
            public void init(GLAutoDrawable glad) {
                final GL gl = glad.getGL();
                
                gl.setSwapInterval(1);
                
                this.commandListBuilders = CommandListBuilders.create(gl);
                bridge.setCommandListBuilders(commandListBuilders);
                pipeline.init(gl);
            }

            @Override
            public void dispose(GLAutoDrawable glad) {
                pipeline.dispose(glad.getGL());
            }

            @Override
            public void display(GLAutoDrawable glad) {
                final GL gl = glad.getGL();
                
                FrameData frameData = bridge.updateCurrent();

                // frame data is not currently used by the pipeline
                //if (frameData == null) {
                //    return;
                //}

                controller.beginRenderFrame();

                pipeline.draw(gl, frameData);

                controller.endRenderFrame();
            }

            @Override
            public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
                final GL gl = glad.getGL();

                int h2 = h == 0 ? 1 : h;

                pipeline.reshape(gl, x, y, w, h2);

                controller.resize(w, h2);
            }
        };
        
        final int fps = this.model.getNormalFPS();
        this.scheduler = new RenderingScheduler(this.drawable, 30);
        
        this.bridge = new FrameDataBridge();
    }
    
    private GLCapabilities createGLCapabilities() {
        final GLCapabilities result = new GLCapabilities(GLProfile.getDefault());
                
        result.setDoubleBuffered(true);
        result.setHardwareAccelerated(true);
        
        final int antiAA = this.model.getAntiAliasing();
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
    public double getFPS() {
        return this.scheduler.getFPS();
    }
    
    /**
     * Gets the class used to pass the graph data to the rendering engine.
     * 
     * @return the frame data bridge
     */
    public FrameDataBridgeIn bridge() {
        return this.bridge;
    }
}
