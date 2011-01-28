/*
Copyright 2008-2010 Gephi
Authors : Martin Škurla <bujacik@gmail.com>
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
package org.gephi.data.attributes.type;

import org.gephi.data.attributes.api.AttributeType;

/**
 * Complex type that define a list of String items. Can be created from a String
 * array, from a char array or from single string using either given or default separators.
 * <p>
 * String list is useful when, for a particular type, the number of string
 * that define an element is not known by advance.
 *
 * @author Martin Škurla
 * @author Mathieu Bastian
 * @see AttributeType
 */
public final class StringList extends AbstractList<String> {

    /**
     * Create a new string from a char array. One char per list cell.
     *
     * @param list      the list
     */
    public StringList(char[] list) {
        super(StringList.parse(list));
    }

    /**
     * Create a new string list with the given items.
     *
     * @param list      the list of string items
     */
    public StringList(String[] list) {
        super(list);
    }

    /**
     * Create a new string list with items found in the given value. Default
     * separators <code>,|;</code> are used to split the string in a list.
     *
     * @param input     a string with default separators
     */
    public StringList(String input) {
        this(input, AbstractList.DEFAULT_SEPARATOR);
    }

    /**
     * Create a new string list with items found using given separators.
     *
     * @param input     a string with separators defined in <code>separator</code>
     * @param separator the separators chars that are to be used to split
     *                  <code>value</code>
     */
    public StringList(String input, String separator) {
        super(input, separator, String.class);
    }

    private static String[] parse(char[] list) {
        String[] resultList = new String[list.length];

        for (int i = 0; i < list.length; i++) {
            resultList[i] = "" + list[i];
        }

        return resultList;
    }

    /**
     * Returns the item at the specified <code>index</code>. May return
     * <code>null</code> if <code>index</code> is out of range.
     *
     * @param index     the position in the string list
     * @return          the item at the specified position, or <code>null</code>
     */
    public String getString(int index) {
        return getItem(index);
    }
}
