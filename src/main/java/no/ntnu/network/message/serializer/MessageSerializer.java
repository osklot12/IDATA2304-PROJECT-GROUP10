package no.ntnu.network.message.serializer;

import no.ntnu.exception.SerializationException;
import no.ntnu.network.message.Message;

/**
 * A serializer translating messages into bytes, and vice versa.
 */
public interface MessageSerializer {
    /**
     * Serializes a Message object to bytes.
     *
     * @param message message to serialize
     * @return serialized bytes
     * @throws SerializationException thrown when message cannot be serialized
     */
    byte[] serialize(Message message) throws SerializationException;

    /**
     * Deserializes bytes to a Message object.
     *
     * @param bytes bytes to deserialize
     * @return Message object
     * @throws SerializationException thrown when bytes cannot be deserialized
     */
    Message deserialize(byte[] bytes) throws SerializationException;
}
