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
package org.gephi.visualization.renderqueue;

import javax.media.opengl.GL;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.RenderArea;

/**
 * Implementation of a <code>RenderQueue</code>. Classes implementing this
 * interface may assume all the states are correctly handles by the 
 * <code>RenderQueue</code> which contains it. Implementations do not have to
 * be thread-safe since the <code>RenderQueue</code> objects are not 
 * thread-safe.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
interface RenderQueueImpl<E> {

    /** Change the state to DRAWABLE. */
    public void makeDrawable();

    /** Change the state to EDITABLE. */
    public void makeEditable();
    
    /**
     * Adds an element to this queue if this class is editable. 
     * @param e the element to add to the queue
     */
    public void add(E e);
    
    /** Clears the queue of objects to draw if this is editable. */
    public void clearQueue();

    /**
     * Draws the current queue on screen if this is drawable.
     * 
     * @param gl the GL object
     * @param camera the current camera
     * @param area the region of the screen used for rendering
     * @param drawCalls the number of times this method has been called
     */
    public void draw(GL gl, Camera camera, RenderArea area, int drawCalls);    
}
