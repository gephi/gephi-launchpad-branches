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

import javax.swing.event.ChangeListener;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityUI;

/**
 * Hosts user interface states and similarity instances, to have access to
 * results strings.
 * 
 * @author Cezary Bartosiak
 *
 * @see SimilarityController
 */
public interface SimilarityModel {
	/**
	 * Returns the report for the given similarity class or {@code null} if no report
	 * exists for this similarity.
	 *
	 * @param similarity a similarity class
	 *
	 * @return the report or {@code null} if not found.
	 */
	public String getReport(Class<? extends Similarity> similarity);

	/**
	 * Returns the result string for the given {@code SimilarityUI} class or
	 * {@code null} if no result string exists for this similarity.
	 *
	 * @param similarityUI a similarityUI class
	 * 
	 * @return the result or {@code null} if not found.
	 */
	public String getResult(SimilarityUI similarityUI);

	/**
	 * Returns {@code true} if the similarity front-end is visible, {@code false} otherwise.
	 *
	 * @param similarityUI an UI instance
	 * 
	 * @return {@code true} if the similarity front-end is visible, {@code false} otherwise.
	 */
	public boolean isSimilarityUIVisible(SimilarityUI similarityUI);

	/**
	 * Returns {@code true} if the UI is in running state, {@code false} otherwise.
	 *
	 * @param similarityUI an UI instance
	 *
	 * @return {@code true} if the similarity is running, {@code false} otherwise.
	 */
	public boolean isRunning(SimilarityUI similarityUI);

	/**
	 * Returns the {@code Similarity} instance currently running for the particular
	 * {@code SimilarityUI} registered or {@code null} if the similarity is not running.
	 *
	 * @param similarityUI an UI instance
	 * 
	 * @return the similarity instance if it is running, or {@code null} if not running.
	 */
	public Similarity getRunning(SimilarityUI similarityUI);

	public void addChangeListener(ChangeListener changeListener);

	public void removeChangeListener(ChangeListener changeListener);
}
