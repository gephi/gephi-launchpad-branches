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

import com.sleepycat.bind.ByteArrayBinding;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.event.CacheEventListener;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.store.serializers.AttributeValueSerializer;

/**
 *
 * @author Ernesto A
 */
public class AttributeStoreImpl implements AttributeStore<Integer, AttributeValue[]> {
    private final String name;

    private final Environment env;
    private final Database db;
    private final StoredMap<Integer, byte[]> diskStore;

    private final CacheManager cacheManager;
    private final Ehcache cache;
    
    private final AttributeValueSerializer serializer;
    
    public AttributeStoreImpl(String name, Environment env, Database db, CacheManager manager, AttributeTable attributeTable) {
        this.name = name;
        
        this.env = env;
        this.db = db;
        IntegerBinding keyBinding = new IntegerBinding();
        ByteArrayBinding valueBinding = new ByteArrayBinding();
        this.diskStore = new StoredMap(db, keyBinding, valueBinding, true);

        this.cacheManager = manager;
        manager.addCache(name);
        cache = new SelfPopulatingCache(manager.getEhcache(name), getCacheEntryFactory());
        cache.getCacheEventNotificationService().registerListener(getCacheEventListener());
     
        serializer = new AttributeValueSerializer(attributeTable);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean contains(Integer key) {
        Element elem = cache.getQuiet(key);
        return elem != null;
    }

    @Override
    public AttributeValue[] get(Integer key) {
        Element elem = cache.get(key);
        return (AttributeValue[]) elem.getObjectValue();
    }

    @Override
    public void put(Integer key, AttributeValue[] value) {
        Element elem = elementFor(key, value);
        cache.put(elem);
    }

    @Override
    public void putAll(Map<Integer, AttributeValue[]> pairs) {
        for (Map.Entry<Integer, AttributeValue[]> entry : pairs.entrySet()) {
            Element elem = elementFor(entry.getKey(), entry.getValue());
            cache.put(elem);
        }
    }

    @Override
    public void delete(Integer key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.getSize();
    }

    public void close() {
        db.close();
        env.close();
        cacheManager.removeCache(name);
    }

    private Element elementFor(Integer key, AttributeValue[] value) {
        return new Element(key, value);
    }
    
    private CacheEventListener getCacheEventListener() {
        return  new CacheEventListener() {

            @Override
            public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
            }

            @Override
            public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            }

            @Override
            public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            }

            @Override
            public void notifyElementExpired(Ehcache cache, Element element) {
            }

            @Override
            public void notifyElementEvicted(Ehcache cache, Element element) {
                Integer key = (Integer)element.getObjectKey();
                AttributeValue[] vals = (AttributeValue[])element.getObjectValue();
                
                byte[] arr = serializer.writeValuesData(vals);
                diskStore.put(key, arr);
            }

            @Override
            public void notifyRemoveAll(Ehcache cache) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public  Object clone() throws CloneNotSupportedException {
                throw new CloneNotSupportedException();
            }
        };
    }
    
    private CacheEntryFactory getCacheEntryFactory() {
        return new CacheEntryFactory() {

            @Override
            public Object createEntry(Object key) throws Exception {
                try {
                    cache.acquireWriteLockOnKey(key);
                    
                    byte[] arr = diskStore.get((Integer)key);
                    
                    if (arr != null) {
                        AttributeValue[] vals = serializer.readValuesData(arr);
                        return vals;
                    }
                    
                    return null;
                }
                finally {
                    cache.releaseWriteLockOnKey(key);
                }
            }
        };
    }
}
