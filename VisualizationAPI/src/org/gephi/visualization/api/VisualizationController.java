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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import org.gephi.graph.api.Node;
import org.gephi.visualization.api.event.VizEventManager;
import org.gephi.visualization.api.rendering.RecordingListener;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;

/**
 * Controller for managing visualization states and configuration.
 * 
 * This is a singleton class and can be found in Lookup:
 * 
 * <pre>VisualizationController vc = Lookup.getDefault().lookup(VisualizationController.class)</pre>
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
     * Returns the instance of SelectionManager.
     */
    public SelectionManager getSelectionManager();

    /**
     * Returns the instance of MotionManager.
     */
    public MotionManager getMotionManager();
    
    /**
     * Returns the instance of MotionManager.
     */
    public VizEventManager getVizEventManager();
    
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

    /**
     * Starts the recording of the visualization output.
     * @param listener          the listener that will be informed about recorded images.
     * @param imageDimensions   dimensions of the image output.
     */
    public void startRecording(RecordingListener listener, Dimension imageDimensions);
    
    /**
     * Stops the recording of the visualization output.
     * @param listener          the listener related to the recording that should be stopped.
     */
    public void stopRecording(RecordingListener listener);
    
}
