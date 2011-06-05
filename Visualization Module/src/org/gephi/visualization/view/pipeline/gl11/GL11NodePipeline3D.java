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
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.visualization.api.color.Color;
import org.gephi.visualization.camera.Camera;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.data.buffer.VizNodeBuffer;
import org.gephi.visualization.view.pipeline.Pipeline;

/**
 * 3D Node pipeline using OpenGL 1.1
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11NodePipeline3D implements Pipeline {

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
    private static final int numLods = 5;

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
        gl2.glMatrixMode(GL2.GL_PROJECTION);

        float[] matrix = new float[16];
        camera.projectiveMatrix().getColumnMajorData(matrix);
        gl2.glLoadMatrixf(matrix, 0);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

        camera.viewMatrix().getColumnMajorData(matrix);
        gl2.glLoadMatrixf(matrix, 0);

        final VizNodeBuffer nodeBuffer = frameData.nodeBuffer();
        for (; !nodeBuffer.isEndOfBuffer(); nodeBuffer.advance()) {
            gl2.glPushMatrix();

            Vec3f position = nodeBuffer.position();
            float size = nodeBuffer.size();
            Color color = nodeBuffer.color();

            gl2.glColor4f(color.r(), color.g(), color.b(), color.a());

            gl2.glTranslatef(position.x(), position.y(), position.z());
            gl2.glScalef(size, size, size);

            final float dist = camera.projectedDistanceFrom(position);
            final float h = (float) (dist * Math.tan(camera.fov() / 2.0));
            final float approxSize = (h * camera.imageHeight()) / (2.0f * size);

            final float log2Size = (float) (Math.log(approxSize) / Math.log(2.0));

            int lod = log2Size < 0.0 ? 0 : (int) log2Size;
            lod = lod >= numLods ? numLods - 1 : lod;

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
