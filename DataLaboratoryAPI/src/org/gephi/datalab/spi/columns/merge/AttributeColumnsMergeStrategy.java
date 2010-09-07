/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.spi.columns.merge;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.datalab.spi.Manipulator;

/**
 * <p>Service for defining strategies for merging attribute columns of a table.</p>
 * <p>Has the same interface as a manipulator.</p>
 * @see Manipulator
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public interface AttributeColumnsMergeStrategy extends Manipulator{

    /**
     * Prepare columns (with their table) for this merge strategy.
     * At least <b>1</b> column will be set up to merge always.
     * @param table Table of the columns
     * @param columns Columns to merge
     */
    void setup(AttributeTable table, AttributeColumn[] columns);
}
