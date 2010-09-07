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
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.datalab.plugin.manipulators.columns.ui.GeneralCreateColumnFromRegexUI;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulator;
import org.gephi.datalab.spi.columns.AttributeColumnsManipulatorUI;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * AttributeColumnsManipulator that creates a new boolean column from the given column and regular expression with boolean values that indicate if
 * each of the old column values match the regular expression.
 * Allows the user to select the title of the new column in the UI
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class CreateBooleanMatchesColumn extends GeneralCreateColumnFromRegex{
    public void execute(AttributeTable table, AttributeColumn column) {
        if (pattern != null) {
            Lookup.getDefault().lookup(AttributeColumnsController.class).createBooleanMatchesColumn(table, column, title, pattern);
        }
    }

    public String getName() {
        return NbBundle.getMessage(CreateBooleanMatchesColumn.class, "CreateBooleanMatchesColumn.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(CreateBooleanMatchesColumn.class, "CreateBooleanMatchesColumn.description");
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        return ac.getTableRowsCount(table)>0;//Make sure that there is at least 1 row
    }

    public AttributeColumnsManipulatorUI getUI(AttributeTable table,AttributeColumn column) {
        GeneralCreateColumnFromRegexUI ui=new GeneralCreateColumnFromRegexUI();
        ui.setMode(GeneralCreateColumnFromRegexUI.Mode.BOOLEAN);
        return ui;
    }

    public int getType() {
        return 200;
    }

    public int getPosition() {
        return 0;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalab/plugin/manipulators/resources/binocular--arrow.png");
    }
}
