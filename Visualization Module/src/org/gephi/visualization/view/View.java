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

package org.gephi.visualization.view;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Component;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.controller.Controller;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.data.NodesArray;
import org.gephi.visualization.pipeline.Pipeline;
import org.gephi.visualization.pipeline.gl11.GL11Pipeline3D;

/**
 *
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class View implements GLEventListener {

    private GLCanvas canvas;
    private FPSAnimator animator;

    private Controller controller;

    private FrameData frameData = null;
    final private Object frameDataLock;

    private Pipeline pipeline;

    static { GLProfile.initSingleton(true); }

    public View(Controller controller) {
        this.frameDataLock = new Object();

        this.controller = controller;

        final GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);
        // TODO: change capabilities based on config files

        this.canvas = new GLCanvas(caps);
        this.canvas.setAutoSwapBufferMode(true);
        this.animator = new FPSAnimator(this.canvas, 30);

        this.canvas.addKeyListener(controller);
	this.canvas.addMouseListener(controller);
        this.canvas.addMouseMotionListener(controller);
        this.canvas.addMouseWheelListener(controller);

        this.pipeline = new GL11Pipeline3D();
    }
    
    public Component getCanvas() {
        return this.canvas;
    }

    public void start() {
        this.canvas.addGLEventListener(this);
        this.canvas.setVisible(true);
        this.animator.start();
    }

    public void stop() {
        this.animator.stop();
        this.canvas.setVisible(false);
        this.canvas.removeGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        final GL gl = glad.getGL();
        gl.setSwapInterval(1);

        // TODO: change initialization code based on config files

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClearDepth(1.0);

        //gl.glEnable(GL.GL_DEPTH_TEST);

        boolean init = this.pipeline.init(gl);

        if (!init) {
            gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        GL gl = glad.getGL();

        this.pipeline.dispose(gl);
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

        this.controller.beginRenderFrame();

        // OpenGL Test

        Camera camera = new Camera(glad.getWidth(), glad.getHeight(), 0.1f, 10.0f);
        camera.lookAt(new Vec3f(0.0f, 0.0f, -5.0f), new Vec3f(), Vec3f.Y_AXIS);

        FrameData frameData2 = new FrameData(true);
        NodesArray nodesArray = frameData2.getNodesArray();
        nodesArray.add(new Vec3f(), 3.0f, new Vec4f(1.0f, 0.0f, 0.0f, 1.0f));

        frameData2.setCamera(camera);

        this.pipeline.draw(gl, frameData2);

        // OpenGL Test

        //this.pipeline.draw(gl, frameData);

        this.controller.endRenderFrame();

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
    }

    public void setCurrentFrameData(FrameData frameData) {
        synchronized (this.frameDataLock) {
            this.frameData = frameData;
	}
    }
    
}
