package org.gephi.data.store.attributes.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.type.BigDecimalList;
import org.gephi.data.attributes.type.BigIntegerList;
import org.gephi.data.attributes.type.BooleanList;
import org.gephi.data.attributes.type.ByteList;
import org.gephi.data.attributes.type.CharacterList;
import org.gephi.data.attributes.type.DoubleList;
import org.gephi.data.attributes.type.DynamicBigDecimal;
import org.gephi.data.attributes.type.DynamicBigInteger;
import org.gephi.data.attributes.type.DynamicBoolean;
import org.gephi.data.attributes.type.DynamicByte;
import org.gephi.data.attributes.type.DynamicCharacter;
import org.gephi.data.attributes.type.DynamicDouble;
import org.gephi.data.attributes.type.DynamicFloat;
import org.gephi.data.attributes.type.DynamicInteger;
import org.gephi.data.attributes.type.DynamicLong;
import org.gephi.data.attributes.type.DynamicShort;
import org.gephi.data.attributes.type.DynamicString;
import org.gephi.data.attributes.type.FloatList;
import org.gephi.data.attributes.type.IntegerList;
import org.gephi.data.attributes.type.LongList;
import org.gephi.data.attributes.type.ShortList;
import org.gephi.data.attributes.type.StringList;
import org.gephi.data.attributes.type.TimeInterval;

/**
 *
 * @author Ernesto A
 */
public class AttributeValueSerializer {
    
    private static final PrimitiveTypeSerializer primitiveSerializer = new PrimitiveTypeSerializer();
    private static final DynamicTypeSerializer dynamicSerializer = new DynamicTypeSerializer();
    private static final ListTypeSerializer listSerializer = new ListTypeSerializer();
    
    private static final EnumMap<AttributeType, Serializer> SERIALIZERS = new EnumMap<AttributeType, Serializer>(AttributeType.class);
    static {
        SERIALIZERS.put(AttributeType.BYTE, primitiveSerializer);
        SERIALIZERS.put(AttributeType.SHORT, primitiveSerializer);
        SERIALIZERS.put(AttributeType.INT, primitiveSerializer);
        SERIALIZERS.put(AttributeType.LONG, primitiveSerializer);
        SERIALIZERS.put(AttributeType.FLOAT, primitiveSerializer);
        SERIALIZERS.put(AttributeType.DOUBLE, primitiveSerializer);
        SERIALIZERS.put(AttributeType.BIGINTEGER, primitiveSerializer);
        SERIALIZERS.put(AttributeType.BIGDECIMAL, primitiveSerializer);
        SERIALIZERS.put(AttributeType.CHAR, primitiveSerializer);
        SERIALIZERS.put(AttributeType.BOOLEAN, primitiveSerializer);
        SERIALIZERS.put(AttributeType.STRING, primitiveSerializer);
        
        SERIALIZERS.put(AttributeType.DYNAMIC_BYTE, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_SHORT, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_INT, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_LONG, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_FLOAT, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_DOUBLE, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_BIGINTEGER, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_BIGDECIMAL, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_CHAR, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_BOOLEAN, dynamicSerializer);
        SERIALIZERS.put(AttributeType.DYNAMIC_STRING, dynamicSerializer);
        SERIALIZERS.put(AttributeType.TIME_INTERVAL, dynamicSerializer);
        
        SERIALIZERS.put(AttributeType.LIST_BYTE, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_SHORT, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_INTEGER, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_LONG, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_FLOAT, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_DOUBLE, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_BIGINTEGER, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_BIGDECIMAL, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_CHARACTER, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_BOOLEAN, listSerializer);
        SERIALIZERS.put(AttributeType.LIST_STRING, listSerializer);
    }
    
    private static final Map<Class<?>, AttributeType> TYPES = new IdentityHashMap();
    static {
        TYPES.put(Byte.class, AttributeType.BYTE);
        TYPES.put(Short.class, AttributeType.SHORT);
        TYPES.put(Integer.class, AttributeType.INT);
        TYPES.put(Long.class, AttributeType.LONG);
        TYPES.put(Float.class, AttributeType.FLOAT);
        TYPES.put(Double.class, AttributeType.DOUBLE);
        TYPES.put(BigInteger.class, AttributeType.BIGINTEGER);
        TYPES.put(BigDecimal.class, AttributeType.BIGDECIMAL);
        TYPES.put(Boolean.class, AttributeType.BOOLEAN);
        TYPES.put(Character.class, AttributeType.CHAR);
        TYPES.put(String.class, AttributeType.STRING);
        
        TYPES.put(DynamicByte.class, AttributeType.DYNAMIC_BYTE);
        TYPES.put(DynamicShort.class, AttributeType.DYNAMIC_SHORT);
        TYPES.put(DynamicInteger.class, AttributeType.DYNAMIC_INT);
        TYPES.put(DynamicLong.class, AttributeType.DYNAMIC_LONG);
        TYPES.put(DynamicFloat.class, AttributeType.DYNAMIC_FLOAT);
        TYPES.put(DynamicDouble.class, AttributeType.DYNAMIC_DOUBLE);
        TYPES.put(DynamicString.class, AttributeType.DYNAMIC_STRING);
        TYPES.put(DynamicBoolean.class, AttributeType.DYNAMIC_BOOLEAN);
        TYPES.put(DynamicCharacter.class, AttributeType.DYNAMIC_CHAR);
        TYPES.put(DynamicBigInteger.class, AttributeType.DYNAMIC_BIGINTEGER);
        TYPES.put(DynamicBigDecimal.class, AttributeType.DYNAMIC_BIGDECIMAL);
        TYPES.put(TimeInterval.class, AttributeType.TIME_INTERVAL);
        
        TYPES.put(ByteList.class, AttributeType.LIST_BYTE);
        TYPES.put(ShortList.class, AttributeType.LIST_SHORT);
        TYPES.put(IntegerList.class, AttributeType.LIST_INTEGER);
        TYPES.put(LongList.class, AttributeType.LIST_LONG);
        TYPES.put(FloatList.class, AttributeType.LIST_FLOAT);
        TYPES.put(DoubleList.class, AttributeType.LIST_DOUBLE);
        TYPES.put(BigIntegerList.class, AttributeType.LIST_BIGINTEGER);
        TYPES.put(BigDecimalList.class, AttributeType.LIST_BIGDECIMAL);
        TYPES.put(BooleanList.class, AttributeType.LIST_BOOLEAN);
        TYPES.put(CharacterList.class, AttributeType.LIST_CHARACTER);
        TYPES.put(StringList.class, AttributeType.LIST_STRING);        
    }
    
    public byte[] writeValueData(Object value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        AttributeType type = TYPES.get(value.getClass());
        Serializer serializer = SERIALIZERS.get(type);

        if (serializer == null)
            throw new SerializationException("Unknown type");
        
        serializer.writeObjectData(dos, value);

        return baos.toByteArray();
    }
    
    public Object readValueData(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);

            Object val = null;
            byte type = dis.readByte();
                
            switch (type) {
                case Serializer.PRIMITIVE_TYPE: 
                    val = primitiveSerializer.readObjectData(dis); 
                    break;
                case Serializer.LIST_TYPE:
                    val = listSerializer.readObjectData(dis);
                    break;
                case Serializer.DYNAMIC_TYPE:
                    val = dynamicSerializer.readObjectData(dis);
                    break;
                default:
                    throw new SerializationException("Unknown type");
            }
                
            return val;
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
}
