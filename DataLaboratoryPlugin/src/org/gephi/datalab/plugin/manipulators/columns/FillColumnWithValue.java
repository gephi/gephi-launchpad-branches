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
package org.gephi.datalab.plugin.manipulators.columns;

import java.awt.Image;
import javax.swing.JOptionPane;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.api.datatables.DataTablesController;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that fills an AttributeColumn with the value that the user provides in the UI.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class FillColumnWithValue implements AttributeColumnsManipulator {

    public void execute(AttributeTable table, AttributeColumn column) {
        String value = JOptionPane.showInputDialog(null,NbBundle.getMessage(FillColumnWithValue.class, "FillColumnWithValue.inputDialog.text"),getName(),JOptionPane.QUESTION_MESSAGE);
        if (value != null) {
            Lookup.getDefault().lookup(AttributeColumnsController.class).fillColumnWithValue(table, column, value);
            Lookup.getDefault().lookup(DataTablesController.class).refreshCurrentTable();
        }
    }

    public String getName() {
        return NbBundle.getMessage(FillColumnWithValue.class, "FillColumnWithValue.name");
    }

    public String getDescription() {
        return "";
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        return Lookup.getDefault().lookup(AttributeColumnsController.class).canChangeColumnData(column);
    }

    public AttributeColumnsManipulatorUI getUI(AttributeTable table,AttributeColumn column) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public int getPosition() {
        return 300;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalab/plugin/manipulators/resources/table-duplicate-column.png");
    }
}
