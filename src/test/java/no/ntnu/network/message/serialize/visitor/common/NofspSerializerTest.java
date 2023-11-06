package no.ntnu.network.message.serialize.visitor.common;

import no.ntnu.network.message.common.byteserializable.ByteSerializableInteger;
import no.ntnu.network.message.common.byteserializable.ByteSerializableList;
import no.ntnu.network.message.deserialize.NofspCommonDeserializer;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import no.ntnu.tools.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code NofspCommonSerializer} class.
 * This class also tests the {@code NofspCommonDeserializer} class.
 */
public class NofspSerializerTest {
    ByteSerializerVisitor serializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        serializer = new NofspSerializer();
    }

    /**
     * Tests that a small positive integer is serialized successfully.
     */
    @Test
    public void testSmallPositiveIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(2);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, NofspCommonDeserializer.deserialize(bytes));
    }

    /**
     * Tests that a big positive integer is serialized successfully.
     */
    @Test
    public void testBigPositiveIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(1245671);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, NofspCommonDeserializer.deserialize(bytes));
    }

    /**
     * Tests that a small negative integer is serialized successfully.
     */
    @Test
    public void testSmallNegativeIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(-15);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, NofspCommonDeserializer.deserialize(bytes));
    }

    /**
     * Tests that a big negative integer is serialized successfully.
     */
    @Test
    public void testBigNegativeIntegerSerialization() {
        ByteSerializableInteger integer = new ByteSerializableInteger(-1500879);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, NofspCommonDeserializer.deserialize(bytes));
    }

    /**
     * Tests that a list of integers is serialized successfully.
     */
    @Test
    public void testIntegerListSerialization() {
        ByteSerializableInteger fifty = new ByteSerializableInteger(50);
        ByteSerializableInteger eleven = new ByteSerializableInteger(11);
        ByteSerializableInteger minusTwoHundredAndThree = new ByteSerializableInteger(-203);
        ByteSerializableList<ByteSerializableInteger> list = new ByteSerializableList<>();
        list.add(fifty);
        list.add(eleven);
        list.add(minusTwoHundredAndThree);

        byte[] bytes = serializer.serialize(list);
        Logger.printBytes(bytes);

        ByteSerializable reconstructedSerializable = NofspCommonDeserializer.deserialize(bytes);
        assertTrue(reconstructedSerializable instanceof ByteSerializableList<?>);
        ByteSerializableList<?> reconstructedList = (ByteSerializableList<?>) reconstructedSerializable;
        assertArrayEquals(list.toArray(), reconstructedList.toArray());
        reconstructedList.forEach(
                item -> Logger.info(item.toString())
        );
    }
}