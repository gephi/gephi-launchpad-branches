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
package org.gephi.visualization.rendering.apiimpl.command.ui;

import java.util.Collection;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;
import org.gephi.math.linalg.Vec2M;
import org.gephi.visualization.api.view.ui.UIShape;
import org.gephi.visualization.api.view.ui.UIShape.UIConvexPolygon;
import org.gephi.visualization.api.view.ui.UIShape.UIEllipse;
import org.gephi.visualization.api.view.ui.UIStyle;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Technique;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class UITechniqueGL12 implements Technique<UIShape> {
    private int currentPass;

    public UITechniqueGL12() {
        this.currentPass = -1;
    }

    // TODO: adjust to support general screenshots
    @Override
    public boolean begin(GL gl, Camera camera, RenderArea renderArea) {
        final GL2 gl2 = gl.getGL2();
        final GLU glu = new GLUgl2();
        
        gl2.glDisable(GL2.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);

        gl2.glLoadIdentity();
        glu.gluOrtho2D(0.0f, renderArea.screenX, renderArea.screenY, 0.0f);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);

        gl2.glLoadIdentity();
        
        return true;
    }

    @Override
    public boolean advanceToNextPass(GL gl) {
        return (++this.currentPass) == 0;
    }

    @Override
    public void draw(GL gl, UIShape e) {
        switch (e.type()) {
            case CONVEX_POLYGON:
                drawPolygon(gl, (UIShape.UIConvexPolygon) e);
                break;
            case ELLIPSE:
                drawEllipse(gl, (UIShape.UIEllipse) e);
                break;
            default:
                // this part of the code shouldn't be reached
                assert false;
        }
    }

    @Override
    public void end(GL gl) {
        final GL2 gl2 = gl.getGL2();
        
        gl2.glDisable(GL2.GL_BLEND);
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        
        gl2.glMatrixMode(GL2.GL_PROJECTION);

        gl2.glLoadIdentity();

        gl2.glMatrixMode(GL2.GL_MODELVIEW);

        gl2.glLoadIdentity();
        
        this.currentPass = -1;
    }

    @Override
    public void disposeElements(GL gl, Collection<? extends UIShape> e) {
        /* EMPTY BLOCK */
    }

    @Override
    public void dispose(GL gl) {
        /* EMPTY BLOCK */
    }

    private void drawPolygon(GL gl, UIConvexPolygon poly) {
        GL2 gl2 = gl.getGL2();
        
        UIStyle style = poly.style();
        
        // fill
        gl2.glColor4f(style.fillColor().ra(), style.fillColor().ga(), style.fillColor().ba(), style.fillColor().a());

        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl2.glBegin(GL2.GL_POLYGON);
                
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
    }

    private void drawEllipse(GL gl, UIEllipse ellipse) {
        GL2 gl2 = gl.getGL2();

        UIStyle style = ellipse.style();

        // TODO: calculate a better number of points
        final int len = 200;

        final Vec2M[] pnts = new Vec2M[len];

        for (int i = 0; i < len; ++i) {
            double angle = (2.0 * Math.PI * i) / len;
            float c = (float) Math.cos(angle);
            float s = (float) Math.sin(angle);

            pnts[i] = ellipse.center.copyM().add(c, ellipse.axis1, s, ellipse.axis2);
        }

        // fill
        gl2.glColor4f(style.fillColor().ra(), style.fillColor().ga(), style.fillColor().ba(), style.fillColor().a());

        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl2.glBegin(GL2.GL_POLYGON);

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

        gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }
    
}
