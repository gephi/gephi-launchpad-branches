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
package org.gephi.datalab.plugin.manipulators.columns.merge;

import javax.swing.Icon;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.api.AttributeColumnsMergeStrategiesController;
import org.gephi.datalab.plugin.manipulators.columns.merge.ui.JoinWithSeparatorUI;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.columns.merge.AttributeColumnsMergeStrategy;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * AttributeColumnsMergeStrategy that joins columns of any type into a new column
 * using the separator string that the user provides.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public class JoinWithSeparator implements AttributeColumnsMergeStrategy {

    public static final String SEPARATOR_SAVED_PREFERENCES = "JoinWithSeparator_Separator";
    private static final String DEFAULT_SEPARATOR = ",";
    private AttributeTable table;
    private AttributeColumn[] columns;
    private String newColumnTitle, separator;

    public void setup(AttributeTable table, AttributeColumn[] columns) {
        this.table = table;
        this.columns = columns;
        separator=NbPreferences.forModule(JoinWithSeparator.class).get(SEPARATOR_SAVED_PREFERENCES, DEFAULT_SEPARATOR);
    }

    public void execute() {
        Lookup.getDefault().lookup(AttributeColumnsMergeStrategiesController.class).joinWithSeparatorMerge(table, columns, null, newColumnTitle, separator);
    }

    public String getName() {
        return NbBundle.getMessage(JoinWithSeparator.class, "JoinWithSeparator.name");
    }

    public String getDescription() {
        return NbBundle.getMessage(JoinWithSeparator.class, "JoinWithSeparator.description");
    }

    public boolean canExecute() {
        return true;
    }

    public ManipulatorUI getUI() {
        return new JoinWithSeparatorUI();
    }

    public int getType() {
        return 0;
    }

    public int getPosition() {
        return 0;
    }

    public Icon getIcon() {
        return ImageUtilities.loadImageIcon("org/gephi/datalab/plugin/manipulators/resources/join.png", true);
    }

    public String getNewColumnTitle() {
        return newColumnTitle;
    }

    public void setNewColumnTitle(String newColumnTitle) {
        this.newColumnTitle = newColumnTitle;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public AttributeTable getTable() {
        return table;
    }
}
