/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.visualization.opengl.compatibility.objects;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.gephi.graph.api.NodeData;
import org.gephi.visualization.VizModel;
import org.gephi.visualization.apiimpl.ModelImpl;
import org.gephi.lib.gleem.linalg.Vecf;
import org.gephi.visualization.opengl.octree.Octant;

/**
 *
 * @author Mathieu Bastian
 */
public class NodeDiskModel extends ModelImpl<NodeData> {

    public int modelType;
    public int modelBorderType;

    public NodeDiskModel() {
        octants = new Octant[1];
    }

    @Override
    public int[] octreePosition(float centerX, float centerY, float centerZ, float size) {
        //float radius = obj.getRadius();
        int index = 0;

        if (obj.y() < centerY) {
            index += 4;
        }
        if (obj.z() > centerZ) {
            index += 2;
        }
        if (obj.x() < centerX) {
            index += 1;
        }

        return new int[]{index};
    }

    @Override
    public boolean isInOctreeLeaf(Octant leaf) {
        if (Math.abs(obj.x() - leaf.getPosX()) > (leaf.getSize() / 2 - obj.getRadius())
                || Math.abs(obj.y() - leaf.getPosY()) > (leaf.getSize() / 2 - obj.getRadius())
                || Math.abs(obj.z() - leaf.getPosZ()) > (leaf.getSize() / 2 - obj.getRadius())) {
            return false;
        }
        return true;
    }

    @Override
    public void display(GL gl, GLU glu, VizModel vizModel) {
        boolean selec = selected;
        boolean neighbor = false;
        highlight = false;
        if (vizModel.isAutoSelectNeighbor() && mark && !selec) {
            selec = true;
            highlight = true;
            neighbor = true;
        }
        mark = false;
        gl.glPushMatrix();
        float size = obj.getSize() * 2;
        gl.glTranslatef(obj.x(), obj.y(), obj.z());
        gl.glScalef(size, size, size);

        if (!selec) {
            if (vizModel.getConfig().isLightenNonSelected()) {
                float[] lightColor = vizModel.getConfig().getLightenNonSelectedColor();
                float lightColorFactor = vizModel.getConfig().getLightenNonSelectedFactor();
                float r = obj.r();
                float g = obj.g();
                float b = obj.b();
                gl.glColor3f(r + (lightColor[0] - r) * lightColorFactor, g + (lightColor[1] - g) * lightColorFactor, b + (lightColor[2] - b) * lightColorFactor);
                gl.glCallList(modelType);
                if (modelBorderType != 0) {
                    float rborder = 0.498f * r;
                    float gborder = 0.498f * g;
                    float bborder = 0.498f * b;
                    gl.glColor3f(rborder + (lightColor[0] - rborder) * lightColorFactor, gborder + (lightColor[1] - gborder) * lightColorFactor, bborder + (lightColor[2] - bborder) * lightColorFactor);
                    gl.glCallList(modelBorderType);
                }
            } else {
                float r = obj.r();
                float g = obj.g();
                float b = obj.b();
                gl.glColor3f(r, g, b);
                gl.glCallList(modelType);
                if (modelBorderType != 0) {
                    float rborder = 0.498f * r;
                    float gborder = 0.498f * g;
                    float bborder = 0.498f * b;
                    gl.glColor3f(rborder, gborder, bborder);
                    gl.glCallList(modelBorderType);
                }
            }
        } else {
            float r;
            float g;
            float b;
            float rborder;
            float gborder;
            float bborder;
            if (vizModel.isUniColorSelected()) {
                if (neighbor) {
                    r = vizModel.getConfig().getUniColorSelectedNeigborColor()[0];
                    g = vizModel.getConfig().getUniColorSelectedNeigborColor()[1];
                    b = vizModel.getConfig().getUniColorSelectedNeigborColor()[2];
                } else {
                    r = vizModel.getConfig().getUniColorSelectedColor()[0];
                    g = vizModel.getConfig().getUniColorSelectedColor()[1];
                    b = vizModel.getConfig().getUniColorSelectedColor()[2];
                }
                rborder = 0.498f * r;
                gborder = 0.498f * g;
                bborder = 0.498f * b;
            } else {
                rborder = obj.r();
                gborder = obj.g();
                bborder = obj.b();
                r = Math.min(1, 0.5f * rborder + 0.5f);
                g = Math.min(1, 0.5f * gborder + 0.5f);
                b = Math.min(1, 0.5f * bborder + 0.5f);
            }
            gl.glColor3f(r, g, b);
            gl.glCallList(modelType);
            if (modelBorderType != 0) {
                gl.glColor3f(rborder, gborder, bborder);
                gl.glCallList(modelBorderType);
            }
        }

        gl.glPopMatrix();
    }

    @Override
    public boolean selectionTest(Vecf distanceFromMouse, float selectionSize) {
        if (distanceFromMouse.get(2) - selectionSize < getViewportRadius()) {
            return true;
        }
        return false;
    }

    @Override
    public float getCollisionDistance(double angle) {
        return obj.getRadius();
    }

    @Override
    public String toSVG() {
        return null;
    }
}
