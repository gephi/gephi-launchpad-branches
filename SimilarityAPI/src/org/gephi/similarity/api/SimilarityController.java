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
package org.gephi.similarity.api;

import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityBuilder;
import org.gephi.similarity.spi.SimilarityUI;
import org.gephi.utils.longtask.api.LongTaskListener;

/**
 * Controller for executing similarity algorithms.
 * <p>
 * This controller is a service and can therefore be found in Lookup:
 * <pre>SimilarityController sc = Lookup.getDefault().lookup(SimilarityController.class);
 * 
 * @author Cezary Bartosiak
 *
 * @see SimilarityBuilder
 */
public interface SimilarityController {
	/**
	 * Executes the similarity algorithm. If {@code similarity} implements
	 * {@code LongTask}, execution is performed in a background thread and
	 * therefore this method returns immediately.
	 *
	 * @param similarity the similarity algorithm instance
	 * @param listener   a listener that is notified when execution finished
	 */
	public void execute(Similarity similarity, LongTaskListener listener);

	/**
	 * Finds the builder from the similarity class.
	 *
	 * @param similarity the similarity class
	 *
	 * @return the builder, or {@code null} if not found.
	 */
	public SimilarityBuilder getBuilder(Class<? extends Similarity> similarity);

	/**
	 * Sets the visible state for a given {@code SimilarityUI}.
	 *
	 * @param ui      the UI instance
	 * @param visible {@code true} to display the front-end
	 */
	public void setSimilarityUIVisible(SimilarityUI ui, boolean visible);

	/**
	 * Returns the current {@code SimilarityModel}, from the current workspace.
	 *
	 * @return the current {@code SimilarityModel}.
	 */
	public SimilarityModel getModel();
}
