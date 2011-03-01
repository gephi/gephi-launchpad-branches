/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.plugin.manipulators.nodes;

import javax.swing.Icon;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Nodes manipulator that centers the graph view to show a node.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class SelectOnGraph extends BasicNodesManipulator {
    private Node node;

    public void setup(Node[] nodes, Node clickedNode) {
        this.node=clickedNode;
    }

    public void execute() {
        VizController.getInstance().getSelectionManager().centerOnNode(node);
    }

    public String getName() {
        return NbBundle.getMessage(SelectOnGraph.class, "SelectOnGraph.name");
    }

    public String getDescription() {
        return "";
    }

    public boolean canExecute() {
        return true;
    }

    public ManipulatorUI getUI() {
        return null;
    }

    public int getType() {
        return 100;
    }

    public int getPosition() {
        return 0;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/datalab/plugin/manipulators/resources/magnifier--arrow.png", true);
    }
}
