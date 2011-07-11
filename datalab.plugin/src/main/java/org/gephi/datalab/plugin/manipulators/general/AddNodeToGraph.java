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
package org.gephi.datalab.plugin.manipulators.general;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import org.gephi.datalab.api.datatables.DataTablesController;
import org.gephi.datalab.api.GraphElementsController;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.general.GeneralActionsManipulator;
import org.gephi.graph.api.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * GeneralActionsManipulator that adds a new node to the graph, asking for its label.
 * Uses the default id for the node.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service=GeneralActionsManipulator.class)
public class AddNodeToGraph implements GeneralActionsManipulator{

    public void execute() {
        String label = JOptionPane.showInputDialog(null, NbBundle.getMessage(AddNodeToGraph.class, "AddNodeToGraph.dialog.text"), NbBundle.getMessage(AddNodeToGraph.class, "AddNodeToGraph.name"), JOptionPane.QUESTION_MESSAGE);
        if (label != null) {
            Node node=Lookup.getDefault().lookup(GraphElementsController.class).createNode(label);
            Lookup.getDefault().lookup(DataTablesController.class).setNodeTableSelection(new Node[]{node});
        }
    }

    public String getName() {
        return NbBundle.getMessage(AddNodeToGraph.class, "AddNodeToGraph.name");
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
        return 0;
    }

    public int getPosition() {
        return 0;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/datalab/plugin/manipulators/resources/plus-circle.png",true);
    }
}
