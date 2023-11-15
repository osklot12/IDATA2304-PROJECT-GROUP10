package no.ntnu.network.message;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.composite.ByteSerializable;

import java.io.IOException;

/**
 * A message sent from an entity to another.
 *
 * @param <C> any message context used for message processing
 */
public interface Message<C extends MessageContext> extends ByteSerializable {
    /**
     * Processes the message.
     *
     * @param context the context to operate on
     * @throws IOException thrown if an I/O exception occurs
     */
    void process(C context) throws IOException;
}
