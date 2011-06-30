package org.gephi.data.attributes.store;

/**
 *
 * @author Ernesto A
 */
public interface StoreSerializationProvider<T> {
    
    T serialize(Object o);
    
    Object deserialize(T t);
}
