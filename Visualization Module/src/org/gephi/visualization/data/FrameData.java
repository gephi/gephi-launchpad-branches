/*
Copyright 2008-2011 Gephi
Authors : Antonio Patriarca <antoniopatriarca@gmail.com>
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

package org.gephi.visualization.data;

import java.util.ArrayList;

/**
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class FrameData {

    final ArrayList<NodeBatch> nodeBatches;

    public FrameData() {
        this.nodeBatches = new ArrayList<NodeBatch>();
    }

    public final void addNodeBatch(NodeBatch nodeBatch) {
        this.nodeBatches.add(nodeBatch);
    }

}
