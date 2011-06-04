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

package org.gephi.visualization.api.selection;

import java.awt.Point;
import org.gephi.lib.gleem.linalg.Mat4f;
/**
 * Interface for accessing visualization data crucial to frustum intersection 
 * computing. Uses only readonly information from the camera.
 *
 * @author Vojtech Bardiovsky
 */
public interface CameraBridge {

    public Mat4f viewMatrix();

    public Mat4f projectiveMatrix();

    public Point projectPoint(float x, float y, float z);

}
