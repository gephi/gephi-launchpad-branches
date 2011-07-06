package org.gephi.data.attributes.store.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import org.gephi.data.attributes.AttributeColumnImpl;
import org.gephi.data.attributes.AttributeValueImpl;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;

/**
 *
 * @author Ernesto A
 */
public class AttributeValueSerializer {
    
    private static final EnumMap<AttributeType, Serializer> SERIALIZERS = new EnumMap<AttributeType, Serializer>(AttributeType.class);
    private static final PrimitiveTypeSerializer primitiveSerializer = new PrimitiveTypeSerializer();
    private static final DynamicTypeSerializer dynamicSerializer = new DynamicTypeSerializer();
    private static final ListTypeSerializer listSerializer = new ListTypeSerializer();
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
    
    private final AttributeTable attributeTable;

    public AttributeValueSerializer(AttributeTable attributeTable) {
        this.attributeTable = attributeTable;
    }
    
    public byte[] writeValuesData(AttributeValue[] row) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
        
            // We need to serialize the size of the array (even if some elements
            // inside might be null and are not serialized), so that when deserialization
            // takes place the array returned is of the same size and contains the same
            // elements (actual objects and null values)
            dos.writeInt(row.length);
            
            // Placeholder for the actual total of non-null elements which will
            // be written to the array after they are serialized, to avoid a double pass
            // over the array
            int totalValues = 0;
            dos.writeInt(totalValues);
            
            for (AttributeValue value : row) {
                if (value == null || value.getValue() == null) continue;
                
                AttributeColumn column = value.getColumn();
                AttributeType type = column.getType();
                Serializer serializer = SERIALIZERS.get(type);
                
                // Object's data
                dos.writeInt(column.getIndex());
                serializer.writeObjectData(dos, value.getValue());
                totalValues++;
            }
            
            byte[] arr = baos.toByteArray();
            // Write total number of persisted objects
            ByteBuffer.wrap(arr).putInt(4, totalValues);
            
            return baos.toByteArray();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public AttributeValue[] readValuesData(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            
            // Size of the array
            int arrayLength = dis.readInt();
            AttributeValueImpl[] vals = new AttributeValueImpl[arrayLength];
            
            // Total of non-null values
            int totalValues = dis.readInt();
            
            for (int i = 0; i < totalValues; i++) {
                // Object's data
                int columnIndex = dis.readInt();
                Object value = null;
                byte type = dis.readByte();
                
                switch (type) {
                    case Serializer.PRIMITIVE_TYPE: 
                        value = primitiveSerializer.readObjectData(dis); 
                        break;
                    case Serializer.LIST_TYPE:
                        value = listSerializer.readObjectData(dis);
                        break;
                    case Serializer.DYNAMIC_TYPE:
                        value = dynamicSerializer.readObjectData(dis);
                        break;
                    default:
                        throw new SerializationException("Unknown type");
                }
                
                AttributeColumn column = attributeTable.getColumn(columnIndex);
                vals[columnIndex] = new AttributeValueImpl((AttributeColumnImpl)column, value);
            }

            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == null) {
                    AttributeColumn column = attributeTable.getColumn(i);
                    vals[i] = new AttributeValueImpl((AttributeColumnImpl)column, column.getDefaultValue());
                }
            }
                        
            return vals;
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
}
