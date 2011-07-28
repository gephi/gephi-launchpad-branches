package org.gephi.data.store.attributes.serializers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeType;

/**
 *
 * @author Ernesto A
 */
public class PrimitiveTypeSerializer implements Serializer {

    private static final Map<Class<?>, AttributeType> PRIMITIVE_TYPES = new IdentityHashMap();
    static {
        PRIMITIVE_TYPES.put(Byte.class, AttributeType.BYTE);
        PRIMITIVE_TYPES.put(Short.class, AttributeType.SHORT);
        PRIMITIVE_TYPES.put(Integer.class, AttributeType.INT);
        PRIMITIVE_TYPES.put(Long.class, AttributeType.LONG);
        PRIMITIVE_TYPES.put(Float.class, AttributeType.FLOAT);
        PRIMITIVE_TYPES.put(Double.class, AttributeType.DOUBLE);
        PRIMITIVE_TYPES.put(BigInteger.class, AttributeType.BIGINTEGER);
        PRIMITIVE_TYPES.put(BigDecimal.class, AttributeType.BIGDECIMAL);
        PRIMITIVE_TYPES.put(Boolean.class, AttributeType.BOOLEAN);
        PRIMITIVE_TYPES.put(Character.class, AttributeType.CHAR);
        PRIMITIVE_TYPES.put(String.class, AttributeType.STRING);
    }
    
    public static AttributeType getPrimitiveTypeFor(Object o) {
        return PRIMITIVE_TYPES.get(o.getClass());
    }
    
    public void writeObjectData(DataOutputStream dos, Object o) {
        AttributeType type = getPrimitiveTypeFor(o);
        
        if (type == null)
            throw new SerializationException("Class " + o.getClass().getName() + " is not a valid primitive type");
        
        try {
            dos.writeByte(PRIMITIVE_TYPE);
            
            switch (type) {
                case BYTE:
                    dos.writeByte(BYTE);
                    dos.writeByte((Byte)o);
                    break;
                case SHORT:
                    dos.writeByte(SHORT);
                    dos.writeShort((Short)o);
                    break;
                case INT:
                    dos.writeByte(INTEGER);
                    dos.writeInt((Integer)o);
                    break;
                case LONG:
                    dos.writeByte(LONG);
                    dos.writeLong((Long)o);
                    break;
                case FLOAT:
                    dos.writeByte(FLOAT);
                    dos.writeFloat((Float)o);
                    break;
                case DOUBLE:
                    dos.writeByte(DOUBLE);
                    dos.writeDouble((Double)o);
                    break;
                case BIGINTEGER:
                    dos.writeByte(BIG_INTEGER);
                    byte[] b1 = ((BigInteger)o).toByteArray();
                    dos.writeInt(b1.length);
                    dos.write(b1);
                    break;
                case BIGDECIMAL:
                    dos.writeByte(BIG_DECIMAL);
                    int scale = ((BigDecimal)o).scale();
                    byte[] unscaled = ((BigDecimal)o).unscaledValue().toByteArray();
                    dos.writeInt(scale);
                    dos.writeInt(unscaled.length);
                    dos.write(unscaled);
                    break;
                case BOOLEAN:
                    dos.writeByte(BOOLEAN);
                    dos.writeBoolean((Boolean)o);
                    break;
                case CHAR:
                    dos.writeByte(CHARACTER);
                    dos.writeChar((Character)o);
                    break;
                case STRING:
                    dos.writeByte(STRING);
                    byte[] b2 = ((String)o).getBytes();
                    dos.writeInt(b2.length);
                    dos.write(b2);
                    break;
                default:
                    throw new SerializationException("Type is not a valid primitive type");
            }
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }

    public Object readObjectData(DataInputStream dis) {
        try {
            Object value = null;
            byte type = dis.readByte();
            
            switch (type) {
                case BYTE:
                    value = (Byte)dis.readByte();
                    break;
                case SHORT:
                    value = (Short)dis.readShort();
                    break;
                case INTEGER:
                    value = (Integer)dis.readInt();
                    break;
                case LONG:
                    value = (Long)dis.readLong();
                    break;
                case FLOAT:
                    value = (Float)dis.readFloat();
                    break;
                case DOUBLE:
                    value = (Double)dis.readDouble();
                    break;
                case BIG_INTEGER:
                    int biLength = dis.readInt();
                    byte[] biBytes = new byte[biLength];
                    dis.read(biBytes);
                    value = new BigInteger(biBytes);
                    break;
                case BIG_DECIMAL:
                    int bdScale = dis.readInt();
                    int bdLength = dis.readInt();
                    byte[] bdBytes = new byte[bdLength];
                    dis.read(bdBytes);
                    BigInteger unscaled = new BigInteger(bdBytes);
                    value = new BigDecimal(unscaled, bdScale);
                    break;
                case BOOLEAN:
                    value = (Boolean)dis.readBoolean();
                    break;
                case CHARACTER:
                    value = (Character)dis.readChar();
                    break;
                case STRING:
                    int strLength = dis.readInt();
                    byte[] strBytes = new byte[strLength];
                    dis.read(strBytes);
                    value = new String(strBytes);
                    break;
                default:
                    throw new SerializationException("Type is not a valid primitive type");
            }
            
            return value;
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
}
