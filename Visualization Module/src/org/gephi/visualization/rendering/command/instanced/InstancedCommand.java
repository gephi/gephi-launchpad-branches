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
package org.gephi.visualization.rendering.command.instanced;

import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.rendering.command.Command;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class InstancedCommand<E> implements Command {
    
    private final List<E> instances;
    private final InstancedTechnique<E> technique;

    public InstancedCommand(List<E> instances, InstancedTechnique<E> technique) {
        this.instances = instances;
        this.technique = technique;
    }
    
    @Override
    public void draw(GL gl, Camera camera) {
        this.technique.begin(gl, camera);
        
        for (E e : this.instances) {
            this.technique.draw(gl, e);
        }
        
        this.technique.end(gl);
    }
    
}
