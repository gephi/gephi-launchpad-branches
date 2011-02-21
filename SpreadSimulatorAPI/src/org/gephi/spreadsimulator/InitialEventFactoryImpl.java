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
package org.gephi.spreadsimulator;

import java.util.HashMap;
import java.util.Map;
import org.gephi.spreadsimulator.api.InitialEventFactory;
import org.gephi.spreadsimulator.spi.InitialEvent;
import org.gephi.spreadsimulator.spi.InitialEventBuilder;
import org.openide.util.Lookup;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class InitialEventFactoryImpl implements InitialEventFactory {
	private Map<String, InitialEventBuilder> builders;

	public InitialEventFactoryImpl() {
		builders = new HashMap<String, InitialEventBuilder>();
		for (InitialEventBuilder ieb : Lookup.getDefault().lookupAll(InitialEventBuilder.class).toArray(new InitialEventBuilder[0]))
			builders.put(ieb.getInitialEventClass().getSimpleName(), ieb);
	}

	@Override
	public InitialEvent getInitialEvent(String params) {
		if (params.contains(":")) {
			String name = params.split(":")[0];
			return builders.get(name).getInitialEvent(params.split(":")[1]);
		}
		return builders.get(params).getInitialEvent("");
	}
}
