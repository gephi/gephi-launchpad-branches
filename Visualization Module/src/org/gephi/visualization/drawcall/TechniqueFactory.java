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
package org.gephi.visualization.drawcall;

import javax.media.opengl.GL;
import org.gephi.visualization.rendering.command.Technique;

/**
 * Creates a new Technique to process draw calls using vertex arrays or VBOs or
 * VAOs based on the OpenGL version.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class TechniqueFactory {
    private final Type type;

    public TechniqueFactory(GL gl) {
        /*
        if (gl.isExtensionAvailable("GL_VERSION_3_1")) {
            type = Type.VAO;
        } else if (gl.isExtensionAvailable("GL_VERSION_1_5")) {
            type = Type.VBO;
        } else {
            type = Type.VA;
        }
         */
        type = Type.VA; // it is the only class implemented so far
    }
    
    public Technique<DrawCall> create(TechniqueImpl impl) {
        switch (type) {
            case VA:
                return new TechniqueVA(impl);
            case VBO:
                return new TechniqueVBO(impl);
            case VAO:
                return new TechniqueVAO(impl);
            default:
                // it shouldn't be possible to reach the following line of code
                assert false : type;
                return null;
        }
    }
    
    private enum Type {
        VA, VBO, VAO
    }
}
