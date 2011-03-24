/*
Copyright 2008-2010 Gephi
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

package org.gephi.visualization.model;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.visualization.controller.Controller;
import org.gephi.visualization.data.FrameData;
import org.gephi.visualization.data.NodeBatch;
import org.openide.util.Lookup;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class DataManager implements Runnable {

    private Thread thread;
    private boolean isRunning;
    private int frameDuration;
    private Controller controller;

    public DataManager(int frameDuration) {
        this.thread = null;
        this.frameDuration = frameDuration;
        this.isRunning = false;
    }

    public void start() {
        if (!isRunning) {
            this.thread = new Thread(this);
            this.isRunning = true;
            this.thread.start();
        }
    }

    public void stop() {
        if (isRunning) {
            this.thread.interrupt();
            this.isRunning = false;
            this.thread = null;
        }
    }

    @Override
    public void run() {

        while (this.isRunning) {
            long beginFrameTime = System.currentTimeMillis();

            if (this.controller != null) {
                this.controller.beginUpdateFrame();
            }

            final FrameData frameData = new FrameData();

            final GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
            final GraphModel model = graphController.getModel();
            
            if (model == null) {
                try {
                    Thread.sleep(this.frameDuration);
		} catch (InterruptedException e) {
                    return;
		}
                continue;
            }

            final Graph graph = model.getGraph();

            final NodeBatch batch = new NodeBatch();

            for(Node n : graph.getNodes()) {
                NodeData data = n.getNodeData();
                batch.addNode(data);
            }
            
            frameData.addNodeBatch(batch);

            if (this.controller != null) {
                this.controller.endUpdateFrame(frameData);
            }

            long endFrameTime = System.currentTimeMillis();

            long time = beginFrameTime - endFrameTime;
            if (time < this.frameDuration) {
                try {
                    Thread.sleep(this.frameDuration - time);
		} catch (InterruptedException e) {
                    return;
		}
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
