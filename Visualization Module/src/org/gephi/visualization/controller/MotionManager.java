package org.gephi.visualization.controller;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import org.gephi.visualization.components.GraphTopComponent;

/**
 * This is a mock class and its proper form would be abstract to allow
 * different implementations for 2D and 3D.
 *
 * @author Vojtech Bardiovsky
 */
public class MotionManager {

    protected float[] mousePosition = new float[2];
    protected float[] mouseDrag = new float[2];

    private static float MOVE_FACTOR = 5.0f;
    private static float ZOOM_FACTOR = 0.008f;
    private static float ORBIT_FACTOR = 0.005f;

    public void mousePressed(MouseEvent e) {
        mouseDrag[0] = e.getX();
        mouseDrag[1] = e.getY();
        if (SwingUtilities.isLeftMouseButton(e)) {
            Controller.getInstance().getCamera().startTranslation();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            Dimension viewDimension = GraphTopComponent.getDefault().getView().getDimension();
            int dx = e.getX() - viewDimension.width / 2;
            int dy = e.getY() - viewDimension.height / 2;
            float orbitModifier = (float) (Math.sqrt(dx * dx + dy * dy) / Math.sqrt(viewDimension.width * viewDimension.width / 4 + viewDimension.height * viewDimension.height / 4));
            Controller.getInstance().getCamera().startOrbit(orbitModifier);
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        float x = e.getX() - mouseDrag[0];
        float y = e.getY() - mouseDrag[1];
        mouseDrag[0] = e.getX();
        mouseDrag[1] = e.getY();
        if (SwingUtilities.isLeftMouseButton(e)) {
            Controller.getInstance().getCamera().updateTranslation(MOVE_FACTOR * x, -MOVE_FACTOR * y);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            Controller.getInstance().getCamera().updateOrbit(ORBIT_FACTOR * x, ORBIT_FACTOR * y);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {

        } else if (SwingUtilities.isRightMouseButton(e)) {

        }
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        Controller.getInstance().getCamera().zoom(ZOOM_FACTOR * e.getUnitsToScroll());
    }

}
