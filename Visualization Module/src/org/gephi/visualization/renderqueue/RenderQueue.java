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
 * A <code>RenderQueue</code> represents a list of rendering commands needed to
 * draw a set of objects of the same type. It may be in one of the two states:
 * EDITABLE and DRAWABLE. In the EDITABLE state additional objects can be added 
 * to the rendering queue, while in the DRAWABLE state the content is fixed 
 * and it is only possible to draw them (and come back to the EDITABLE state).
 * The <code>makeEditable</code> and <code>makeDrawable</code> methods should 
 * be used to change the current state of the class. It uses the bridge pattern
 * to maintain some common behaviour while changing the implementation. It is
 * not thread-safe.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class RenderQueue<E> {
    /** Possible states in which a <code>RenderQueue</code> can stay. */
    private enum State {
        /** The <code>RenderQueue</code> can be modified adding new objects. */
        EDITABLE,
        /** The <code>RenderQueue</code> can only be rendered. */
        DRAWABLE
    }
    
    /** Current state of this class. */
    private State state;
    
    /**
     * Number of times this queue has been rendered from the last time it became
     * drawable.
     */
    private int drawCalls;
    
    /**
     * Internal implementation of a <code>RenderQueue</code>.
     */
    private final RenderQueueImpl<E> impl;

    /**
     * Creates a new <code>RenderQueue</code> from its implementaion.
     * 
     * @param impl the implementation
     */
    RenderQueue(RenderQueueImpl<E> impl) {
        this.state = State.EDITABLE;
        this.drawCalls = 0;
        this.impl = impl;
    }
    
    /** Change the state to DRAWABLE. */
    public final void makeDrawable() {
        if (isDrawable()) return;
                
        this.state = State.DRAWABLE;
        this.drawCalls = 0;
        impl.makeDrawable();
    }
    
    /**
     * Query if this class is drawable.
     * @return <code>true</code> if the state of this class is DRAWABLE, 
     *         <code>false</code> otherwise
     */
    public final boolean isDrawable() {
        return this.state == State.DRAWABLE;
    }
    
    /** Change the state to DRAWABLE. */
    public final void makeEditable() {
        if (isEditable()) return;
        
        this.state = State.EDITABLE;
        impl.makeEditable();
    }
    
    /**
     * Query if this class is editable.
     * @return <code>true</code> if the state of this class is EDITABLE, 
     *         <code>false</code> otherwise
     */
    public final boolean isEditable() {
        return this.state == State.EDITABLE;
    }
    
    /**
     * Adds an element to this queue if this class is editable. 
     * @param e the element to add to the queue
     */
    public final void add(E e) {
        if (!isEditable()) {
            assert false : this.state; // crash in debug
            return;
        }
        
        impl.add(e);
    }
    
    /** Clears the queue of objects to draw if this is editable. */
    public final void clearQueue() {
        if (!isEditable()) {
            assert false : this.state; // crash in debug
            return;
        }
        
        impl.clearQueue();
    }
    
    /**
     * Draws the current queue on screen if this is drawable.
     * 
     * @param gl the GL object
     * @param camera the current camera
     * @param area the region of the screen used for rendering
     */
    public final void draw(GL gl, Camera camera, RenderArea area) {
        if (!isDrawable()) {
            assert false : this.state; // crash in debug
            return;
        }
        
        impl.draw(gl, camera, area, this.drawCalls);
        ++this.drawCalls;
    }
    
}
