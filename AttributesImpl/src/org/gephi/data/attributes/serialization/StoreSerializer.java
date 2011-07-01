package org.gephi.data.attributes.serialization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.gephi.data.attributes.type.AbstractList;
import org.gephi.data.attributes.type.BigDecimalList;
import org.gephi.data.attributes.type.BigIntegerList;
import org.gephi.data.attributes.type.ByteList;
import org.gephi.data.attributes.type.DoubleList;
import org.gephi.data.attributes.type.DynamicBigDecimal;
import org.gephi.data.attributes.type.DynamicBigInteger;
import org.gephi.data.attributes.type.DynamicByte;
import org.gephi.data.attributes.type.DynamicDouble;
import org.gephi.data.attributes.type.DynamicFloat;
import org.gephi.data.attributes.type.DynamicInteger;
import org.gephi.data.attributes.type.DynamicLong;
import org.gephi.data.attributes.type.DynamicShort;
import org.gephi.data.attributes.type.DynamicType;
import org.gephi.data.attributes.type.FloatList;
import org.gephi.data.attributes.type.IntegerList;
import org.gephi.data.attributes.type.LongList;
import org.gephi.data.attributes.type.ShortList;
import org.gephi.data.attributes.type.TimeInterval;

/**
 *
 * @author Ernesto A
 */
public class StoreSerializer {

    private StoreSerializer() throws IOException {
    }

    private void serializeByte(DataOutputStream dos, Byte b) throws IOException {
        dos.writeByte(b);
    }

    private void serializeShort(DataOutputStream dos, Short s) throws IOException {
        dos.writeShort(s);
    }

    private void serializeInteger(DataOutputStream dos, Integer i) throws IOException {
        dos.writeInt(i);
    }

    private void serializeLong(DataOutputStream dos, Long l) throws IOException {
        dos.writeLong(l);
    }

    private void serializeFloat(DataOutputStream dos, Float f) throws IOException {
        dos.writeFloat(f);
    }

    private void serializeDouble(DataOutputStream dos, Double d) throws IOException {
        dos.writeDouble(d);
    }

    private void serializeBigInteger(DataOutputStream dos, BigInteger bi) throws IOException {
        dos.write(bi.toByteArray());
    }

    private void serializeBigDecimal(DataOutputStream dos, BigDecimal bd) throws IOException {
        BigInteger unscaled = bd.unscaledValue();
        Integer scale = bd.scale();
        serializeBigInteger(dos, unscaled);
        serializeInteger(dos, scale);
    }

    
    
    
    private void serializeList(AbstractList list) {
    }

    private void serializeByteList(DataOutputStream dos, ByteList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeByte(list.getItem(i));
        }
    }

    private void serializeShortList(DataOutputStream dos, ShortList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeShort(list.getItem(i));
        }
    }

    private void serializeIntegerList(DataOutputStream dos, IntegerList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeInt(list.getItem(i));
        }
    }

    private void serializeLongList(DataOutputStream dos, LongList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeLong(list.getItem(i));
        }
    }

    private void serializeFloatList(DataOutputStream dos, FloatList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeFloat(list.getItem(i));
        }
    }

    private void serializeDoubleList(DataOutputStream dos, DoubleList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            dos.writeDouble(list.getItem(i));
        }
    }

    private void serializeBigIntegerList(DataOutputStream dos, BigIntegerList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            serializeBigInteger(dos, list.getItem(i));
        }
    }

    private void serializeBigDecimalList(DataOutputStream dos, BigDecimalList list) throws IOException {
        dos.writeInt(list.size());
        for (int i = 0; i < list.size(); i++) {
            serializeBigDecimal(dos, list.getItem(i));
        }
    }

    private void serializeDynamic(DynamicType dt) {
    }

    private void serializeDynamicByte(DataOutputStream dos, DynamicByte db) throws IOException {
    }

    private void serializeDynamicShort(DataOutputStream dos, DynamicShort ds) throws IOException {
    }

    private void serializeDynamicInteger(DataOutputStream dos, DynamicInteger di) throws IOException {
    }

    private void serializeDynamicLong(DataOutputStream dos, DynamicLong list) throws IOException {
    }

    private void serializeDynamicFloat(DataOutputStream dos, DynamicFloat df) throws IOException {
    }

    private void serializeDynamicDouble(DataOutputStream dos, DynamicDouble db) throws IOException {
    }

    private void serializeDynamicBigInteger(DataOutputStream dos, DynamicBigInteger dbi) throws IOException {
    }

    private void serializeDynamicBigDecimal(DataOutputStream dos, DynamicBigDecimal dbd) throws IOException {
    }

    private void serializeTimeInterval(DataOutputStream dos, TimeInterval ti) throws IOException {
    }
}
