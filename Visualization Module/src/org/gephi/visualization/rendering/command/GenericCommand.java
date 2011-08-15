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

import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;

/**
 * Generic implementation of command. This implementation works equally well
 * for instanced and buffered techniques. It's behaviour is defined by the
 * technique and it is therefore final.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class GenericCommand<E> implements Command {
    private final List<E> objects;
    private final Technique<E> technique;

    public GenericCommand(List<E> objects, Technique<E> technique) {
        this.objects = objects;
        this.technique = technique;
    }
    
    @Override
    public void draw(GL gl, Camera camera, RenderArea renderArea) {
        if (!this.technique.begin(gl, camera, renderArea)) return;
        
        while (this.technique.advanceToNextPass(gl)) {            
            for (E e : objects) {
                this.technique.draw(gl, e);
            }
        }
        
        this.technique.end(gl);
    }

    @Override
    public void dispose(GL gl) {
            this.technique.disposeElements(gl, this.objects);
    }
}
