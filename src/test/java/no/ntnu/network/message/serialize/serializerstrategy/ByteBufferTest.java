package no.ntnu.network.message.serialize.serializerstrategy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the ByteBuffer class.
 */
public class ByteBufferTest {
    ByteBuffer buffer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        buffer = new ByteBuffer();
    }

    /**
     * Tests that the buffer dynamically expands as more bytes are added to the buffer.
     */
    @Test
    public void testDynamicExpansion() {
        for (int i = 0; i < 18; i++) {
            buffer.addByte((byte) 100);
        }

        assertEquals(64, buffer.getCapacity());
    }

    /**
     * Tests that the toArray() method returns the expected array.
     */
    @Test
    public void testToArray() {
        for (int i = 0; i < 18; i++) {
            buffer.addByte((byte) 100);
        }

        assertEquals(18, buffer.toArray().length);
    }

    /**
     * Tests that adding a whole array into the buffer is handled correctly.
     */
    @Test
    public void testInsertingArray() {
        byte[] byteArray = new byte[3];
        byteArray[0] = 1;
        byteArray[1] = 60;
        byteArray[2] = 13;

        buffer.addBytes(byteArray);

        assertArrayEquals(byteArray, buffer.toArray());
    }
}