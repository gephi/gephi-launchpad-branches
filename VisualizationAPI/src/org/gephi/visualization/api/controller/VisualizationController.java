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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.camera.Camera;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;

/**
 * Controller for visualization.
 * 
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
public interface VisualizationController {

    /**
     * Returns the dimensions of visualization displaying component.
     */
    public Dimension getViewDimensions();

    /**
     * Returns the location of visualization displaying component.
     */
    public Point getViewLocationOnScreen();

    /**
     * Returns the visualization displaying component.
     */
    public Component getViewComponent();

    /**
     * Returns the copy of current camera.
     */
    public Camera getCameraCopy();

    /**
     * Returns the current camera.
     */
    public Camera getCamera();

    /**
     * Returns the current active VizModel.
     */
    public VizModel getVizModel();
    
    /**
     * Returns the current active VizConfig.
     */
    public VizConfig getVizConfig();
    
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
     * Called when the mode of scene display and navigation has been changed to
     * 3D or 2D.
     */
    public void modeChanged();
    
    /**
     * Starts the main loop.
     */
    public void start();

    /**
     * Stops the main loop.
     */
    public void stop();

}
