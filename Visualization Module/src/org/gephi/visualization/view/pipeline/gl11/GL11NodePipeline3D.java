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

package org.gephi.visualization.view.pipeline.gl11;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.gl2.GLUgl2;
import org.gephi.math.linalg.Vec3;
import org.gephi.visualization.api.color.Color;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.data.graph.VizNode;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.Rectangle;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.view.pipeline.AbstractPipeline;

/**
 * 3D Node pipeline using OpenGL 1.1
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11NodePipeline3D extends AbstractPipeline {

    @Override
    public String name() {
        return "GL11NodePipeline3D.";
    }

    @Override
    public String description() {
        return "3D Node pipeline using OpenGL 1.1.";
    }

    @Override
    public String debugInfo() {
        return "";
    }

    private int smallerSphere;
    private static final int numLods = 4;

    public GL11NodePipeline3D() {
        this.smallerSphere = 0;
    }

    @Override
    public boolean init(GL gl) {
        if (!gl.isGL2()) return false;

        final GL2 gl2 = gl.getGL2();
        final GLU glu = new GLUgl2();

        this.smallerSphere = gl2.glGenLists(4);
        if (this.smallerSphere == 0) return false;

        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);

        for (int i = 0; i < GL11NodePipeline3D.numLods; ++i) {
            gl2.glNewList(this.smallerSphere + i, GL2.GL_COMPILE);
                glu.gluSphere(quad, 1.0, 4 << i, 2 << i);
            gl2.glEndList();
        }

        glu.gluDeleteQuadric(quad);

        return true;
    }

    @Override
    public void draw(GL gl, FrameData frameData) {
        if (!gl.isGL2()) return;

        final GL2 gl2 = gl.getGL2();

        final Camera camera = frameData.camera();
        final RenderArea area = new RenderArea(this.screenWidth / this.screenHeight, new Rectangle(0.0f, 0.0f, 1.0f, 1.0f), -500000.0f, 500000.0f);
        gl2.glMatrixMode(GL2.GL_PROJECTION);

        gl2.glLoadMatrixf(camera.projMatrix(area).toArray(), 0);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

        gl2.glLoadMatrixf(camera.viewMatrix(area).toArray(), 0);

        for (VizNode node : frameData.nodeBuffer()) {
            gl2.glPushMatrix();

            Vec3 position = node.position;
            float size = node.size;
            Color color = node.color;

            if (frameData.somethingIsSelected()) {
                if (node.selected) {
                    //Color selectionColor = Color.RED;
                    //gl2.glColor3f(selectionColor.r, selectionColor.g, selectionColor.b);
                    gl2.glColor3f(color.r, color.g, color.b);
                } else {
                    //Color lightColor = Color.WHITE;
                    float lightColorFactor = 0.65f;
                    final float r = color.r + (1.0f - color.r) * lightColorFactor;
                    final float g = color.g + (1.0f - color.g) * lightColorFactor;
                    final float b = color.b + (1.0f - color.b) * lightColorFactor;
                    gl2.glColor3f(r, g, b);
                }
            } else {
                gl2.glColor3f(color.r, color.g, color.b);
            }
                
            gl2.glTranslatef(position.x(), position.y(), position.z());
            gl2.glScalef(size, size, size);

            // implement LOD
            int lod = numLods - 1;

            gl2.glCallList(this.smallerSphere + lod);

            gl2.glPopMatrix();
        }
    }

    @Override
    public void dispose(GL gl) {
        if (!gl.isGL2()) return;
        
        gl.getGL2().glDeleteLists(this.smallerSphere, numLods);
    }
    
    

}
