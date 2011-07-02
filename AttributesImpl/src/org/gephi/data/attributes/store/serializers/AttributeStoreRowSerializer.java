package org.gephi.data.attributes.store.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;

/**
 *
 * @author Ernesto A
 */
public class AttributeStoreRowSerializer {
    
    private final EnumMap<AttributeType, Serializer> SERIALIZERS = new EnumMap<AttributeType, Serializer>(AttributeType.class);
    private final PrimitiveTypeSerializer primitiveSerializer = new PrimitiveTypeSerializer();
    private final DynamicTypeSerializer dynamicSerializer = new DynamicTypeSerializer();
    private final ListTypeSerializer listSerializer = new ListTypeSerializer();
    
    public AttributeStoreRowSerializer() {
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
    
    public byte[] writeRowData(AttributeRow row) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
        
            // Total values
            int totalValues = row.countValues();
            dos.writeInt(totalValues);
        
            for (AttributeValue value : row.getValues()) {
                AttributeType type = value.getColumn().getType();
                Serializer serializer = SERIALIZERS.get(type);
                
                // Column index
                dos.writeInt(value.getColumn().getIndex());
                
                // Object's data
                serializer.writeObjectData(dos, value.getValue());
            }
            
            return baos.toByteArray();
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
    
    public AttributeRow readRowData(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            
            // Total values
            int totalValues = dis.readInt();
            for (int i = 0; i < totalValues; i++) {
                // Column index
                int columnIndex = dis.readInt();
                
                // Object's data
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
            }
            
            return null;
            
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }
}
