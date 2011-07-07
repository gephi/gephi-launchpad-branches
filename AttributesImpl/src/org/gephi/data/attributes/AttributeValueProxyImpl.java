
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

import java.util.concurrent.atomic.AtomicInteger;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.store.Store;

/**
 *
 * @author Mathieu Bastian
 * @author Martin Škurla
 */
public final class AttributeValueProxyImpl implements AttributeValue {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    
    private final int id = ID_COUNTER.incrementAndGet();
    
    private final Store store;
    private final AttributeColumnImpl column;
    
    public AttributeValueProxyImpl(Store store, AttributeColumnImpl column, Object value) {
        this.store = store;
        this.column = column;
        
        if (value != null) {
            store.put(id, value);
        }
    }

    public AttributeColumnImpl getColumn() {
        return column;
    }

    public Object getValue() {
        return store.get(id);
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
