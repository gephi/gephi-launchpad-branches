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

import java.util.ArrayList;
import javax.swing.Icon;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.api.datatables.DataTablesController;
import org.gephi.datalab.plugin.manipulators.GeneralColumnsAndRowChooser;
import org.gephi.datalab.plugin.manipulators.ui.GeneralChooseColumnsAndRowUI;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.graph.api.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Nodes manipulator that copies the given columns data of one node to the other selected nodes.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class CopyNodeDataToOtherNodes extends BasicNodesManipulator implements GeneralColumnsAndRowChooser {

    private Node clickedNode;
    private Node[] nodes;
    private AttributeColumn[] columnsToCopyData;

    public void setup(Node[] nodes, Node clickedNode) {
        this.clickedNode = clickedNode;
        this.nodes = nodes;
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        ArrayList<AttributeColumn> columnsToCopyDataList = new ArrayList<AttributeColumn>();
        for (AttributeColumn column : Lookup.getDefault().lookup(AttributeController.class).getModel().getNodeTable().getColumns()) {
            if (ac.canChangeColumnData(column)) {
                columnsToCopyDataList.add(column);
            }
        }
        columnsToCopyData = columnsToCopyDataList.toArray(new AttributeColumn[0]);
    }

    public void execute() {
        if (columnsToCopyData.length >= 0) {
            AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
            ac.copyNodeDataToOtherNodes(clickedNode, nodes, columnsToCopyData);
            Lookup.getDefault().lookup(DataTablesController.class).refreshCurrentTable();
        }
    }

    public String getName() {
        return NbBundle.getMessage(CopyNodeDataToOtherNodes.class, "CopyNodeDataToOtherNodes.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CopyNodeDataToOtherNodes.class, "CopyNodeDataToOtherNodes.description");
    }

    public boolean canExecute() {
        return nodes.length > 1;//At least 2 nodes to copy data from one to the other.
    }

    public ManipulatorUI getUI() {
        return new GeneralChooseColumnsAndRowUI(NbBundle.getMessage(CopyNodeDataToOtherNodes.class, "CopyNodeDataToOtherNodes.ui.rowDescription"),NbBundle.getMessage(CopyNodeDataToOtherNodes.class, "CopyNodeDataToOtherNodes.ui.columnsDescription"));
    }

    public int getType() {
        return 200;
    }

    public int getPosition() {
        return 200;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/datalab/plugin/manipulators/resources/broom--arrow.png", true);
    }

    public AttributeColumn[] getColumns() {
        return columnsToCopyData;
    }

    public void setColumns(AttributeColumn[] columnsToClearData) {
        this.columnsToCopyData = columnsToClearData;
    }

    public Object[] getRows() {
        return nodes;
    }

    public Object getRow() {
        return clickedNode;
    }

    public void setRow(Object row) {
        clickedNode=(Node) row;
    }
}