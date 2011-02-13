/*
Copyright 2008-2010 Gephi
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

package org.gephi.visualization;

/**
 * Null implementation of VizModel used to provide a visualization model
 * which does nothing when there aren't other visualization models available.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
class NullVizModel implements VizModel {

    public NullVizModel() {
    }

    public boolean isEdgeHasUniColor() {
        return false;
    }

    public void setEdgeHasUniColor(boolean b) {
    }

    public float getMetaEdgeScale() {
        return 0.0f;
    }

}
