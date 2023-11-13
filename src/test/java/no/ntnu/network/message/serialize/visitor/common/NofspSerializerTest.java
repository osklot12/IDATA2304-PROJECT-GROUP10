package no.ntnu.network.message.serialize.visitor.common;

import no.ntnu.exception.SerializationException;
import no.ntnu.fieldnode.device.DeviceClass;
import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableList;
import no.ntnu.network.message.common.ByteSerializableMap;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.deserialize.ByteDeserializer;
import no.ntnu.network.message.deserialize.NofspDeserializer;
import no.ntnu.network.message.request.RegisterControlPanelRequest;
import no.ntnu.network.message.serialize.composite.ByteSerializable;
import no.ntnu.network.message.serialize.visitor.ByteSerializerVisitor;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code NofspCommonSerializer} class.
 * This class also tests the {@code NofspCommonDeserializer} class.
 */
public class NofspSerializerTest {
    ByteSerializerVisitor serializer;
    ByteDeserializer deserializer;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        serializer = NofspSerializer.getInstance();
        deserializer = NofspDeserializer.getInstance();
    }

    /**
     * Tests that a small positive integer is serialized and deserialized successfully.
     */
    @Test
    public void testSmallPositiveIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(2);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, deserializer.deserialize(bytes));
    }

    /**
     * Tests that a big positive integer is serialized and deserialized successfully.
     */
    @Test
    public void testBigPositiveIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(1245671);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, deserializer.deserialize(bytes));
    }

    /**
     * Tests that a small negative integer is serialized and deserialized successfully.
     */
    @Test
    public void testSmallNegativeIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(-15);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, deserializer.deserialize(bytes));
    }

    /**
     * Tests that a big negative integer is serialized and deserialized successfully.
     */
    @Test
    public void testBigNegativeIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(-1500879);

        byte[] bytes = serializer.serialize(integer);

        assertEquals(integer, deserializer.deserialize(bytes));
    }

    /**
     * Tests that a string is serialized and deserialized successfully.
     */
    @Test
    public void testStringSerialization() throws IOException {
        ByteSerializableString string = new ByteSerializableString("Hello World!");

        byte[] bytes = serializer.serialize(string);

        assertEquals(string, deserializer.deserialize(bytes));
    }

    /**
     * Tests that a two different serialized strings will not be deserialized to the same {@code BytSerializableString}.
     */
    @Test
    public void testStringSerializationNotEqual() throws IOException {
        ByteSerializableString string = new ByteSerializableString("Hello World!");
        ByteSerializableString anotherString = new ByteSerializableString("Goodbye World!");

        byte[] bytes = serializer.serialize(string);
        byte[] differentBytes = serializer.serialize(anotherString);

        assertNotEquals(deserializer.deserialize(bytes), deserializer.deserialize(differentBytes));
    }

    /**
     * Tests that a list of integers is serialized and deserialized successfully.
     */
    @Test
    public void testIntegerListSerialization() throws IOException {
        ByteSerializableInteger fifty = new ByteSerializableInteger(50);
        ByteSerializableInteger eleven = new ByteSerializableInteger(11);
        ByteSerializableInteger minusTwoHundredAndThree = new ByteSerializableInteger(-203);
        ByteSerializableList<ByteSerializableInteger> list = new ByteSerializableList<>();
        list.add(fifty);
        list.add(eleven);
        list.add(minusTwoHundredAndThree);

        byte[] bytes = serializer.serialize(list);
        ByteSerializable reconstructedList = deserializer.deserialize(bytes);

        assertEquals(list, reconstructedList);
    }

    /**
     * Tests that a list of strings is serialized and deserialized successfully.
     */
    @Test
    public void testStringListSerialization() throws IOException {
        ByteSerializableString firstString = new ByteSerializableString("This is the first string!");
        ByteSerializableString secondString = new ByteSerializableString("This is the second string...");
        ByteSerializableString thirdString = new ByteSerializableString("I think this is the third string???");
        ByteSerializableList<ByteSerializableString> list = new ByteSerializableList<>();
        list.add(firstString);
        list.add(secondString);
        list.add(thirdString);

        byte[] bytes = serializer.serialize(list);
        ByteSerializable reconstructedList = deserializer.deserialize(bytes);

        assertEquals(list, reconstructedList);
    }

    /**
     * Tests that a map is serialized and deserialized successfully.
     */
    @Test
    public void testMapSerialization() throws IOException {
        ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> map = new ByteSerializableMap<>();
        map.put(new ByteSerializableInteger(2), new ByteSerializableString("Hello"));
        map.put(new ByteSerializableInteger(206), new ByteSerializableString("Good morning"));
        map.put(new ByteSerializableInteger(-60), new ByteSerializableString("Goodbye!"));

        byte[] bytes = serializer.serialize(map);
        ByteSerializable reconstructedMap = deserializer.deserialize(bytes);

        assertEquals(map, reconstructedMap);
    }

    /**
     * Tests that a RegisterControlPanelRequest is serialized and deserialized successfully.
     */
    @Test
    public void testRegisterControlPanelRequestSerialization() throws IOException {
        Set<DeviceClass> compatibilityList = new HashSet<>();
        compatibilityList.add(DeviceClass.A1);
        compatibilityList.add(DeviceClass.S3);
        RegisterControlPanelRequest request = new RegisterControlPanelRequest(0, compatibilityList);

        byte[] bytes = serializer.serialize(request);
        ByteSerializable reconstructedRequest = deserializer.deserialize(bytes);

        assertEquals(request, reconstructedRequest);
    }
}