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

package org.gephi.visualization.view.profile.options;

import javax.swing.JPanel;
import org.gephi.visualization.view.profile.VizProfile;
import org.gephi.visualization.view.profile.VizProperty;

/**
 * Panel used to change the properties of a visualization profile.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public abstract class VizProfileOptionPanel extends JPanel {

    private final VizProfile profile;

    public VizProfileOptionPanel(VizProfile profile) {
        this.profile = profile;
    }

    protected abstract void updateValues();

    public void load() {
        this.profile.loadProperties();

        this.updateValues();
    }

    protected abstract VizProperty[] getProperties();

    public void store() {
        VizProperty[] properties = this.getProperties();

        for (VizProperty p : properties) {
            this.profile.setProperty(p);
        }

        this.profile.saveProperties();
    }

    public abstract boolean valid();
}
