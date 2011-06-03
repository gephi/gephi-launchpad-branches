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

package org.gephi.visualization.controller;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.apiimpl.shape.Rectangle;
import org.openide.util.Lookup;

public class MotionManager3D implements MotionManager {

    protected float[] mousePosition = new float[2];
    protected float[] mouseDrag = new float[2];

    protected Shape selectionShape;

    protected static float MOVE_FACTOR = 5.0f;
    protected static float ZOOM_FACTOR = 0.008f;
    protected static float ORBIT_FACTOR = 0.005f;

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDrag[0] = e.getX();
        mouseDrag[1] = e.getY();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        if (vizConfig.isSelectionEnabled()) {
            // TODO make differentiating between the shapes properly - either an enum or a string property or class
            if (vizConfig.isRectangleSelection()) {
                selectionShape = Rectangle.initRectangle(e.getX(), e.getY());
            }
        }
        if (vizConfig.isDraggingEnabled()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Controller.getInstance().getCamera().startTranslation();
            } else if (SwingUtilities.isRightMouseButton(e)) {
                Dimension viewDimension = Controller.getInstance().getViewDimensions();
                int dx = e.getX() - viewDimension.width / 2;
                int dy = e.getY() - viewDimension.height / 2;
                float orbitModifier = (float) (Math.sqrt(dx * dx + dy * dy) / Math.sqrt(viewDimension.width * viewDimension.width / 4 + viewDimension.height * viewDimension.height / 4));
                Controller.getInstance().getCamera().startOrbit(orbitModifier);
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        float x = e.getX() - mouseDrag[0];
        float y = e.getY() - mouseDrag[1];
        mouseDrag[0] = e.getX();
        mouseDrag[1] = e.getY();
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        if (vizConfig.isSelectionEnabled()) {
            selectionShape = selectionShape.continuousUpdate(e.getX(), e.getY());
            Lookup.getDefault().lookup(SelectionManager.class).addSelection(selectionShape);
        }
        if (vizConfig.isDraggingEnabled()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
            Controller.getInstance().getCamera().updateTranslation(MOVE_FACTOR * x, -MOVE_FACTOR * y);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                Controller.getInstance().getCamera().updateOrbit(ORBIT_FACTOR * x, ORBIT_FACTOR * y);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {

        } else if (SwingUtilities.isRightMouseButton(e)) {

        }
        selectionShape = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Controller.getInstance().getCamera().zoom(ZOOM_FACTOR * e.getUnitsToScroll());
    }

}
