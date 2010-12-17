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
import org.gephi.spreadsimulator.api.ChoiceFactory;
import org.gephi.spreadsimulator.spi.Choice;
import org.gephi.spreadsimulator.spi.ChoiceBuilder;
import org.openide.util.Lookup;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class ChoiceFactoryImpl implements ChoiceFactory {
	private Map<String, ChoiceBuilder> builders;

	public ChoiceFactoryImpl() {
		builders = new HashMap<String, ChoiceBuilder>();
		for (ChoiceBuilder cb : Lookup.getDefault().lookupAll(ChoiceBuilder.class).toArray(new ChoiceBuilder[0]))
			builders.put(cb.getChoiceClass().getSimpleName(), cb);
	}

	@Override
	public Choice getChoice(String params) {
		String name = params.split(":")[0];
		return builders.get(name).getChoice();
	}
}
