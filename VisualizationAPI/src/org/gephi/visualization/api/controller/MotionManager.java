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

package org.gephi.visualization.api.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.gephi.math.linalg.Vec3;
import org.gephi.visualization.api.selection.Shape;

/**
 * Class for handling all basic user events that lead to motion. Handles graph
 * dragging, selection shape creation, camera rotation etc.
 *
 * @author Vojtech Bardiovsky
 */
public interface MotionManager {

    public Shape getSelectionShape();

    public int[] getDrag();

    public int[] getMousePosition();

    public Vec3 getMousePosition3d();

    public boolean isPressing();

    public boolean isInside();

    public void mousePressed(MouseEvent e);

    public void mouseClicked(MouseEvent e);

    public void mouseDragged(MouseEvent e);

    public void mouseReleased(MouseEvent e);

    public void mouseMoved(MouseEvent e);

    public void mouseEntered(MouseEvent e);

    public void mouseExited(MouseEvent e);

    public void mouseWheelMoved(MouseWheelEvent e);

    /**
     * Call repeatedly after or before display.
     */
    public void refresh();

}
