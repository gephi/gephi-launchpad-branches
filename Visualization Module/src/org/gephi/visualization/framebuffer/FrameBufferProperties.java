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

package org.gephi.visualization.framebuffer;

/**
 * Class used to set the properties of a new frame buffer.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameBufferProperties {

    private int width, height;

    private int renderTargetNumbers;
    private boolean useDepthBuffer;
    private boolean useStencilBuffer;

    public boolean readable;

    public FrameBufferProperties(int width, int height) {
        this.width = width;
        this.height = height;

        this.renderTargetNumbers = 1;
        this.useDepthBuffer = true;
        this.useStencilBuffer = false;

        this.readable = false;
    }

}
