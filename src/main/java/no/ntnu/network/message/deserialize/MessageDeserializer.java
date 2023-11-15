package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.serialize.tool.TlvFrame;

import java.io.IOException;

/**
 * A deserializer for deserializing messages.
 *
 * @param <M> type of message to deserialize
 */
public interface MessageDeserializer<M extends Message<?>> {
    /**
     * Deserializes an array of bytes into a message.
     *
     * @param bytes bytes to deserialize
     * @return the deserialized message
     * @throws IOException thrown if an I/O exception occurs
     */
    M deserializeMessage(byte[] bytes) throws IOException;

    /**
     * Returns the TlvFrame used for deserialization.
     *
     * @return the tlv frame
     */
    TlvFrame getTlvFrame();
}
