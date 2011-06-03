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

package org.gephi.visualization.data.buffer;

import java.nio.ByteBuffer;
import java.util.List;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;
import org.gephi.visualization.data.layout.VizUILayout;
import org.gephi.visualization.utils.Pair;
import org.gephi.visualization.view.ui.UIStyle;

/**
 * Specialization of VizBuffer which can be used to retrieve ui shapes
 * information.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class VizUIBuffer extends VizBuffer<Pair<Shape, UIStyle>> {

    private UIPrimitive.Shape shape;
    private float[] data;
    private UIStyle style;

    public VizUIBuffer(VizUILayout layout, List<ByteBuffer> buffers) {
        super(layout, buffers);
    }

    public static VizUIBuffer wrap(VizBuffer<Pair<Shape, UIStyle>> vb) {
        VizUILayout uiLayout = (VizUILayout) vb.layout;
        VizUIBuffer w = new VizUIBuffer(uiLayout, vb.buffers);
        w.currentBuffer = vb.currentBuffer;

        if (w.isEndOfBuffer()) return w;

        ByteBuffer b = w.buffers.get(w.currentBuffer);

        w.shape = uiLayout.shape(b);
        w.data = uiLayout.data(b);
        w.style = uiLayout.style(b);

        return w;
    }

    @Override
    public void advance() {
        super.advance();

        if (isEndOfBuffer()) return;

        ByteBuffer b = this.buffers.get(this.currentBuffer);
        VizUILayout uiLayout = (VizUILayout) this.layout;

        this.shape = uiLayout.shape(b);
        this.data = uiLayout.data(b);
        this.style = uiLayout.style(b);
    }

    public UIPrimitive.Shape shape() {
        return this.shape;
    }

    public float[] data() {
        return this.data;
    }

    public UIStyle style() {
        return this.style;
    }

}
