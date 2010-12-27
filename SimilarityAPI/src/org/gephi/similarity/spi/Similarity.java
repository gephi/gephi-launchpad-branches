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

import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphModel;

/**
 * Defines a Similarity execution task, that performs analysis and write results
 * as new attribute columns and/or HTML report.
 * 
 * @author Cezary Bartosiak
 *
 * @see SimilarityBuilder
 */
public interface Similarity {
	/**
	 * Executes the similarity algorithm on the source graph and target graphs,
	 * that will be compared with the source.
	 *
	 * @param sourceGraphModel      the source graph topology
	 * @param targetGraphModels     the list of target graphs topologies (including source)
	 * @param sourceAttributeModel  the source elements attributes and where to write table results
	 * @param targetAttributeModels the list of targets elements attributes and where to write table results (including source)
	 * @param graphNames            the list of all considered graphs names (including source)
	 */
	public void execute(GraphModel sourceGraphModel, GraphModel[] targetGraphModels,
			AttributeModel sourceAttributeModel, AttributeModel[] targetAttributeModels,
			String[] graphNames);

	/**
	 * Returns an HTML string that displays the similarity result. Can contains
	 * complex HTML snippets and images.
	 *
	 * @return an HTML string that displays the results for this Similarity.
	 */
	public String getReport();
}
