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

package org.gephi.visualization.utils;

/**
 * Class representing a pair of value to be used with generics requiring a 
 * single type.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public class Pair<F, S> {
    public final F first;
    public final S second;


    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<F, S>(first, second);
    }

    public static <F, S> Pair<F, S> ofNotNull(F first, S second) {
        if (first == null || second == null) return null;
        else return Pair.of(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
