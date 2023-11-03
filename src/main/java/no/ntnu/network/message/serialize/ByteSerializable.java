package no.ntnu.network.message.serialize;

import no.ntnu.network.message.serialize.serializerstrategy.SerializerStrategy;

/**
 * Can be serialized into a {@code Deserializable}.
 */
public interface ByteSerializable {
    /**
     * Serializes into a ByteDeserializable.
     *
     * @param strategy the strategy used for serialization
     * @return a ByteSerializable
     */
    ByteDeserializable serialize(SerializerStrategy strategy);
}
