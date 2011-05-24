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

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.camera.Camera;

/**
 * Interface used by Model to send frame data to View.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface FrameDataBridgeIn {

    public void beginFrame(Camera camera);

    public void add(Node node);

    public void add(Edge edge);

    public void endFrame();

}
