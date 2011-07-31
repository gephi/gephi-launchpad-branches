/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gephi.io.importer.spi;

import org.gephi.io.importer.api.FileType;
import org.openide.filesystems.FileObject;

/**
 * Importer builder specific for {@link FileImporter}.
 * 
 * @author Mathieu Bastian
 */
public interface FileImporterBuilder extends ImporterBuilder {

    /**
     * Builds a new file importer instance, ready to be used.
     * @return  a new file importer
     */
    public FileImporter buildImporter();

    /**
     * Get default file types this importer can deal with.
     * @return an array of file types this importer can read
     */
    public FileType[] getFileTypes();

    /**
     * Returns <code>true</code> if this importer can import <code>fileObject</code>. Called from
     * controllers to identify dynamically which importers can be used for a particular file format.
     * <p>
     * Use <code>FileObject.getExt()</code> to retrieve file extension. Matching can be done not only with
     * metadata but also with file content. The <code>fileObject</code> can be read in that way.
     * @param fileObject the file in input
     * @return <code>true</code> if the importer is compatible with <code>fileObject</code> or <code>false</code>
     * otherwise
     */
    public boolean isMatchingImporter(FileObject fileObject);
}
