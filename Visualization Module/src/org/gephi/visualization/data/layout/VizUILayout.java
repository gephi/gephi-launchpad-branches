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

package org.gephi.visualization.data.layout;

import java.nio.ByteBuffer;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.view.ui.UIPrimitive;
import org.gephi.visualization.utils.Pair;
import org.gephi.visualization.view.ui.UIStyle;

/**
 * Interface which controls how UI shapes are stored in the buffers.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface VizUILayout extends VizLayout<Pair<Shape, UIStyle>> {

    public UIPrimitive.Shape shape(ByteBuffer b);
    public float[] data(ByteBuffer b);
    public UIStyle style(ByteBuffer b);

}
