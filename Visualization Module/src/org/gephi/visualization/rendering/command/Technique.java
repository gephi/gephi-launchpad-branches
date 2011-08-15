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

import java.util.Collection;
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
     * Initializes the rendering states to use this technique. This method MUST
     * be called before using any rendering method of this class.
     * 
     * @param gl the GL object used for rendering
     * @param camera the current camera
     * @return <code>true</code> if it is possible to initialize this technique.
     */
    public boolean begin(GL gl, Camera camera, RenderArea renderArea);
    
    /**
     * Initializes the rendering states for the next pass or returns false if
     * there is no successive pass. It MUST be the first method called after the
     * begin method to set the first pass.
     * 
     * @param gl the GL object used for rendering
     * @return <code>true</code> if there is a successive pass, <code>false</code>
     *         otherwise
     */
    public boolean advanceToNextPass(GL gl);
    
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
     * Disposes the OpenGL resources used for the elements.
     * 
     * @param gl the GL object used for rendering
     * @param e the elements to be disposed
     */
    public void disposeElements(GL gl, Collection<? extends E> e);
    
    /**
     * Frees all the graphics resources used by this technique.
     * 
     * @param gl the GL object used for rendering
     */
    public void dispose(GL gl);
}
