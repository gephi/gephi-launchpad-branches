package org.gephi.data.attributes.store.serializers;

import org.gephi.data.store.attributes.serializers.DynamicTypeSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
import org.gephi.data.attributes.type.DynamicType;
import org.gephi.data.attributes.type.Interval;
import org.gephi.data.attributes.type.TimeInterval;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ernesto A
 */
public class DynamicTypeSerializerTest {
    
    public static final int MAX_ELEMENTS = 10000;
    
    @Test
    public void testDynamicByte() {
        testDynamicType(Byte.class);
    }

    @Test
    public void testDynamicShort() {
        testDynamicType(Short.class);
    }
    
    @Test
    public void testDynamicInteger() {
        testDynamicType(Integer.class);
    }
    
    @Test
    public void testDynamicLong() {
        testDynamicType(Long.class);
    }
    
    @Test
    public void testDynamicFloat() {
        testDynamicType(Float.class);
    }
    
    @Test
    public void testDynamicDouble() {
        testDynamicType(Double.class);
    }
    
    @Test
    public void testDynamicBigDecimal() {
        testDynamicType(BigDecimal.class);
    }
    
    @Test
    public void testDynamicBigInteger() {
        testDynamicType(BigInteger.class);
    }
    
    @Test
    public void testDynamicCharacter() {
        testDynamicType(Character.class);
    }
    
    @Test
    public void testDynamicBoolean() {
        testDynamicType(Boolean.class);
    }
    
    @Test
    public void testDynamicString() {
        testDynamicType(String.class);
    }
    
    @Test
    public void testTimeInterval() {
        testDynamicType(TimeInterval.class);
    }
    
    private void testDynamicType(Class typeClass) {
        System.out.println("testDynamic" + typeClass.getSimpleName());
        
        DynamicTypeSerializer serializer = new DynamicTypeSerializer();
        
        DynamicType expected = newDynamicType(typeClass);
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    private Object doRoundTrip(DynamicTypeSerializer serializer, Object expected) {
        byte[] bytes = doSerialization(serializer, expected);
        Object actual = doDeserialization(serializer, bytes);
        return actual;
    }
    
    private byte[] doSerialization(DynamicTypeSerializer serializer, Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        serializer.writeObjectData(os, o);
        return baos.toByteArray();
    }
    
    private Object doDeserialization(DynamicTypeSerializer serializer, byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        
        // skip the first byte which is always the type byte (list, dynamic, primitive)
        try { dis.readByte(); }
        catch(IOException ex) { fail(ex.getMessage()); }
        
        return serializer.readObjectData(dis);
    }
    
    private <T> DynamicType<T> newDynamicType(Class<T> typeClass) {
        DynamicType val = null;
        
        if (typeClass == Byte.class) {
            List<Interval<Byte>> intervals = getIntervalListOf(Byte.class, MAX_ELEMENTS);
            val = new DynamicByte(intervals);
        } else if (typeClass == Short.class) {
            List<Interval<Short>> intervals = getIntervalListOf(Short.class, MAX_ELEMENTS);
            val = new DynamicShort(intervals);
        } else if (typeClass == Integer.class) {
            List<Interval<Integer>> intervals = getIntervalListOf(Integer.class, MAX_ELEMENTS);
            val = new DynamicInteger(intervals);
        } else if (typeClass == Long.class) {
            List<Interval<Long>> intervals = getIntervalListOf(Long.class, MAX_ELEMENTS);
            val = new DynamicLong(intervals);
        } else if (typeClass == Float.class) {
            List<Interval<Float>> intervals = getIntervalListOf(Float.class, MAX_ELEMENTS);
            val = new DynamicFloat(intervals);
        } else if (typeClass == Double.class) {
            List<Interval<Double>> intervals = getIntervalListOf(Double.class, MAX_ELEMENTS);
            val = new DynamicDouble(intervals);
        } else if (typeClass == BigInteger.class) {
            List<Interval<BigInteger>> intervals = getIntervalListOf(BigInteger.class, MAX_ELEMENTS);
            val = new DynamicBigInteger(intervals);
        } else if (typeClass == BigDecimal.class) {
            List<Interval<BigDecimal>> intervals = getIntervalListOf(BigDecimal.class, MAX_ELEMENTS);
            val = new DynamicBigDecimal(intervals);
        } else if (typeClass == Boolean.class) {
            List<Interval<Boolean>> intervals = getIntervalListOf(Boolean.class, MAX_ELEMENTS);
            val = new DynamicBoolean(intervals);
        } else if (typeClass == Character.class) {
            List<Interval<Character>> intervals = getIntervalListOf(Character.class, MAX_ELEMENTS);
            val = new DynamicCharacter(intervals);
        } else if (typeClass == String.class) {
            List<Interval<Byte>> intervals = getIntervalListOf(Byte.class, MAX_ELEMENTS);
            val = new DynamicByte(intervals);
        } else if (typeClass == TimeInterval.class) {
            List<Interval> intervals = new ArrayList<Interval>();
            for (int j = 0; j < MAX_ELEMENTS; j++) {
                intervals.add(getRandomIntervalOf(typeClass));
            }
            val = new TimeInterval(intervals);
        }
        
        return val;
    }
    
    private <T> List<Interval<T>> getIntervalListOf(Class<T> typeClass, int length) {
        List<Interval<T>> intervals = new ArrayList<Interval<T>>();
        for (int i = 0; i < length; i++) {
            intervals.add(getRandomIntervalOf(typeClass));
        }
        return intervals;
    }
    
    private <T> Interval<T> getRandomIntervalOf(Class<T> typeClass) {
        double low = 1000 * Math.random();
        double high = 1001 + 1000 * Math.random();
        boolean lopen = Double.compare(Math.random(), 0.5d) <= 0;
        boolean ropen = Double.compare(Math.random(), 0.5d) <= 0;
        Object value = null;
        
        if (typeClass == Byte.class) {
            value = (byte)(Byte.MAX_VALUE * Math.random() + Byte.MIN_VALUE * Math.random());
        }
        else if (typeClass == Short.class) {
            value = (short)(Short.MAX_VALUE * Math.random() + Short.MIN_VALUE * Math.random());
        }
        else if (typeClass == Integer.class) {
            value = (int)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
        }
        else if (typeClass == Long.class) {
            value = (long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random());
        }
        else if (typeClass == Float.class) {
            value = (float)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
        }
        else if (typeClass == Double.class) {
            value = Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random();
        }
        else if (typeClass == BigInteger.class) {
            long l = (long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random());
            value = new BigInteger(String.valueOf(l));
        }
        else if (typeClass == BigDecimal.class) {
            double d  = Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random();
            value = new BigDecimal(d);
        }
        else if (typeClass == Boolean.class) {
            value = Double.compare(Math.random(), 0.5d) <= 0;
        }
        else if (typeClass == Character.class) {
            value = generateRandomString(128).charAt((int)(128 * Math.random()));
        }
        else if (typeClass == String.class) {
            value = generateRandomString(256);
        }
        else if (typeClass == TimeInterval.class) {
            value = null;
        }
        
        if (value == null)
            return new Interval<T>(low, high, lopen, ropen);
        else
            return new Interval<T>(low, high, lopen, ropen, (T)value);
    }
    
    private String generateRandomString(int length) {
        char[] alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890~!@#$%^&*()_+{}|:<>?[];',./'".toCharArray();
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(alpha.length * Math.random());
            sb.append(alpha[index]);
        }
        return sb.toString();
    }    
}
