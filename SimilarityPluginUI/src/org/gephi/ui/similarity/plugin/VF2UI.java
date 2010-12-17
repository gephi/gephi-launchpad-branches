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
package org.gephi.ui.similarity.plugin;

import javax.swing.JPanel;
import org.gephi.similarity.plugin.VF2;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = SimilarityUI.class)
public class VF2UI implements SimilarityUI {
	private VF2Panel panel;
	private VF2 vf2;

	@Override
	public JPanel getSettingsPanel() {
		panel = new VF2Panel();
		return panel;
	}

	@Override
	public void setup(Similarity similarity) {
		vf2 = (VF2)similarity;
		if (panel != null)
			panel.setDirected(vf2.isDirected());
	}

	@Override
	public void unsetup() {
		if (panel != null)
			vf2.setDirected(panel.isDirected());
		panel = null;
		vf2 = null;
	}

	@Override
	public Class<? extends Similarity> getSimilarityClass() {
		return VF2.class;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return "VF2";
	}

	@Override
	public String getCategory() {
		return SimilarityUI.CATEGORY_ISOMORPHISM;
	}

	@Override
	public int getPosition() {
		return 101;
	}
}
