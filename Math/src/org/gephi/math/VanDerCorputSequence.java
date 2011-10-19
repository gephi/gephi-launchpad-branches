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

package org.gephi.math;

/**
 * Generates a sequence of numbers in [0, 1) with the low-discrepancy property
 * using the Van der Corput sequence.
 *
 * @author Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public final class VanDerCorputSequence {
    /**
     * The class has only static methods.
     */
    private VanDerCorputSequence() {}

    /**
     * Generates the <code>n</code><sup>th</sup> number in the Van der Corput
     * sequence. The algorithm to generate the number is taken from the
     * "Physically based rendering" book by M.Pharr and G.Humphreys.
     *
     * @param n
     * @return
     */
    public static float get(int n) {
        n = (n << 16) | (n >> 16);
        n = ((n & 0x00ff00ff) << 8) | ((n & 0xff00ff00) >> 8);
        n = ((n & 0x0f0f0f0f) << 4) | ((n & 0xf0f0f0f0) >> 4);
        n = ((n & 0x33333333) << 2) | ((n & 0xcccccccc) >> 2);
        n = ((n & 0x55555555) << 1) | ((n & 0xaaaaaaaa) >> 1);
        return (float) n / (float) 0x100000000L;
    }
}
