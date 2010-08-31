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
package org.gephi.filters.spi;

import org.gephi.graph.api.Graph;

/**
 * Filter working with full graphs and generally returning a subgraph. Node and
 * Edge filters removes either nodes or edges but not both. This filter interface
 * has to be used in these case.
 *
 * @author Mathieu Bastian
 */
public interface ComplexFilter extends Filter {

    public Graph filter(Graph graph);
}
