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
package org.gephi.similarity.spi;

import javax.swing.JPanel;
import org.openide.util.NbBundle;

/**
 * Similarity UI integration information. Implement this interface
 * for defining a new similarity method in the user interface.
 * <p>
 * One could define multiple {@code SimilarityUI} that relies on a single
 * algorithm. SimilarityUIs therefore exist in the system alone, and wait for
 * {@code setup()} method to be called to turn on with a compatible
 * {@code Similarity} instance.
 * <p>
 * Implementors must add <b>@ServiceProvider</b> annotation to be found by the system.
 * 
 * @author Cezary Bartosiak
 *
 * @see SimilarityBuilder
 */
public interface SimilarityUI {
	public static final String CATEGORY_ISOMORPHISM       = NbBundle.getMessage(SimilarityUI.class, "SimilarityUI.category.isomorphism");
	public static final String CATEGORY_EDIT_DISTANCE     = NbBundle.getMessage(SimilarityUI.class, "SimilarityUI.category.editDistance");
	public static final String CATEGORY_ITERATIVE_METHODS = NbBundle.getMessage(SimilarityUI.class, "SimilarityUI.category.iterativeMethods");

	/**
	 * Returns a settings panel instance.
	 *
	 * @return a settings panel instance.
	 */
	public JPanel getSettingsPanel();

	/**
	 * Push a similarity instance to the UI to load its settings. Note that this
	 * method is always called after {@code getSettingsPanel} and before the
	 * panel is displayed.
	 *
	 * @param similarity the similarity instance that is linked to the UI.
	 */
	public void setup(Similarity similarity);

	/**
	 * Notify the settings panel has been closed and that the settings values
	 * can be saved to the similarity instance.
	 */
	public void unsetup();

	/**
	 * Returns the similarity class this UI belongs to.
	 * 
	 * @return the similarity class this UI belongs to.
	 */
	public Class<? extends Similarity> getSimilarityClass();

	/**
	 * Returns this similarity method result as a String, if exists.
	 *
	 * @return this similarity method result string.
	 */
	public String getValue();

	/**
	 * Returns this similarity method display name.
	 *
	 * @return this similarity method display name.
	 */
	public String getDisplayName();

	/**
	 * Returns the category of this similarity method. Default category can be used, see:
	 * <ul>
     * <li>{@link SimilarityUI#CATEGORY_ISOMORPHISM}</li>
     * <li>{@link SimilarityUI#CATEGORY_EDIT_DISTANCE}</li>
	 * <li>{@link SimilarityUI#CATEGORY_ITERATIVE_METHODS}</li>
	 * </ul>
     * Returns a custom String for defining a new category.
	 *
	 * @return this similarity category.
	 */
	public String getCategory();

	/**
	 * Returns a position value, around 1 and 1000, that indicates the position
	 * of the Similarity in the UI. Less means upper.
	 *
	 * @return this similarity position value.
	 */
	public int getPosition();
}
