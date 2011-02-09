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
package org.gephi.ui.statistics.plugin;

import javax.swing.JPanel;
import org.gephi.statistics.plugin.PageRank;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class PageRankUI implements StatisticsUI {

    private final StatSettings settings = new StatSettings();
    private PageRankPanel panel;
    private PageRank pageRank;

    public JPanel getSettingsPanel() {
        panel = new PageRankPanel();
        return panel;
    }

    public void setup(Statistics statistics) {
        this.pageRank = (PageRank) statistics;
        if (panel != null) {
            settings.load(pageRank);
            panel.setEpsilon(pageRank.getEpsilon());
            panel.setProbability(pageRank.getProbability());
            panel.setDirected(pageRank.getDirected());
            panel.setEdgeWeight(pageRank.isUseEdgeWeight());
        }
    }

    public void unsetup() {
        if (panel != null) {
            pageRank.setEpsilon(panel.getEpsilon());
            pageRank.setProbability(panel.getProbability());
            pageRank.setDirected(panel.isDirected());
            pageRank.setUseEdgeWeight(panel.isEdgeWeight());
            settings.save(pageRank);
        }
        panel = null;
        pageRank = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return PageRank.class;
    }

    public String getValue() {
        return null;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "PageRankUI.name");
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    public int getPosition() {
        return 800;
    }

    private static class StatSettings {

        private double epsilon = 0.001;
        private double probability = 0.85;
        private boolean useEdgeWeight = false;

        private void save(PageRank stat) {
            this.epsilon = stat.getEpsilon();
            this.probability = stat.getProbability();
            this.useEdgeWeight = stat.isUseEdgeWeight();
        }

        private void load(PageRank stat) {
            stat.setEpsilon(epsilon);
            stat.setProbability(probability);
            stat.setUseEdgeWeight(useEdgeWeight);
        }
    }
}
