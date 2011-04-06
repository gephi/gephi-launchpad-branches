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

import java.util.ArrayList;
import java.util.Collection;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class NodesArray3D extends NodesArray {

    private final ArrayList<Float> positions;
    private final ArrayList<Float> sizes;
    private final ArrayList<Float> colors;
    private int size;

    public NodesArray3D(int initialCapacity) {
        this.positions = new ArrayList<Float>(3 * initialCapacity);
        this.sizes = new ArrayList<Float>(initialCapacity);
        this.colors = new ArrayList<Float>(3 * initialCapacity);
        this.size = 0;
    }

    NodesArray3D() {
        this.positions = new ArrayList<Float>();
        this.sizes = new ArrayList<Float>();
        this.colors = new ArrayList<Float>();
        this.size = 0;
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        this.positions.ensureCapacity(3 * minCapacity);
        this.sizes.ensureCapacity(minCapacity);
        this.colors.ensureCapacity(3 * minCapacity);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void add(Node node) {
        NodeData nodeData = node.getNodeData();

        this.positions.add(nodeData.x());
        this.positions.add(nodeData.y());
        this.positions.add(nodeData.z());

        this.sizes.add(nodeData.getSize());

        this.colors.add(nodeData.r());
        this.colors.add(nodeData.g());
        this.colors.add(nodeData.b());

        ++this.size;
    }

    @Override
    public void addAll(Node[] nodes) {
        for (Node n : nodes) {
            this.add(n);
        }
    }

    @Override
    public void addAll(Collection<? extends Node> nodes) {
        for (Node n : nodes) {
            this.add(n);
        }
    }

    @Override
    public Vec3f getPositionOf(int i) {
        int s = 3*i;
        return new Vec3f(this.positions.get(s), this.positions.get(s+1), this.positions.get(s+2));
    }

    @Override
    public float getSizeOf(int i) {
        return this.sizes.get(i);
    }

    @Override
    public Vec4f getColorOf(int i) {
        int s = 3*i;
        return new Vec4f(this.positions.get(s), this.positions.get(s+1), this.positions.get(s+2), 1.0f);
    }

}
