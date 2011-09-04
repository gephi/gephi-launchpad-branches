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
package org.gephi.visualization.rendering.command;

import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class CommandList {
    private final List<Command> edgeCommands;
    private final List<Command> nodeCommands;
    private final List<Command> uiCommands;
    
    boolean isValid;

    public CommandList(List<Command> edgeCommands, List<Command> nodeCommands, List<Command> uiCommands) {
        this.edgeCommands = Collections.unmodifiableList(edgeCommands);
        this.nodeCommands = Collections.unmodifiableList(nodeCommands);
        this.uiCommands = Collections.unmodifiableList(uiCommands);
        
        this.isValid = true;
    }
    
    public void draw(GL gl, Camera camera, RenderArea renderArea) {
        if (!isValid) return;
        
        this.drawOnlyGraph(gl, camera, renderArea);
        
        for (Command c : uiCommands) {
            c.draw(gl, camera, renderArea);
        }
    }
    
    public void drawOnlyGraph(GL gl, Camera camera, RenderArea renderArea) {
        if (!isValid) return;
        
        for (Command c : edgeCommands) {
            c.draw(gl, camera, renderArea);
        }
        
        for (Command c : nodeCommands) {
            c.draw(gl, camera, renderArea);
        }
    }
    
    public void recycle() {
        for (Command c : edgeCommands) {
            c.recycle();
        }
        
        for (Command c : nodeCommands) {
            c.recycle();
        }
        
        for (Command c : uiCommands) {
            c.recycle();
        }
    }
            
    public void dispose(GL gl) {
        for (Command c : edgeCommands) {
            c.dispose(gl);
        }
        
        for (Command c : nodeCommands) {
            c.dispose(gl);
        }
        
        for (Command c : uiCommands) {
            c.dispose(gl);
        }        
    }
}
