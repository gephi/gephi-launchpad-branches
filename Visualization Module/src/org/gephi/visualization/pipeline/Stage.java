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

package org.gephi.visualization.pipeline;

import org.gephi.visualization.framebuffer.FrameBuffer;
import java.util.Arrays;
import javax.media.opengl.GL;
import org.gephi.visualization.data.FrameData;

/**
 * Part of the Visualization Pipeline. Subclasses of this class may contains 
 * the code to visualize nodes, edges or labels or implements some 
 * post-processing effect.
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class Stage {

    protected final FrameBuffer[] inputs;
    protected final FrameBuffer[] targets;

    protected Stage(FrameBuffer[] inputs, FrameBuffer[] targets) {
        this.inputs = Arrays.copyOf(inputs, inputs.length);
        this.targets = Arrays.copyOf(targets, targets.length);
    }

    public boolean init(GL gl) {
        return true;
    }

    public void dispose(GL gl) {
        // empty block
    }

    public abstract void draw(GL gl, final FrameData frameData, int frameNumber);
}
