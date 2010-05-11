/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.desktop.importer.impl;

import javax.swing.JPanel;
import org.gephi.io.database.drivers.SQLDriver;
import org.gephi.io.importer.api.Database;
import org.gephi.io.importer.api.EdgeListDatabase;
import org.gephi.io.importer.plugin.database.EdgeListDatabaseUI;
import org.gephi.io.importer.spi.DatabaseType;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = EdgeListDatabaseUI.class)
public class EdgeListDatabaseUIImpl implements EdgeListDatabaseUI {

    private EdgeListPanel panel;
    private Database database;

    public EdgeListDatabaseUIImpl() {
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new EdgeListPanel();
        }
        return EdgeListPanel.createValidationPanel(panel);
    }

    public void setup(DatabaseType type) {
        if (panel == null) {
            panel = new EdgeListPanel();
        }
        panel.setDatabaseType(type);

        //Driver Combo
        SQLDriver[] driverArray = new SQLDriver[0];
        driverArray = Lookup.getDefault().lookupAll(SQLDriver.class).toArray(driverArray);
        panel.setSQLDrivers(driverArray);
    }

    public void unsetup() {
        this.database = (EdgeListDatabase) panel.getSelectedDatabase();
        panel = null;
    }

    public Database getDatabase() {
        return database;
    }
}
