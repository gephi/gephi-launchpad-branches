
/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalab.spi.general;

import org.gephi.datalab.spi.Manipulator;

/**
 * <p>Manipulator for general actions that don't need to obtain any data before being executed.</p>
 * <p>They are added as buttons in Data table toolbar.</p>
 * <p>The implementations don't need a builder and can simply be published with <code>@ServiceProvider(service = GeneralActionsManipulator.class)</code> annotation</p>
 * @see Manipulator
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
public interface GeneralActionsManipulator extends Manipulator {

}
