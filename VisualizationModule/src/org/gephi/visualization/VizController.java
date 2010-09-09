/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.visualization;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.project.api.ProjectController;
import org.gephi.visualization.apiimpl.GraphIO;
import org.gephi.visualization.apiimpl.VizEventManager;
import org.gephi.visualization.config.VizCommander;
import org.gephi.visualization.apiimpl.VizConfig;
import org.gephi.visualization.events.StandardVizEventManager;
import org.gephi.visualization.api.objects.ModelClassLibrary;
import org.gephi.visualization.objects.StandardModelClassLibrary;
import org.gephi.visualization.opengl.AbstractEngine;
import org.gephi.visualization.opengl.compatibility.CompatibilityEngine;
import org.gephi.visualization.opengl.compatibility.CompatibilityScheduler;
import org.gephi.visualization.apiimpl.Scheduler;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.bridge.DHNSDataBridge;
import org.gephi.visualization.bridge.DHNSEventBridge;
import org.gephi.visualization.bridge.DataBridge;
import org.gephi.visualization.bridge.EventBridge;
import org.gephi.visualization.mode.ModeManager;
import org.gephi.visualization.screenshot.ScreenshotMaker;
import org.gephi.visualization.opengl.text.TextManager;
import org.gephi.visualization.swing.GraphDrawableImpl;
import org.gephi.visualization.swing.StandardGraphIO;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.visualization.api.VisualizationController;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = VisualizationController.class)
public class VizController implements VisualizationController {

    //Singleton
    private static VizController instance;

    public VizController() {
    }

    public synchronized static VizController getInstance() {
        if (instance == null) {
            instance = (VizController)Lookup.getDefault().lookup(VisualizationController.class);
            instance.initInstances();
        }
        return instance;
    }
    //Architecture
    private GraphDrawableImpl drawable;
    private AbstractEngine engine;
    private Scheduler scheduler;
    private VizConfig vizConfig;
    private GraphIO graphIO;
    private VizEventManager vizEventManager;
    private ModelClassLibrary modelClassLibrary;
    private GraphLimits limits;
    private DataBridge dataBridge;
    private EventBridge eventBridge;
    private ModeManager modeManager;
    private TextManager textManager;
    private ScreenshotMaker screenshotMaker;
    private SelectionManager selectionManager;
    //Variable
    private VizModel currentModel;

    public void initInstances() {
        VizCommander commander = new VizCommander();

        vizConfig = new VizConfig();
        graphIO = new StandardGraphIO();
        engine = new CompatibilityEngine();
        vizEventManager = new StandardVizEventManager();
        scheduler = new CompatibilityScheduler();
        modelClassLibrary = new StandardModelClassLibrary();
        limits = new GraphLimits();
        dataBridge = new DHNSDataBridge();
        eventBridge = new DHNSEventBridge();
        //dataBridge = new TestDataBridge();
        modeManager = new ModeManager();
        textManager = new TextManager();
        screenshotMaker = new ScreenshotMaker();
        currentModel = new VizModel(true);
        selectionManager = new SelectionManager();

        if (vizConfig.isUseGLJPanel()) {
            drawable = commander.createPanel();
        } else {
            drawable = commander.createCanvas();
        }
        drawable.initArchitecture();
        engine.initArchitecture();
        ((CompatibilityScheduler) scheduler).initArchitecture();
        ((StandardGraphIO) graphIO).initArchitecture();
        dataBridge.initArchitecture();
        eventBridge.initArchitecture();
        modeManager.initArchitecture();
        textManager.initArchitecture();
        screenshotMaker.initArchitecture();
        vizEventManager.initArchitecture();
        selectionManager.initArchitecture();

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
                workspace.add(new VizModel());
            }

            public void select(Workspace workspace) {
                engine.reinit();
                dataBridge.resetGraph();
            }

            public void unselect(Workspace workspace) {
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
                engine.reinit();
            }
        });

        if (pc.getCurrentWorkspace() != null) {
            engine.reinit();
        }
    }

    public void refreshWorkspace() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        VizModel model = null;
        if (pc.getCurrentWorkspace() == null) {
            model = new VizModel(true);
        } else {
            model = pc.getCurrentWorkspace().getLookup().lookup(VizModel.class);
            if (model == null) {
                model = new VizModel();
                pc.getCurrentWorkspace().add(model);
            }
        }
        if (model != currentModel) {
            model.setListeners(currentModel.getListeners());
            model.getTextModel().setListeners(currentModel.getTextModel().getListeners());
            currentModel.setListeners(null);
            currentModel.getTextModel().setListeners(null);
            currentModel = model;
            VizController.getInstance().getModelClassLibrary().getNodeClass().setCurrentModeler(currentModel.getNodeModeler());
            currentModel.init();
        }
    }

    public void resetSelection() {
        selectionManager.resetSelection();
    }

    public void selectNode(Node node) {
        selectionManager.selectNode(node);
    }

    public void selectEdge(Edge edge) {
        selectionManager.selectEdge(edge);
    }

    public void selectNodes(Node[] nodes) {
        selectionManager.selectNodes(nodes);
    }

    public void selectEdges(Edge[] edges) {
        selectionManager.selectEdges(edges);
    }

    public VizModel getVizModel() {
        return currentModel;
    }

    public GraphDrawableImpl getDrawable() {
        return drawable;
    }

    public AbstractEngine getEngine() {
        return engine;
    }

    public GraphIO getGraphIO() {
        return graphIO;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public VizConfig getVizConfig() {
        return vizConfig;
    }

    public ModelClassLibrary getModelClassLibrary() {
        return modelClassLibrary;
    }

    public VizEventManager getVizEventManager() {
        return vizEventManager;
    }

    public GraphLimits getLimits() {
        return limits;
    }

    public DataBridge getDataBridge() {
        return dataBridge;
    }

    public EventBridge getEventBridge() {
        return eventBridge;
    }

    public ModeManager getModeManager() {
        return modeManager;
    }

    public TextManager getTextManager() {
        return textManager;
    }

    public ScreenshotMaker getScreenshotMaker() {
        return screenshotMaker;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public float getMetaEdgeScale() {
        if(currentModel!=null) {
            return currentModel.getMetaEdgeScale();
        }
        return 1f;
    }
}
