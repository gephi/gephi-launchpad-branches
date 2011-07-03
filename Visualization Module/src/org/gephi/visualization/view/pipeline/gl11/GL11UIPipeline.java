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
import javax.media.opengl.glu.gl2.GLUgl2;
import org.gephi.math.Vec2M;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.camera.CameraImpl;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.view.pipeline.Pipeline;
import org.gephi.visualization.api.view.ui.UIStyle;

/**
 * UI pipeline based on OpenGL 1.1
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11UIPipeline implements Pipeline {

    @Override
    public String name() {
        return "GL11UIPipeline.";
    }

    @Override
    public String description() {
        return "UI pipeline based on OpenGL 1.1.";
    }

    @Override
    public String debugInfo() {
        return "";
    }

    @Override
    public boolean init(GL gl) {
        return true;
    }

    @Override
    public void draw(GL gl, FrameData frameData) {
        if (!gl.isGL2()) return;

        final GL2 gl2 = gl.getGL2();
        final GLU glu = new GLUgl2();

        // premultiplied alpha blending..
        gl2.glDisable(GL2.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

        final CameraImpl camera = frameData.camera();

        gl2.glMatrixMode(GL2.GL_PROJECTION);

        gl2.glLoadIdentity();
        glu.gluOrtho2D(0.0f, camera.imageWidth(), camera.imageHeight(), 0.0f);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);

        gl2.glLoadIdentity();

        for (UIShape shape : frameData.uiBuffer()) {
            UIStyle style = shape.style();

            if (shape.type() == UIShape.Type.CONVEX_POLYGON) {
                UIShape.UIConvexPolygon poly = (UIShape.UIConvexPolygon)shape;

                // fill
                gl2.glColor4f(style.fillColor().ra(), style.fillColor().ga(), style.fillColor().ba(), style.fillColor().a());

               gl2.glBegin(GL2.GL_TRIANGLE_FAN);

                for (int i = 0; i < poly.numPoints(); ++i) {
                    gl2.glVertex2f(poly.point(i).x(), poly.point(i).y());
                }

                gl2.glEnd();

                // border
                gl2.glColor4f(style.borderColor().ra(), style.borderColor().ga(), style.borderColor().ba(), style.borderColor().a());

                gl2.glLineWidth(style.borderWidth());

                gl2.glBegin(GL2.GL_LINE_LOOP);

                for (int i = 0; i < poly.numPoints(); ++i) {
                    gl2.glVertex2f(poly.point(i).x(), poly.point(i).y());
                }

                gl2.glEnd();

            } else {
                UIShape.UIEllipse ellipse = (UIShape.UIEllipse)shape;

                // TODO: calculate a better number of points
                final int len = 200;

                final Vec2M[] pnts = new Vec2M[len];

                for (int i = 0; i < len; ++i) {
                    double angle = (2.0 * Math.PI * i) / len;
                    float c = (float) Math.cos(angle);
                    float s = (float) Math.sin(angle);

                    pnts[i] = ellipse.center.plusM(c, ellipse.axis1).plusEq(s, ellipse.axis2);
                }

                // fill
                gl2.glColor4f(style.fillColor().ra(), style.fillColor().ga(), style.fillColor().ba(), style.fillColor().a());

                gl2.glBegin(GL2.GL_TRIANGLE_FAN);

                for (int i = 0; i < len; ++i) {
                    gl2.glVertex2f(pnts[i].x(), pnts[i].y());
                }

                gl2.glEnd();

                // border
                gl2.glColor4f(style.borderColor().ra(), style.borderColor().ga(), style.borderColor().ba(), style.borderColor().a());

                gl2.glLineWidth(style.borderWidth());

                gl2.glBegin(GL2.GL_LINE_LOOP);

                for (int i = 0; i < len; ++i) {
                    gl2.glVertex2f(pnts[i].x(), pnts[i].y());
                }

                gl2.glEnd();
            }

            gl2.glDisable(GL2.GL_BLEND);
            gl2.glEnable(GL2.GL_DEPTH_TEST);
        }
    }

    @Override
    public void dispose(GL gl) {
    }

}
