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
package org.gephi.visualization.selection;

import org.gephi.graph.api.Node;
import org.gephi.visualization.api.selection.NodeSpatialStructure;

/**
 * Base class for trees based on quadrants: {@link Quadtree} and {@link Octree}.
 * 
 * @author Vojtech Bardiovsky
 */
public abstract class QuadrantTree implements NodeSpatialStructure {
    
    protected boolean changeMarker;
    protected boolean reassignNodes = true;
    
    @Override
    public void clearCache() {
        changeMarker = true;
    }
    
    @Override
    public void clearSelection() {
        for (Node node : getSelectedNodes()) {
            node.getNodeData().setSelected(false);
        }
        changeMarker = true;
    }
    
    @Override
    public void setLayoutRunning(boolean running) {
        reassignNodes = !running;
    }
    
}
