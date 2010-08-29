/*
Copyright 2008-2010 Gephi
Authors : Patick J. McSweeney <pjmcswee@syr.edu>
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
package org.gephi.statistics.plugin.builder;

import org.gephi.statistics.plugin.PageRank;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pjmcswee
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class PageRankBuilder implements StatisticsBuilder {

    public String getName() {
        return NbBundle.getMessage(PageRankBuilder.class, "PageRank.name");
    }

    public Statistics getStatistics() {
        return new PageRank();
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return PageRank.class;
    }
}
