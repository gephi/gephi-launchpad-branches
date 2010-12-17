/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ui.statistics.plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.gephi.statistics.plugin.AverageGlobalConnectionEfficiency;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = StatisticsUI.class)
public class AverageGlobalConnectionEfficiencyUI implements StatisticsUI {
	private AverageGlobalConnectionEfficiencyPanel panel;
	private AverageGlobalConnectionEfficiency agce;

	public JPanel getSettingsPanel() {
		panel = new AverageGlobalConnectionEfficiencyPanel();
		return panel;
	}

	public void setup(Statistics statistics) {
		agce = (AverageGlobalConnectionEfficiency)statistics;
		if (panel != null) {
			panel.setDirected(agce.isDirected());
			panel.setSamplesCount(agce.getSamplesCount());
			panel.setK(agce.getK());
			panel.setExactlyK(agce.isExactlyK());
			panel.setMstype(agce.getMstype());
		}
	}

	public void unsetup() {
		if (panel != null) {
			agce.setDirected(panel.isDirected());
			agce.setSamplesCount(panel.getSamplesCount());
			agce.setK(panel.getK());
			agce.setExactlyK(panel.isExactlyK());
			agce.setMstype(panel.getMstype());
		}
		agce  = null;
		panel = null;
	}

	public Class<? extends Statistics> getStatisticsClass() {
		return AverageGlobalConnectionEfficiency.class;
	}

	public String getValue() {
		NumberFormat f = new DecimalFormat("#0.0000");
		return "" + f.format(agce.getAvgGCE());
	}

	public String getDisplayName() {
		return "Average Global Connection Efficiency";
	}

	public String getCategory() {
		return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
	}

	public int getPosition() {
		return 3;
	}
}
