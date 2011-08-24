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
package org.gephi.visualization.rendering.command.buffer;

import java.util.Collection;
import javax.media.opengl.GL;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Technique;

/**
 * Technique for drawing objects stored in a buffer.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class BufferedTechnique<E> implements Technique<Buffer<E>> {
    private final Layout<E> layout;
    private final Buffer.Type bufferType;
    protected int currentPass;
    
    public BufferedTechnique(Layout<E> layout, Buffer.Type bufferType) {
        this.layout = layout;
        this.bufferType = bufferType;
        this.currentPass = -1;
    }
    
    public final Layout<E> layout() {
        return this.layout;
    }
    
    public final Buffer.Type bufferType() {
        return this.bufferType;
    }

    @Override
    public void draw(GL gl, Buffer<E> e) {
        e.draw(gl, this.currentPass);
    }

    @Override
    public void end(GL gl) {
        this.layout.disableClientStates(gl);
        this.currentPass = -1;
    }

    @Override
    public boolean advanceToNextPass(GL gl) {
        ++this.currentPass;
        return true;
    }

    @Override
    public void disposeElements(GL gl, Collection<? extends Buffer<E>> e) {
        for (Buffer<E> b : e) {
            b.bufferImpl.dispose(gl);
        }
    }

    @Override
    public boolean begin(GL gl, Camera camera, RenderArea renderArea) {
        this.layout.enableClientStates(gl);
        return this.setCamera(gl, camera, renderArea);
    }

    protected abstract boolean setCamera(GL gl, Camera camera, RenderArea renderArea);
    
}
