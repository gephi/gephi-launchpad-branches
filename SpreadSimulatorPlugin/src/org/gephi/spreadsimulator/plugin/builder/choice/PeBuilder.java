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
package org.gephi.spreadsimulator.plugin.builder.choice;

import org.gephi.spreadsimulator.plugin.choice.Pe;
import org.gephi.spreadsimulator.spi.Choice;
import org.gephi.spreadsimulator.spi.ChoiceBuilder;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = ChoiceBuilder.class)
public class PeBuilder implements ChoiceBuilder {
	@Override
	public String getName() {
		return NbBundle.getMessage(Pe.class, "Pe.name");
	}

	@Override
	public Choice getChoice() {
		return new Pe();
	}

	@Override
	public Class<? extends Choice> getChoiceClass() {
		return Pe.class;
	}
}
