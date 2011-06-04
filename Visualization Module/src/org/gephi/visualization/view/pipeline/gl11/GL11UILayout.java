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
import org.gephi.visualization.api.color.Color;
import org.gephi.visualization.api.view.ui.UIPrimitive;
import org.gephi.visualization.api.view.ui.UIPrimitive.Shape;
import org.gephi.visualization.data.layout.VizUILayout;
import org.gephi.visualization.utils.Pair;
import org.gephi.visualization.view.ui.UIStyle;

/**
 * VizNodeLayout used by GL11Pipeline3D and GL11Pipeline2D.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class GL11UILayout implements VizUILayout {

    final static private int headerSize = 4 /* size */ + 4 /* shape */ + 4*9 /* style */;

    @Override
    public UIStyle style(ByteBuffer b) {
        int i = b.position() + 8;

        Color fillColor = Color.readArrayRGBAFrom(b, i);
        Color borderColor = Color.readArrayRGBAFrom(b, i+16);
        float borderWidth = b.getFloat(i+32);

        return new UIStyle(fillColor, borderColor, borderWidth);
    }

    @Override
    public int suggestedBlockSize() {
        return 4096;
    }

    @Override
    public boolean advance(ByteBuffer b) {
        int i = b.position();

        final int totalSize = b.getInt(i);

        if (b.remaining() > totalSize) {
            b.position(b.position() + totalSize);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean add(ByteBuffer b, Pair<UIPrimitive, UIStyle> p) {
        final UIPrimitive primitive = p.first;
        final UIStyle style = p.second;

        final int dataSize = 4 * primitive.arguments().length;
        final int totalSize = headerSize + dataSize;

        if (b.remaining() < totalSize) {
            return false;
        } else {
            /* HEADER */
            b.putInt(totalSize);
            b.putInt(primitive.shape().ordinal());

            style.fillColor().writeArrayRGBATo(b);
            style.borderColor().writeArrayRGBATo(b);
            b.putFloat(style.borderWidth());

            /* DATA */
            for (float f : primitive.arguments()) {
                b.putFloat(f);
            }

            return true;
        }
    }

    @Override
    public UIPrimitive primitive(ByteBuffer b) {
        int i = b.position();

        final int totalSize = b.getInt(i);
        final int dataLength = (totalSize - headerSize)/4;

        final Shape shape = UIPrimitive.Shape.values()[b.getInt(i+4)];

        final float[] data = new float[dataLength];

        i += headerSize;
        for (int j = 0; j < dataLength; ++j) {
            data[j] = b.getFloat(i+4*j);
        }

        return UIPrimitive.fromData(shape, data);
    }

}
