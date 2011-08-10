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

import javax.media.opengl.GL;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.rendering.camera.Rectangle;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Command;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class PipelineGL12 implements PipelineImpl {
    private Rectangle viewport;

    @Override
    public boolean init(GL gl) {
        return true;
    }

    @Override
    public void reshape(GL gl, int x, int y, int width, int height) {
        this.viewport = new Rectangle(x, y, width, height);
    }

    @Override
    public void draw(GL gl, FrameData frameData) {
        gl.glViewport((int)this.viewport.x, (int)this.viewport.y, (int)this.viewport.width, (int)this.viewport.height);
        
        RenderArea renderArea = new RenderArea(this.viewport.width / this.viewport.height, new Rectangle(0.0f, 0.0f, 1.0f, 1.0f), frameData.near, frameData.far);
        
        drawGraph(gl, frameData, renderArea);
    }
    
    private void drawGraph(GL gl, FrameData frameData, RenderArea renderArea) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        for (Command c : frameData.edgeCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
        
        for (Command c : frameData.nodeCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
        
        for (Command c : frameData.uiCommands) {
            c.draw(gl, frameData.camera, renderArea);
        }
    }

    @Override
    public void dispose(GL gl) {
        /* Empty BLOCK */
    }
    
}
