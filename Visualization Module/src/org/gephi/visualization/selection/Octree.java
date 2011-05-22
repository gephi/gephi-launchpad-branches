/*
Copyright 2008-2011 Gephi
Authors : Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
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

package org.gephi.visualization.selection;

import java.util.Collection;
import java.util.List;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.camera.Camera;

public class Octree {

    private final Octant root = null;

    private final Graph graph;

    private Collection<Node> selectedNodes;

    public Octree(Graph graph) {
        this.graph = graph;
    }

    public void rebuild() {

    }

    public void addToSelection(Camera camera, Shape shape) {}

    public void removeFromSelection(Camera camera, Shape shape) {}

    public Collection<Node> getSelectedNodes() {
        return null;
    }

}

class Octant {

    private final static int MAX_NODES = 10;

    private List<Node> nodes;
    private Octant[] octants;
    private final float x, y, z;
    private final float size;

    public Octant(float x, float y, float z, float size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    public void addNode(Node node) {
        if (nodes != null) {
            nodes.add(node);
            if (nodes.size() > MAX_NODES) {
                octants = new Octant[8];
                for (Node n : nodes) {
                    int octantPosition = getChildPosition(n.getNodeData().x(), n.getNodeData().y(), n.getNodeData().z());
                    addToChild(n, octantPosition);
                }
            }
        } else {
            int octantPosition = getChildPosition(node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z());
            addToChild(node, octantPosition);
        }
    }

    /**
     * Adds node to octant's child. Children array must be initialized.
     */
    private void addToChild(Node node, int childPosition) {
        if (octants[childPosition] == null) {
            float newSize = size / 2;
            float dx = (childPosition & 1) == 1 ? newSize : 0;
            float dy = (childPosition & 2) == 2 ? newSize : 0;
            float dz = (childPosition & 4) == 4 ? newSize : 0;
            octants[childPosition] = new Octant(x + dx, y + dy, z + dz, newSize);
        }
        octants[childPosition].addNode(node);
    }

    private int getChildPosition(float nodeX, float nodeY, float nodeZ) {
        int octantPosition = 0;
        float halfSize = size / 2;
        if (nodeX <= x + halfSize) {
            octantPosition |= 1;
        }
        if (nodeY <= y + halfSize) {
            octantPosition |= 2;
        }
        if (nodeZ <= z + halfSize) {
            octantPosition |= 4;
        }
        return octantPosition;
    }

}
