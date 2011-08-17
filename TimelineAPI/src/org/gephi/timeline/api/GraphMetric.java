/*
Copyright 2008-2011 Gephi
Authors : Daniel Bernardes <daniel.bernardes@gephi.org>
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

package org.gephi.timeline.api;

import org.gephi.graph.api.Graph;

/**
 *
 * @author daniel
 */
public interface GraphMetric {
    
    /**
     * Returns a string with the name of this metric.
     * 
     */
    public String getName();
    
    /**
     * Returns a number corresponding to the measurement of the graph with
     * this metric.
     * 
     * @param graph         the Graph to be measured
     */
    public Number measureGraph(Graph graph);
    
}
