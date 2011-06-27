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

package org.gephi.visualization.data.layout;

import java.nio.ByteBuffer;

/**
 * Generic interface which controls how some data is stored in a buffer.
 *
 * Antonio Patriarca <antoniopatriarca@gmail.com>
 */
public interface Layout<I, O> {

    /**
     * Returns the dimension a new buffer filled using this layout
     * should have.
     *
     * @return the suggested dimension of new buffers
     */
    public int suggestedBufferSize();

    /**
     * Writes the data to the buffer.
     *
     * @param b the buffer where the new data is written
     * @param e the new data to be written
     * @return <code>true</code> if the data can be written on the buffer, 
     *         <code>false</code> otherwise
     */
    public boolean add(ByteBuffer b, I e);

    /**
     * Retrieves and removes the next element in the buffer, or returns
     * <code>null</code> if the buffer does not contain any more elements.
     *
     * @param b the buffer to be read from
     * @return the next element in the buffer, or <code>null</code> if the
     *         buffer has no remaining elements
     */
    public O get(ByteBuffer b);

    /**
     * Retrieves and removes the next element in the buffer from position 
     * <code>i[0]</code>, or returns <code>null</code> if the buffer does not
     * contain any more elements. The position is updated to point to the next
     * element.
     *
     * @param b the buffer to be read from
     * @param i starting position from which the new element should be read. It
     *          should be an array of length one and it is modified to the new
     *          reading position
     * @return the next element in the buffer, or <code>null</code> if the
     *         buffer has no remaining elements
     * @throws IndexOutOfBoundsException if the array <code>i</code> does not
     *         contains at least one element
     */
    public O get(ByteBuffer b, int[] i) throws IndexOutOfBoundsException;

    /**
     * Queries the remaining size of a buffer to know if it's possible to read
     * another element from it.
     *
     * @param b the buffer to test
     * @return <code>true</code> if it's possible to get another element from
     *         the buffer, <code>false</code> otherwise
     */
    public boolean hasNext(ByteBuffer b);

    /**
     * Queries the remaining size of a buffer to know if it's possible to read
     * another element from a particular position.
     *
     * @param b the buffer to test
     * @param i the position of the first byte to read
     * @return <code>true</code> if it's possible to get another element from
     *         the buffer, <code>false</code> otherwise
     */
    public boolean hasNext(ByteBuffer b, int i);
}
