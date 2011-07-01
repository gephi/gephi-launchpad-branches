package org.gephi.data.attributes.store.serializers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.gephi.data.attributes.type.BigDecimalList;
import org.gephi.data.attributes.type.BigIntegerList;
import org.gephi.data.attributes.type.BooleanList;
import org.gephi.data.attributes.type.ByteList;
import org.gephi.data.attributes.type.CharacterList;
import org.gephi.data.attributes.type.DoubleList;
import org.gephi.data.attributes.type.FloatList;
import org.gephi.data.attributes.type.IntegerList;
import org.gephi.data.attributes.type.LongList;
import org.gephi.data.attributes.type.ShortList;
import org.gephi.data.attributes.type.StringList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ernesto A
 */
public class ListSerializerTest {
    
    public static final int MAX_ELEMENTS = 1000;
    
    public ListSerializerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testByteList() {
        System.out.println("testByteList");
        
        ListSerializer serializer = new ListSerializer();
        
        ByteList expected = new ByteList(newByteArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testShortList() {
        System.out.println("testShortList");
        
        ListSerializer serializer = new ListSerializer();
        
        ShortList expected = new ShortList(newShortArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testIntegerList() {
        System.out.println("testIntegerList");
        
        ListSerializer serializer = new ListSerializer();
        
        IntegerList expected = new IntegerList(newIntArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testLongList() {
        System.out.println("testLongList");
        
        ListSerializer serializer = new ListSerializer();
        
        LongList expected = new LongList(newLongArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testFloatList() {
        System.out.println("testFloatList");
        
        ListSerializer serializer = new ListSerializer();
        
        FloatList expected = new FloatList(newFloatArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testDoubleList() {
        System.out.println("testDoubleList");
        
        ListSerializer serializer = new ListSerializer();
                
        DoubleList expected = new DoubleList(newDoubleArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testBigIntegerList() {
        System.out.println("testBigIntegerList");
        
        ListSerializer serializer = new ListSerializer();
        
        BigIntegerList expected = new BigIntegerList(newBigIntegerArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testBigDecimalList() {
        System.out.println("testBigDecimalList");
        
        ListSerializer serializer = new ListSerializer();
        
        BigDecimalList expected = new BigDecimalList(newBigDecimalArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testBooleanList() {
        System.out.println("testBooleanList");
        
        ListSerializer serializer = new ListSerializer();
        
        BooleanList expected = new BooleanList(newBooleanArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
                
        assertEquals(expected, actual);
    }
    
    @Test
    public void testCharacterList() {
        System.out.println("testCharacterList");
        
        ListSerializer serializer = new ListSerializer();
        
        CharacterList expected = new CharacterList(newCharArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testStringList() {
        System.out.println("testStringList");
        
        ListSerializer serializer = new ListSerializer();
        
        StringList expected = new StringList(newStringArray(MAX_ELEMENTS));
        Object actual = doRoundTrip(serializer, expected);
        
        assertEquals(expected, actual);
    }
    
    private Object doRoundTrip(ListSerializer serializer, Object expected) {
        byte[] bytes = doSerialization(serializer, expected);
        Object actual = doDeserialization(serializer, bytes);
        return actual;
    }
    private byte[] doSerialization(ListSerializer serializer, Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        serializer.writeObjectData(os, o);
        return baos.toByteArray();
    }
    
    private Object doDeserialization(ListSerializer serializer, byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        
        // skip the first byte which is always the type byte (list, dynamic, primitive)
        try { dis.readByte(); }
        catch(IOException ex) { fail(ex.getMessage()); }
        
        return serializer.readObjectData(dis);
    }
    
    private byte[] newByteArray(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = (byte)(Byte.MAX_VALUE * Math.random() + Byte.MIN_VALUE * Math.random());
        return bytes;
    }
    
    private short[] newShortArray(int length) {
        short[] shorts = new short[length];
        for (int i = 0; i < length; i++)
            shorts[i] = (short)(Short.MAX_VALUE * Math.random() + Short.MIN_VALUE * Math.random());
        return shorts;
    }
    
    private int[] newIntArray(int length) {
        int[] ints = new int[length];
        for (int i = 0; i < length; i++)
            ints[i] = (int)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
        return ints;
    }
    
    private long[] newLongArray(int length) {
        long[] longs = new long[length];
        for (int i = 0; i < length; i++)
            longs[i] = (long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random());
        return longs;
    }
    
    private float[] newFloatArray(int length) {
        float[] floats = new float[length];
        for (int i = 0; i < length; i++)
            floats[i] = (float)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
        return floats;
    }
    
    private double[] newDoubleArray(int length) {
        double[] doubles = new double[length];
        for (int i = 0; i < length; i++)
            doubles[i] = Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random();
        return doubles;
    }
    
    private BigInteger[] newBigIntegerArray(int length) {
        BigInteger[] values = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            long l = (long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random());
            values[i] = new BigInteger(String.valueOf(l));
        }
        return values;
    }
    
    private BigDecimal[] newBigDecimalArray(int length) {
        BigDecimal[] values = new BigDecimal[length];
        for (int i = 0; i < length; i++) {
            double d  = Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random();
            values[i] = new BigDecimal(d);
        }
        return values;
    }
    
    private boolean[] newBooleanArray(int length) {
        boolean[] bools = new boolean[length];
        for (int i = 0; i < length; i++) {
            double rand = Math.random();
            bools[i] = Double.compare(rand, 0.5d) <= 0;
        }
        return bools;
    }
    
    private char[] newCharArray(int length) {
        char[] chars = generateRandomString(length).toCharArray();
        return chars;
    }
    
    private String[] newStringArray(int length) {
        String[] strs = new String[length];
        for (int i = 0; i < length; i++) {
            strs[i] = generateRandomString(256);
        }
        return strs;
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
