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
package org.gephi.spreadsimulator;

import javax.swing.JPanel;
import org.gephi.spreadsimulator.api.RemovalStrategy;
import org.gephi.spreadsimulator.api.RemovalStrategyUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = RemovalStrategyUI.class)
public class RemovalStrategyUIImpl implements RemovalStrategyUI {
	private RemovalStrategyPanel panel;
	private RemovalStrategyImpl rs;

	@Override
	public JPanel getSettingsPanel() {
		panel = new RemovalStrategyPanel();
		return panel;
	}

	@Override
	public void setup(RemovalStrategy removalStrategy) {
		rs = (RemovalStrategyImpl)removalStrategy;
		if (panel != null) {
			panel.setAttributeColumn(rs.getAttributeColumn());
			panel.setK(rs.getK());
			panel.setExactlyK(rs.isExactlyK());
			panel.setMstype(rs.getMstype());
		}
	}

	@Override
	public void unsetup() {
		if (panel != null) {
			rs.setAttributeColumn(panel.getAttributeColumn());
			rs.setK(panel.getK());
			rs.setExactlyK(panel.isExactlyK());
			rs.setMstype(panel.getMstype());
		}
		panel = null;
		rs = null;
	}
}
