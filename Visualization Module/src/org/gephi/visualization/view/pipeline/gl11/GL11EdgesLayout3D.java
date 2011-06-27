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

package org.gephi.visualization.view.pipeline.gl11;

import java.nio.ByteBuffer;
import org.gephi.graph.api.Edge;
import org.gephi.visualization.data.graph.VizEdge;
import org.gephi.visualization.data.layout.Layout;

/**
 * EdgeLayout used by GL11Pipeline3D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11EdgesLayout3D implements Layout<Edge, VizEdge> {

    @Override
    public int suggestedBufferSize() {
        return 0;
    }

    @Override
    public boolean add(ByteBuffer buffer, Edge edge) {
        return true;
    }

    @Override
    public VizEdge get(ByteBuffer b) {
        return null;
    }

    @Override
    public VizEdge get(ByteBuffer b, int[] i) {
        return null;
    }

    @Override
    public boolean hasNext(ByteBuffer b) {
        return false;
    }

    @Override
    public boolean hasNext(ByteBuffer b, int i) {
        return false;
    }

}
