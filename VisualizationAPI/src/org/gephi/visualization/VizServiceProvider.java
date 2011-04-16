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

package org.gephi.visualization;

import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.apiimpl.VizConfig;
import org.gephi.visualization.apiimpl.VizEventManager;

/**
 * Static class which gives access to several services provided by the 
 * visualization module (selection and event managment and visualization 
 * properties).
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class VizServiceProvider {

    private static SelectionManager selectionManager_;
    private static VizEventManager vizEventManager_;
    private static VizModel vizModel_;
    private static VizConfig vizConfig_;

    static {
        selectionManager_ = new NullSelectionManager();
        vizEventManager_ = new NullVizEventManager();
        vizModel_ = new NullVizModel();
        vizConfig_ = new VizConfig();
    }

    synchronized public static SelectionManager getSelectionManager() {
        return selectionManager_;
    }

    synchronized public static void registerSelectionManager(SelectionManager selectionManager) {
        selectionManager_ = selectionManager;
    }

    synchronized public static VizEventManager getVizEventManager() {
        return vizEventManager_;
    }

    synchronized public static void registerVizEventManager(VizEventManager vizEventManager) {
        vizEventManager_ = vizEventManager;
    }

    synchronized public static VizModel getVizModel() {
        return vizModel_;
    }

    synchronized public static void registerVizModel(VizModel vizModel) {
        vizModel_ = vizModel;
    }

    synchronized public static VizConfig getVizConfig() {
        return vizConfig_;
    }

    synchronized public static void registeVizConfig(VizConfig vizConfig) {
        vizConfig_ = vizConfig;
    }
}
