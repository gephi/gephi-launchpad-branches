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

package org.gephi.visualization.data;

import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.drawcall.MemoryPool;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.RenderArea;
import org.gephi.visualization.rendering.command.Command;

/**
 * Class used to get the current graph data in View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameData {

    private final Camera camera;
    
    private final MemoryPool memory;
    
    private final List<Command> edgeCommands;
    private final List<Command> nodeCommands;
    private final List<Command> uiCommands;
    
    private boolean alreadyDrawn;

    FrameData(Camera camera, MemoryPool memory, List<Command> edgeCommands, 
            List<Command> nodeCommands, List<Command> uiCommands) {
        this.camera = camera;
        this.memory = memory;
        this.edgeCommands = Collections.unmodifiableList(edgeCommands);
        this.nodeCommands = Collections.unmodifiableList(nodeCommands);
        this.uiCommands = Collections.unmodifiableList(uiCommands);
        this.alreadyDrawn = false;
    }
    
    public void draw(GL gl, RenderArea renderArea) {        
        for (Command c : edgeCommands) {
            c.draw(gl, this.camera, renderArea, this.alreadyDrawn);
        }
        
        for (Command c : nodeCommands) {
            c.draw(gl, this.camera, renderArea, this.alreadyDrawn);
        }
        
        for (Command c : uiCommands) {
            c.draw(gl, this.camera, renderArea, this.alreadyDrawn);
        }
        
        this.alreadyDrawn = true;
    }
    
    MemoryPool memory() {
        return memory;        
    }
}
