package org.gephi.data.attributes.store.serializers;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author Ernesto A
 */
public interface Serializer {

    public static final byte PRIMITIVE_TYPE             = 0x10;
    public static final byte BYTE                       = 0x11;
    public static final byte SHORT                      = 0x12;
    public static final byte INTEGER                    = 0x13;
    public static final byte LONG                       = 0x14;
    public static final byte FLOAT                      = 0x15;
    public static final byte DOUBLE                     = 0x16;
    public static final byte BIGINTEGER                 = 0x17;
    public static final byte BIGDECIMAL                 = 0x18;
    public static final byte BOOLEAN                    = 0x19;
    public static final byte CHARACTER                  = 0x1A;
    public static final byte STRING                     = 0x1B;
    
    public static final byte LIST_TYPE                  = 0x20;             
    public static final byte BYTE_LIST                  = 0x21;
    public static final byte SHORT_LIST                 = 0x22;
    public static final byte INTEGER_LIST               = 0x23;
    public static final byte LONG_LIST                  = 0x24;
    public static final byte FLOAT_LIST                 = 0x25;
    public static final byte DOUBLE_LIST                = 0x26;
    public static final byte BIGINTEGER_LIST            = 0x27;
    public static final byte BIGDECIMAL_LIST            = 0x28;
    public static final byte BOOLEAN_LIST               = 0x29;
    public static final byte CHARACTER_LIST             = 0x2A;
    public static final byte STRING_LIST                = 0x2B;
    
    public static final byte DYNAMIC_TYPE               = 0x40;
    public static final byte DYNAMIC_BYTE               = 0x41;
    public static final byte DYNAMIC_SHORT              = 0x42;
    public static final byte DYNAMIC_INTEGER            = 0x43;
    public static final byte DYNAMIC_LONG               = 0x44;
    public static final byte DYNAMIC_FLOAT              = 0x45;
    public static final byte DYNAMIC_DOUBLE             = 0x46;
    public static final byte DYNAMIC_BIGINTEGER         = 0x47;
    public static final byte DYNAMIC_BIGDECIMAL         = 0x48;
    public static final byte DYNAMIC_BOOLEAN            = 0x49;
    public static final byte DYNAMIC_CHARACTER          = 0x4A;
    public static final byte DYNAMIC_STRING             = 0x4B;
    public static final byte TIME_INTERVAL              = 0x4C;
    
    void writeObjectData (DataOutputStream dos, Object o);

    Object readObjectData(DataInputStream dis);
}
