package org.gephi.data.attributes.store.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ernesto A
 */
public class PrimitiveTypeSerializerTest {
    
    private static final int MAX_ELEMENTS = 100000;
    
    /**
     * Test of writeObjectData method, of class PrimitiveTypeSerializer.
     */
    @Test
    public void testReadWriteObjectData() {
        Object[] vals = getRandomObjectArray();
        
        PrimitiveTypeSerializer serializer = new PrimitiveTypeSerializer();
        
        for(Object expected : vals) {
            Object actual = doRoundTrip(serializer, expected);
            assertEquals(expected, actual);
        }
    }
    
    private Object doRoundTrip(Serializer serializer, Object expected) {
        byte[] bytes = doSerialization(serializer, expected);
        Object actual = doDeserialization(serializer, bytes);
        return actual;
    }
    
    private byte[] doSerialization(Serializer serializer, Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        serializer.writeObjectData(os, o);
        return baos.toByteArray();
    }
    
    private Object doDeserialization(Serializer serializer, byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        
        // skip the first byte which is always the type byte (list, dynamic, primitive)
        try { dis.readByte(); }
        catch(IOException ex) { fail(ex.getMessage()); }
        
        return serializer.readObjectData(dis);
    }

    private Object[] getRandomObjectArray() {
        // make the random array's length always a multiple of 11
        // so it won't cause an index out of bounds exceptions
        final int TOTAL_TYPES = 11;
        final int TOTAL_OBJECTS = MAX_ELEMENTS / TOTAL_TYPES * TOTAL_TYPES;
        
        Object[] vals = new Object[TOTAL_OBJECTS];
        
        for (int i = 0; i < TOTAL_OBJECTS; i += TOTAL_TYPES) {
            vals[i]     = (byte)(Byte.MAX_VALUE * Math.random() + Byte.MIN_VALUE * Math.random());
            vals[i+1]   = (short)(Short.MAX_VALUE * Math.random() + Short.MIN_VALUE * Math.random());
            vals[i+2]   = (int)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
            vals[i+3]   = (long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random());
            vals[i+4]   = (float)(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
            vals[i+5]   = (double)Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random();
            vals[i+6]   = new BigInteger(String.valueOf((long)(Long.MAX_VALUE * Math.random() + Long.MIN_VALUE * Math.random())));
            vals[i+7]   = new BigDecimal(Integer.MAX_VALUE * Math.random() + Integer.MIN_VALUE * Math.random());
            vals[i+8]   = Double.compare(Math.random(), 0.5d) <= 0;
            vals[i+9]   = generateRandomString(128).charAt((int)(128 * Math.random()));
            vals[i+10]  = generateRandomString(256);
        }
        
        return vals;
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
