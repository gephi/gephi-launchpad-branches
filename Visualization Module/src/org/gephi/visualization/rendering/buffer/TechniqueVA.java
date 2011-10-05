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
package org.gephi.visualization.rendering.buffer;

import javax.media.opengl.GL;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.camera.RenderArea;
import org.gephi.visualization.rendering.command.Technique;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class TechniqueVA implements Technique<DrawCall> {
    
    private final TechniqueImpl impl;
    private int currentPass;

    public TechniqueVA(TechniqueImpl impl) {
        this.impl = impl;
        this.currentPass = -1;
    }

    @Override
    @SuppressWarnings("unused") // reuseResourses is not useful in this case
    public boolean begin(GL gl, Camera camera, RenderArea renderArea, boolean reuseResources) {
        return this.impl.begin(gl, camera, renderArea);
    }

    @Override
    public boolean advanceToNextPass(GL gl) {
        this.impl.disableClientStates(gl, this.currentPass);
        this.impl.endPass(gl, this.currentPass);
        ++this.currentPass;
        
        if (this.currentPass >= this.impl.numberOfPasses())
            return false;
            
        this.impl.enableClientStates(gl, this.currentPass);
        this.impl.initPass(gl, this.currentPass);
        return true;
    }

    @Override
    public void draw(GL gl, DrawCall e) {
        this.impl.setPointers(gl, e, this.currentPass);
        
        final int oldPosition = e.indexBuffer.position();
        e.indexBuffer.position(e.start);
        
        gl.glDrawElements(e.mode, e.count, e.type, e.indexBuffer);
        
        e.indexBuffer.position(oldPosition);
    }

    @Override
    public void end(GL gl) {
        this.impl.end(gl);
        this.currentPass = -1;
    }

    @Override
    public void dispose(GL gl) {
        this.impl.dispose(gl);
    }
    
}
