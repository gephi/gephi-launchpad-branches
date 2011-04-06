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

package org.gephi.visualization.controller;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.lib.gleem.linalg.Vec3f;
import org.gephi.visualization.camera.Camera;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Camera camera;

    public Controller() {
        // Random values..
        this.camera = new Camera(800, 600, 0.1f, 10.0f);
    }

    public Camera getCurrentCamera() {
        return this.camera;
    }

    public void resize(int width, int height) {
        this.camera.setImageSize(width, height);
    }

    public void beginUpdateFrame() {

    }

    public void endUpdateFrame() {

    }

    public void beginRenderFrame() {

    }

    public void endRenderFrame() {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    public void initCamera(GraphModel model) {
        Vec3f min = new Vec3f();
        Vec3f max = new Vec3f();

        for (Node n : model.getGraph().getNodes()) {
            NodeData nodeData = n.getNodeData();

            min.setX(Math.min(min.x(), nodeData.x()));
            min.setY(Math.min(min.y(), nodeData.y()));
            min.setZ(Math.min(min.z(), nodeData.z()));

            max.setX(Math.max(max.x(), nodeData.x()));
            max.setY(Math.max(max.y(), nodeData.y()));
            max.setZ(Math.max(max.z(), nodeData.z()));
        }

        float centerX = 0.5f * (max.x() + min.x());
        float centerY = 0.5f * (max.y() + min.y());
        float dY = 0.5f * (max.y() - min.y());

        float d = dY / (float)Math.tan(0.5 * this.camera.fov());

        Vec3f origin = new Vec3f(centerX, centerY, min.z() - d*1.1f);
        this.camera.lookAt(origin, new Vec3f(centerX, centerY, min.z()), Vec3f.Y_AXIS);
        this.camera.setClipPlanes(d, max.z() - min.z() + d*1.2f);
    }

}
