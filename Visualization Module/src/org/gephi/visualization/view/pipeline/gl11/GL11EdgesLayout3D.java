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
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.lib.gleem.linalg.Vec4f;
import org.gephi.visualization.data.layout.VizEdgeLayout;

/**
 * VizEdgeLayout used by GL11Pipeline3D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11EdgesLayout3D implements VizEdgeLayout {

    @Override
    public int suggestedBlockSize() {
        return 0;
    }

    @Override
    public boolean add(ByteBuffer buffer, Edge edge) {
        return true;
    }

    @Override
    public Vec3f startNodePosition(ByteBuffer b) {
        return new Vec3f();
    }

    @Override
    public float startNodeSize(ByteBuffer b) {
        return 0.0f;
    }

    @Override
    public Vec4f startColor(ByteBuffer b) {
        return new Vec4f();
    }

    @Override
    public Vec3f endNodePosition(ByteBuffer b) {
        return new Vec3f();
    }

    @Override
    public float endNodeSize(ByteBuffer b) {
        return 0.0f;
    }

    @Override
    public Vec4f endColor(ByteBuffer b) {
        return new Vec4f();
    }

    @Override
    public float thickness(ByteBuffer b) {
        return 0.0f;
    }

    @Override
    public boolean isBidirectional(ByteBuffer b) {
        return false;
    }

    @Override
    public boolean isSelected(ByteBuffer b) {
        return false;
    }

    @Override
    public boolean advance(ByteBuffer b) {
        return false;
    }

}
