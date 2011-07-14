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


import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.camera.Camera;

/**
 * Controller for visualization.
 * 
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public interface VisualizationController {

    /**
     * Returns the dimensions of visualization viewing component.
     */
    public Dimension getViewDimensions();

    /**
     * Returns the location of visualization viewing component.
     */
    public Point getViewLocationOnScreen();

    /**
     * Returns the canvas of visualization viewing component.
     */
    public Canvas getViewCanvas();

    /**
     * Returns the copy of current camera.
     */
    public Camera getCameraCopy();

    /**
     * Centers camera on the graph. Whole graph will be visible.
     */
    public void centerOnGraph();

    /**
     * Centers camera on the point with coordinates [0,0,0].
     */
    public void centerOnZero();

    /**
     * Centers camera on given node.
     */
    public void centerOnNode(Node node);

    /**
     * Returns <code>true</code> if a centering request for camera is pending.
     */
    public boolean isCentering();

    /**
     * Starts the main loop.
     */
    public void start();

    /**
     * Stops the main loop.
     */
    public void stop();

}
