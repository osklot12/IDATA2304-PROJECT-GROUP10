package no.ntnu.network.message.serialize.serializerstrategy;

import no.ntnu.network.message.common.bytedeserialized.ByteDeserializedInteger;
import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.ByteSerializable;
import no.ntnu.tools.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit tests for the NofspSerializerStrategy class.
 */
public class NofspSerializerStrategyTest {
    SerializerStrategy strategy;

    /**
     * Setting up for the following test methods.
     */
    @Before
    public void setup() {
        strategy = new NofspSerializerStrategy();
    }

    /**
     * Tests that an integer is serialized and deserialized as expected.
     */
    @Test
    public void testSerializationOfInteger() {
        ByteSerializable serializableInt = new ByteDeserializedInteger(20);

        ByteDeserializable deserializableInt = serializableInt.serialize(strategy);

        Logger.printBytes(deserializableInt.getBytes());

        ByteSerializable serializableIntRecreation = deserializableInt.deserialize(strategy);

        assertEquals(serializableInt, serializableIntRecreation);
    }
}