package no.ntnu.network.message.deserialize;

import no.ntnu.network.message.Message;
import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.tool.TlvFrame;

import java.io.IOException;

/**
 * A deserializer for deserialization of bytes into messages.
 * The {@code MessageDeserializer} works with arrays of bytes, deserializing them into {@code Message} objects that
 * can be processed with the given {@code MessageContext}.
 *
 * @param <C> type of message context to deserialize for
 */
public interface MessageDeserializer<C extends MessageContext> {
    /**
     * Deserializes an array of bytes into a message.
     *
     * @param bytes bytes to deserialize
     * @return the deserialized message
     * @throws IOException thrown if an I/O exception occurs
     */
    Message<C> deserializeMessage(byte[] bytes) throws IOException;

    /**
     * Returns the TlvFrame used for deserialization.
     *
     * @return the tlv frame
     */
    TlvFrame getTlvFrame();
}
