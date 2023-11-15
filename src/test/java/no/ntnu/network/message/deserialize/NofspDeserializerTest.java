package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.serialize.composite.ByteSerializable;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * JUnit testing for the NofspDeserializer class.
 */
public class NofspDeserializerTest {
    /**
     * Since {@code NofspDeserializer} is an abstract class, a concrete class needs to be established for testing
     * purposes.
     */
    private static class NofspDeserializerTestClass extends NofspDeserializer {

    }
}