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
package org.gephi.layout.plugin.quadtree;

import org.gephi.graph.api.Spatial;
import org.gephi.layout.plugin.force.AbstractForce;
import org.gephi.layout.plugin.force.ForceVector;
import org.gephi.layout.plugin.force.quadtree.BarnesHut;
import org.gephi.layout.plugin.force.quadtree.QuadTree;
import static org.junit.Assert.*;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class BarnesHutTest {

    public static final double eps = 1e-6;

    class TestForce extends AbstractForce {

        @Override
        public ForceVector calculateForce(Spatial node1, Spatial node2, float distance) {
            return new ForceVector(1, 1);
        }
    }

    @org.junit.Test
    public void testLeafTree() {
        QuadTree tree = new QuadTree(0, 0, 10, 10);
        tree.addNode(new TestNode(1, 1));
        BarnesHut barnesHut = new BarnesHut(new TestForce());

        ForceVector f = barnesHut.calculateForce(tree, tree);
        assertNotNull(f);
        assertEquals(f.x(), 1, eps);
        assertEquals(f.y(), 1, eps);
    }
}
