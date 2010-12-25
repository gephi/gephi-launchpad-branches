/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Martin Škurla <bujacik@gmail.com>
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
package org.gephi.data.attributes;

import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.spi.AttributeValueDelegateProvider;

/**
 *
 * @author Mathieu Bastian
 * @author Martin Škurla
 */
public final class AttributeValueImpl implements AttributeValue {

    private final AttributeColumnImpl column;
    private final Object value;

    public AttributeValueImpl(AttributeColumnImpl column, Object value) {
        this.column = column;
        this.value = value;
    }

    public AttributeColumnImpl getColumn() {
        return column;
    }

    public Object getValue() {
        if (column.getOrigin() != AttributeOrigin.DELEGATE) {
            return value;
        }
        else {
            if (value == null)
                return null;

            AttributeValueDelegateProvider attributeValueDelegateProvider = column.getProvider();

            Object result;
            if (AttributeUtilsImpl.getDefault().isEdgeColumn(column))
                result = attributeValueDelegateProvider.getEdgeAttributeValue(value, column);
            else if (AttributeUtilsImpl.getDefault().isNodeColumn(column))
                result = attributeValueDelegateProvider.getNodeAttributeValue(value, column);
            else
                throw new AssertionError();

            // important for Neo4j and in future also for other storing engines
            // the conversion can be necessary because of types mismatch
            // for Neo4j return type can be array of primitive type which must be
            // converted into List type
            if (result.getClass().isArray())
                result = ListFactory.fromArray(result);

            return result;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof AttributeValue) {
            if (this == obj) {
                return true;
            }
            Object thisVal = this.getValue();
            Object objVal = ((AttributeValue) obj).getValue();
            if (thisVal == null && objVal == null) {
                return true;
            }
            if (thisVal != null && objVal != null && thisVal.equals(objVal)) {
                return true;
            }
        }
        return false;
    }
}
