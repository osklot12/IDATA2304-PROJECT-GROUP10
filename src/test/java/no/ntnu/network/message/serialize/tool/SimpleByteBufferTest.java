package no.ntnu.network.message.serialize.tool;

import no.ntnu.tools.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the ByteBuffer class.
 */
public class SimpleByteBufferTest {
    SimpleByteBuffer buffer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        buffer = new SimpleByteBuffer(32);
    }

    /**
     * Tests that the buffer dynamically expands as more bytes are added to the buffer.
     */
    @Test
    public void testDynamicExpansion() {
        for (int i = 0; i < 40; i++) {
            buffer.addByte((byte) 1);
        }

        assertTrue(buffer.size() > 32);
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
        byte[] byteArray = new byte[] {14, 6, 30};

        buffer.addBytes(byteArray);

        assertArrayEquals(byteArray, buffer.toArray());
    }

    /**
     * Tests that reading a byte from the buffer will then remove it from the buffer.
     */
    @Test
    public void testRead() {
        byte[] byteArray = new byte[] {14, 6, 30};

        buffer.addBytes(byteArray);

        try {
            assertEquals(14, buffer.read());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertArrayEquals(new byte[]{6, 30}, buffer.toArray());
    }
}