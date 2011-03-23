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

/**
 *
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Viewer implements GLEventListener {
    
    private GLWindow window;
    private NewtCanvasAWT canvas;
    private FPSAnimator animator;

    static float angle = 0.0f;

    public Viewer() {
        final GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        // TODO: change capabilities based on config files

        this.window = GLWindow.create(caps);
        this.window.setAutoSwapBufferMode(true);

        this.animator = new FPSAnimator(this.window, 30);

        this.canvas = new NewtCanvasAWT(this.window);
    }
    
    public Component getCanvas() {
        return this.canvas;
    }

    public void start() {
        this.window.addGLEventListener(this);
        this.window.setVisible(true);
        this.canvas.setVisible(true);

        this.animator.start();
    }

    public void stop() {
        this.animator.stop();

        this.window.removeGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();

        // TODO: change initialization code based on config files

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        final GL2 gl = glad.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glBegin(GL2.GL_POLYGON);

        final float c = (float) Math.cos(angle);
        final float s = (float) Math.sin(angle);

        gl.glVertex3f(-0.5f*c, -0.5f*s, 0.0f);
        gl.glVertex3f(-0.5f*s, 0.5f*c, 0.0f);
        gl.glVertex3f(0.5f*c, 0.5f*s, 0.0f);
        gl.glVertex3f(0.5f*s, -0.5f*c, 0.0f);
        
        gl.glEnd();

        angle += Math.toRadians(1.0f);

        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {
        final GL gl = glad.getGL();

        gl.glViewport(0, 0, w, h);
    }

    public void updateSize(int x, int y, int w, int h) {
        this.canvas.setBounds(x, y, w, h);
    }
}
