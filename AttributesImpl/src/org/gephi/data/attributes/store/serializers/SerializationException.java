package org.gephi.data.attributes.store.serializers;

/**
 *
 * @author Ernesto A
 */
public class SerializationException extends RuntimeException {

    public SerializationException(Throwable thrwbl) {
        super(thrwbl);
    }

    public SerializationException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public SerializationException(String string) {
        super(string);
    }

    public SerializationException() {
    }
    
}
