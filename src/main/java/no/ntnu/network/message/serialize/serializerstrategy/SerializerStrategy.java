package no.ntnu.network.message.serialize.serializerstrategy;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.serialize.ByteDeserializable;
import no.ntnu.network.message.serialize.ByteSerializable;

/**
 * A set of algorithms for serializing and deserializing {@code ByteSerializable} and {@code ByteDeserializable}.
 * This interface is used as the strategy interface in the strategy pattern.
 */
public interface SerializerStrategy {
    /**
     * Serializes an integer.
     *
     * @param integer the integer to serialize
     * @return a deserializable representation of the integer
     */
    ByteDeserializable serializeInteger(int integer);

    /**
     * Deserializes a {@code ByteSerializable} object.
     *
     * @param deserializable the deserializable object to deserialize
     * @return a {@code ByteSerializable} object
     * @throws SerializationException thrown if deserializable cannot be deserialized
     */
    ByteSerializable deserialize(ByteDeserializable deserializable) throws SerializationException;
}
