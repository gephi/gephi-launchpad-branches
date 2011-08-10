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

import javax.media.opengl.GL;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;

/**
 * A rendering technique is a class which can be used to draw some kind of 
 * object.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface Technique<E> {
    /**
     * Initializes the rendering states to use this technique. The use of other 
     * techniques methods between calls of this technique begin and end may 
     * result in undefined behaviour. This method MUST be called before using
     * any rendering method of this class.
     * 
     * @param gl the GL object used for rendering
     * @param camera the current camera
     */
    public void begin(GL gl, Camera camera, RenderArea renderArea);
    
    /**
     * Initializes the rendering states for the <code>i</code><sup>th</sup> 
     * pass in the rendering process. If <code>i</code> is greater than the 
     * number of passes of this technique then it does nothing.
     * 
     * @param gl the GL object used for rendering
     * @param i the new current pass
     */
    public void setCurrentPass(GL gl, int i);
    
    /**
     * Returns the number of passes this technique uses to render the objects. 
     * 
     * @return the number of passes
     */
    public int numberOfPasses();
    
    /**
     * Renders a single element. <code>e</code> can be a single instance or a 
     * buffer containing several instances. The rendering code is based on the
     * current pass.
     * 
     * @param gl the GL object used for rendering
     * @param e the object to render
     */
    public void draw(GL gl, E e);
    
    /**
     * Restores the rendering states active before the call to begin.
     * 
     * @param gl the GL object used for rendering 
     */
    public void end(GL gl);
    
    /**
     * Frees all the graphics resources used by this technique.
     * 
     * @param gl the GL object used for rendering
     */
    public void dispose(GL gl);
}
