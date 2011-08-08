/*
Copyright 2008-2011 Gephi
Authors : Taras Klaskovsky <megaterik@gmail.com>
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
package org.gephi.lsg_generator;

import java.util.HashMap;

//Key in hashMap is a number of edge in list
public class HashMapForEdges extends HashMap<Pair, Integer> {

    public boolean existEdge(Node source, Node target) {
        return (containsKey(new Pair(source.id, target.id)));
    }

    private void replaceEdge(Node source, Node target, Node newTarget) {
        int key = get(new Pair(source.id, target.id));
        remove(new Pair(source.id, target.id));
        source.edge[key] = newTarget;
        put(new Pair(source.id, newTarget.id), new Integer(key));
    }

    public void swapEdge(Node source1, Node target1, Node source2, Node target2) {
        replaceEdge(source1, target1, target2);
        replaceEdge(target1, source1, source2);
        replaceEdge(source2, target2, target1);
        replaceEdge(target2, source2, source1);
    }

    public void addEdge(Node source, Node target, int key) {
        put(new Pair(source.id, target.id), key);//key is for source.edge[key]
    }
}