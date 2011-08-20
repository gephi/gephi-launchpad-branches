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


import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.gephi.graph.api.Node;
import org.gephi.math.linalg.Vec3;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.visualization.api.MotionManager;
import org.gephi.visualization.api.Camera;
import org.gephi.visualization.api.VisualizationController;
import org.gephi.visualization.api.event.VizEventManager;
import org.gephi.visualization.api.rendering.RecordingListener;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.api.vizmodel.VizConfig;
import org.gephi.visualization.api.vizmodel.VizModel;
import org.gephi.visualization.camera.Camera2d;
import org.gephi.visualization.camera.Camera3d;
import org.gephi.visualization.api.geometry.AABB;
import org.gephi.visualization.event.VizEventManagerImpl;
import org.gephi.visualization.model.Model;
import org.gephi.visualization.rendering.RenderingEngine;
import org.gephi.visualization.selection.SelectionManagerImpl;
import org.gephi.visualization.vizmodel.VizModelImpl;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of visualization controller.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 * @author Vojtech Bardiovsky <vojtech.bardiovsky@gmail.com>
 */
@ServiceProvider(service = VisualizationController.class)
public class VisualizationControllerImpl implements VisualizationController, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WorkspaceListener {

    private Camera camera;

    private static VisualizationControllerImpl instance;
    private final RenderingEngine renderingEngine;
    private final Model dataManager;
    
    private SelectionManager selectionManager;
    private MotionManager motionManager;
    private VizEventManager vizEventManager;

    private Dimension viewSize;

    private VizModel vizModel;
    
    private boolean centerGraph = true;
    private boolean centerZero;
    private float[] centerNode;

    private boolean use3d;
    private boolean reinit;
    private boolean hasWorkspace;
    
    private float lightenAnimationDelta;
    private boolean previouslySelected;
    
    public VisualizationControllerImpl() {
        this.viewSize = new Dimension();
        
        this.vizModel = new VizModelImpl(true);
        this.selectionManager = new SelectionManagerImpl();
        this.motionManager = new MotionManagerImpl();
        this.vizEventManager = new VizEventManagerImpl();
        
        motionManager.initialize(this);
        selectionManager.initialize();
        
        this.renderingEngine = new RenderingEngine(this, this.vizModel);
        this.dataManager = new Model(this, this.renderingEngine.bridge(), 33);
        
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(this);

        if (pc.getCurrentWorkspace() != null) {
            reinit = true;
        }
        
        vizModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (VizConfig.CAMERA_USE_3D.equals(evt.getPropertyName())) {
                    modeChanged();
                }
            }
        });
    }

    public void resize(int width, int height) {
        this.viewSize = new Dimension(width, height);
        Camera camera = vizModel.getCamera();
        camera.screenSize(viewSize);
        vizModel.setCamera(camera);
    }

    @Override
    public Dimension getViewDimensions() {
        return viewSize;
    }

    @Override
    public Point getViewLocationOnScreen() {
        return this.renderingEngine.renderingCanvas().getLocationOnScreen();
    }

    @Override
    public Component getViewComponent() {
        return this.renderingEngine.renderingCanvas();
    }

    public void setCursor(Cursor cursor) {
        this.renderingEngine.renderingCanvas().setCursor(cursor);
    }

    @Override
    public Camera getCamera() {
        return vizModel.getCamera();
    }

    @Override
    public Camera getCameraCopy() {
        return vizModel.getCamera().copy();
    }

    @Override
    public VizModel getVizModel() {
        return vizModel;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public MotionManager getMotionManager() {
        return motionManager;
    }

    @Override
    public VizEventManager getVizEventManager() {
        return vizEventManager;
    }
    
    @Override
    public VizConfig getVizConfig() {
        return vizModel.getConfig();
    }
    
    @Override
    public void centerOnGraph() {
        centerGraph = true;
    }

    @Override
    public void centerOnZero() {
        centerZero = true;
    }

    @Override
    public void centerOnNode(Node node) {
        centerNode = new float[]{node.getNodeData().x(), node.getNodeData().y(), node.getNodeData().z()};
    }

    @Override
    public boolean isCentering() {
        return centerGraph || centerZero || centerNode != null;
    }

    public void modeChanged() {
        boolean modelUse3d = vizModel.isUse3d();
        if (modelUse3d == this.use3d) {
            return;
        }
        Camera newCamera = null;
        Camera cam = vizModel.getCamera();
        if (modelUse3d) {
        // Set 2D mode
            if (cam instanceof Camera2d) {
                newCamera = new Camera3d((Camera2d) cam);
                
                // TODO add other engine code
                //
            }
        } else {
        // Set 3D mode
            if (cam instanceof Camera3d) {
                newCamera = new Camera2d((Camera3d) cam);

                // TODO add other engine code
                //
            }
        }
        vizModel.setCamera(newCamera);
        selectionManager.refresh();
    }

    public void beginUpdateFrame() {
        if (reinit) {
            refreshWorkspace();
            reinit = false;
        }
        // Highlight non-selected nodes animation setup
        boolean anySelected = selectionManager.isNodeSelected();
        if (vizModel.isHighlightNonSelectedEnabled()) {
            if (vizModel.getConfig().getBooleanProperty(VizConfig.HIGHLIGHT_NON_SELECTED_ANIMATION)) {
                if (!anySelected && previouslySelected) {
                    //Start animation
                    lightenAnimationDelta = 0.07f;
                } else if (anySelected && !previouslySelected) {
                    //Stop animation
                    lightenAnimationDelta = -0.07f;
                }
                vizModel.getConfig().setProperty(VizConfig.HIGHLIGHT_NON_SELECTED, previouslySelected || lightenAnimationDelta != 0);
            } else {
                vizModel.getConfig().setProperty(VizConfig.HIGHLIGHT_NON_SELECTED, previouslySelected);
            }
        }
        previouslySelected = anySelected;
        // Highlight non-selected nodes animation step
        if (lightenAnimationDelta != 0) {
            float factor = vizModel.getConfig().getFloatProperty(VizConfig.HIGHLIGHT_NON_SELECTED_FACTOR);
            factor += lightenAnimationDelta;
            if (factor >= 0.5f && factor <= 0.98f) {
                vizModel.getConfig().setProperty(VizConfig.HIGHLIGHT_NON_SELECTED_FACTOR, factor);
            } else {
                lightenAnimationDelta = 0;
                vizModel.getConfig().setProperty(VizConfig.HIGHLIGHT_NON_SELECTED, previouslySelected);
            }
        }
    }

    public void endUpdateFrame() {
        Camera camera = vizModel.getCamera();
        if (centerGraph && vizModel.getGraphLimits().getMinX() < Float.MAX_VALUE) {
            camera.centerGraph(vizModel.getGraphLimits());
            centerGraph = false;
        }
        if (centerZero) {
            camera.lookAt(Vec3.ZERO, Vec3.E2);
            centerZero = false;
        }
        if (centerNode != null) {
            camera.lookAt(new Vec3(centerNode[0], centerNode[1], centerNode[2]), Vec3.E2);
            centerNode = null;
        }
        motionManager.refresh();
    }

    public void beginRenderFrame() {
    }

    public void endRenderFrame() {
    }

    @Override
    public void start() {
        this.dataManager.start();
        this.renderingEngine.startRendering();
    }

    @Override
    public void stop() {
        this.dataManager.stop();
        this.renderingEngine.stopRendering();
    }

    @Override
    public void startRecording(RecordingListener listener, Dimension imageDimensions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopRecording(RecordingListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void refreshWorkspace() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        VizModel model = null;
        if (pc.getCurrentWorkspace() == null) {
            model = new VizModelImpl(true);
        } else {
            model = pc.getCurrentWorkspace().getLookup().lookup(VizModel.class);
            if (model == null) {
                model = new VizModelImpl();
                pc.getCurrentWorkspace().add(model);
            }
        }
        if (model != vizModel) {
            model.setListeners(vizModel.getListeners());
            model.getTextModel().setListeners(vizModel.getTextModel().getListeners());
            vizModel.setListeners(null);
            vizModel.getTextModel().setListeners(null);
            vizModel = model;
            vizModel.init();
        }
        selectionManager.refresh();
    }
    
    // User events
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (hasWorkspace) {
            motionManager.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (hasWorkspace) {
            motionManager.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (hasWorkspace) {
            motionManager.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.renderingEngine.setFPS(this.vizModel.getNormalFPS());
        
        if (hasWorkspace) {
            motionManager.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (this.vizModel.isReduceFPSWhenMouseOut()) {
            this.renderingEngine.setFPS(this.vizModel.getReduceFPSWhenMouseOutValue());
        }
        
        if (hasWorkspace) {
            motionManager.mouseExited(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (hasWorkspace) {
            motionManager.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (hasWorkspace) {
            motionManager.mouseMoved(e);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (hasWorkspace) {
             motionManager.mouseWheelMoved(e);
        }
    }

    // Workspace events
    @Override
    public void initialize(Workspace workspace) {
        workspace.add(new VizModelImpl());
    }

    @Override
    public void select(Workspace workspace) {
        hasWorkspace = true;
        reinit = true;
    }

    @Override
    public void unselect(Workspace workspace) {}

    @Override
    public void close(Workspace workspace) {}

    @Override
    public void disable() {
        reinit = true;
    }
    
}
