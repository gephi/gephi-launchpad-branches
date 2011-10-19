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

package org.gephi.visualization.api;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.gephi.math.Vec3;
import org.gephi.visualization.api.selection.Shape;

/**
 * Manager for handling all basic user events that lead to motion. Handles graph
 * dragging, selection shape creation, camera rotation etc.
 *
 * This is a singleton class and is provided by the {@link VisualizationController}.
 * 
 * @see VisualizationController#getMotionManager()
 * 
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public interface MotionManager {

    public void initialize(VisualizationController visualizationController);
    
    /**
     * Returns the current selection shape if a selection is being done by user,
     * returns <code>null</code> otherwise.
     */
    public Shape getSelectionShape();

    /**
     * Returns the drag vector in absolute value.
     */
    public int[] getDrag();

    /**
     * Returns the current mouse position relative to visualization canvas.
     */
    public int[] getMousePosition();

    /**
     * Returns the current mouse position in world coordinates. In 3D this is a
     * best estimate.
     */
    public Vec3 getMousePosition3d();

    /**
     * Returns <code>true</code> if mouse button is pressed.
     */
    public boolean isPressing();

    /**
     * Returns <code>true</code> if mouse pointer is inside the visualization
     * canvas.
     */
    public boolean isInside();

    /**
     * Call repeatedly after or before display.
     */
    public void refresh();
    
    public void mousePressed(MouseEvent e);

    public void mouseClicked(MouseEvent e);

    public void mouseDragged(MouseEvent e);

    public void mouseReleased(MouseEvent e);

    public void mouseMoved(MouseEvent e);

    public void mouseEntered(MouseEvent e);

    public void mouseExited(MouseEvent e);

    public void mouseWheelMoved(MouseWheelEvent e);

}
