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
package org.gephi.data.attributes.model;

import org.gephi.data.attributes.AbstractAttributeModel;
import org.gephi.data.attributes.api.AttributeListener;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.event.AbstractEvent;

/**
 * Specific manager for temporary storing of attributes. This is typically used when new attributes are
 * imported in the system. No index system is required.
 * <p>
 *
 * @author Mathieu Bastian
 * @see IndexedAttributeManager
 */
public class TemporaryAttributeModel extends AbstractAttributeModel {

    public TemporaryAttributeModel() {
        createPropertiesColumn();
    }

    @Override
    public Object getManagedValue(Object obj, AttributeType attributeType) {
        return obj;
    }

    @Override
    public void addAttributeListener(AttributeListener listener) {
        throw new UnsupportedOperationException("Temporary Attribute Model doens't supper events");
    }

    @Override
    public void removeAttributeListener(AttributeListener listener) {
        throw new UnsupportedOperationException("Temporary Attribute Model doens't supper events");
    }

    @Override
    public void fireAttributeEvent(AbstractEvent event) {
    }
}
