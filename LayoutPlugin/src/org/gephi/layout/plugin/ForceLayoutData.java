/*
Copyright 2008-2010 Gephi
Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.plugin;

import org.gephi.layout.plugin.force.ForceVector;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class ForceLayoutData extends ForceVector {

    public float energy0;
    public float step;
    public int progress;

    public ForceLayoutData() {
        progress = 0;
        step = 0;
        energy0 = Float.POSITIVE_INFINITY;
    }
}
