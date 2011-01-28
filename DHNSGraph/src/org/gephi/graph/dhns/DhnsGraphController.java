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
package org.gephi.graph.dhns;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.dhns.core.Dhns;
import org.gephi.graph.dhns.core.IDGen;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.spi.WorkspaceDuplicateProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * Singleton which manages the graph access.
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = GraphController.class)
public class DhnsGraphController implements GraphController {

    protected IDGen iDGen;

    public DhnsGraphController() {
        iDGen = new IDGen();
    }

    public Dhns newDhns(Workspace workspace) {
        Dhns dhns = new Dhns(this, workspace);
        workspace.add(dhns);
        return dhns;
    }

    public IDGen getIDGen() {
        return iDGen;
    }

    private synchronized Dhns getCurrentDhns() {
        Workspace currentWorkspace = Lookup.getDefault().lookup(ProjectController.class).getCurrentWorkspace();
        if (currentWorkspace == null) {
            return null;
        }
        Dhns dhns = currentWorkspace.getLookup().lookup(Dhns.class);
        if (dhns == null) {
            dhns = newDhns(currentWorkspace);
        }
        return dhns;
    }

    public GraphModel getModel(Workspace workspace) {
        Dhns dhns = workspace.getLookup().lookup(Dhns.class);
        if (dhns == null) {
            dhns = newDhns(workspace);
        }
        return dhns;
    }

    public GraphModel getModel() {
        return getCurrentDhns();
    }
}
