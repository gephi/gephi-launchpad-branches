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
package org.gephi.visualization.rendering.apiimpl.command.node.texture;

import java.nio.ByteBuffer;

/**
 * Generates a square procedural texture.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
abstract class ProceduralTextureGenerator {
    
    public void createFillTexture(ByteBuffer image, int size) {
        final float step = 1.0f / (float) (size - 2);
        final float start = 0.5f * (size - 1) * step;
        for (int i = 0; i < size; ++i) {
            final float x = i * step - start;
            for (int j = 0; j < size; ++j) {
                final float y = start - j * step;
                final float color = calculateValue(x, y, 1.0f, step);
                final int iv = (int) Math.floor(color * 255.0f + 0.5f);
                image.put((byte) iv);
                image.put((byte) iv);
            }
        }
        image.rewind();
    }
    
    public void createBorderTexture(float borderSize, ByteBuffer image, int size) {
        final float step = 1.0f / (float) (size - 2);
        final float start = 0.5f * (size - 1) * step;
        for (int i = 0; i < size; ++i) {
            final float x = i * step - start;
            for (int j = 0; j < size; ++j) {
                final float y = start - j * step;
                final float colorOut = calculateValue(x, y, 1.0f, step);
                final float colorIn = 1.0f - calculateValue(x, y, 1.0f - borderSize, step);
                
                final float color = colorOut + colorIn;
                final int iv = (int) Math.floor(color * 255.0f + 0.5f);
                image.put((byte) iv);
                image.put((byte) iv);
            }
        }
        image.rewind();        
    }
    
    protected float calculateValue(float x, float y, float radius, float step) {
        float d = calculateDistance(x, y, radius, step);
        
        if (d < -1.0f) {
            return 1.0f;
        } else if (d > 1.0f) {
            return 0.0f;
        } else {
            final float t = d + 1.0f;
            return t*t*t*(10.0f + t*(6.0f*t - 15.0f));
        }
    }
    
    protected abstract float calculateDistance(float x, float y, float radius, float step);
}
