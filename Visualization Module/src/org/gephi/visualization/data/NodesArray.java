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

import java.util.Collection;
import org.gephi.graph.api.Node;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;

/**
 * A NodesArray contains the nodes
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class NodesArray {

    public abstract void ensureCapacity(int minCapacity);
    public abstract int size();

    public abstract void add(Node node);
    public abstract void add(Vec3f position, float size, Vec4f color);
    public abstract void addAll(Node[] nodes);
    public abstract void addAll(Collection<? extends Node> nodes);

    public abstract Vec3f getPositionOf(int i);
    public abstract float getSizeOf(int i);
    public abstract Vec4f getColorOf(int i);

    public static NodesArray create(boolean is3D) {
        if (is3D) return new NodesArray3D();
        else return new NodesArray2D();
    }

    public static NodesArray create(boolean is3D, int initialCapacity) {
        if (is3D) return new NodesArray3D(initialCapacity);
        else return new NodesArray2D(initialCapacity);
    }
    
}
