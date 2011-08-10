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
package org.gephi.visualization.rendering.apiimpl.command.node;

import javax.media.opengl.GL;
import org.gephi.visualization.data.graph.VizNode3D;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Technique;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Node3DTechniqueGL12 implements Technique<VizNode3D> {

    private int smallerSphere;
    private static final int numLods = 4;
    
    public Node3DTechniqueGL12() {
        this.smallerSphere = 0;
    }

    @Override
    public void setCurrentPass(GL gl, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int numberOfPasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void draw(GL gl, VizNode3D e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void end(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose(GL gl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void begin(GL gl, Camera camera, RenderArea renderArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
