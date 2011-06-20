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
import org.gephi.spreadsimulator.api.StateChangeStrategy;
import org.gephi.spreadsimulator.api.StateChangeStrategyUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = StateChangeStrategyUI.class)
public class StateChangeStrategyUIImpl implements StateChangeStrategyUI {
	private StateChangeStrategyPanel panel;
	private StateChangeStrategyImpl scs;

	@Override
	public JPanel getSettingsPanel() {
		panel = new StateChangeStrategyPanel();
		return panel;
	}

	@Override
	public void setup(StateChangeStrategy stateChangeStrategy) {
		scs = (StateChangeStrategyImpl)stateChangeStrategy;
		if (panel != null) {
			panel.setAttributeColumn(scs.getAttributeColumn());
			panel.setK(scs.getK());
			panel.setExactlyK(scs.isExactlyK());
			panel.setMstype(scs.getMstype());
			panel.setStateName(scs.getStateName());
		}
	}

	@Override
	public void unsetup() {
		if (panel != null) {
			scs.setAttributeColumn(panel.getAttributeColumn());
			scs.setK(panel.getK());
			scs.setExactlyK(panel.isExactlyK());
			scs.setMstype(panel.getMstype());
			scs.setStateName(panel.getStateName());
		}
		panel = null;
		scs = null;
	}
}
