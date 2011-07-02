package org.gephi.data.attributes.store.serializers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeType;
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
import org.gephi.data.attributes.type.DynamicType;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.attributes.type.TimeInterval;

/**
 *
 * @author Ernesto A
 */
public class DynamicTypeSerializer implements Serializer {

    private static final Map<Class<?>, AttributeType> DYNAMIC_CLASSES = new IdentityHashMap<Class<?>, AttributeType>();
    static {
        DYNAMIC_CLASSES.put(DynamicByte.class, AttributeType.DYNAMIC_BYTE);
        DYNAMIC_CLASSES.put(DynamicShort.class, AttributeType.DYNAMIC_SHORT);
        DYNAMIC_CLASSES.put(DynamicInteger.class, AttributeType.DYNAMIC_INT);
        DYNAMIC_CLASSES.put(DynamicLong.class, AttributeType.DYNAMIC_LONG);
        DYNAMIC_CLASSES.put(DynamicFloat.class, AttributeType.DYNAMIC_FLOAT);
        DYNAMIC_CLASSES.put(DynamicDouble.class, AttributeType.DYNAMIC_DOUBLE);
        DYNAMIC_CLASSES.put(DynamicString.class, AttributeType.DYNAMIC_STRING);
        DYNAMIC_CLASSES.put(DynamicBoolean.class, AttributeType.DYNAMIC_BOOLEAN);
        DYNAMIC_CLASSES.put(DynamicCharacter.class, AttributeType.DYNAMIC_CHAR);
        DYNAMIC_CLASSES.put(DynamicBigInteger.class, AttributeType.DYNAMIC_BIGINTEGER);
        DYNAMIC_CLASSES.put(DynamicBigDecimal.class, AttributeType.DYNAMIC_BIGDECIMAL);
        DYNAMIC_CLASSES.put(TimeInterval.class, AttributeType.TIME_INTERVAL);
    }
    
    public static AttributeType getDynamicTypeFor(Object o) {
        return DYNAMIC_CLASSES.get(o.getClass());
    }
    
    public void writeObjectData(DataOutputStream dos, Object o) {
        AttributeType type = getDynamicTypeFor(o);
        
        if (type == null)
            throw new SerializationException("Class " + o.getClass().getName() + " is not a valid dynamic type");
        
        DynamicType value = (DynamicType)o;
        
        try {
            dos.writeByte(DYNAMIC_TYPE);
            
            switch (type) {
                case DYNAMIC_BYTE:          serializeDynamicByte(dos, (DynamicByte)value); break;
                case DYNAMIC_SHORT:         serializeDynamicShort(dos, (DynamicShort)value); break; 
                case DYNAMIC_INT:           serializeDynamicInteger(dos, (DynamicInteger)value); break;
                case DYNAMIC_LONG:          serializeDynamicLong(dos, (DynamicLong)value); break;
                case DYNAMIC_FLOAT:         serializeDynamicFloat(dos, (DynamicFloat)value); break;
                case DYNAMIC_DOUBLE:        serializeDynamicDouble(dos, (DynamicDouble)value); break;
                case DYNAMIC_BIGINTEGER:    serializeDynamicBigInteger(dos, (DynamicBigInteger)value); break;
                case DYNAMIC_BIGDECIMAL:    serializeDynamicBigDecimal(dos, (DynamicBigDecimal)value); break;
                case DYNAMIC_BOOLEAN:       serializeDynamicBoolean(dos, (DynamicBoolean)value); break;
                case DYNAMIC_CHAR:          serializeDynamicChar(dos, (DynamicCharacter)value); break;
                case DYNAMIC_STRING:        serializeDynamicString(dos, (DynamicString)value); break;
                case TIME_INTERVAL:         serializeTimeInterval(dos, (TimeInterval)value); break;
                default:                    throw new SerializationException("Type is not a valid dynamic type");
            }
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }

    private void serializeDynamicByte(DataOutputStream dos, DynamicByte value) throws IOException { 
        dos.writeByte(DYNAMIC_BYTE);
        List<Interval<Byte>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Byte> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Byte b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeByte(b);
        }
    }

    private void serializeDynamicShort(DataOutputStream dos, DynamicShort value) throws IOException { 
        dos.writeByte(DYNAMIC_SHORT);
        List<Interval<Short>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Short> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Short b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeShort(b);
        }
    }

    private void serializeDynamicInteger(DataOutputStream dos, DynamicInteger value) throws IOException { 
        dos.writeByte(DYNAMIC_INTEGER);
        List<Interval<Integer>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Integer> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Integer b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeInt(b);
        }
    }

    private void serializeDynamicLong(DataOutputStream dos, DynamicLong value) throws IOException { 
        dos.writeByte(DYNAMIC_LONG);
        List<Interval<Long>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Long> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Long b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeLong(b);
        }
    }

    private void serializeDynamicFloat(DataOutputStream dos, DynamicFloat value) throws IOException { 
        dos.writeByte(DYNAMIC_FLOAT);
        List<Interval<Float>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Float> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Float b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeFloat(b);
        }
    }

    private void serializeDynamicDouble(DataOutputStream dos, DynamicDouble value) throws IOException { 
        dos.writeByte(DYNAMIC_DOUBLE);
        List<Interval<Double>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Double> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Double b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeDouble(b);
        }
    }

    private void serializeDynamicBigInteger(DataOutputStream dos, DynamicBigInteger value) throws IOException { 
        dos.writeByte(DYNAMIC_BIGINTEGER);
        List<Interval<BigInteger>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<BigInteger> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            BigInteger b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            
            byte[] bytes = b.toByteArray();
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }

    private void serializeDynamicBigDecimal(DataOutputStream dos, DynamicBigDecimal value) throws IOException { 
        dos.writeByte(DYNAMIC_BIGDECIMAL);
        List<Interval<BigDecimal>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<BigDecimal> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            BigDecimal b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            
            byte[] unscaled = b.unscaledValue().toByteArray();
            int scale = b.scale();
            dos.writeInt(scale);
            dos.writeInt(unscaled.length);
            dos.write(unscaled);
        }
    }

    private void serializeDynamicBoolean(DataOutputStream dos, DynamicBoolean value) throws IOException { 
        dos.writeByte(DYNAMIC_BOOLEAN);
        List<Interval<Boolean>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Boolean> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Boolean b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeByte((b) ? 1 : 0);
        }
    }

    private void serializeDynamicChar(DataOutputStream dos, DynamicCharacter value) throws IOException { 
        dos.writeByte(DYNAMIC_CHARACTER);
        List<Interval<Character>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Character> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            Character b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            dos.writeChar(b);
        }
    }

    private void serializeDynamicString(DataOutputStream dos, DynamicString value) throws IOException { 
        dos.writeByte(DYNAMIC_STRING);
        List<Interval<String>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<String> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            String b = interval.getValue();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
            
            byte[] bytes = b.getBytes();
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }
    
    private void serializeTimeInterval(DataOutputStream dos, TimeInterval value) throws IOException {
        dos.writeByte(TIME_INTERVAL);
        List<Interval<Double[]>> intervals = value.getIntervals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        dos.writeInt(intervals.size());
        for (int i = 0; i < intervals.size(); i++) {
            Interval<Double[]> interval = intervals.get(i);
            double low = interval.getLow();
            double high = interval.getHigh();
            
            dos.writeByte(encodeEndpoints(interval));
            dos.writeDouble(low);
            dos.writeDouble(high);
        }        
    }

    public Object readObjectData(DataInputStream dis) {
        try {
            Object value = null;
            byte type = dis.readByte();
            
            switch (type) {
                case DYNAMIC_BYTE:          value = deserializeDynamicByte(dis); break;
                case DYNAMIC_SHORT:         value = deserializeDynamicShort(dis); break;
                case DYNAMIC_INTEGER:       value = deserializeDynamicInteger(dis); break;
                case DYNAMIC_LONG:          value = deserializeDynamicLong(dis); break;
                case DYNAMIC_FLOAT:         value = deserializeDynamicFloat(dis); break;
                case DYNAMIC_DOUBLE:        value = deserializeDynamicDouble(dis); break;
                case DYNAMIC_BIGINTEGER:    value = deserializeDynamicBigInteger(dis); break;
                case DYNAMIC_BIGDECIMAL:    value = deserializeDynamicBigDecimal(dis); break;
                case DYNAMIC_BOOLEAN:       value = deserializeDynamicBoolean(dis); break;
                case DYNAMIC_CHARACTER:     value = deserializeDynamicChar(dis); break;
                case DYNAMIC_STRING:        value = deserializeDynamicString(dis); break;
                case TIME_INTERVAL:         value = deserializeTimeInterval(dis); break;
                default:                    throw new SerializationException("Type is not a valid dynamic type");
            }
            
            return value;
        }
        catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }

    private DynamicByte deserializeDynamicByte(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Byte>> intervals = new ArrayList<Interval<Byte>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            byte val = dis.readByte();
            Interval<Byte> interval = new Interval<Byte>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicByte(intervals);
    }

    private DynamicShort deserializeDynamicShort(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Short>> intervals = new ArrayList<Interval<Short>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            short val = dis.readShort();
            Interval<Short> interval = new Interval<Short>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicShort(intervals);
    }

    private DynamicInteger deserializeDynamicInteger(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            int val = dis.readInt();
            Interval<Integer> interval = new Interval<Integer>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicInteger(intervals);
    }

    private DynamicLong deserializeDynamicLong(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Long>> intervals = new ArrayList<Interval<Long>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            long val = dis.readLong();
            Interval<Long> interval = new Interval<Long>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicLong(intervals);
    }

    private DynamicFloat deserializeDynamicFloat(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Float>> intervals = new ArrayList<Interval<Float>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            float val = dis.readFloat();
            Interval<Float> interval = new Interval<Float>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicFloat(intervals);
    }

    private DynamicDouble deserializeDynamicDouble(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Double>> intervals = new ArrayList<Interval<Double>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            double val = dis.readDouble();
            Interval<Double> interval = new Interval<Double>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicDouble(intervals);
    }

    private DynamicBigInteger deserializeDynamicBigInteger(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<BigInteger>> intervals = new ArrayList<Interval<BigInteger>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            
            int nbytes = dis.readInt();
            byte[] bytes = new byte[nbytes];
            dis.read(bytes);
            
            BigInteger val = new BigInteger(bytes);
            Interval<BigInteger> interval = new Interval<BigInteger>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicBigInteger(intervals);
    }

    private DynamicBigDecimal deserializeDynamicBigDecimal(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<BigDecimal>> intervals = new ArrayList<Interval<BigDecimal>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            
            int scale = dis.readInt();
            int nbytes = dis.readInt();
            byte[] unscaled = new byte[nbytes];
            dis.read(unscaled);
            
            BigInteger bi = new BigInteger(unscaled);
            BigDecimal val = new BigDecimal(bi, scale);
            Interval<BigDecimal> interval = new Interval<BigDecimal>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicBigDecimal(intervals);
    }

    private DynamicBoolean deserializeDynamicBoolean(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Boolean>> intervals = new ArrayList<Interval<Boolean>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            boolean val = (dis.readByte() == 1 ? true : false);
            
            Interval<Boolean> interval = new Interval<Boolean>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicBoolean(intervals);
    }

    private DynamicCharacter deserializeDynamicChar(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<Character>> intervals = new ArrayList<Interval<Character>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            char val = dis.readChar();
            
            Interval<Character> interval = new Interval<Character>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicCharacter(intervals);
    }

    private DynamicString deserializeDynamicString(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval<String>> intervals = new ArrayList<Interval<String>>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            
            int nbytes = dis.readInt();
            byte[] bytes = new byte[nbytes];
            dis.read(bytes);
            String val = new String(bytes);
            
            Interval<String> interval = new Interval<String>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints), val);
            intervals.add(interval);
        }
        return new DynamicString(intervals);
    }
    
    private TimeInterval deserializeTimeInterval(DataInputStream dis) throws IOException {
        int size = dis.readInt();
        List<Interval> intervals = new ArrayList<Interval>(size);
        for (int i = 0; i < size; i++) {
            byte endpoints = dis.readByte();
            double low = dis.readDouble();
            double high = dis.readDouble();
            
            Interval interval = new Interval<String>(low, high, isLowExcluded(endpoints), isHighExcluded(endpoints));
            intervals.add(interval);
        }
        return new TimeInterval(intervals);
    }

    private static byte encodeEndpoints(Interval interval) {
        byte val = 0;
        if (interval.isLowExcluded()) val |= 0x0F;
        if (interval.isHighExcluded()) val |= 0xF0;
        return val;
    }
    
    private static boolean isLowExcluded(byte val) {
        return (val & 0x0F) == 0x0F;
    }
    
    private static boolean isHighExcluded(byte val) {
        return (val & 0xF0) == 0xF0;
    }
}