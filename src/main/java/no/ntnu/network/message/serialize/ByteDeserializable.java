package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.serializerstrategy.SerializerStrategy;

/**
 * Can deserialize into a {@code ByteSerializable}.
 */
public interface ByteDeserializable {
    /**
     * Deserializes into a ByteSerializable.
     *
     * @param strategy the strategy used for deserialization
     * @return a ByteSerializable
     */
    ByteSerializable deserialize(SerializerStrategy strategy);

    /**
     * Returns the bytes for the ByteDeserializable.
     *
     * @return the bytes
     */
    byte[] getBytes();
}
