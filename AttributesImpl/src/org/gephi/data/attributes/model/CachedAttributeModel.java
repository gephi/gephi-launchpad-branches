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
package org.gephi.data.attributes.model;

import org.gephi.data.attributes.AbstractAttributeModel;
import org.gephi.data.attributes.AttributeFactoryImpl;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.event.AttributeEventManager;
import org.gephi.data.attributes.store.Store;
import org.gephi.data.attributes.store.StoreController;
import org.openide.util.Lookup;

/**
 *
 * @author Ernesto A
 */
public class CachedAttributeModel extends AbstractAttributeModel {

    private final Store store;
    
    public CachedAttributeModel() {
        eventManager = new AttributeEventManager(this);
        createPropertiesColumn();

        StoreController storeController = Lookup.getDefault().lookup(StoreController.class);
        store = storeController.newStore(this);
        factory = new AttributeFactoryImpl(this);
        
        eventManager.start();        
    }

    @Override
    public Object getManagedValue(Object obj, AttributeType attributeType) {
        return obj;
    }

    @Override
    public void clear() {
        // TODO
    }
}
