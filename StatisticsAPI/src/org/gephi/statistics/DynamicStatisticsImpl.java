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
package org.gephi.statistics;

import org.gephi.data.attributes.api.Estimator;
import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.statistics.spi.DynamicStatistics;

/**
 * The default implementation of {@link DynamicStatistics}.
 *
 * @author Cezary Bartosiak
 */
public class DynamicStatisticsImpl implements DynamicStatistics {
	protected TimeInterval timeInterval = new TimeInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	protected double       window       = Double.POSITIVE_INFINITY - Double.NEGATIVE_INFINITY;
	protected Estimator    estimator    = Estimator.FIRST;

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public double getWindow() {
		return window;
	}

	public Estimator getEstimator() {
		return estimator;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setWindow(double window) {
		if (window < 0 || window > timeInterval.getHigh() - timeInterval.getLow())
			throw new IllegalArgumentException(
						"The window must be greater than 0 " +
						"and less or equal to (high - low).");

		this.window = window;
	}

	public void setEstimator(Estimator estimator) {
		if (estimator != Estimator.MEDIAN && estimator != Estimator.MODE &&
				estimator != Estimator.FIRST && estimator != Estimator.LAST)
			throw new IllegalArgumentException(
					"The given estimator must be one of the following: " +
					"MEDIAN, MODE, FIRST, LAST.");

		this.estimator = estimator;
	}
}
