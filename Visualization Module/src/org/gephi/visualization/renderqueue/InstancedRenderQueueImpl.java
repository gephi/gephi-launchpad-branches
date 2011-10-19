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

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import org.gephi.visualization.data.camera.Camera;
import org.gephi.visualization.data.camera.RenderArea;
import org.gephi.visualization.rendering.command.Technique;

/**
 * Render queue implementation which simply draw a list of objects.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class InstancedRenderQueueImpl<E> implements RenderQueueImpl<E> {
    private final List<E> elements;
    private final Technique<E> technique;

    public InstancedRenderQueueImpl(Technique<E> technique) {
        this.elements = new ArrayList<E>();
        this.technique = technique;
    }

    @Override
    public void makeDrawable() {
        /* EMPTY BLOCK */
    }

    @Override
    public void makeEditable() {
        /* EMPTY BLOCK */
    }

    @Override
    public void add(E e) {
        this.elements.add(e);
    }

    @Override
    public void clearQueue() {
        this.elements.clear();
    }

    @Override
    public void draw(GL gl, Camera camera, RenderArea area, int drawCalls) {
        if (!this.technique.begin(gl, camera, area, drawCalls > 0)) return;
        
        while (this.technique.advanceToNextPass(gl)) {            
            for (E e : this.elements) {
                this.technique.draw(gl, e);
            }
        }
        
        this.technique.end(gl);
    }
    
}
