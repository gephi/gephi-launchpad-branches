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
package org.gephi.similarity;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.gephi.project.api.Workspace;
import org.gephi.project.spi.WorkspacePersistenceProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = WorkspacePersistenceProvider.class)
public class SimilarityPersistenceProvider implements WorkspacePersistenceProvider {
	@Override
	public void writeXML(XMLStreamWriter writer, Workspace workspace) {
		SimilarityModelImpl similarityModel = workspace.getLookup().lookup(SimilarityModelImpl.class);
		if (similarityModel != null)
			try {
				similarityModel.writeXML(writer);
			}
			catch (XMLStreamException ex) {
				throw new RuntimeException(ex);
			}
	}

	@Override
	public void readXML(XMLStreamReader reader, Workspace workspace) {
		SimilarityModelImpl similarityModel = new SimilarityModelImpl();
		try {
			similarityModel.readXML(reader);
		}
		catch (XMLStreamException ex) {
			throw new RuntimeException(ex);
		}
		workspace.add(similarityModel);
	}

	@Override
	public String getIdentifier() {
		return "similaritymodel";
	}
}
