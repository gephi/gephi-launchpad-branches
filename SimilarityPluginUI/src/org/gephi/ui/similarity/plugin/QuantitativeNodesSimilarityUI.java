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
import org.gephi.similarity.plugin.QuantitativeNodesSimilarity;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityUI;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = SimilarityUI.class)
public class QuantitativeNodesSimilarityUI implements SimilarityUI {
	private QuantitativeNodesSimilarityPanel panel;
	private QuantitativeNodesSimilarity qns;

	@Override
	public JPanel getSettingsPanel() {
		panel = new QuantitativeNodesSimilarityPanel();
		return panel;
	}

	@Override
	public void setup(Similarity similarity) {
		qns = (QuantitativeNodesSimilarity)similarity;
		if (panel != null) {
			panel.setColumns(qns.getColumns());
			panel.setP(qns.getP());
			panel.setDoNorm(qns.getDoNorm());
			panel.setLambdas(qns.getLambdas());
		}
	}

	@Override
	public void unsetup() {
		if (panel != null) {
			qns.setColumns(panel.getColumns());
			qns.setP(panel.getP());
			qns.setDoNorm(panel.getDoNorm());
			qns.setLambdas(panel.getLambdas());
		}
		panel = null;
		qns = null;
	}

	@Override
	public Class<? extends Similarity> getSimilarityClass() {
		return QuantitativeNodesSimilarity.class;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return "Quantitative Nodes Similarity";
	}

	@Override
	public String getCategory() {
		return SimilarityUI.CATEGORY_ITERATIVE_METHODS;
	}

	@Override
	public int getPosition() {
		return 301;
	}
}
