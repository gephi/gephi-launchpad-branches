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
package org.gephi.visualization.rendering.pipeline;

import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicReference;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import org.gephi.math.linalg.Vec2;
import org.gephi.visualization.api.Color;
import org.gephi.visualization.api.rendering.background.Background;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.rendering.RenderingEngine;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.OrthoCamera;
import org.gephi.visualization.rendering.camera.Rectangle;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Command;

/**
 * 
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Pipeline {
    // Viewport
    private int screenX, screenY;
    private int screenWidth, screenHeight;
    
    // text renderer used for drawing statistics
    private final TextRenderer textRenderer;
    
    // VizModel used to get visualization preferences
    private final VizModel model;
     
    private final RenderingEngine engine;
    
    private final AtomicReference<Color> backgroundColor;
    
    public Pipeline(RenderingEngine engine, VizModel model) {
        this.screenX = 0;
        this.screenY = 0;
        this.screenWidth = 1;
        this.screenHeight = 1;
        
        this.engine = engine;
        
        this.textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 14));
        
        this.model = model;
        
        this.backgroundColor = new AtomicReference<Color>(new Color(model.getBackground().getColor()));
        
        this.model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(VizConfig.BACKGROUND)) {
                    Background background = (Background)evt.getNewValue();
                    backgroundColor.set(new Color(background.getColor()));
                }
            }
        });
    }
    
    public boolean init(GL gl) {
        return true;
    }
    
    public void reshape(GL gl, int x, int y, int width, int height) {
        this.screenX = x;
        this.screenY = y;
        this.screenWidth = width;
        this.screenHeight = height > 0 ? height : 1;
    }
    
    public void draw(GL gl, FrameData frameData) {
        // sets general states like background color
        Color bc = this.backgroundColor.get();
        gl.glClearColor(bc.r, bc.g, bc.b, 1.0f);
        
        // screenshots ..
        
        // screen
        gl.glViewport(0, 0, this.screenWidth, this.screenHeight);
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        // draw graph
        RenderArea renderArea = new RenderArea((float)this.screenWidth / (float)this.screenHeight, new Rectangle(0.0f, 0.0f, 1.0f, 1.0f), frameData.near, frameData.far);
        
        test(gl.getGL2(), frameData, renderArea);
        
        for (Command c : frameData.edgeCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
        
        for (Command c : frameData.nodeCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
        
        for (Command c : frameData.uiCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
     
        if (this.model.isShowFPS()) {
            this.textRenderer.setColor(java.awt.Color.BLACK);
            this.textRenderer.beginRendering(this.screenWidth, this.screenHeight);
            
            this.textRenderer.draw("FPS: " + engine.getFPS(), 0, screenHeight - 20);
            
            this.textRenderer.endRendering();
        }
    }
    
    public void dispose(GL gl) {
        
    }

    private void test(GL2 gl2, FrameData frameData, RenderArea renderArea) {
        Camera camera = frameData.camera;
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        
        final float[] projMatrix = camera.projMatrix(renderArea).toArray();
        gl2.glLoadMatrixf(projMatrix, 0);
        
        final Vec2 center = ((OrthoCamera)frameData.camera).center;
        final float height = ((OrthoCamera)frameData.camera).height;
        final float aspect = renderArea.aspectRatio;
        
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        
        final float[] viewMatrix = camera.viewMatrix(renderArea).toArray();
        gl2.glLoadMatrixf(viewMatrix, 0);
        
        gl2.glColor3f(1.0f, 0.0f, 0.0f);
        
        gl2.glBegin(GL2.GL_TRIANGLES);
        
        gl2.glVertex2f(center.x(), center.y());
        
        gl2.glVertex2f(center.x() + height * aspect * 0.5f, center.y());
        
        gl2.glVertex2f(center.x(), center.y() + height * 0.5f);
        
        gl2.glEnd();
    }
}
