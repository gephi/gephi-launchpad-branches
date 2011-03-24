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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.gephi.graph.api.NodeData;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class NodeBatch {

    private final ArrayList<Float> positions;
    private final ArrayList<Float> colors;
    private final ArrayList<Float> sizes;
    //private final ArrayList<Boolean> selected;

    public NodeBatch() {
        this.positions = new ArrayList<Float>();
        this.colors = new ArrayList<Float>();
        this.sizes = new ArrayList<Float>();
        //this.selected = new ArrayList<Boolean>();
    }

    public void addNode(NodeData node) {
        this.positions.add(node.x());
        this.positions.add(node.y());
        this.positions.add(node.z());

        this.colors.add(node.r());
        this.colors.add(node.g());
        this.colors.add(node.b());
        
        this.sizes.add(node.getSize());

        //this.selected.add(node.getModel().isSelected());
    }

    public FloatBuffer fillBuffer(NodeBufferLayout layout) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.positions.size());

        return byteBuffer.asFloatBuffer();
    }

}
