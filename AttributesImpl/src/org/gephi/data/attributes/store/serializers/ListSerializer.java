package org.gephi.data.attributes.store.serializers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.type.*;

/**
 *
 * @author Ernesto A
 */
public class ListSerializer implements Serializer {
    
    private static final Map<Class<?>, AttributeType> LIST_TYPES = new HashMap<Class<?>, AttributeType>();
    static {
        LIST_TYPES.put(ByteList.class, AttributeType.LIST_BYTE);
        LIST_TYPES.put(ShortList.class, AttributeType.LIST_SHORT);
        LIST_TYPES.put(IntegerList.class, AttributeType.LIST_INTEGER);
        LIST_TYPES.put(LongList.class, AttributeType.LIST_LONG);
        LIST_TYPES.put(FloatList.class, AttributeType.LIST_FLOAT);
        LIST_TYPES.put(DoubleList.class, AttributeType.LIST_DOUBLE);
        LIST_TYPES.put(BigIntegerList.class, AttributeType.LIST_BIGINTEGER);
        LIST_TYPES.put(BigDecimalList.class, AttributeType.LIST_BIGDECIMAL);
        LIST_TYPES.put(BooleanList.class, AttributeType.LIST_BOOLEAN);
        LIST_TYPES.put(CharacterList.class, AttributeType.LIST_CHARACTER);
        LIST_TYPES.put(StringList.class, AttributeType.LIST_STRING);
    }
    
    public static AttributeType getListType(Object o) {
        return LIST_TYPES.get(o.getClass());
    }
    
    public void writeObjectData(DataOutputStream dos, Object o) {
        AttributeType type = getListType(o);
        
        if (type == null) 
            throw new RuntimeException("Class " + o.getClass().getName() + " is not a valid list class");
        
        AbstractList list = (AbstractList) o;
        
        try {
            dos.writeByte(LIST_TYPE);
        
            switch (type) {
                case LIST_BYTE:         serializeByteList(dos, (ByteList)list); break;
                case LIST_SHORT:        serializeShortList(dos, (ShortList)list); break;
                case LIST_INTEGER:      serializeIntegerList(dos, (IntegerList)list); break;
                case LIST_LONG:         serializeLongList(dos, (LongList)list); break;
                case LIST_FLOAT:        serializeFloatList(dos, (FloatList)list); break;
                case LIST_DOUBLE:       serializeDoubleList(dos, (DoubleList)list); break;
                case LIST_BIGINTEGER:   serializeBigIntegerList(dos, (BigIntegerList)list); break;
                case LIST_BIGDECIMAL:   serializeBigDecimalList(dos, (BigDecimalList)list); break;
                case LIST_BOOLEAN:      serializeBooleanList(dos, (BooleanList)list); break;    
                case LIST_CHARACTER:    serializeCharacterList(dos, (CharacterList)list); break;    
                case LIST_STRING:       serializeStringList(dos, (StringList)list); break;
                default:                throw new RuntimeException("Type is not a valid list type");
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void serializeByteList(DataOutputStream dos, ByteList list) throws IOException {
        dos.writeByte(BYTE_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeByte(list.getItem(i));
        }
    }

    
    private void serializeShortList(DataOutputStream dos, ShortList list) throws IOException {
        dos.writeByte(SHORT_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeShort(list.getItem(i));
        }
    }
    
    private void serializeIntegerList(DataOutputStream dos, IntegerList list) throws IOException {
        dos.writeByte(INTEGER_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeInt(list.getItem(i));
        }
    }

    private void serializeLongList(DataOutputStream dos, LongList list) throws IOException {
        dos.writeByte(LONG_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeLong(list.getItem(i));
        }
    }

    private void serializeFloatList(DataOutputStream dos, FloatList list) throws IOException {
        dos.writeByte(FLOAT_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeFloat(list.getItem(i));
        }
    }

    private void serializeDoubleList(DataOutputStream dos, DoubleList list) throws IOException {
        dos.writeByte(DOUBLE_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeDouble(list.getItem(i));
        }
    }

    private void serializeBigIntegerList(DataOutputStream dos, BigIntegerList list) throws IOException {
        dos.writeByte(BIGINTEGER_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            byte[] bytes = list.getItem(i).toByteArray();
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }

    private void serializeBigDecimalList(DataOutputStream dos, BigDecimalList list) throws IOException {
        dos.writeByte(BIGDECIMAL_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            byte[] bytes = list.getItem(i).unscaledValue().toByteArray();
            int scale = list.getItem(i).scale();

            dos.writeInt(scale);
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }
    
    private void serializeBooleanList(DataOutputStream dos, BooleanList list) throws IOException {
        dos.writeByte(BOOLEAN_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            boolean val = list.getItem(i);
            dos.writeByte((val) ? 1 : 0);
        }
    }

    private void serializeCharacterList(DataOutputStream dos, CharacterList list) throws IOException {
        dos.writeByte(CHARACTER_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i ++) {
            dos.writeChar(list.getItem(i));
        }
    }
    
    private void serializeStringList(DataOutputStream dos, StringList list) throws IOException {
        dos.writeByte(STRING_LIST);
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i ++) {
            byte[] bytes = list.getItem(i).getBytes();
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }
    
    
    public Object readObjectData(DataInputStream dis) {
        try {
            Object value = null;
            byte type = dis.readByte();
            
            switch (type) {
                case BYTE_LIST:         value = deserializeByteList(dis); break;
                case SHORT_LIST:        value = deserializeShortList(dis); break;
                case INTEGER_LIST:      value = deserializeIntegerList(dis); break;
                case LONG_LIST:         value = deserializeLongList(dis); break;
                case FLOAT_LIST:        value = deserializeFloatList(dis); break;
                case DOUBLE_LIST:       value = deserializeDoubleList(dis); break;
                case BIGINTEGER_LIST:   value = deserializeBigIntegerList(dis); break;
                case BIGDECIMAL_LIST:   value = deserializeBigDecimalList(dis); break;
                case BOOLEAN_LIST:      value = deserializeBooleanList(dis); break;    
                case CHARACTER_LIST:    value = deserializeCharacterList(dis); break;    
                case STRING_LIST:       value = deserializeStringList(dis); break;
                default:                throw new RuntimeException("Type is not a valid list type");
            }
            
            return value;
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private ByteList deserializeByteList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        byte[] values = new byte[len];
        for (int i = 0; i < len; i++) {
            values[i] = dis.readByte();
        }
        return new ByteList(values);
    }
    
    private ShortList deserializeShortList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        short[] values = new short[len];
        for (int i = 0; i < len; i++) {
            values[i] = dis.readShort();
        }
        return new ShortList(values);
    }

    private IntegerList deserializeIntegerList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        int[] values = new int[len];
        for (int i = 0; i < len; i ++) {
            values[i] = dis.readInt();
        }
        return new IntegerList(values);
    }

    private LongList deserializeLongList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        long[] values = new long[len];
        for (int i = 0; i < len; i++) {
            values[i] = dis.readLong();
        }
        return new LongList(values);
    }

    private FloatList deserializeFloatList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        float[] values = new float[len];
        for (int i = 0; i < len; i++) {
            values[i] = dis.readFloat();
        }
        return new FloatList(values);
    }

    private DoubleList deserializeDoubleList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        double[] values = new double[len];
        for (int i = 0; i < len; i++) {
            values[i] = dis.readDouble();
        }
        return new DoubleList(values);
    }

    private Object deserializeBigIntegerList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        BigInteger[] values = new BigInteger[len];
        for (int i = 0; i < len; i++) {
            int nbytes = dis.readInt();
            byte[] bytes = new byte[nbytes];
            dis.read(bytes);
            
            values[i] = new BigInteger(bytes);
        }
        return new BigIntegerList(values);
    }

    private BigDecimalList deserializeBigDecimalList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        BigDecimal[] values = new BigDecimal[len];
        for (int i = 0; i < len; i++) {
            int scale = dis.readInt();
            int nbytes = dis.readInt();
            byte[] bytes = new byte[nbytes];
            dis.read(bytes);
            
            BigInteger bi = new BigInteger(bytes);
            values[i] = new BigDecimal(bi, scale);
        }
        return new BigDecimalList(values);
    }

    private BooleanList deserializeBooleanList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        boolean[] values = new boolean[len];
        for (int i = 0; i < len; i++) {
            byte val = dis.readByte();
            values[i] = (val == 1) ? true : false;
        }
        return new BooleanList(values);
    }

    private CharacterList deserializeCharacterList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        Character[] values = new Character[len];
        for (int i = 0; i < len; i++) {
            char ch = dis.readChar();
            values[i] = Character.valueOf(ch);
        }
        return new CharacterList(values);
    }
    
    private StringList deserializeStringList(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        String[] values = new String[len];
        for (int i = 0; i < len; i++) {
            int nbytes = dis.readInt();
            byte[] bytes = new byte[nbytes];
            dis.read(bytes);
            
            values[i] = new String(bytes);
        }
        return new StringList(values);
    }
}
