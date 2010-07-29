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
package org.gephi.statistics.plugin;

import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphModel;
import org.gephi.statistics.DynamicStatisticsImpl;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 * This class measures how closely the degree distribution of a
 * network follows a power-law scale.  An alpha value between 2 and 3
 * implies a power law. It's a dynamic version that means the statistic
 * is measured for every part (year, month etc. it depends on user's
 * settings) of the given time interval.
 *
 * @author Cezary Bartosiak
 */
public class DynamicDegreeDistribution extends DynamicStatisticsImpl implements Statistics, LongTask {
	private boolean        cancel;
	private ProgressTicket progressTicket;

	/**
	 * Executes the metric.
	 * 
	 * @param graphModel     graph's model to work on
	 * @param attributeModel attributes' model to work on
	 */
	public void execute(GraphModel graphModel, AttributeModel attributeModel) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Returns the report based on the interpretation of the network.
	 * 
	 * @return the report based on the interpretation of the network.
	 */
	public String getReport() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Stops the execution of this metric.
	 *
	 * @return true if the execution was stopped, otherwise false.
	 */
	public boolean cancel() {
		cancel = true;
		return true;
	}

	/**
	 * Sets the progress ticket for the metric.
	 *
	 * @param progressTicket progress ticket to set
	 */
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
}
