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
 * for instanced and buffered techniques. It's behaviour is mainly defined by 
 * the technique.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GenericCommand<E> implements Command {
    
    /**
     * The list of objects to draw.
     */
    private final List<E> objects;
    
    /**
     * The technique which should be used for rendering the objects.
     */
    private final Technique<E> technique;

    /**
     * Creates a new command from the list of objects and the technique which
     * should be used to draw them.
     * 
     * @param objects the list of objects
     * @param technique the rendering technique
     */
    public GenericCommand(List<E> objects, Technique<E> technique) {
        this.objects = objects;
        this.technique = technique;
    }
    
    /**
     * Draws the list of objects on the screen.
     * 
     * @param gl the GL object
     * @param camera the current camera
     * @param reuseResources it's <code>true</code> if the technique has already
     *                          rendered the current batch.
     * @param renderArea the screen area where the objects are drawn
     */
    @Override
    public void draw(GL gl, Camera camera, RenderArea renderArea, boolean reuseResources) {
        if (!this.technique.begin(gl, camera, renderArea, reuseResources)) return;
        
        while (this.technique.advanceToNextPass(gl)) {            
            for (E e : objects) {
                this.technique.draw(gl, e);
            }
        }
        
        this.technique.end(gl);
    }
    
}
