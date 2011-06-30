/*
Copyright 2008-2011 Gephi
Authors : Ernesto Aneiros
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
package org.gephi.data.attributes.store;

import java.util.Map;

/**
 *
 * @author Ernesto A
 */
public interface AttributeStore<K, V> {
    
    String getName();
    
    boolean contains(K key);
    
    V get(K key);
    
    void put(K key, V value);
    
    void putAll(Map<K, V> pairs);
    
    void delete(K key);

    int size();
    
    void close();
}
