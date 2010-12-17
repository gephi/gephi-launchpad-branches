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
package org.gephi.spreadsimulator.plugin.builder.stopcondition;

import org.gephi.spreadsimulator.plugin.stopcondition.StepsCount;
import org.gephi.spreadsimulator.spi.StopCondition;
import org.gephi.spreadsimulator.spi.StopConditionBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = StopConditionBuilder.class)
public class StepsCountBuilder implements StopConditionBuilder {
	@Override
	public String getName() {
		return NbBundle.getMessage(StepsCountBuilder.class, "StepsCount.name");
	}

	@Override
	public StopCondition getStopCondition() {
		return new StepsCount();
	}

	@Override
	public Class<? extends StopCondition> getStopConditionClass() {
		return StepsCount.class;
	}
}
