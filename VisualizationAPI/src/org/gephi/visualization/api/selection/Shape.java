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

package org.gephi.visualization.api.selection;

public interface Shape {

    void setCameraBridge(CameraBridge cameraBridge);

    /**
     * Returns true if given 3D coordinate point is inside the projection
     * frustum.
     */
    boolean isInside3D(float x, float y, float z);

    /**
     * Returns true if given 2D screen coordinate point is inside the shape.
     */
    boolean isInside2D(int x, int y);

}
