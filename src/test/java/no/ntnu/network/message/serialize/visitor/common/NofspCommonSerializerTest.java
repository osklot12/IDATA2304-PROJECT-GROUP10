package no.ntnu.network.message.serialize.visitor.common;

import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.deserialize.NofspCommonDeserializer;
import no.ntnu.network.message.serialize.NofspCommonSerializer;
import no.ntnu.tools.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * JUnit testing for the {@code NofspCommonSerializer} class.
 * This class also tests the {@code NofspCommonDeserializer} class.
 */
public class NofspCommonSerializerTest {
    /**
     * Tests that a small positive integer is serialized successfully.
     */
    @Test
    public void testSmallPositiveIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(2);

        byte[] bytes = NofspCommonSerializer.serializeInteger(integer);

        assertEquals(integer, NofspCommonDeserializer.getInteger(bytes));
    }

    /**
     * Tests that a big positive integer is serialized successfully.
     */
    @Test
    public void testBigPositiveIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(1245671);

        byte[] bytes = NofspCommonSerializer.serializeInteger(integer);

        assertEquals(integer, NofspCommonDeserializer.getInteger(bytes));
    }

    /**
     * Tests that a small negative integer is serialized successfully.
     */
    @Test
    public void testSmallNegativeIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(-15);

        byte[] bytes = NofspCommonSerializer.serializeInteger(integer);

        assertEquals(integer, NofspCommonDeserializer.getInteger(bytes));
    }

    /**
     * Tests that a big negative integer is serialized successfully.
     */
    @Test
    public void testBigNegativeIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(-1500879);

        byte[] bytes = NofspCommonSerializer.serializeInteger(integer);

        assertEquals(integer, NofspCommonDeserializer.getInteger(bytes));
    }
}