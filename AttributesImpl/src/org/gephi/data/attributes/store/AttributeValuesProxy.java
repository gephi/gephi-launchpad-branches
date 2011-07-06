package org.gephi.data.attributes.store;

import java.util.concurrent.atomic.AtomicInteger;
import org.gephi.data.attributes.api.AttributeValue;

/**
 *
 * @author Ernesto A
 */
public class AttributeValuesProxy {
    
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();

    private final int cacheID = ID_COUNTER.incrementAndGet();
    private AttributeStore<Integer, AttributeValue[]> store;

    public AttributeValuesProxy(AttributeStore<Integer, AttributeValue[]> store) {
        this.store = store;
    }
    
    public AttributeValue get(int index) {
        return values()[index];
    }
    
    public AttributeValue set(int index, AttributeValue newValue) {
        AttributeValue oldValue = get(index);
        values()[index] = newValue;
        return oldValue;
    }
    
    public void setValues(AttributeValue[] newValues) {
        store.put(cacheID, newValues);
    }
    
    public AttributeValue[] getValues() {
        return values();
    }
    
    public int size() {
        return values() == null ? 0 : values().length;
    }
    
    private AttributeValue[] values() {
        return store.get(cacheID);
    }
}
