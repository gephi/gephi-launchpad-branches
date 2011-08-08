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

import java.security.SecureRandom;

public class DistributionGenerator extends java.util.Random {

    public DistributionGenerator() {
        super();
    }

    int nextPowerLaw(int min, int max, double power) {
        //x = [(x1^(n+1) - x0^(n+1))*y + x0^(n+1)]^(1/(n+1))
        //where y is a uniform variate, n is the distribution power, 
        //    x0 and x1 define the range of the distribution, and x is your power-law distributed variate.
        //
        double rand = nextDouble();
        int res = (int) Math.round(Math.pow((Math.pow(max, power + 1) - Math.pow(min, power + 1)) * rand + Math.pow(min, power + 1), 1.0 / (power + 1)));
        res = (max - res) + min;
        if (res > max || res < min)//bad hack for overflow
        {
            res = min;
        }
        return res;
    }
}