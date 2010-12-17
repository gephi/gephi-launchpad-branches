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
package org.gephi.spreadsimulator.plugin.builder.initialevent;

import org.gephi.spreadsimulator.plugin.initialevent.Empty;
import org.gephi.spreadsimulator.spi.InitialEvent;
import org.gephi.spreadsimulator.spi.InitialEventBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = InitialEventBuilder.class)
public class EmptyBuilder implements InitialEventBuilder {
	@Override
	public String getName() {
		return NbBundle.getMessage(Empty.class, "Empty.name");
	}

	@Override
	public InitialEvent getInitialEvent(String params) {
		return new Empty();
	}

	@Override
	public Class<? extends InitialEvent> getInitialEventClass() {
		return Empty.class;
	}
}
