/*
Copyright 2008-2010 Gephi
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

package org.gephi.visualization.view;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Component;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import org.gephi.visualization.controller.Controller;
import org.gephi.visualization.data.FrameData;

/**
 *
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Viewer implements GLEventListener {

    private GLWindow window;
    private NewtCanvasAWT canvas;
    private FPSAnimator animator;

    private Controller controller;

    private FrameData frameData = null;
    final private Object frameDataLock;

    // Start OpenGL testing code
    int angle = 0;
    // End OpenGL testing code

    static { GLProfile.initSingleton(true); }

    public Viewer() {
        this.frameDataLock = new Object();

        final GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);
        // TODO: change capabilities based on config files
        
        this.window = GLWindow.create(caps);
        this.window.setAutoSwapBufferMode(true);
        this.window.setVisible(false);

        this.animator = new FPSAnimator(this.window, 30);

        this.canvas = new NewtCanvasAWT(this.window);
        this.canvas.validate();
    }
    
    public Component getCanvas() {
        return this.canvas;
    }

    public void start() {
        
        this.window.addGLEventListener(this);
        this.window.setVisible(true);

        this.animator.start();
    }

    public void stop() {
        this.animator.stop();

        this.window.setVisible(false);
        this.window.removeGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        final GL gl = glad.getGL();
        gl.setSwapInterval(1);

        // TODO: change initialization code based on config files

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0);

        // Start OpenGL testing code
        GL2 gl2 = gl.getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        gl2.glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);
        // End OpenGL testing
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        final GL gl = glad.getGL();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        FrameData currentFrameData;
        synchronized (this.frameDataLock) {
            currentFrameData = this.frameData;
        }
        if (currentFrameData == null) {
            return;
        }

        if (this.controller != null) {
            this.controller.beginRenderFrame();
        }

        // TODO: Rendering code

        // Start OpenGL testing code
        GL2 gl2 = gl.getGL2();

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        gl2.glRotatef(angle, 0.0f, 0.0f, 1.0f);

        gl2.glColor3f(1.0f, 1.0f, 1.0f);
        gl2.glBegin(GL2.GL_POLYGON);
            gl2.glVertex2f(-0.5f, -0.5f);
            gl2.glVertex2f(0.5f, -0.5f);
            gl2.glVertex2f(0.5f, 0.5f);
            gl2.glVertex2f(-0.5f, 0.5f);
        gl2.glEnd();
        angle++;
        // End OpenGL testing code


        if (this.controller != null) {
            this.controller.endRenderFrame();
        }

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        final GL gl = glad.getGL();

        int h2 = h == 0 ? 1 : h;

        gl.glViewport(0, 0, w, h2);

        if (this.controller != null) {
            this.controller.resize(w, h2);
        }
    }

    public void updateSize(int x, int y, int w, int h) {
        this.canvas.setBounds(x, y, w, h);
        //this.reshape(this.window, x, y, w, h);
    }

    public void setCurrentFrameData(FrameData frameData) {
        synchronized (this.frameDataLock) {
            this.frameData = frameData;
	}
    }

    public void setController(Controller controller) {
        this.controller = controller;
        this.window.addKeyListener(controller);
	this.window.addMouseListener(controller);
    }
}
