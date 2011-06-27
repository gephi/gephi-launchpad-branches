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

package org.gephi.visualization.data.graph;

/**
 * Immutable edge visualization data class.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class VizEdge {

    public final VizNode start;
    public final VizNode end;
    public final float thickness;
    public final boolean selected;
    public final float directional;
    public final float bidirectional;

    public VizEdge(VizNode start, VizNode end, float thickness, boolean selected,
            float directional, float bidirectional) {
        this.start = start;
        this.end = end;
        this.thickness = thickness;
        this.selected = selected;
        this.directional = directional;
        this.bidirectional = bidirectional;
    }
}
