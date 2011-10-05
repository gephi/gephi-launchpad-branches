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
package org.gephi.visualization.rendering.texture;

/**
 * It represents a rectangular subset of a texture.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class TextureArea {
    /**
     * OpenGL handle of the texture.
     */
    public final int handle;
    
    /**
     * Minimum s-coordinate of the rectangular region.
     */
    public final int minS;
    
    /**
     * Minimum t-coordinate of the rectangular region.
     */
    public final int minT;
    
    /**
     * Maximum s-coordinate of the rectangular region.
     */
    public final int maxS;
    
    /**
     * Maximum t-coordinate of the rectangular region.
     */
    public final int maxT;

    /**
     * Creates a TextureArea from its public final fields.
     * 
     * @param handle the OpenGL handle of the texture
     * @param minS the minimum s-coordinate of the rectangular region
     * @param minT the minimum t-coordinate of the rectangular region
     * @param maxS the maximum s-coordinate of the rectangular region
     * @param maxT the maximum t-coordinate of the rectangular region
     */
    public TextureArea(int handle, int minS, int minT, int maxS, int maxT) {
        this.handle = handle;
        this.minS = minS;
        this.minT = minT;
        this.maxS = maxS;
        this.maxT = maxT;
    }
}
