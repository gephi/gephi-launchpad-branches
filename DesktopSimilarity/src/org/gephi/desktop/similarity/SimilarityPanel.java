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
package org.gephi.desktop.similarity;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.gephi.similarity.api.SimilarityModel;
import org.gephi.similarity.spi.SimilarityUI;
import org.gephi.ui.components.JSqueezeBoxPanel;
import org.openide.util.Lookup;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class SimilarityPanel extends JPanel {
	private SimilarityCategory[] categories;
	private List<UIFrontEnd>     frontEnds;

	private JSqueezeBoxPanel squeezeBoxPanel;

	public SimilarityPanel() {
		initComponents();
		initCategories();
		initFrontEnds();
	}

	public void refreshModel(SimilarityModel model) {
		boolean needRefreshVisible = false;
		for (UIFrontEnd entry : frontEnds) {
			entry.getFrontEnd().refreshModel(model);
			if (model != null) {
				boolean visible = model.isSimilarityUIVisible(entry.getSimilarityUI());
				if (visible != entry.visible) {
					needRefreshVisible = true;
					entry.setVisible(visible);
				}
			}
		}
		if (needRefreshVisible)
			refreshFrontEnd();
	}

	private void refreshFrontEnd() {
		squeezeBoxPanel.cleanPanels();
		for (SimilarityCategory category : categories) {
			// Find uis in this category
			List<UIFrontEnd> uis = new ArrayList<UIFrontEnd>();
			for (UIFrontEnd uife : frontEnds)
				if (uife.getCategory().equals(category) && uife.isVisible())
					uis.add(uife);

			if (uis.size() > 0) {
				// Sort it by position
				Collections.sort(uis, new Comparator() {
					@Override
					public int compare(Object o1, Object o2) {
						Integer p1 = ((UIFrontEnd)o1).getSimilarityUI().getPosition();
						Integer p2 = ((UIFrontEnd)o2).getSimilarityUI().getPosition();
						return p1.compareTo(p2);
					}
				});

				MigLayout migLayout = new MigLayout("insets 0");
				migLayout.setColumnConstraints("[grow,fill]");
				migLayout.setRowConstraints("[pref!]");
				JPanel innerPanel = new JPanel(migLayout);

				for (UIFrontEnd sui : uis)
					innerPanel.add(sui.frontEnd, "wrap");

				squeezeBoxPanel.addPanel(innerPanel, category.getName());
			}
		}
	}

	private void initFrontEnds() {
		SimilarityUI[] statisticsUIs = Lookup.getDefault().lookupAll(SimilarityUI.class).toArray(new SimilarityUI[0]);
		frontEnds = new ArrayList<UIFrontEnd>();

		for (SimilarityCategory category : categories) {
			// Find uis in this category
			List<SimilarityUI> uis = new ArrayList<SimilarityUI>();
			for (SimilarityUI sui : statisticsUIs)
				if (sui.getCategory().equals(category.getName()))
					uis.add(sui);

			if (uis.size() > 0) {
				// Sort it by position
				Collections.sort(uis, new Comparator() {
					@Override
					public int compare(Object o1, Object o2) {
						Integer p1 = ((SimilarityUI)o1).getPosition();
						Integer p2 = ((SimilarityUI)o2).getPosition();
						return p1.compareTo(p2);
					}
				});

				MigLayout migLayout = new MigLayout("insets 0");
				migLayout.setColumnConstraints("[grow,fill]");
				migLayout.setRowConstraints("[pref!]");
				JPanel innerPanel = new JPanel(migLayout);

				for (SimilarityUI sui : uis) {
					SimilarityFrontEnd frontEnd = new SimilarityFrontEnd(sui);
					UIFrontEnd uife = new UIFrontEnd(sui, frontEnd, category);
					frontEnds.add(uife);
					innerPanel.add(frontEnd, "wrap");
				}

				squeezeBoxPanel.addPanel(innerPanel, category.getName());
			}
		}
	}

	private void initCategories() {
		Map<String, SimilarityCategory> cats = new LinkedHashMap<String, SimilarityCategory>();
		cats.put(SimilarityUI.CATEGORY_ISOMORPHISM, new SimilarityCategory(SimilarityUI.CATEGORY_ISOMORPHISM, 100));
		cats.put(SimilarityUI.CATEGORY_EDIT_DISTANCE, new SimilarityCategory(SimilarityUI.CATEGORY_EDIT_DISTANCE, 200));
		cats.put(SimilarityUI.CATEGORY_ITERATIVE_METHODS, new SimilarityCategory(SimilarityUI.CATEGORY_ITERATIVE_METHODS, 300));

		int position = 400;
		for (SimilarityUI uis : Lookup.getDefault().lookupAll(SimilarityUI.class)) {
			String category = uis.getCategory();
			if (!cats.containsKey(category)) {
				cats.put(category, new SimilarityCategory(category, position));
				position += 100;
			}
		}

		categories = cats.values().toArray(new SimilarityCategory[0]);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		squeezeBoxPanel = new JSqueezeBoxPanel();
		add(squeezeBoxPanel, BorderLayout.CENTER);
	}

	public SimilarityCategory[] getCategories() {
		return categories;
	}

	private static class UIFrontEnd {
		private SimilarityUI       similarityUI;
		private SimilarityFrontEnd frontEnd;
		private SimilarityCategory category;
		
		private boolean visible;

		public UIFrontEnd(SimilarityUI similarityUI, SimilarityFrontEnd frontEnd, SimilarityCategory category) {
			this.similarityUI = similarityUI;
			this.frontEnd     = frontEnd;
			this.category     = category;
			this.visible      = true;
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public SimilarityFrontEnd getFrontEnd() {
			return frontEnd;
		}

		public SimilarityUI getSimilarityUI() {
			return similarityUI;
		}

		public SimilarityCategory getCategory() {
			return category;
		}
	}
}
