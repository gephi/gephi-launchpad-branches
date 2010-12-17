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
import javax.swing.JPanel;
import org.gephi.statistics.plugin.SquareDegree;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = StatisticsUI.class)
public class SquareDegreeUI implements StatisticsUI {
	private SquareDegree squareDegree;

	public JPanel getSettingsPanel() {
		return null;
	}

	public void setup(Statistics statistics) {
		this.squareDegree = (SquareDegree)statistics;
	}

	public void unsetup() {
		squareDegree = null;
	}

	public Class<? extends Statistics> getStatisticsClass() {
		return SquareDegree.class;
	}

	public String getValue() {
		DecimalFormat df = new DecimalFormat("###.###");
        return "" + df.format(squareDegree.getAverageDegree());
	}

	public String getDisplayName() {
		return NbBundle.getMessage(getClass(), "SquareDegreeUI.name");
	}

	public String getCategory() {
		return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
	}

	public int getPosition() {
		return 1;
	}
}
