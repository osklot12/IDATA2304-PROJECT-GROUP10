package no.ntnu.network.message.deserialize.component;

import no.ntnu.network.message.common.ByteSerializableInteger;
import no.ntnu.network.message.common.ByteSerializableSet;
import no.ntnu.network.message.common.ByteSerializableString;
import no.ntnu.network.message.serialize.NofspSerializationConstants;
import no.ntnu.network.message.serialize.tool.tlv.Tlv;
import no.ntnu.network.message.serialize.tool.tlv.TlvReader;
import no.ntnu.network.message.serialize.visitor.NofspSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the {@code NofspDeserializer} class.
 * This class tests the common {@code NofspSerializer} as well.
 */
public class NofspDeserializerTest {
    NofspSerializer serializer;
    NofspDeserializerTestClass deserializer;

    /**
     * Since {@code NofspDeserializer} is an abstract class, a concrete class needs to be established for testing
     * purposes.
     */
    private static class NofspDeserializerTestClass extends NofspDeserializer {}

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        serializer = new NofspSerializer();
        deserializer = new NofspDeserializerTestClass();
    }

    /**
     * Tests that integers are serialized and deserialized as expected.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(64);

        byte[] bytes = serializer.serialize(integer);
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(integer, deserializer.deserialize(tlv));
    }

    /**
     * Tests that negative integers are serialized and deserialized as expected.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testNegativeIntegerSerialization() throws IOException {
        ByteSerializableInteger integer = new ByteSerializableInteger(-4605);

        byte[] bytes = serializer.serialize(integer);
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(integer, deserializer.deserialize(tlv));
    }

    /**
     * Tests that strings are serialized and deserialized as expected.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testStringSerialization() throws IOException {
        ByteSerializableString string = new ByteSerializableString("Hello world!");

        byte[] bytes = serializer.serialize(string);
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(string, deserializer.deserialize(tlv));
    }

    /**
     * Tests that maps are serialized and deserialized as expected.
     *
     * @throws IOException thrown if an I/O exception occurs
     */
    @Test
    public void testSetSerialization() throws IOException {
        ByteSerializableSet<ByteSerializableString> set = new ByteSerializableSet<>();
        set.add(new ByteSerializableString("This is the first string."));
        set.add(new ByteSerializableString("This is the second string."));
        set.add(new ByteSerializableString("This is the third string."));

        byte[] bytes = serializer.serialize(set);
        Tlv tlv = TlvReader.contructTlv(bytes, NofspSerializationConstants.TLV_FRAME);

        assertEquals(set, deserializer.deserialize(tlv));
    }
}