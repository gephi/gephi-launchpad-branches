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
package org.gephi.similarity.plugin.builder;

import org.gephi.similarity.plugin.QuantitativeNodesSimilarity;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service=SimilarityBuilder.class)
public class QuantitativeNodesSimilarityBuilder implements SimilarityBuilder {
	@Override
	public String getName() {
		return NbBundle.getMessage(QuantitativeNodesSimilarityBuilder.class, "QuantitativeNodesSimilarity.name");
	}

	@Override
	public Similarity getSimilarity() {
		return new QuantitativeNodesSimilarity();
	}

	@Override
	public Class<? extends Similarity> getSimilarityClass() {
		return QuantitativeNodesSimilarity.class;
	}
}
