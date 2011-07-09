/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
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
package org.gephi.data.attributes.api;

/**
 * Attribute event interface, that {@link AttributeListener } receives when the
 * attribute model or any attribute row is modified.
 * <p>
 * <ul>
 * <li><b>ADD_COLUMN:</b> One or several columns have been created, look at
 * {@link AttributeEventData#getAddedColumns() } to get data.</li>
 * <li><b>REMOVE_COLUMN:</b> One or several columns have been removed, look at
 * {@link AttributeEventData#getRemovedColumns() } to get data.</li>
 * <li><b>SET_VALUE:</b> A value has been set in a row, look at*
 * {@link AttributeEventData#getTouchedValues()} to get new values and
 * {@link AttributeEventData#getTouchedObjects() } to get objects where value
 * has been modified.</li>
 * </ul>
 *
 * @author Mathieu Bastian
 */
public interface AttributeEvent {

    /**
     * Attribute model events.
     * <ul>
     * <li><b>ADD_COLUMN:</b> One or several columns have been created, look at
     * {@link AttributeEventData#getAddedColumns() } to get data.</li>
     * <li><b>REMOVE_COLUMN:</b> One or several columns have been removed, look at
     * {@link AttributeEventData#getRemovedColumns() } to get data.</li>
     * <li><b>SET_VALUE:</b> A value has been set in a row, look at*
     * {@link AttributeEventData#getTouchedValues()} to get new values and
     * {@link AttributeEventData#getTouchedObjects() } to get objects where value
     * has been modified.</li>
     * </ul>
     */
    public enum EventType {

        ADD_COLUMN, REMOVE_COLUMN, SET_VALUE
    };

    public EventType getEventType();

    public AttributeTable getSource();

    public AttributeEventData getData();

    /**
     * Returns <code>true</code> if this event is one of these in parameters.
     * @param type  the event types that are to be compared with this event
     * @return      <code>true</code> if this event is <code>type</code>,
     *              <code>false</code> otherwise
     */
    public boolean is(EventType... type);
}
