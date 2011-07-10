/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.clustering.plugin.mcl;

import javax.swing.JPanel;
import org.gephi.clustering.spi.Clusterer;
import org.gephi.clustering.spi.ClustererBuilder;
import org.gephi.clustering.spi.ClustererUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = ClustererBuilder.class)
public class MarkovClusteringBuilder implements ClustererBuilder {

    public Clusterer getClusterer() {
        return new MarkovClustering();
    }

    public String getName() {
        return "MCL (experimental)";
    }

    public String getDescription() {
        return NbBundle.getMessage(MarkovClusteringBuilder.class, "MarkovClustering.description");
    }

    public Class getClustererClass() {
        return MarkovClustering.class;
    }

    public ClustererUI getUI() {
        return new MarkovClusteringUI();
    }

    private static class MarkovClusteringUI implements ClustererUI {

        MarkovClusteringPanel panel;

        public JPanel getPanel() {
            panel = new MarkovClusteringPanel();
            return panel;
        }

        public void setup(Clusterer clusterer) {
            panel.setup(clusterer);
        }

        public void unsetup() {
            panel.unsetup();
            panel = null;
        }
    }
}
