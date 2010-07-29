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

import org.gephi.data.attributes.type.TimeInterval;
import org.gephi.statistics.spi.DynamicStatistics;

/**
 * The default implementation of {@link DynamicStatistics}.
 *
 * @author Cezary Bartosiak
 */
public class DynamicStatisticsImpl implements DynamicStatistics {
	private TimeInterval timeInterval = new TimeInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	private double       window       = Double.POSITIVE_INFINITY - Double.NEGATIVE_INFINITY;

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public double getWindow() {
		return window;
	}

	public void setTimeInterval(double low, double high) {
		if (low > high)
			throw new IllegalArgumentException(
						"The left endpoint of the interval must be less than " +
						"the right endpoint.");

		timeInterval = new TimeInterval(low, high);
	}

	public void setWindow(double window) {
		if (window < 0)
			throw new IllegalArgumentException("The window must be greater than 0.");

		this.window = window;
	}
}
