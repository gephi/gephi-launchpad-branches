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

package org.gephi.visualization.data;

import java.util.Collections;
import java.util.List;
import org.gephi.visualization.rendering.camera.Camera;
import org.gephi.visualization.rendering.command.Command;

/**
 * Class used to get the current graph data in View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameData {

    public final Camera camera;
    public final float near;
    public final float far;

    public final List<Command> nodeCommands;
    public final List<Command> edgeCommands;
    public final List<Command> uiCommands;

    public FrameData(Camera camera, float near, float far, List<Command> nodeCommands, List<Command> edgeCommands, List<Command> uiCommands) {
        this.camera = camera;
        this.near = near;
        this.far = far;
        this.nodeCommands = Collections.unmodifiableList(nodeCommands);
        this.edgeCommands = Collections.unmodifiableList(edgeCommands);
        this.uiCommands = Collections.unmodifiableList(uiCommands);
    }
    
}
