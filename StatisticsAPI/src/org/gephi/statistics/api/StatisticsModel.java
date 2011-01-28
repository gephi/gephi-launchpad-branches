/*
Copyright 2008-2010 Gephi
Authors : Patick J. McSweeney <pjmcswee@syr.edu>,
          Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.statistics.api;

import org.gephi.statistics.spi.Statistics;
import javax.swing.event.ChangeListener;
import org.gephi.statistics.spi.StatisticsUI;

/**
 * Hosts user interface states and statistics instances, to have access to
 * results strings.
 * 
 * @author Patrick J. McSweeney, Mathieu Bastian
 * @see StatisticsController
 */
public interface StatisticsModel {

    /**
     * Returns the report for the given statistics class or <code>null</code> if no report
     * exists for this statistics.
     * @param statistics        a statistics class
     * @return                  the report or <code>null</code> if not found
     */
    public String getReport(Class<? extends Statistics> statistics);

    /**
     * Returns the result string for the given <code>StatisticsUI</code> class or
     * <code>null</code> if no result string exists for this statistics.
     * @param statisticsUI      a statisticsUI class
     * @return                  the result or <code>null</code> if not found
     */
    public String getResult(StatisticsUI statisticsUI);

    /**
     * Returns <code>true</code> if the statistics front-end is visible, <code>
     * false</code> otherwise.
     * @param statisticsUI      an UI instance
     * @return                  <code>true</code> if the statistics front-end
     *                          is visible, <code>false</code> otherwise
     */
    public boolean isStatisticsUIVisible(StatisticsUI statisticsUI);

    /**
     * Returns <code>true</code> if the UI is in running state, <code>false</code>
     * otherwise.
     * @param statisticsUI      an UI instance
     * @return                  <code>true</code> if the statistics is running,
     *                          <code>false</code> otherwise
     */
    public boolean isRunning(StatisticsUI statisticsUI);

    /**
     * Returns the <code>Statistics</code> instance currently running for the
     * particular <code>StatisticsUI</code> registered or <code>null</code> if
     * the statistics is not running.
     * @param statisticsUI      an UI instance
     * @return                  the statistics instance if it is running, or
     *                          <code>null</code> if not running
     */
    public Statistics getRunning(StatisticsUI statisticsUI);

    public void addChangeListener(ChangeListener changeListener);

    public void removeChangeListener(ChangeListener changeListener);
}
