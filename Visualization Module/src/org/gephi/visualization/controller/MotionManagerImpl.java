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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import org.gephi.graph.api.Node;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.config.VizConfig;
import org.gephi.visualization.api.event.VizEventManager;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.selection.SelectionType;
import org.gephi.visualization.api.selection.Shape;
import org.gephi.visualization.api.selection.Shape.SelectionModifier;
import org.gephi.visualization.apiimpl.shape.ShapeUtils;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = MotionManager.class)
public class MotionManagerImpl implements MotionManager {

    protected static float MOVE_FACTOR = 5.0f;
    protected static float ZOOM_FACTOR = 0.008f;
    protected static float ORBIT_FACTOR = 0.005f;

    protected int[] mousePosition = new int[2];
    protected int[] mouseDrag = new int[2];
    protected int[] startDrag = new int[2];

    protected Shape selectionShape;

    protected boolean dragging;
    protected boolean pressing;

    @Override
    public Shape getSelectionShape() {
        return selectionShape;
    }

    @Override
    public float[] getDrag() {
        return new float[] {mouseDrag[0], mouseDrag[1]};
    }

    @Override
    public float[] getDrag3d() {
        // TODO real displacement
        return new float[3];
    }

    @Override
    public float[] getMousePosition() {
        return new float[] {mousePosition[0], mousePosition[1]};
    }

    @Override
    public float[] getMousePosition3d() { // NOT VERY MEANINGFUL
        Vec3f position = Controller.getInstance().getCamera().projectPointInverse(mousePosition[0], mousePosition[1]);
        return new float[]{position.x(), position.y(), position.z()};
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePosition[0] = e.getXOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getX();
        mousePosition[1] = e.getYOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getY();
        startDrag[0] = mousePosition[0];
        startDrag[1] = mousePosition[1];
        dragging = true;

        SelectionModifier modifier = extractSelectionModifier(e);
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);

        if (vizConfig.isDirectMouseSelection()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (modifier == SelectionModifier.DEFAULT) {
                    selectionManager.clearSelection();
                }
                selectionManager.selectSingle(e.getPoint(), modifier.isPositive());
            }
        } else if (vizConfig.getSelectionType() != SelectionType.NONE) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (modifier == SelectionModifier.DEFAULT) {
                    selectionManager.clearSelection();
                }

                // Initialize selections
                if (selectionShape == null || selectionShape.getSelectionType() != vizConfig.getSelectionType()) {
                    selectionShape = ShapeUtils.initShape(vizConfig.getSelectionType(), modifier, e.getX(), e.getY());
                // And also update discrete type selections for better responsiveness
                } else {
                    selectionShape = ShapeUtils.singleUpdate(selectionShape, e.getX(), e.getY());

                    if (selectionShape.isDiscretelyUpdated()) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            selectionManager.applyContinuousSelection(selectionShape);
                        }
                    }
                }
            }
        }
        // Movement
        if (SwingUtilities.isRightMouseButton(e)) {
            Controller.getInstance().getCamera().startTranslation();
            Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            Dimension viewDimension = Controller.getInstance().getViewDimensions();
            int dx = e.getX() - viewDimension.width / 2;
            int dy = e.getY() - viewDimension.height / 2;
            float orbitModifier = (float) (Math.sqrt(dx * dx + dy * dy) / Math.sqrt(viewDimension.width * viewDimension.width / 4 + viewDimension.height * viewDimension.height / 4));
            Controller.getInstance().getCamera().startOrbit(orbitModifier);
            Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }

        // TODO do not send events during selection
        VizEventManager vizEventManager = Lookup.getDefault().lookup(VizEventManager.class);
        if (SwingUtilities.isRightMouseButton(e)) {
            vizEventManager.mouseRightPress();
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            vizEventManager.mouseMiddlePress();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            vizEventManager.mouseLeftPress();
            vizEventManager.startDrag();
            pressing = true;
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        VizEventManager eventManager = Lookup.getDefault().lookup(VizEventManager.class);
        if (SwingUtilities.isLeftMouseButton(e)) {
            eventManager.mouseLeftClick();
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            eventManager.mouseMiddleClick();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            eventManager.mouseRightClick();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int newX = e.getXOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getX();
        int newY = e.getYOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getY();
        int x = newX - mousePosition[0];
        int y = newY - mousePosition[1];
        mousePosition[0] = newX;
        mousePosition[1] = newY;
        mouseDrag[0] = mousePosition[0] - startDrag[0];
        mouseDrag[1] = startDrag[1] - mousePosition[1];

        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);

        if (vizConfig.getSelectionType() != SelectionType.NONE && selectionShape != null) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                selectionShape = ShapeUtils.continuousUpdate(selectionShape, e.getX(), e.getY());
                selectionManager.cancelContinuousSelection();
                selectionManager.applyContinuousSelection(selectionShape);
            }
        } else if (vizConfig.isDraggingEnabled()) {
            Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (SwingUtilities.isLeftMouseButton(e)) {
                Vec3f translation = Controller.getInstance().getCamera().projectVectorInverse(MOVE_FACTOR * x, MOVE_FACTOR * y);
                for (Node node : selectionManager.getSelectedNodes()) {
                    node.getNodeData().setPosition(node.getNodeData().x() - translation.x(), node.getNodeData().y() - translation.y(), node.getNodeData().z() - translation.z());
                }
            }
        } else {
            Lookup.getDefault().lookup(VizEventManager.class).drag();
        }
        // Movement
        if (SwingUtilities.isRightMouseButton(e)) {
            Controller.getInstance().getCamera().updateTranslation(-MOVE_FACTOR * x, MOVE_FACTOR * y);
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            Controller.getInstance().getCamera().updateOrbit(ORBIT_FACTOR * x, ORBIT_FACTOR * y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
        dragging = false;
        pressing = false;

        if (vizConfig.getSelectionType() != SelectionType.NONE && selectionShape != null) {
            selectionShape = ShapeUtils.continuousUpdate(selectionShape, e.getX(), e.getY());

            if (!selectionShape.isDiscretelyUpdated() && SwingUtilities.isLeftMouseButton(e) ||
                selectionShape.isDiscretelyUpdated() && SwingUtilities.isRightMouseButton(e)) {
                selectionManager.cancelContinuousSelection();
                selectionManager.applySelection(selectionShape);
            }

        }

        if (selectionShape != null && (!selectionShape.isDiscretelyUpdated() ||
            selectionShape.isDiscretelyUpdated() && SwingUtilities.isRightMouseButton(e))) {
            selectionShape = null;
        }
        Lookup.getDefault().lookup(VizEventManager.class).stopDrag();
        Lookup.getDefault().lookup(VizEventManager.class).mouseReleased();
        Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition[0] = e.getXOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getX();
        mousePosition[1] = e.getYOnScreen() - (int) Controller.getInstance().getViewLocationOnScreen().getY();

        VizConfig vizConfig = Lookup.getDefault().lookup(VizConfig.class);
        SelectionManager selectionManager = Lookup.getDefault().lookup(SelectionManager.class);
        SelectionModifier modifier = extractSelectionModifier(e);

        // Only discretely updated shapes
        if (vizConfig.getSelectionType() != SelectionType.NONE && selectionShape != null && selectionShape.isDiscretelyUpdated()) {
            selectionShape = ShapeUtils.continuousUpdate(selectionShape, e.getX(), e.getY());
            selectionManager.cancelContinuousSelection();
            selectionManager.applyContinuousSelection(selectionShape);
        }

        if (vizConfig.isDirectMouseSelection()) {
            // TODO temporary
            selectionManager.deselectSingle();
            boolean selected = selectionManager.selectContinuousSingle(e.getPoint(), modifier.isPositive());
            if (selected) {
                Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                Controller.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Controller.getInstance().getCamera().zoom(ZOOM_FACTOR * e.getUnitsToScroll());
    }



    private SelectionModifier extractSelectionModifier(MouseEvent e) {
        return (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) > 0 ? SelectionModifier.INCREMENTAL :
               (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0 ? SelectionModifier.DECREMENTAL :
               SelectionModifier.DEFAULT;
    }

    @Override
    public void refresh() {
        if (pressing) {
            Lookup.getDefault().lookup(VizEventManager.class).mouseLeftPressing();
        }
    }

}
