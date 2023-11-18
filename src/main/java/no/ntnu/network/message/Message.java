package no.ntnu.network.message;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.composite.ByteSerializable;

import java.io.IOException;

/**
 * A message sent from an entity to another.
 * Messages encapsulates their processing logic, and uses any implementation of the {@code MessageContext} for this
 * processing. The interface uses java generics to allow for different context implementations during runtime, which
 * provides a tailored message interface for each network entity.
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
