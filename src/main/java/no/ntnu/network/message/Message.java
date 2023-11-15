package no.ntnu.network.message;

import no.ntnu.network.message.context.MessageContext;
import no.ntnu.network.message.serialize.composite.ByteSerializable;

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
     */
    void process(C context);
}
