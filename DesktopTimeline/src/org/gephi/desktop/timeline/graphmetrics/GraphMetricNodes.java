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
package org.gephi.desktop.timeline.graphmetrics;

import org.gephi.graph.api.Graph;
import org.gephi.timeline.api.GraphMetric;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author daniel
 */
@ServiceProviders(value={
    @ServiceProvider(service = GraphMetric.class),
    @ServiceProvider(service = GraphMetricNodes.class)}
)
public class GraphMetricNodes implements GraphMetric {
    
    private final String MetricName = "Number of nodes";
    
    public String getName() {
        return MetricName;
    }
    
    public Number measureGraph(Graph graph) {
        return graph.getNodeCount();
    }
}